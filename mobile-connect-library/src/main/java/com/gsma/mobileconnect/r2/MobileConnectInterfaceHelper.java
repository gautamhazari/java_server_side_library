/*
 * SOFTWARE USE PERMISSION
 *
 * By downloading and accessing this software and associated documentation files ("Software") you are granted the
 * unrestricted right to deal in the Software, including, without limitation the right to use, copy, modify, publish,
 * sublicense and grant such rights to third parties, subject to the following conditions:
 *
 * The following copyright notice and this permission notice shall be included in all copies, modifications or
 * substantial portions of this Software: Copyright © 2016 GSM Association.
 *
 * THE SOFTWARE IS PROVIDED "AS IS," WITHOUT WARRANTY OF ANY KIND, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. YOU AGREE TO
 * INDEMNIFY AND HOLD HARMLESS THE AUTHORS AND COPYRIGHT HOLDERS FROM AND AGAINST ANY SUCH LIABILITY.
 */
package com.gsma.mobileconnect.r2;

import com.gsma.mobileconnect.r2.authentication.AuthenticationOptions;
import com.gsma.mobileconnect.r2.authentication.IAuthenticationService;
import com.gsma.mobileconnect.r2.authentication.RequestTokenResponse;
import com.gsma.mobileconnect.r2.authentication.StartAuthenticationResponse;
import com.gsma.mobileconnect.r2.constants.DefaultOptions;
import com.gsma.mobileconnect.r2.constants.Parameters;
import com.gsma.mobileconnect.r2.discovery.DiscoveryOptions;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.discovery.IDiscoveryService;
import com.gsma.mobileconnect.r2.discovery.ParsedDiscoveryRedirect;
import com.gsma.mobileconnect.r2.encoding.IMobileConnectEncodeDecoder;
import com.gsma.mobileconnect.r2.exceptions.AbstractMobileConnectException;
import com.gsma.mobileconnect.r2.exceptions.InvalidArgumentException;
import com.gsma.mobileconnect.r2.identity.IIdentityService;
import com.gsma.mobileconnect.r2.identity.IdentityResponse;
import com.gsma.mobileconnect.r2.json.IJsonService;
import com.gsma.mobileconnect.r2.utils.*;
import com.gsma.mobileconnect.r2.validation.IJWKeysetService;
import com.gsma.mobileconnect.r2.validation.JWKeyset;
import com.gsma.mobileconnect.r2.validation.TokenValidation;
import com.gsma.mobileconnect.r2.validation.TokenValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.net.URI;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Performs interaction on behalf of {@link MobileConnectInterface}.
 *
 * @since 2.0
 */
class MobileConnectInterfaceHelper
{
    private static final Logger LOGGER =
        LoggerFactory.getLogger(MobileConnectInterfaceHelper.class);
    private static final Pattern NONCE_REGEX = Pattern.compile("\"?nonce\"?:\"(.*?)\"");
    private static final String DISCOVERY_RESPONSE = "discoveryResponse";

    private MobileConnectInterfaceHelper()
    {
    }

    static MobileConnectStatus attemptDiscovery(final IDiscoveryService discoveryService,
        final String msisdn, final String mcc, final String mnc,
        final Iterable<KeyValuePair> cookies, final MobileConnectConfig config,
        final DiscoveryOptions.Builder discoveryOptionsBuilder)
    {
        try
        {
            discoveryOptionsBuilder
                .withMsisdn(msisdn)
                .withIdentifiedMcc(mcc)
                .withIdentifiedMnc(mnc)
                .withRedirectUrl(config.getRedirectUrl())
                .withXRedirect(config.getXRedirect());

            final DiscoveryResponse response =
                discoveryService.startAutomatedOperatorDiscovery(config, config.getRedirectUrl(),
                    discoveryOptionsBuilder.build(), cookies);

            return extractStatus(response, discoveryService, "attemptDiscovery");
        }
        catch (final Exception e)
        {
            LOGGER.warn("attemptDiscovery failed for msisdn={}, mcc={}, mnc={}",
                LogUtils.mask(msisdn, LOGGER, Level.WARN), mcc, mnc, e);
            return MobileConnectStatus.error("start automated discovery", e);
        }
    }

    static MobileConnectStatus attemptDiscoveryAfterOperatorSelection(
        final IDiscoveryService discoveryService, final URI redirectedUrl,
        final MobileConnectConfig config)
    {
        final ParsedDiscoveryRedirect parsedDiscoveryRedirect =
            discoveryService.parseDiscoveryRedirect(redirectedUrl);

        if (!parsedDiscoveryRedirect.hasMccAndMnc())
        {
            LOGGER.debug(
                "Responding with responseType={} for attemptDiscoveryAfterOperatorSelection for redirectedUrl={}",
                MobileConnectStatus.ResponseType.START_DISCOVERY,
                LogUtils.maskUri(redirectedUrl, LOGGER, Level.DEBUG));
            return MobileConnectStatus.startDiscovery();
        }
        else
        {
            try
            {
                DiscoveryResponse response =
                    discoveryService.completeSelectedOperatorDiscovery(config,
                        config.getRedirectUrl(), parsedDiscoveryRedirect.getSelectedMcc(),
                        parsedDiscoveryRedirect.getSelectedMnc());

                if (response.getResponseData().getSubscriberId() == null)
                {
                    final String encryptedMsisdn = parsedDiscoveryRedirect.getEncryptedMsisdn();
                    LOGGER.debug(
                        "Setting encryptedMsisdn={} against cached DiscoveryResponse for redirectedUrl={}",
                        LogUtils.mask(encryptedMsisdn, LOGGER, Level.DEBUG),
                        LogUtils.maskUri(redirectedUrl, LOGGER, Level.DEBUG));
                    response = response.withSubscriberId(encryptedMsisdn);
                }

                return extractStatus(response, discoveryService,
                    "attemptDiscoveryAfterOperatorSelection");
            }
            catch (final Exception e)
            {
                LOGGER.warn("attemptDiscoveryAfterOperatorSelection failed for redirectedUrl={}",
                    LogUtils.maskUri(redirectedUrl, LOGGER, Level.WARN), e);
                return MobileConnectStatus.error("attempt discovery after operator selection", e);
            }
        }
    }

    static MobileConnectStatus startAuthentication(final IAuthenticationService authnService,
        final DiscoveryResponse discoveryResponse, final String encryptedMsisdn, final String state,
        final String nonce, final MobileConnectConfig config,
        final AuthenticationOptions.Builder authnOptionsBuilder, final String currentVersion)
    {
        ObjectUtils.requireNonNull(discoveryResponse, DISCOVERY_RESPONSE);

        try
        {
            final String clientId = ObjectUtils.defaultIfNull(
                discoveryResponse.getResponseData().getResponse().getClientId(),
                config.getClientId());
            final String correlationId =
                    discoveryResponse.getResponseData().getCorrelationId();
            final URI authorizationUrl =
                URI.create(discoveryResponse.getOperatorUrls().getAuthorizationUrl());
            if(discoveryResponse.getClientName()!=null) {
                authnOptionsBuilder.withClientName(discoveryResponse.getClientName());
            }
            final StartAuthenticationResponse startAuthenticationResponse =
                authnService.startAuthentication(clientId, correlationId, authorizationUrl,
                    config.getRedirectUrl(), state, nonce, encryptedMsisdn,
                    authnOptionsBuilder.build(), currentVersion);

            LOGGER.debug(
                "Responding with responseType={} for startAuthentication for encryptedMsisdn={}, state={}, nonce={}, startAuthenticationResponseUrl={}",
                MobileConnectStatus.ResponseType.AUTHENTICATION,
                LogUtils.mask(encryptedMsisdn, LOGGER, Level.DEBUG), state,
                LogUtils.mask(nonce, LOGGER, Level.DEBUG),
                LogUtils.maskUri(startAuthenticationResponse.getUrl(), LOGGER, Level.DEBUG));

            return MobileConnectStatus.authentication(
                startAuthenticationResponse.getUrl().toString(), state, nonce);
        }
        catch (final Exception e)
        {
            LOGGER.warn("startAuthentication failed for encryptedMsisdn={}, state={}, nonce={}",
                LogUtils.mask(encryptedMsisdn, LOGGER, Level.WARN), state,
                LogUtils.mask(nonce, LOGGER, Level.WARN), e);
            return MobileConnectStatus.error("start authentication", e);
        }
    }

    static MobileConnectStatus requestHeadlessAuthentication(
        final IAuthenticationService authnService, final IIdentityService identityService,
        final DiscoveryResponse discoveryResponse, final String encryptedMsisdn,
        final String expectedState, final String expectedNonce, final MobileConnectConfig config,
        final MobileConnectRequestOptions options,
        final IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder,
        final IJWKeysetService jwKeysetService, final IJsonService jsonService, final String currentVersion)
    {
        ObjectUtils.requireNonNull(discoveryResponse, DISCOVERY_RESPONSE);

        final AuthenticationOptions.Builder builder = options != null
                                                      ? options.getAuthenticationOptionsBuilder()
                                                      : new AuthenticationOptions.Builder();
        try
        {
            final long maxAge = extractMaxAge(options);

            final String clientId = ObjectUtils.defaultIfNull(
                discoveryResponse.getResponseData().getResponse().getClientId(),
                config.getClientId());
            final String clientSecret =
                discoveryResponse.getResponseData().getResponse().getClientSecret();
            final String correlationId =
                discoveryResponse.getResponseData().getCorrelationId();
            final URI authorizationUrl =
                URI.create(discoveryResponse.getOperatorUrls().getAuthorizationUrl());
            final URI tokenUrl =
                URI.create(discoveryResponse.getOperatorUrls().getRequestTokenUrl());

            builder.withClientName(discoveryResponse.getClientName());

            final String issuer = discoveryResponse.getProviderMetadata().getIssuer();

            final AuthenticationOptions authenticationOptions = builder.build();

            final Future<RequestTokenResponse> requestTokenResponseAsync =
                authnService.requestHeadlessAuthentication(clientId, clientSecret, correlationId, authorizationUrl,
                    tokenUrl, config.getRedirectUrl(), expectedState, expectedNonce,
                    encryptedMsisdn, authenticationOptions, currentVersion);

            final RequestTokenResponse requestTokenResponse = requestTokenResponseAsync.get();

            final MobileConnectStatus status =
                processRequestTokenResponse(requestTokenResponse, expectedState, expectedNonce,
                    config.getRedirectUrl(), iMobileConnectEncodeDecoder, jwKeysetService,
                    discoveryResponse, clientId, issuer, maxAge, jsonService,
                    discoveryResponse.getProviderMetadata().getVersion(), currentVersion);

            if (!StringUtils.isNull(status.getRequestTokenResponse().getResponseData().getCorrelationId()) &&
                    !status.getDiscoveryResponse().getResponseData().getCorrelationId().equals(correlationId))
            {
                throw new Exception("Invalid correlation id in headless authentication response");
            }

            if (status.getResponseType() == MobileConnectStatus.ResponseType.ERROR || (options
                != null && !options.isAutoRetrieveIdentitySet()) || StringUtils.isNullOrEmpty(
                discoveryResponse.getOperatorUrls().getPremiumInfoUri()))
            {
                return status;
            }

            MobileConnectStatus identityStatus = requestInfo(identityService,
                requestTokenResponse.getResponseData().getAccessToken(),
                discoveryResponse.getOperatorUrls().getPremiumInfoUri(), "requestIdentity",
                MobileConnectStatus.ResponseType.IDENTITY, iMobileConnectEncodeDecoder);

            return new MobileConnectStatus.Builder(status)
                .withIdentityResponse(identityStatus.getIdentityResponse())
                .build();
        }
        catch (final Exception e)
        {
            LOGGER.warn(
                "requestHeadlessAuthentication failed for encryptedMsisdn={}, state={}, nonce={}",
                LogUtils.mask(encryptedMsisdn, LOGGER, Level.WARN), expectedState,
                LogUtils.mask(expectedNonce, LOGGER, Level.WARN), e);
            return MobileConnectStatus.error("request headless authentication", e);
        }
    }


    static MobileConnectStatus requestToken(final IAuthenticationService authnService,
        final IJWKeysetService jwKeysetService, final DiscoveryResponse discoveryResponse,
        final URI redirectedUrl, final String expectedState, final String expectedNonce,
        final MobileConnectConfig config, final MobileConnectRequestOptions options,
        final IJsonService jsonService,
        final IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder, final String currentVersion)
    {
        ObjectUtils.requireNonNull(discoveryResponse, DISCOVERY_RESPONSE);
        StringUtils.requireNonEmpty(expectedState, "expectedState");

        final AuthenticationOptions.Builder builder = options != null
                ? options.getAuthenticationOptionsBuilder()
                : new AuthenticationOptions.Builder();

//        options = options.Builder.

//        options = builder.withClientSecret(discoveryResponse.getResponseData().getResponse().getClientSecret()).build();

//        if (currentVersion.equals(DefaultOptions.MC_V2_3)) {
//            StringUtils.requireNonEmpty(options.getClientSecret(), "client_secret");
//        }

        long maxAge = extractMaxAge(options);

        if (!isUsableDiscoveryResponse(discoveryResponse))
        {
            return MobileConnectStatus.startDiscovery();
        }

        final String actualState = HttpUtils.extractQueryValue(redirectedUrl, "state");
        if (!expectedState.equals(actualState))
        {
            LOGGER.warn(
                "Responding with responseType={} for requestToken for redirectedUrl={}, expectedState={}, expectedNonce={}, as actualState={}; possible cross-site forgery",
                MobileConnectStatus.ResponseType.ERROR,
                LogUtils.maskUri(redirectedUrl, LOGGER, Level.WARN), expectedState,
                LogUtils.mask(expectedNonce, LOGGER, Level.WARN), actualState);

            return MobileConnectStatus.error("invalid_state",
                "state values do not match, possible cross-site request forgery", null);
        }
        else
        {
            final String code = HttpUtils.extractQueryValue(redirectedUrl, "code");


            final String clientId = ObjectUtils.defaultIfNull(
                discoveryResponse.getResponseData().getResponse().getClientId(),
                config.getClientId());
            final String clientSecret = ObjectUtils.defaultIfNull(
                discoveryResponse.getResponseData().getResponse().getClientSecret(),
                config.getClientSecret());

            if (currentVersion.equals(DefaultOptions.MC_V2_3)) {
                StringUtils.requireNonEmpty(clientSecret, "client_secret");
            }
            final String correlationId =
                    discoveryResponse.getResponseData().getCorrelationId();
            final String requestTokenUrl = discoveryResponse.getOperatorUrls().getRequestTokenUrl();
            final String issuer = discoveryResponse.getProviderMetadata().getIssuer();

            try
            {
                final Future<RequestTokenResponse> requestTokenResponseFuture =
                    authnService.requestTokenAsync(clientId, clientSecret, correlationId,
                        URI.create(requestTokenUrl), config.getRedirectUrl(), code);

                final RequestTokenResponse requestTokenResponse = requestTokenResponseFuture.get();

                MobileConnectStatus status = processRequestTokenResponse(requestTokenResponse, expectedState,
                        expectedNonce, redirectedUrl, iMobileConnectEncodeDecoder, jwKeysetService,
                        discoveryResponse, clientId, issuer, maxAge, jsonService,
                        discoveryResponse.getProviderMetadata().getVersion(), currentVersion);
                return status;
            }
            catch (final Exception e)
            {
                LOGGER.warn(
                    "requestToken failed for redirectedUrl={}, expectedState={}, expectedNonce={}",
                    LogUtils.maskUri(redirectedUrl, LOGGER, Level.WARN), expectedState,
                    LogUtils.mask(expectedNonce, LOGGER, Level.WARN), e);

                return MobileConnectStatus.error("request token", e);
            }
        }
    }

    private static long extractMaxAge(final MobileConnectRequestOptions options)
    {
        long maxAge = DefaultOptions.AUTHENTICATION_MAX_AGE;
        if (options != null && options.getAuthenticationOptions() != null)
        {
            maxAge = options.getAuthenticationOptions().getMaxAge();
        }
        return maxAge;
    }

    private static MobileConnectStatus processRequestTokenResponse( //NOSONAR
        final RequestTokenResponse requestTokenResponse, final String expectedState,
        final String expectedNonce, final URI redirectedUrl,
        final IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder, final IJWKeysetService jwks,
        final DiscoveryResponse discoveryResponse, final String clientId, final String issuer,
        final long maxAge, final IJsonService jsonService, final String version, final String currentVersion)
            throws Exception {
        final ErrorResponse errorResponse = requestTokenResponse.getErrorResponse();
        if (errorResponse != null)
        {
            LOGGER.warn(
                "Responding with responseType={} for requestToken for redirectedUrl={}, expectedState={}, expectedNonce={}, authentication service responded with error={}",
                MobileConnectStatus.ResponseType.ERROR, redirectedUrl, expectedState,
                LogUtils.mask(expectedNonce, LOGGER, Level.WARN), errorResponse);

            if (!StringUtils.isNull(errorResponse.getCorrelationId())  &&  !errorResponse.getCorrelationId().equals(discoveryResponse.getResponseData().getCorrelationId()))
            {
                throw new Exception("Invalid correlation id in error response");
            }

            return MobileConnectStatus.error(errorResponse.getError(),
                errorResponse.getErrorDescription(), null, requestTokenResponse);
        }
        else if (!isExpectedNonce(requestTokenResponse.getResponseData().getIdToken(), expectedNonce,
            iMobileConnectEncodeDecoder))
        {
            LOGGER.warn(
                "Responding with responseType={} for requestToken for redirectedUrl={}, expectedState={}, expectedNonce={}, as jwtToken did not contain expectedNonce; possible replay attack",
                MobileConnectStatus.ResponseType.ERROR,
                LogUtils.maskUri(redirectedUrl, LOGGER, Level.WARN), expectedState,
                LogUtils.mask(expectedNonce, LOGGER, Level.WARN));

            return MobileConnectStatus.error("invalid_nonce",
                "nonce values do not match, possible replay attack", null);
        }
        else
        {
            // MC v1.1 when version is null & JWKS was not mandatory in MC v1.1
            if (version == null && discoveryResponse.getOperatorUrls().getJwksUri() == null)
            {
                // TokenValidated set to 'false' by default
                return MobileConnectStatus.complete(requestTokenResponse);
            }
            else
            {

                if (!StringUtils.isNull(requestTokenResponse.getResponseData().getCorrelationId()) &&
                        !requestTokenResponse.getResponseData().getCorrelationId().equals(discoveryResponse.getResponseData().getCorrelationId()))
                {
                    throw new Exception("Invalid correlation id in request token response");
                }

                final JWKeyset jwKeyset =
                    jwks.retrieveJwks(discoveryResponse.getOperatorUrls().getJwksUri());

                final TokenValidationResult accessTokenValidationResult =
                    TokenValidation.validateAccessToken(requestTokenResponse.getResponseData());
                if (!TokenValidationResult.VALID.equals(accessTokenValidationResult))
                {
                    LOGGER.info("Access Token Validation Failure...");
                    return MobileConnectStatus.error("Invalid Access Token",
                        "Access Token validation failed", null, requestTokenResponse);
                }

                LOGGER.debug(
                    "Responding with responseType={} for requestToken for redirectedUrl={}, expectedState={}, expectedNonce={}",
                    MobileConnectStatus.ResponseType.COMPLETE,
                    LogUtils.maskUri(redirectedUrl, LOGGER, Level.DEBUG), expectedState,
                    LogUtils.mask(expectedNonce, LOGGER, Level.DEBUG));

                final TokenValidationResult tokenValidationResult = TokenValidation.validateIdToken(
                    requestTokenResponse.getResponseData().getIdToken(), clientId, issuer,
                    expectedNonce, maxAge, jwKeyset, jsonService, iMobileConnectEncodeDecoder, currentVersion);

                if (TokenValidationResult.VALID.equals(tokenValidationResult))
                {
                    LOGGER.info("Id Token Validation Success");
                    RequestTokenResponse validatedResponse =
                        new RequestTokenResponse.Builder(requestTokenResponse)
                            .withTokenValidated(true)
                            .build();
                    // TokenValidated set to 'true'
                    return MobileConnectStatus.complete(validatedResponse);
                }
                else
                {
                    LOGGER.info("Id Token Validation Failure");
                    return MobileConnectStatus.error("Invalid Id Token", "Token validation failed",
                        null, requestTokenResponse);
                }
            }

        }
    }

    private static boolean isExpectedNonce(final String token, final String expectedNonce,
        final IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder)
    {
        final String decodedPayload =
            JsonWebTokens.Part.CLAIMS.decode(token, iMobileConnectEncodeDecoder);
        final Matcher matcher = NONCE_REGEX.matcher(decodedPayload);
        return matcher.find() && matcher.group(1).equals(expectedNonce);
    }


    static MobileConnectStatus handleUrlRedirect(final IDiscoveryService discoveryService, //NOSONAR
        final IJWKeysetService jwKeysetService, final IAuthenticationService authnService,
        final URI redirectedUrl, final DiscoveryResponse discoveryResponse,
        final String expectedState, final String expectedNonce, final MobileConnectConfig config,
        final MobileConnectRequestOptions options, final IJsonService jsonService,
        final IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder, final String currentVersion)
    {
        ObjectUtils.requireNonNull(redirectedUrl, "redirectedUrl");

        if (HttpUtils.extractQueryValue(redirectedUrl, Parameters.CODE) != null)
        {
            LOGGER.debug(
                "handleUrlRedirect redirecting to requestToken for redirectedUrl={}, expectedState={}, expectedNonce={}",
                LogUtils.maskUri(redirectedUrl, LOGGER, Level.DEBUG), expectedState,
                LogUtils.mask(expectedNonce, LOGGER, Level.DEBUG));

            MobileConnectStatus tokenStatus = requestToken(authnService, jwKeysetService, discoveryResponse, redirectedUrl,
                    expectedState, expectedNonce, config, options, jsonService,
                    iMobileConnectEncodeDecoder, currentVersion);
            return tokenStatus;

        }
        else  if (redirectedUrl.toString().contains(Parameters.DISCOVERY_CALLBACK))
        {
            LOGGER.debug(
                    "handleUrlRedirect redirecting to requestToken for redirectedUrl={}, expectedState={}, expectedNonce={}",
                    LogUtils.maskUri(redirectedUrl, LOGGER, Level.DEBUG), expectedState,
                    LogUtils.mask(expectedNonce, LOGGER, Level.DEBUG));

            MobileConnectStatus tokenStatus = requestToken(authnService, jwKeysetService, discoveryResponse, redirectedUrl,
                    expectedState, expectedNonce, config, options, jsonService,
                    iMobileConnectEncodeDecoder, currentVersion);
            return tokenStatus;


        }
        else if (HttpUtils.extractQueryValue(redirectedUrl, Parameters.MCC_MNC) != null)
        {
            LOGGER.debug(
                "handleUrlRedirect redirecting to attemptDiscoveryAfterOperatorSelection for redirectedUrl={}, expectedState={}, expectedNonce={}",
                LogUtils.maskUri(redirectedUrl, LOGGER, Level.DEBUG), expectedState,
                LogUtils.mask(expectedNonce, LOGGER, Level.DEBUG));

            return attemptDiscoveryAfterOperatorSelection(discoveryService, redirectedUrl, config);
        }
        else
        {
            final String errorCode = HttpUtils.extractQueryValue(redirectedUrl, Parameters.ERROR);
            String errorDescription =
                HttpUtils.extractQueryValue(redirectedUrl, Parameters.ERROR_DESCRIPTION);

            if (errorDescription == null)
            {
                errorDescription =
                    HttpUtils.extractQueryValue(redirectedUrl, Parameters.DESCRIPTION);
            }

            final MobileConnectStatus status =
                MobileConnectStatus.error(ObjectUtils.defaultIfNull(errorCode, "invalid_request"),
                    ObjectUtils.defaultIfNull(errorDescription,
                        String.format("unable to parse next step using %s", redirectedUrl)), null);

            LOGGER.warn(
                "Responding with responseType={} for handleUrlRedirect for redirectedUrl={}, expectedState={}, expectedNonce={}; with error={}, description={}",
                MobileConnectStatus.ResponseType.ERROR,
                LogUtils.maskUri(redirectedUrl, LOGGER, Level.DEBUG), expectedState,
                LogUtils.mask(expectedNonce, LOGGER, Level.WARN), status.getErrorCode(),
                status.getErrorMessage());

            return status;
        }
    }

    static MobileConnectStatus requestUserInfo(final IIdentityService identityService,
        final DiscoveryResponse discoveryResponse, final String accessToken,
        final IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder)
    {
        return requestInfo(identityService, accessToken,
            discoveryResponse.getOperatorUrls().getUserInfoUrl(), "requestUserInfo",
            MobileConnectStatus.ResponseType.USER_INFO, iMobileConnectEncodeDecoder);
    }

    static MobileConnectStatus requestIdentity(final IIdentityService identityService,
        final DiscoveryResponse discoveryResponse, final String accessToken,
        final IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder)
    {
        return requestInfo(identityService, accessToken,
            discoveryResponse.getOperatorUrls().getPremiumInfoUri(), "requestIdentity",
            MobileConnectStatus.ResponseType.IDENTITY, iMobileConnectEncodeDecoder);
    }

    private static MobileConnectStatus requestInfo(final IIdentityService identityService,
        final String accessToken, final String infoUrl, final String method,
        final MobileConnectStatus.ResponseType responseType,
        final IMobileConnectEncodeDecoder mobileConnectEncodeDecoder)
    {
        if (StringUtils.isNullOrEmpty(infoUrl))
        {
            LOGGER.warn(
                "Responding with responseType={} for {} for accessToken={}, provider does not support {}",
                MobileConnectStatus.ResponseType.ERROR, method,
                LogUtils.mask(accessToken, LOGGER, Level.WARN), responseType);

            return MobileConnectStatus.error("not_supported",
                String.format("%s not supported by current operator", responseType), null);
        }
        else
        {
            return processRequestInfoRequest(identityService, accessToken, infoUrl, method,
                responseType, mobileConnectEncodeDecoder);
        }
    }

    private static MobileConnectStatus processRequestInfoRequest(
        final IIdentityService identityService, final String accessToken, final String infoUrl,
        final String method, final MobileConnectStatus.ResponseType responseType,
        final IMobileConnectEncodeDecoder mobileConnectEncodeDecoder)
    {
        try
        {
            final IdentityResponse response =
                identityService.requestInfo(URI.create(infoUrl), accessToken,
                    mobileConnectEncodeDecoder);

            final ErrorResponse errorResponse = response.getErrorResponse();
            if (errorResponse != null)
            {
                LOGGER.warn(
                    "Responding with responseType={} for {} for accessToken={}, identity service responded with error={}",
                    MobileConnectStatus.ResponseType.ERROR, method,
                    LogUtils.mask(accessToken, LOGGER, Level.WARN), errorResponse);

                return MobileConnectStatus.error(errorResponse.getError(),
                    errorResponse.getErrorDescription(), null);
            }
            else
            {
                LOGGER.debug("Responding with responseType={} for {} for accessToken={}",
                    MobileConnectStatus.ResponseType.USER_INFO, method,
                    LogUtils.mask(accessToken, LOGGER, Level.DEBUG));

                return new MobileConnectStatus.Builder()
                    .withResponseType(responseType)
                    .withIdentityResponse(response)
                    .build();
            }
        }
        catch (final InvalidArgumentException e)
        {
            return handleErrorStatus(e, method, accessToken, responseType);
        }
        catch (final AbstractMobileConnectException e)
        {
            return handleErrorStatus(e, method, accessToken, responseType);
        }
        catch (final Exception e)
        {
            LOGGER.warn("{} failed for accessToken={}", method,
                LogUtils.mask(accessToken, LOGGER, Level.WARN), e);
            return MobileConnectStatus.error(String.format("request %s", responseType), e);
        }
    }

    private static MobileConnectStatus handleErrorStatus(final IHasMobileConnectStatus e,
        final String method, final String accessToken,
        final MobileConnectStatus.ResponseType responseType)
    {
        LOGGER.warn("{} failed for accessToken={}", method,
            LogUtils.mask(accessToken, LOGGER, Level.WARN), e);
        return e.toMobileConnectStatus(String.format("request %s", responseType));
    }

    public static MobileConnectStatus extractStatus(final DiscoveryResponse response,
        final IDiscoveryService service, final String task)
    {
        if (!response.isCached() && response.getErrorResponse() != null)
        {
            LOGGER.info("Responding with responseType={} for {}; errorResponse={}",
                MobileConnectStatus.ResponseType.ERROR, task, response.getErrorResponse());

            return MobileConnectStatus.error(response.getErrorResponse().getError(),
                ObjectUtils.defaultIfNull(response.getErrorResponse().getErrorDescription(),
                    "failure reported by discovery service, see response for more information"),
                null, response);
        }
        else
        {
            final String operatorSelectionUrl = service.extractOperatorSelectionURL(response);
            if (!StringUtils.isNullOrEmpty(operatorSelectionUrl))
            {
                LOGGER.debug("Responding with responseType={} for {}; operatorSelectionUrl={}",
                    MobileConnectStatus.ResponseType.OPERATOR_SELECTION, task,
                    LogUtils.maskUri(operatorSelectionUrl, LOGGER, Level.DEBUG));

                return MobileConnectStatus.operatorSelection(operatorSelectionUrl);
            }
            else
            {
                LOGGER.debug("Responding with responseType={} for {}",
                    MobileConnectStatus.ResponseType.START_AUTHENTICATION, task);

                return MobileConnectStatus.startAuthentication(response);
            }
        }
    }

    private static boolean isUsableDiscoveryResponse(final DiscoveryResponse response)
    {
        // if response is null or does not have operator urls
        // then it isn't usable for the process after discovery
        return response != null && response.getOperatorUrls() != null;
    }

    static MobileConnectStatus refreshToken(final IAuthenticationService authnService,
        final String refreshToken, final DiscoveryResponse discoveryResponse,
        final MobileConnectConfig config)
    {
        ObjectUtils.requireNonNull(discoveryResponse, DISCOVERY_RESPONSE);
        ObjectUtils.requireNonNull(refreshToken, "refreshToken");

        if (!isUsableDiscoveryResponse(discoveryResponse))
        {
            return MobileConnectStatus.startDiscovery();
        }

        final String refreshTokenUrl =
            ObjectUtils.defaultIfNull(discoveryResponse.getOperatorUrls().getRefreshTokenUrl(),
                discoveryResponse.getOperatorUrls().getRequestTokenUrl());

        final String clientId = ObjectUtils.defaultIfNull(
            discoveryResponse.getResponseData().getResponse().getClientId(), config.getClientId());

        final String clientSecret = ObjectUtils.defaultIfNull(
            discoveryResponse.getResponseData().getResponse().getClientSecret(),
            config.getClientSecret());

        try
        {
            final RequestTokenResponse requestTokenResponse =
                authnService.refreshToken(clientId, clientSecret, URI.create(refreshTokenUrl),
                    refreshToken);

            final ErrorResponse errorResponse = requestTokenResponse.getErrorResponse();
            if (errorResponse != null)
            {
                LOGGER.warn("Responding with responseType={} for refreshToken for "
                        + "authentication service responded with error={}",
                    MobileConnectStatus.ResponseType.ERROR, errorResponse);

                return MobileConnectStatus.error(errorResponse.getError(),
                    errorResponse.getErrorDescription(), null, requestTokenResponse);
            }
            else
            {
                LOGGER.info("Refresh token success");
                return MobileConnectStatus.complete(requestTokenResponse);
            }
        }
        catch (final Exception e)
        {
            LOGGER.warn("RefreshToken failed", e);
            return MobileConnectStatus.error("Refresh token error", e);
        }
    }

    static MobileConnectStatus revokeToken(final IAuthenticationService authnService,
        final String token, String tokenTypeHint, final DiscoveryResponse discoveryResponse,
        final MobileConnectConfig config)
    {
        try
        {
            ObjectUtils.requireNonNull(discoveryResponse, DISCOVERY_RESPONSE);
            ObjectUtils.requireNonNull(token, "token");

            final String revokeTokenUrl = discoveryResponse.getOperatorUrls().getRevokeTokenUrl();

            final String clientId = ObjectUtils.defaultIfNull(
                discoveryResponse.getResponseData().getResponse().getClientId(),
                config.getClientId());

            final String clientSecret = ObjectUtils.defaultIfNull(
                discoveryResponse.getResponseData().getResponse().getClientSecret(),
                config.getClientSecret());

            return MobileConnectStatus.complete(
                authnService.revokeToken(clientId, clientSecret, URI.create(revokeTokenUrl), token,
                    tokenTypeHint));
        }
        catch (final Exception e)
        {
            LOGGER.warn("RevokeToken failed", e);
            return MobileConnectStatus.error("Revoke token failed", e);
        }
    }
}
