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
package com.gsma.mobileconnect.r2.authentication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gsma.mobileconnect.r2.ErrorResponse;
import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.cache.ConcurrentCache;
import com.gsma.mobileconnect.r2.cache.DiscoveryCache;
import com.gsma.mobileconnect.r2.claims.Claims;
import com.gsma.mobileconnect.r2.claims.ClaimsValue;
import com.gsma.mobileconnect.r2.claims.KYCClaimsParameter;
import com.gsma.mobileconnect.r2.constants.DefaultOptions;
import com.gsma.mobileconnect.r2.constants.Parameters;
import com.gsma.mobileconnect.r2.constants.Scope;
import com.gsma.mobileconnect.r2.constants.Scopes;
import com.gsma.mobileconnect.r2.discovery.*;
import com.gsma.mobileconnect.r2.encoding.DefaultEncodeDecoder;
import com.gsma.mobileconnect.r2.encoding.IMobileConnectEncodeDecoder;
import com.gsma.mobileconnect.r2.exceptions.InvalidResponseException;
import com.gsma.mobileconnect.r2.exceptions.RequestFailedException;
import com.gsma.mobileconnect.r2.json.IJsonService;
import com.gsma.mobileconnect.r2.json.JsonDeserializationException;
import com.gsma.mobileconnect.r2.json.JsonSerializationException;
import com.gsma.mobileconnect.r2.rest.IRestClient;
import com.gsma.mobileconnect.r2.rest.RestAuthentication;
import com.gsma.mobileconnect.r2.rest.RestResponse;
import com.gsma.mobileconnect.r2.utils.*;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Concrete implementation of {@link IAuthenticationService}
 *
 * @since 2.0
 */
public class AuthenticationService implements IAuthenticationService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);
    public static final String REVOKE_TOKEN_SUCCESS = "Revoke token successful";
    static final String UNSUPPORTED_TOKEN_TYPE_ERROR = "Unsupported token type";
    private static IJsonService jsonService;
    private IDiscoveryService discoveryService;
    private ConcurrentCache discoveryCache;
    private IRestClient restClient;
    private final IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder;

    private AuthenticationService(final Builder builder)
    {
        this.jsonService = builder.jsonService;
        this.restClient = builder.restClient;
        this.iMobileConnectEncodeDecoder = builder.iMobileConnectEncodeDecoder;

        LOGGER.info("New instance of AuthenticationService created");
    }

    @Override
    public StartAuthenticationResponse startAuthentication(final String clientId, String correlationId,
                                                           final URI authorizeUrl, final URI redirectUrl, final String state, final String nonce,
                                                           final String encryptedMSISDN, final SupportedVersions versions,
                                                           final AuthenticationOptions options, final String currentVersion)
    {

        final String loginHint = extractLoginHint(options, encryptedMSISDN);

        if (options!=null && !options.getUsingCorrelationId()) {
            correlationId = "";
        }

        final AuthenticationOptions.Builder optionsBuilder =
                new AuthenticationOptions.Builder(options)
                        .withState(StringUtils.requireNonEmpty(state, "state"))
                        .withNonce(StringUtils.requireNonEmpty(nonce, "nonce"))
                        .withLoginHint(loginHint)
                        .withRedirectUrl(ObjectUtils.requireNonNull(redirectUrl, "redirectUrl"))
                        .withClientId(StringUtils.requireNonEmpty(clientId, "clientId"))
                        .withCorrelationId(correlationId);


        final String scope;
        final String context;
        if (options == null)
        {
            scope = Scopes.MOBILE_CONNECT;
            context = "";
        }
        else
        {
            scope = StringUtils.isNullOrEmpty(options.getScope()) ? Scopes.MOBILE_CONNECT : options.getScope();
            context = options.getContext();
        }

        final boolean useAuthorize = this.shouldUseAuthorize(scope, context);

        if (useAuthorize && new SupportedVersions.Builder(versions).build().getSupportedVersion(optionsBuilder.build()).equals(DefaultOptions.VERSION_MOBILECONNECTAUTHZ))
        {
            StringUtils.requireNonEmpty(options == null ? null : options.getContext(), "context");
            StringUtils.requireNonEmpty(options == null ? null : options.getClientName(),
                    "clientName");
        }


//        if (scope.contains(Scope.KYC_PLAIN) || scope.contains(Scope.KYC_HASHED)) {
//            KYCClaimsParameter kycClaims = options.getKycClaims();
            KYCClaimsParameter kycClaims =
                    new KYCClaimsParameter.Builder()
                            .withName(new Claims.Builder().add("name", new ClaimsValue.Builder().withValue("Name").build()).build())
                            .withAddress(new Claims.Builder().add("address", new ClaimsValue.Builder().withValue("Address").build()).build())
                            .build();
            System.out.println(kycClaims.getName());
            System.out.println(kycClaims.getAddress());
//        }

        try
        {
            final URI uri = new URIBuilder(ObjectUtils.requireNonNull(authorizeUrl, "authorizeUrl"))
                    .addParameters(
                            this.getAuthenticationQueryParams(optionsBuilder.build(), useAuthorize,
                                    currentVersion, loginHint))
                    .build();

            return new StartAuthenticationResponse(uri);
        }
        catch (final URISyntaxException use)
        {
            LOGGER.warn("Failed to construct uri for startAuthentication", use);
            throw new IllegalArgumentException("Failed to construct uri for startAuthentication",
                    use);
        }
    }

    private String extractLoginHint(final AuthenticationOptions options,
                                    final String encryptedMSISDN)
    {
        String loginHint = null;
        if (options != null)
        {
            if (options.getLoginHint() != null)
            {
                loginHint = options.getLoginHint();
            }
            else if (encryptedMSISDN != null)
            {
                loginHint = String.format("ENCR_MSISDN:%s", encryptedMSISDN);
            }
        }
        return loginHint;
    }

    private boolean shouldUseAuthorize(final String scope, final String context)
    {
        final int authnIndex = scope.indexOf(Scope.AUTHN.toLowerCase());
        final boolean authnRequested = authnIndex > -1;
        final boolean mcProductRequested = scope.toLowerCase().equals(Scope.AUTHZ.toLowerCase());

        return mcProductRequested || (!authnRequested && !StringUtils.isNullOrEmpty(context));
    }

    /**
     * Fetches the version required and modifies the scope based upon it.  mc_authn may be added or
     * removed from the scopes depending on the version required.
     *
     * @param scope          specified in the original request.
     * @param optionsBuilder to store the modified scopes to.
     * @param versions       specified in the original request.
     * @param useAuthorize   should mc_authz be used over mc_authn?
     * @return the version of the scope to use.  The modified scope will be stored to the
     * optionsBuilder.
     */
    private String coerceAuthenticationScope(final String scope,
                                             final AuthenticationOptions.Builder optionsBuilder, final SupportedVersions versions,
                                             final boolean useAuthorize)
    {
        final String requiredScope =
                useAuthorize ? Scopes.MOBILE_CONNECT_AUTHORIZATION : Scopes.MOBILE_CONNECT_AUTHENTICATION;
        final String disallowedScope = useAuthorize ? Scope.AUTHN : Scope.AUTHZ;

        final String version =
                new SupportedVersions.Builder(versions).build().getSupportedVersion(optionsBuilder.build());

        List<String> scopes = Scopes.coerceOpenIdScope(Arrays.asList(scope.split("\\s")), requiredScope);

        ListUtils.removeIgnoreCase(scopes, disallowedScope);

        if (!useAuthorize && DefaultOptions.VERSION_MOBILECONNECTAUTHN.equals(version))
        {
            ListUtils.removeIgnoreCase(scopes, Scope.AUTHN);
        }

        optionsBuilder.withScope(StringUtils.join(scopes, " "));

        return version;
    }

    private List<NameValuePair> getAuthenticationQueryParams(final AuthenticationOptions options,
                                                             final boolean useAuthorize, final String version, final String encryptedMSISDN)
    {
        String claimsJson = options.getClaimsJson();
        if (StringUtils.isNullOrEmpty(claimsJson) && options.getClaims() != null)
        {
            try
            {
                claimsJson = this.jsonService.serialize(options.getClaims());
            }
            catch (final JsonSerializationException jse)
            {
                LOGGER.warn(
                        "Failed to serialize claims into JSON for authentication query parameters",
                        jse);
                throw new IllegalArgumentException(
                        "Failed to serialize claims into JSON for authentication query parameters",
                        jse);
            }
        }

        final KeyValuePair.ListBuilder builder = new KeyValuePair.ListBuilder()
                .addIfNotEmpty(Parameters.AUTHENTICATION_REDIRECT_URI, options.getRedirectUrl().toString())
                .addIfNotEmpty(Parameters.CLIENT_ID, options.getClientId())
                .addIfNotEmpty(Parameters.CORRELATION_ID, options.getCorrelationId())
                .addIfNotEmpty(Parameters.RESPONSE_TYPE, DefaultOptions.AUTHENTICATION_RESPONSE_TYPE)
                .addIfNotEmpty(Parameters.SCOPE, options.getScope())
                .addIfNotEmpty(Parameters.ACR_VALUES, options.getAcrValues())
                .addIfNotEmpty(Parameters.STATE, options.getState())
                .addIfNotEmpty(Parameters.NONCE, options.getNonce())
                .addIfNotEmpty(Parameters.DISPLAY, options.getDisplay())
                .addIfNotEmpty(Parameters.PROMPT, options.getPrompt())
                .addIfNotEmpty(Parameters.MAX_AGE, String.valueOf(options.getMaxAge()))
                .addIfNotEmpty(Parameters.UI_LOCALES, options.getUiLocales())
                .addIfNotEmpty(Parameters.CLAIMS_LOCALES, options.getClaimsLocales())
                .addIfNotEmpty(Parameters.ID_TOKEN_HINT, options.getIdTokenHint())
                .addIfNotEmpty(Parameters.DTBS, options.getDbts())
                .addIfNotEmpty(Parameters.CLAIMS, claimsJson)
                .addIfNotEmpty(Parameters.VERSION, version);

        if (!StringUtils.isNullOrEmpty(options.getLoginHint()) && !StringUtils.isNullOrEmpty(options.getLoginHintToken()))
        {
            builder.addIfNotEmpty(Parameters.LOGIN_HINT_TOKEN, extractLoginHint(options, encryptedMSISDN));
        }
        else if (StringUtils.isNullOrEmpty(options.getLoginHint()) && !StringUtils.isNullOrEmpty(options.getLoginHintToken()))
        {
            builder.addIfNotEmpty(Parameters.LOGIN_HINT_TOKEN, options.getLoginHintToken());
        }
        else
        {
            builder.addIfNotEmpty(Parameters.LOGIN_HINT, extractLoginHint(options, encryptedMSISDN));
        }

        if (useAuthorize)
        {
            builder
                    .addIfNotEmpty(Parameters.CLIENT_NAME, options.getClientName())
                    .addIfNotEmpty(Parameters.CONTEXT, options.getContext())
                    .addIfNotEmpty(Parameters.BINDING_MESSAGE, options.getBindingMessage());
        }

        return builder.buildAsNameValuePairList();
    }

    @Override
    public Future<RequestTokenResponse> requestHeadlessAuthentication(final String clientId, final String clientSecret,
                                                                      final String correlationId, final URI authorizationUrl, final URI requestTokenUrl,
                                                                      final URI redirectUrl, final String state, final String nonce, final String encryptedMsisdn,
                                                                      final SupportedVersions versions, final AuthenticationOptions options, final String currentVersion)
            throws RequestFailedException
    {
        final String scope;
        final String context;
        final AuthenticationOptions.Builder optionsBuilder;
        if (options == null)
        {
            optionsBuilder = new AuthenticationOptions.Builder();
            scope = "";
            context = "";
        }
        else
        {
            optionsBuilder = new AuthenticationOptions.Builder(options);
            scope = ObjectUtils.defaultIfNull(options.getScope(), "").toLowerCase();
            context = options.getContext();
        }

        if (this.shouldUseAuthorize(scope, context))
        {
            optionsBuilder.withPrompt(DefaultOptions.PROMPT);
        }

        StartAuthenticationResponse startAuthenticationResponse =
                startAuthentication(clientId, correlationId, authorizationUrl, redirectUrl, state, nonce,
                        encryptedMsisdn, versions, optionsBuilder.build(), currentVersion);
        final RestAuthentication authentication =
                RestAuthentication.basic(clientId, clientSecret, iMobileConnectEncodeDecoder);

        URI authUrl = startAuthenticationResponse.getUrl();
        URI finalRedirectUrl = restClient.getFinalRedirect(authUrl, redirectUrl, authentication);

        final String code = HttpUtils.extractQueryValue(finalRedirectUrl, "code");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<RequestTokenResponse> requestTokenResponseFuture = executorService.submit(new Callable<RequestTokenResponse>()
        {
            @Override
            public RequestTokenResponse call() throws Exception
            {
                return AuthenticationService.this.requestToken(clientId, clientSecret, correlationId,
                        requestTokenUrl, redirectUrl, code);
            }
        });
        executorService.shutdownNow();
        return requestTokenResponseFuture;
    }

    @Override
    public RequestTokenResponse refreshToken(final String clientId, final String clientSecret,
                                             final URI refreshTokenUrl, final String refreshToken) throws RequestFailedException,
            InvalidResponseException
    {
        final List<KeyValuePair> formData = new KeyValuePair.ListBuilder()
                .add(Parameters.REFRESH_TOKEN,
                        StringUtils.requireNonEmpty(refreshToken, "refreshToken"))
                .add(Parameters.GRANT_TYPE, DefaultOptions.GRANT_TYPE_REFRESH_TOKEN)
                .build();

        final RestAuthentication authentication =
                RestAuthentication.basic(clientId, clientSecret, this.iMobileConnectEncodeDecoder);
        final RestResponse restResponse =
                this.restClient.postFormData(refreshTokenUrl, authentication,null, formData, null, null);

        return RequestTokenResponse.fromRestResponse(restResponse, this.jsonService,
                this.iMobileConnectEncodeDecoder);
    }

    @Override
    public String revokeToken(final String clientId, final String clientSecret,
                              final URI refreshTokenUrl, final String token, final String tokenTypeHint)
            throws RequestFailedException, InvalidResponseException, JsonDeserializationException
    {

        final KeyValuePair.ListBuilder formDataBuilder =
                new KeyValuePair.ListBuilder().add(Parameters.TOKEN,
                        StringUtils.requireNonEmpty(token, "token"));

        if (tokenTypeHint != null)
        {
            formDataBuilder.add(Parameters.TOKEN_TYPE_HINT, tokenTypeHint);
        }

        final List<KeyValuePair> formData = formDataBuilder.build();

        final RestAuthentication authentication =
                RestAuthentication.basic(clientId, clientSecret, this.iMobileConnectEncodeDecoder);
        final RestResponse restResponse =
                this.restClient.postFormData(refreshTokenUrl, authentication, null, formData, null, null);

        ErrorResponse errorResponse = null;
        if (HttpUtils.isHttpErrorCode(restResponse.getStatusCode()))
        {
            errorResponse =
                    this.jsonService.deserialize(restResponse.getContent(), ErrorResponse.class);
        }
        // As per the OAuth2 spec an error (non-200 response code) should only be returned by the
        // endpoint for the error code unsupported_token_type
        return (restResponse.getStatusCode() == 200 && errorResponse == null)
                ? REVOKE_TOKEN_SUCCESS
                : errorResponse != null
                ? errorResponse.getError()
                : UNSUPPORTED_TOKEN_TYPE_ERROR;
    }

    @Override
    public RequestTokenResponse requestToken(final String clientId, final String clientSecret, final String correlationId,
                                             final URI requestTokenUrl, final URI redirectUrl, final String code)
            throws RequestFailedException, InvalidResponseException
    {
        final List<KeyValuePair> formData = new KeyValuePair.ListBuilder()
                .add(Parameters.AUTHENTICATION_REDIRECT_URI,
                        ObjectUtils.requireNonNull(redirectUrl, "redirectUrl").toString())
                .add(Parameters.CODE, StringUtils.requireNonEmpty(code, "code"))
                .add(Parameters.GRANT_TYPE, DefaultOptions.GRANT_TYPE_AUTH_CODE)
                .addIfNotEmpty(Parameters.CORRELATION_ID, correlationId)
                .build();

        final RestAuthentication authentication =
                RestAuthentication.basic(clientId, clientSecret, this.iMobileConnectEncodeDecoder);
        final RestResponse restResponse =
                this.restClient.postFormData(requestTokenUrl, authentication, null, formData, null, null);

        return RequestTokenResponse.fromRestResponse(restResponse, this.jsonService,
                this.iMobileConnectEncodeDecoder);
    }

    @Override
    public Future<RequestTokenResponse> requestTokenAsync(final String clientId,
                                                          final String clientSecret, final String correlationId,
                                                          final URI requestTokenUrl, final URI redirectUrl,
                                                          final String code)
    {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<RequestTokenResponse> requestTokenResponseFuture = executorService.submit(new Callable<RequestTokenResponse>()
        {
            @Override
            public RequestTokenResponse call() throws Exception
            {
                return AuthenticationService.this.requestToken(clientId, clientSecret, correlationId,
                        requestTokenUrl, redirectUrl, code);
            }
        });
        executorService.shutdownNow();
        return requestTokenResponseFuture;
    }

    public static final class Builder implements IBuilder<AuthenticationService>
    {
        private IJsonService jsonService;
        private IRestClient restClient;
        private IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder;

        public Builder withJsonService(final IJsonService val)
        {
            this.jsonService = val;
            return this;
        }

        public Builder withRestClient(final IRestClient val)
        {
            this.restClient = val;
            return this;
        }

        public Builder withIMobileConnectEncodeDecoder(final IMobileConnectEncodeDecoder val)
        {
            this.iMobileConnectEncodeDecoder = val;
            return this;
        }

        @Override
        public AuthenticationService build()
        {
            ObjectUtils.requireNonNull(this.jsonService, "jsonService");
            ObjectUtils.requireNonNull(this.restClient, "restClient");
            if (this.iMobileConnectEncodeDecoder == null)
            {
                this.iMobileConnectEncodeDecoder = new DefaultEncodeDecoder();
            }

            return new AuthenticationService(this);
        }
    }

    @Override
    public DiscoveryResponse makeDiscoveryForAuthorization(final String clientSecret, final String clientKey,
                                                           final String subscriberId, final String name, OperatorUrls operatorUrls)
            throws JsonDeserializationException
    {
        ObjectUtils.requireNonNull(clientSecret, "clientSecret");
        ObjectUtils.requireNonNull(clientKey, "clientKey");
        ObjectUtils.requireNonNull(name, "appName");
        ObjectUtils.requireNonNull(operatorUrls, "operator urls");

        discoveryCache = new DiscoveryCache.Builder().withJsonService(jsonService).build();
        discoveryService = new DiscoveryService.Builder()
                .withJsonService(jsonService)
                .withCache(discoveryCache)
                .withRestClient(restClient)
                .build();
        ProviderMetadata providerMetadata = new ProviderMetadata.Builder().build();
        DiscoveryResponseGenerateOptions discoveryResponseGenerateOptions = new DiscoveryResponseGenerateOptions.BuilderResponse()
                .withClientKey(clientKey)
                .withSecretKey(clientSecret)
                .withName(name)
                .withSubscriberId(subscriberId)
                .withLinks(operatorUrls.getOperatorsUrls())
                .withRel(operatorUrls.getOperatorsRel()).build();

        MobileConnectRequestOptions mobileConnectRequestOptions = new MobileConnectRequestOptions.Builder()
                .withAuthOptionDiscoveryResponse(discoveryResponseGenerateOptions)
                .build();

        ObjectNode discoveryResponseWithoutRequest = mobileConnectRequestOptions.getDiscoveryResponseGenerateOptions().responseToJson();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode discoveryResponseJSONTree;
        JsonNode linkToProviderMetadata = null;

        try {
            discoveryResponseJSONTree = mapper.readTree(discoveryResponseWithoutRequest.toString());
            int openIdIndex = 0;

            int max = discoveryResponseJSONTree.path("response").path("apis").path("operatorid").path("link").size();
            for (int index = 0; index < max; index++) {
                JsonNode openIdLink = discoveryResponseJSONTree.path("response").path("apis").path("operatorid").path("link").get(index).findValue("rel");
                String providerMetadataText = openIdLink.textValue();
                if (providerMetadataText.contains("openid-configuration")) {
                    openIdIndex = index;
                    break;
                }
            }
            linkToProviderMetadata = discoveryResponseJSONTree.path("response").path("apis").path("operatorid").path("link").get(openIdIndex).findValue("href");

            if (!linkToProviderMetadata.isNull()) {
                providerMetadata = discoveryService.retrieveProviderMetadata(URI.create(linkToProviderMetadata.asText()), true);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        final RestResponse post_response = new RestResponse.Builder()
                .withStatusCode(HttpStatus.SC_OK)
                .withContent(discoveryResponseWithoutRequest.toString())
                .build();

        DiscoveryResponse discoveryResponse = DiscoveryResponse.fromRestResponse(post_response, this.jsonService);
        discoveryResponse.setProviderMetadata(providerMetadata);

        return discoveryResponse;
    }
}
