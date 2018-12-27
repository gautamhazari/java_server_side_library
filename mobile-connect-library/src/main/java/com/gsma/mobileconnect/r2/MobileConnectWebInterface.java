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
import com.gsma.mobileconnect.r2.cache.CacheAccessException;
import com.gsma.mobileconnect.r2.cache.ICache;
import com.gsma.mobileconnect.r2.discovery.*;
import com.gsma.mobileconnect.r2.encoding.DefaultEncodeDecoder;
import com.gsma.mobileconnect.r2.encoding.IMobileConnectEncodeDecoder;
import com.gsma.mobileconnect.r2.exceptions.InvalidScopeException;
import com.gsma.mobileconnect.r2.identity.IIdentityService;
import com.gsma.mobileconnect.r2.json.IJsonService;
import com.gsma.mobileconnect.r2.json.JsonDeserializationException;
import com.gsma.mobileconnect.r2.utils.*;
import com.gsma.mobileconnect.r2.validation.IJWKeysetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.UUID;

/**
 * Convenience wrapper for Mobile Connect interfaces for use with web applications.
 *
 * @see IDiscoveryService
 * @see IAuthenticationService
 * @see IIdentityService
 * @since 2.0
 */
public class MobileConnectWebInterface
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MobileConnectWebInterface.class);

    private static final String ARG_REQUEST = "request";

    private static final MobileConnectStatus CACHE_DISABLED_ERROR =
        MobileConnectStatus.error("cache_disabled",
            "cache is not enabled for session id caching of discovery response", null);

    private final IDiscoveryService discoveryService;
    private final IAuthenticationService authnService;
    private final IIdentityService identityService;
    private final IJWKeysetService jwKeysetService;
    private final IJsonService jsonService;
    private final MobileConnectConfig config;
    private final IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder;

    private MobileConnectWebInterface(Builder builder)
    {
        this.discoveryService = builder.discoveryService;
        this.authnService = builder.authnService;
        this.identityService = builder.identityService;
        this.jwKeysetService = builder.jwKeysetService;
        this.jsonService = builder.jsonService;
        this.config = builder.config;
        this.iMobileConnectEncodeDecoder = builder.iMobileConnectEncodeDecoder;

        LOGGER.info("Created new instance of MobileConnectWebInterface");
    }

    /**
     * Attempt discovery using the supplied parameters. If msisdn, mcc and mnc are null the result
     * will be operator selection, otherwise valid parameters will result in a StartAuthorization
     * status.
     *
     * @param request            Originating web request
     * @param msisdn             MSISDN from user
     * @param mcc                Mobile Country Code
     * @param mnc                Mobile Network Code
     * @param shouldProxyCookies If cookies from the original request should be sent onto the
     *                           discovery service
     * @param options            Optional parameters
     * @return MobileConnectStatus Object with required information for continuing the mobile
     * connect process
     */
    public MobileConnectStatus attemptDiscovery(final HttpServletRequest request,
        final String msisdn, final String mcc, final String mnc, final boolean shouldProxyCookies, final boolean includeRequestIP,
        final MobileConnectRequestOptions options)
    {
        LOGGER.info("**************************************************************\nmsisdn: " + msisdn + " \nmcc: " + mcc + " \nmnc: "+ mnc);
        ObjectUtils.requireNonNull(request, ARG_REQUEST);

        final String clientIp =
                includeRequestIP ? HttpUtils.extractClientIp(request) : null;

        final DiscoveryOptions.Builder builder =
            options == null ? new DiscoveryOptions.Builder() : options.getDiscoveryOptionsBuilder();

        final Iterable<KeyValuePair> cookies =
            shouldProxyCookies ? HttpUtils.extractCookiesFromRequest(request) : null;

        LOGGER.debug(
            "Running attemptDiscovery for msisdn={}, mcc={}, mnc={}, shouldProxyCookies={}, includeRequestIP={}, clientIp={}",
            LogUtils.mask(msisdn, LOGGER, Level.DEBUG), mcc, mnc, shouldProxyCookies, includeRequestIP, clientIp);

        final MobileConnectStatus status =
            MobileConnectInterfaceHelper.attemptDiscovery(this.discoveryService, msisdn, mcc, mnc,
                cookies, this.config, builder);

        return this.cacheIfRequired(status);
    }

    /**
     * Attempt manually discovery using the supplied parameters.
     * @param response The response returned by the generateDiscoveryManually
     * @return MobileConnectStatus Object with required information for continuing the mobile
     * connect process
     */
    public MobileConnectStatus attemptManuallyDiscovery(DiscoveryResponse response) {

        MobileConnectStatus status = MobileConnectInterfaceHelper.extractStatus(response, this.discoveryService, "discovery manually");

        return this.cacheIfRequired(status);
    }

    /**
     * Attempt discovery using the values returned from the operator selection redirect.
     *
     * @param request       Originating web request.
     * @param redirectedUrl Uri redirected to by the completion of the operator selection UI.
     * @return MobileConnectStatus Object with required information for continuing the mobile
     * connect process
     */
    public MobileConnectStatus attemptDiscoveryAfterOperatorSelection(
        final HttpServletRequest request, final URI redirectedUrl)
    {
        ObjectUtils.requireNonNull(request, ARG_REQUEST);

        LOGGER.debug(
            "Running attemptDiscoveryAfterOperatorSelection for redirectedUrl={}, clientIp={}",
            LogUtils.maskUri(redirectedUrl, LOGGER, Level.DEBUG),
            HttpUtils.extractClientIp(request));

        final MobileConnectStatus status =
            MobileConnectInterfaceHelper.attemptDiscoveryAfterOperatorSelection(
                this.discoveryService, redirectedUrl, this.config);

        return this.cacheIfRequired(status);
    }

    /**
     * Creates an authorization url with parameters to begin the authentication process.
     *
     * @param request           Originating web request
     * @param discoveryResponse The response returned by the discovery process
     * @param encryptedMsisdn   Encrypted MSISDN/Subscriber Id returned from the Discovery process
     * @param state             Unique string to be used to prevent Cross Site Forgery Request
     *                          attacks during request token process (defaults to guid if not
     *                          supplied, value will be returned in MobileConnectStatus object)
     * @param nonce             Unique string to be used to prevent replay attacks during request
     *                          token process (defaults to guid if not supplied, value will be
     *                          returned in MobileConnectStatus object)
     * @param options           Optional parameters
     * @return MobileConnectStatus Object with required information for continuing the mobile
     * connect process
     */
    public MobileConnectStatus startAuthentication(final HttpServletRequest request,
        final DiscoveryResponse discoveryResponse, final String encryptedMsisdn, final String state,
        final String nonce, final MobileConnectRequestOptions options, final String currentVersion)
    {
        ObjectUtils.requireNonNull(request, ARG_REQUEST);

        final AuthenticationOptions.Builder builder = options != null
                                                      ? options.getAuthenticationOptionsBuilder()
                                                      : new AuthenticationOptions.Builder();

        final String rState =
            StringUtils.isNullOrEmpty(state) ? UUID.randomUUID().toString() : state;
        final String rNonce =
            StringUtils.isNullOrEmpty(nonce) ? UUID.randomUUID().toString() : nonce;

        LOGGER.debug(
            "Running startAuthentication for encryptedMsisdn={}, state={}, nonce={}, clientIp={}",
            LogUtils.mask(encryptedMsisdn, LOGGER, Level.DEBUG), rState,
            LogUtils.mask(rNonce, LOGGER, Level.DEBUG), HttpUtils.extractClientIp(request));

        try {
            return MobileConnectInterfaceHelper.startAuthentication(this.authnService,
                discoveryResponse, encryptedMsisdn, rState, rNonce, this.config, builder,
                    VersionDetection.getCurrentVersion(currentVersion, getScopeFromOptions(options),
                            discoveryResponse.getProviderMetadata()));
        } catch (InvalidScopeException e) {
            return e.toMobileConnectStatus(currentVersion);
        }
    }

    /**
     * Creates an authorization url with parameters to begin the authentication process.
     *
     * @param request         Originating web request
     * @param sdkSession      SDKSession id used to fetch the discovery response with additional
     *                        parameters that are required to request a token
     * @param encryptedMsisdn Encrypted MSISDN/Subscriber Id returned from the Discovery process
     * @param state           Unique string to be used to prevent Cross Site Forgery Request attacks
     *                        during request token process (defaults to guid if not supplied, value
     *                        will be returned in MobileConnectStatus object)
     * @param nonce           Unique string to be used to prevent replay attacks during request
     *                        token process (defaults to guid if not supplied, value will be
     *                        returned in MobileConnectStatus object)
     * @param options         Optional parameters
     * @return MobileConnectStatus Object with required information for continuing the mobile
     * connect process
     */
    public MobileConnectStatus startAuthentication(final HttpServletRequest request,
        final String sdkSession, final String encryptedMsisdn, final String state,
        final String nonce, final MobileConnectRequestOptions options, final String currentVersion)
    {
        ObjectUtils.requireNonNull(request, ARG_REQUEST);

        LOGGER.debug(
            "Running startAuthentication for skdSession={}, encryptedMsisdn={}, state={}, nonce={}, clientIp={}",
            sdkSession, LogUtils.mask(encryptedMsisdn, LOGGER, Level.DEBUG), state,
            LogUtils.mask(nonce, LOGGER, Level.DEBUG), HttpUtils.extractClientIp(request));

        return this.withCachedValue(sdkSession, false, new CacheCallback()
        {
            @Override
            public MobileConnectStatus apply(final DiscoveryResponse cached)
            {
                try {
                    return MobileConnectWebInterface.this.startAuthentication(request, cached, encryptedMsisdn, state,
                            nonce, options, VersionDetection.getCurrentVersion(currentVersion,
                                    getScopeFromOptions(options), cached.getProviderMetadata()));
                } catch (InvalidScopeException e) {
                    return e.toMobileConnectStatus(currentVersion);
                }
            }
        });
    }

    /**
     * Performs headless authentication followed by request token if successful.
     * Tokens will be validated before being returned. This may be a long running method
     * as it waits for the authenticating user to respond using their authenticating device.
     *
     * @param request           Originating web request
     * @param discoveryResponse The response returned by the discovery process
     * @param encryptedMsisdn   Encrypted MSISDN/Subscriber Id returned from the Discovery process
     * @param state             Unique string to be used to prevent Cross Site Forgery Request
     *                          attacks during request token process (defaults to guid if not
     *                          supplied, value will be returned in MobileConnectStatus object)
     * @param nonce             Unique string to be used to prevent replay attacks during request
     *                          token process (defaults to guid if not supplied, value will be
     *                          returned in MobileConnectStatus object)
     * @param options           Optional parameters
     * @return MobileConnectStatus Object with required information for continuing the mobile
     * connect process
     */
    public MobileConnectStatus requestHeadlessAuthentication(final HttpServletRequest request,
        final DiscoveryResponse discoveryResponse, final String encryptedMsisdn, final String state,
        final String nonce, final MobileConnectRequestOptions options, final String currentVersion)
    {
        ObjectUtils.requireNonNull(request, ARG_REQUEST);

        final String rState =
            StringUtils.isNullOrEmpty(state) ? UUID.randomUUID().toString() : state;
        final String rNonce =
            StringUtils.isNullOrEmpty(nonce) ? UUID.randomUUID().toString() : nonce;

        LOGGER.debug(
            "Running requestHeadlessAuthentication for encryptedMsisdn={}, state={}, nonce={}, clientIp={}",
            LogUtils.mask(encryptedMsisdn, LOGGER, Level.DEBUG), rState,
            LogUtils.mask(rNonce, LOGGER, Level.DEBUG), HttpUtils.extractClientIp(request));

        try {
            return MobileConnectInterfaceHelper.requestHeadlessAuthentication(this.authnService,
                this.identityService, discoveryResponse, encryptedMsisdn, rState, rNonce, this.config,
                options, iMobileConnectEncodeDecoder, this.jwKeysetService, this.jsonService,
                    VersionDetection.getCurrentVersion(currentVersion, getScopeFromOptions(options),
                            discoveryResponse.getProviderMetadata()));
        } catch (InvalidScopeException e) {
            return e.toMobileConnectStatus(currentVersion);
        }
    }

    private static String getScopeFromOptions(MobileConnectRequestOptions options) {
        return options != null && options.getAuthenticationOptions() != null
                ? options.getAuthenticationOptions().getScope() : null;
    }

    /**
     * Performs headless authentication followed by request token if successful.
     * Tokens will be validated before being returned. This may be a long running method
     * as it waits for the authenticating user to respond using their authenticating device.
     *
     * @param request         Originating web request
     * @param sdkSession      SDKSession id used to fetch the discovery response with additional
     *                        parameters that are required to request a token
     * @param encryptedMsisdn Encrypted MSISDN/Subscriber Id returned from the Discovery process
     * @param state           Unique string to be used to prevent Cross Site Forgery Request attacks
     *                        during request token process (defaults to guid if not supplied, value
     *                        will be returned in MobileConnectStatus object)
     * @param nonce           Unique string to be used to prevent replay attacks during request
     *                        token process (defaults to guid if not supplied, value will be
     *                        returned in MobileConnectStatus object)
     * @param options         Optional parameters
     * @return MobileConnectStatus Object with required information for continuing the mobile
     * connect process
     */
    public MobileConnectStatus requestHeadlessAuthentication(final HttpServletRequest request,
        final String sdkSession, final String encryptedMsisdn, final String state,
        final String nonce, final MobileConnectRequestOptions options, final String currentVersion)
    {
        ObjectUtils.requireNonNull(request, ARG_REQUEST);

        LOGGER.debug(
            "Running requestHeadlessAuthentication for skdSession={}, encryptedMsisdn={}, state={}, nonce={}, clientIp={}",
            sdkSession, LogUtils.mask(encryptedMsisdn, LOGGER, Level.DEBUG), state,
            LogUtils.mask(nonce, LOGGER, Level.DEBUG), HttpUtils.extractClientIp(request));

        return this.withCachedValue(sdkSession, true, new CacheCallback()
        {
            @Override
            public MobileConnectStatus apply(final DiscoveryResponse cached)
            {
                try {
                    return MobileConnectWebInterface.this.requestHeadlessAuthentication(request, cached,
                        encryptedMsisdn, state, nonce, options,
                            VersionDetection.getCurrentVersion(currentVersion, getScopeFromOptions(options),
                                    cached.getProviderMetadata()));
                } catch (InvalidScopeException e) {
                    return e.toMobileConnectStatus(currentVersion);
                }
            }
        });
    }

    /**
     * Request token using the values returned from the authorization redirect.
     *
     * @param request           Originating web request
     * @param discoveryResponse The response returned by the discovery process
     * @param redirectedUrl     Uri redirected to by the completion of the authorization UI
     * @param expectedState     The state value returned from the StartAuthorization call should be
     *                          passed here, it will be used to validate the authenticity of the
     *                          authorization process
     * @param expectedNonce     The nonce value returned from the StartAuthorization call should be
     *                          passed here, it will be used to ensure the token was not requested
     *                          using a replay attack
     * @param options           Optional parameters
     * @return MobileConnectStatus Object with required information for continuing the mobile
     * connect process
     */
    public MobileConnectStatus requestToken(final HttpServletRequest request,
        final DiscoveryResponse discoveryResponse, final URI redirectedUrl,
        final String expectedState, final String expectedNonce,
        final MobileConnectRequestOptions options, final String currentVersion)
    {
        ObjectUtils.requireNonNull(request, ARG_REQUEST);

        LOGGER.debug(
            "Running requestToken for redirectedUrl={}, expectedState={}, expectedNonce={}, clientIp={}",
            LogUtils.maskUri(redirectedUrl, LOGGER, Level.DEBUG), expectedState,
            LogUtils.mask(expectedNonce, LOGGER, Level.DEBUG), HttpUtils.extractClientIp(request));

        try {
            return MobileConnectInterfaceHelper.requestToken(this.authnService, this.jwKeysetService,
                discoveryResponse, redirectedUrl, expectedState, expectedNonce, this.config, options,
                this.jsonService, this.iMobileConnectEncodeDecoder,
                    VersionDetection.getCurrentVersion(currentVersion, getScopeFromOptions(options),
                            discoveryResponse.getProviderMetadata()));
        } catch (InvalidScopeException e) {
            return e.toMobileConnectStatus(currentVersion);
        }
    }

    /**
     * Request token using the values returned from the authorization redirect.
     *
     * @param request       Originating web request
     * @param sdkSession    SDKSession id used to fetch the discovery response with additional
     *                      parameters that are required to request a token
     * @param redirectedUrl Uri redirected to by the completion of the authorization UI
     * @param expectedState The state value returned from the StartAuthorization call should be
     *                      passed here, it will be used to validate the authenticity of the
     *                      authorization process
     * @param expectedNonce The nonce value returned from the StartAuthorization call should be
     *                      passed here, it will be used to ensure the token was not requested using
     *                      a replay attack
     * @param options       Optional parameters
     * @return MobileConnectStatus Object with required information for continuing the mobile
     * connect process
     */
    public MobileConnectStatus requestToken(final HttpServletRequest request,
        final String sdkSession, final URI redirectedUrl, final String expectedState,
        final String expectedNonce, final MobileConnectRequestOptions options, final String currentVersion)
    {
        ObjectUtils.requireNonNull(request, ARG_REQUEST);

        LOGGER.debug(
            "Running requestToken for sdkSession={}, redirectedUrl={}, expectedState={}, expectedNonce={}, clientIp={}",
            sdkSession, LogUtils.maskUri(redirectedUrl, LOGGER, Level.DEBUG), expectedState,
            LogUtils.mask(expectedNonce, LOGGER, Level.DEBUG), HttpUtils.extractClientIp(request));

        ICache cache = discoveryService.getCache();

        return this.withCachedValue(sdkSession, true, new CacheCallback()
        {
            @Override
            public MobileConnectStatus apply(final DiscoveryResponse cached)
            {
                try {
                    return MobileConnectWebInterface.this.requestToken(request, cached, redirectedUrl,
                        expectedState, expectedNonce, options,
                            VersionDetection.getCurrentVersion(currentVersion, getScopeFromOptions(options),
                                    cached.getProviderMetadata()));
                } catch (InvalidScopeException e) {
                    return e.toMobileConnectStatus(currentVersion);
                }
            }
        });
    }

    /**
     * Refresh token using using the refresh token provided in the RequestToken response
     *
     * @param request           Originating web request
     * @param refreshToken      Refresh token returned from RequestToken request
     * @param discoveryResponse The response returned by the discovery process

     * @return MobileConnectStatus Object with required information for continuing the mobile
     * connect process
     */
    public MobileConnectStatus refreshToken(final HttpServletRequest request,
        final String refreshToken, final DiscoveryResponse discoveryResponse)
    {
        ObjectUtils.requireNonNull(request, ARG_REQUEST);

        LOGGER.debug("Running refreshToken for clientIp={}",
            HttpUtils.extractClientIp(request));

        return MobileConnectInterfaceHelper.refreshToken(this.authnService, refreshToken,
            discoveryResponse, this.config);
    }

    /**
     * Refresh token using using the refresh token provided in the RequestToken response
     *
     * @param request       Originating web request
     * @param refreshToken  Refresh token returned from RequestToken request
     * @param sdkSession    SDKSession id used to fetch the discovery response with additional
     *                      parameters that are required to request a token
     * @return MobileConnectStatus Object with required information for continuing the mobile
     * connect process
     */
    public MobileConnectStatus refreshToken(final HttpServletRequest request,
        final String refreshToken, final String sdkSession)
    {
        ObjectUtils.requireNonNull(request, ARG_REQUEST);

        LOGGER.debug("Running refreshToken for sdkSession={}, clientIp={}",
            sdkSession, HttpUtils.extractClientIp(request));

        return this.withCachedValue(sdkSession, true, new CacheCallback()
        {
            @Override
            public MobileConnectStatus apply(final DiscoveryResponse cached)
            {
                return MobileConnectWebInterface.this.refreshToken(request, refreshToken, cached);
            }
        });
    }

    /**
     * Revoke token using using the access / refresh token provided in the RequestToken response
     *
     * @param request           Originating web request
     * @param token             Access/Refresh token returned from RequestToken request
     * @param tokenTypeHint     Hint to indicate the type of token being passed in
     * @param discoveryResponse The response returned by the discovery process
     * @return MobileConnectStatus Object with required information for continuing the mobile
     * connect process
     */
    public MobileConnectStatus revokeToken(final HttpServletRequest request,
        final String token, final String tokenTypeHint,
        final DiscoveryResponse discoveryResponse)
    {
        ObjectUtils.requireNonNull(request, ARG_REQUEST);

        LOGGER.debug("Running revokeToken for clientIp={}",
            HttpUtils.extractClientIp(request));

        return MobileConnectInterfaceHelper.revokeToken(this.authnService, token, tokenTypeHint,
            discoveryResponse, this.config);
    }

    /**
     * Revoke token using using the access / refresh token provided in the RequestToken response
     *
     * @param request       Originating web request
     * @param token         Refresh token returned from RequestToken request
     * @param tokenTypeHint Hint to indicate the type of token being passed in
     * @param sdkSession    SDKSession id used to fetch the discovery response with additional
     *                      parameters that are required to request a token
     * @return MobileConnectStatus Object with required information for continuing the mobile
     * connect process
     */
    public MobileConnectStatus revokeToken(final HttpServletRequest request,
        final String token, final String tokenTypeHint, final String sdkSession)
    {
        ObjectUtils.requireNonNull(request, ARG_REQUEST);

        LOGGER.debug("Running revokeToken for sdkSession={}, clientIp={}",
            sdkSession, HttpUtils.extractClientIp(request));

        return this.withCachedValue(sdkSession, true, new CacheCallback()
        {
            @Override
            public MobileConnectStatus apply(final DiscoveryResponse cached)
            {
                return MobileConnectWebInterface.this.revokeToken(request, token, tokenTypeHint,
                    cached);
            }
        });
    }

    /**
     * Handles continuation of the process following a completed redirect, the request token url
     * must be provided if it has been returned by the discovery process. Only the request and
     * redirectedUrl are required, however if the redirect being handled is the result of calling
     * the Authorization URL then the remaining parameters are required.
     *
     * @param request           Originating web request (required)
     * @param redirectedUrl     Url redirected to by the completion of the previous step (required)
     * @param discoveryResponse The response returned by the discovery process
     * @param expectedState     The state value returned from the StartAuthorization call should be
     *                          passed here, it will be used to validate the authenticity of the
     *                          authorization process
     * @param expectedNonce     The nonce value returned from the StartAuthorization call should be
     *                          passed here, it will be used to ensure the token was not requested
     *                          using a replay attack
     * @param options           Optional parameters
     * @return MobileConnectStatus Object with required information for continuing the mobile
     * connect process
     */
    public MobileConnectStatus handleUrlRedirect(final HttpServletRequest request,
        final URI redirectedUrl, final DiscoveryResponse discoveryResponse,
        final String expectedState, final String expectedNonce,
        final MobileConnectRequestOptions options, final String currentVersion)
    {
        ObjectUtils.requireNonNull(request, ARG_REQUEST);

        LOGGER.debug(
            "Running handleUrlRedirect for redirectedUrl={}, expectedState={}, expectedNonce={}, clientIp={}",
            LogUtils.maskUri(redirectedUrl, LOGGER, Level.DEBUG), expectedState,
            LogUtils.mask(expectedNonce, LOGGER, Level.DEBUG), HttpUtils.extractClientIp(request));

        final MobileConnectStatus status;
        try {
            status = MobileConnectInterfaceHelper.handleUrlRedirect(this.discoveryService,
                this.jwKeysetService, this.authnService, redirectedUrl, discoveryResponse,
                expectedState, expectedNonce, this.config, options, this.jsonService,
                this.iMobileConnectEncodeDecoder,
                    VersionDetection.getCurrentVersion(currentVersion, getScopeFromOptions(options),
                            discoveryResponse.getProviderMetadata()));
        } catch (InvalidScopeException e) {
            return e.toMobileConnectStatus(currentVersion);
        }

        return this.cacheIfRequired(status);
    }

    /**
     * Handles continuation of the process following a completed redirect, the request token url
     * must be provided if it has been returned by the discovery process. Only the request and
     * redirectedUrl are required, however if the redirect being handled is the result of calling
     * the Authorization URL then the remaining parameters are required.
     *
     * @param request       Originating web request (required)
     * @param redirectedUrl Url redirected to by the completion of the previous step (required)
     * @param sdkSession    SDKSession id used to fetch the discovery response with additional
     *                      parameters that are required to request a token
     * @param expectedState The state value returned from the StartAuthorization call should be
     *                      passed here, it will be used to validate the authenticity of the
     *                      authorization process
     * @param expectedNonce The nonce value returned from the StartAuthorization call should be
     *                      passed here, it will be used to ensure the token was not requested using
     *                      a replay attack
     * @param options       Optional parameters
     * @return MobileConnectStatus Object with required information for continuing the mobile
     * connect process
     */
    public MobileConnectStatus handleUrlRedirect(final HttpServletRequest request,
        final URI redirectedUrl, final String sdkSession, final String expectedState,
        final String expectedNonce, final MobileConnectRequestOptions options, final String currentVersion)
    {
        ObjectUtils.requireNonNull(request, ARG_REQUEST);

        LOGGER.debug(
            "Running handleUrlRedirect for sdkSession={}, redirectedUrl={}, expectedState={}, expectedNonce={}, clientIp={}",
            sdkSession, LogUtils.maskUri(redirectedUrl, LOGGER, Level.DEBUG), expectedState,
            LogUtils.mask(expectedNonce, LOGGER, Level.DEBUG), HttpUtils.extractClientIp(request));

        return this.withCachedValue(sdkSession, false, new CacheCallback()
        {
            @Override public MobileConnectStatus apply(final DiscoveryResponse cached)
            {
                try {
                    return MobileConnectWebInterface.this.cacheIfRequired(
                            MobileConnectInterfaceHelper.handleUrlRedirect(
                                    MobileConnectWebInterface.this.discoveryService,
                                    MobileConnectWebInterface.this.jwKeysetService,
                                    MobileConnectWebInterface.this.authnService, redirectedUrl, cached,
                                    expectedState, expectedNonce, MobileConnectWebInterface.this.config,
                                    options, MobileConnectWebInterface.this.jsonService,
                                    MobileConnectWebInterface.this.iMobileConnectEncodeDecoder,
                                    VersionDetection.getCurrentVersion(currentVersion, getScopeFromOptions(options),
                                            cached.getProviderMetadata())));
                } catch (InvalidScopeException e) {
                    return e.toMobileConnectStatus(currentVersion);
                }
            }
        });
    }

    private boolean validateParameters(final String sdkSession, final String expectedState,
        final String expectedNonce)
    {
        return !StringUtils.isNullOrEmpty(expectedNonce) || !StringUtils.isNullOrEmpty(
            expectedState) || !StringUtils.isNullOrEmpty(sdkSession);
    }

    /**
     * Request user info using the access token returned by {@link MobileConnectInterface#requestTokenAsync(
     * DiscoveryResponse, URI, String, String, MobileConnectRequestOptions, String)}
     *
     * @param request           Originating web request
     * @param discoveryResponse The response returned by the discovery process
     * @param accessToken       Access token returned from RequestToken required to authenticate the
     *                          request
     * @return MobileConnectStatus object with requested UserInfo information
     */
    public MobileConnectStatus requestUserInfo(final HttpServletRequest request,
        final DiscoveryResponse discoveryResponse, final String accessToken)
    {
        ObjectUtils.requireNonNull(request, ARG_REQUEST);

        LOGGER.debug("Running requestUserInfo for accessToken={}, clientIp={}",
            LogUtils.mask(accessToken, LOGGER, Level.DEBUG), HttpUtils.extractClientIp(request));

        return MobileConnectInterfaceHelper.requestUserInfo(this.identityService, discoveryResponse,
            accessToken, iMobileConnectEncodeDecoder);
    }

    /**
     * Request user info using the access token returned by {@link MobileConnectInterface#requestTokenAsync(
     * DiscoveryResponse, URI, String, String, MobileConnectRequestOptions, String)}
     *
     * @param request     Originating web request
     * @param sdkSession  SDKSession id used to fetch the discovery response with additional
     *                    parameters that are required to request a user info
     * @param accessToken Access token returned from RequestToken required to authenticate the
     *                    request
     * @return MobileConnectStatus object with requested UserInfo information
     */
    public MobileConnectStatus requestUserInfo(final HttpServletRequest request,
        final String sdkSession, final String accessToken)
    {
        ObjectUtils.requireNonNull(request, ARG_REQUEST);

        LOGGER.debug("Running requestUserInfo for sdkSession={}, accessToken={}, clientIp={}",
            sdkSession, LogUtils.mask(accessToken, LOGGER, Level.DEBUG),
            HttpUtils.extractClientIp(request));

        return this.withCachedValue(sdkSession, true, new CacheCallback()
        {
            @Override
            public MobileConnectStatus apply(final DiscoveryResponse cached)
            {
                return MobileConnectWebInterface.this.requestUserInfo(request, cached, accessToken);
            }
        });
    }

    /**
     * Request identity using the access token returned by {@link #requestToken(
     *HttpServletRequest, DiscoveryResponse, URI, String, String, MobileConnectRequestOptions, String)}
     *
     * @param request           Originating web request
     * @param discoveryResponse SDKSession id used to fetch the discovery response with additional
     *                          parameters that are required to request a identity info
     * @param accessToken       Access token returned from RequestToken required to authenticate the
     *                          request
     * @return MobileConnectStatus object with requested identity information
     */
    public MobileConnectStatus requestIdentity(final HttpServletRequest request,
        final DiscoveryResponse discoveryResponse, final String accessToken)
    {
        ObjectUtils.requireNonNull(request, ARG_REQUEST);

        LOGGER.debug("Running requestIdentity for accessToken={}, clientIp={}",
            LogUtils.mask(accessToken, LOGGER, Level.DEBUG), HttpUtils.extractClientIp(request));

        return MobileConnectInterfaceHelper.requestIdentity(this.identityService, discoveryResponse,
            accessToken, iMobileConnectEncodeDecoder);
    }

    /**
     * Request identity using the access token returned by {@link #requestToken(
     *HttpServletRequest, DiscoveryResponse, URI, String, String, MobileConnectRequestOptions, String)}
     *
     * @param request     Originating web request
     * @param sdkSession  SDKSession id used to fetch the discovery response with additional
     *                    parameters that are required to request identity info
     * @param accessToken Access token returned from RequestToken required to authenticate the
     *                    request
     * @return MobileConnectStatus object with requested identity information
     */
    public MobileConnectStatus requestIdentity(final HttpServletRequest request,
        final String sdkSession, final String accessToken)
    {
        ObjectUtils.requireNonNull(request, ARG_REQUEST);

        LOGGER.debug("Running requestIdentity for sdkSession={}, accessToken={}, clientIp={}",
            sdkSession, LogUtils.mask(accessToken, LOGGER, Level.DEBUG),
            HttpUtils.extractClientIp(request));

        return this.withCachedValue(sdkSession, true, new CacheCallback()
        {
            @Override
            public MobileConnectStatus apply(final DiscoveryResponse cached)
            {
                return MobileConnectWebInterface.this.requestIdentity(request, cached, accessToken);
            }
        });
    }

    private MobileConnectStatus cacheIfRequired(final MobileConnectStatus status)
    {
        if (this.config.isCacheResponsesWithSessionId()
            && status.getResponseType() == MobileConnectStatus.ResponseType.START_AUTHENTICATION
            && status.getDiscoveryResponse() != null)
        {
            final String sessionId = UUID.randomUUID().toString();
            try
            {
                LOGGER.debug("Storing discovery response with sdkSession={}", sessionId);
                this.discoveryService.getCache().add(sessionId, status.getDiscoveryResponse());
                return status.withSdkSession(sessionId);
            }
            catch (final CacheAccessException cae)
            {
                LOGGER.warn("Failed to store discovery response in cache sdkSession={}", sessionId,
                    cae);
            }
        }
        return status;
    }

    private MobileConnectStatus withCachedValue(final String sdkSession, final boolean required,
        final CacheCallback callback)
    {
        if (!this.config.isCacheResponsesWithSessionId())
        {
            LOGGER.warn("Received invalid request for sdkSession={} when cache is disabled");
            return CACHE_DISABLED_ERROR;
        }
        else
        {
            try
            {
                final DiscoveryResponse response =
                    this.discoveryService.getCache().get(sdkSession, DiscoveryResponse.class);
                if (response == null && required)
                {
                    LOGGER.info("Failed to find cached session sdkSession={}", sdkSession);
                    return this.cacheError(null);
                }
                else
                {
                    return callback.apply(response);
                }
            }
            catch (final CacheAccessException cae)
            {
                LOGGER.warn("Failed to fetch discovery response from cache sdkSession={}",
                    sdkSession, cae);
                return this.cacheError(cae);
            }
        }
    }

    private MobileConnectStatus cacheError(final Exception e)
    {
        return MobileConnectStatus.error("sdksession_not_found", "session not found or expired", e);
    }

    private interface CacheCallback
    {
        MobileConnectStatus apply(final DiscoveryResponse cached);
    }


    public static final class Builder implements IBuilder<MobileConnectWebInterface>
    {
        private IDiscoveryService discoveryService;
        private IAuthenticationService authnService;
        private IIdentityService identityService;
        private IJWKeysetService jwKeysetService;
        private IJsonService jsonService;
        private MobileConnectConfig config;
        private IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder =
            new DefaultEncodeDecoder();

        public Builder withAuthnService(final IAuthenticationService val)
        {
            this.authnService = val;
            return this;
        }

        public Builder withDiscoveryService(final IDiscoveryService val)
        {
            this.discoveryService = val;
            return this;
        }

        public Builder withIdentityService(final IIdentityService val)
        {
            this.identityService = val;
            return this;
        }

        public Builder withJsonService(final IJsonService val)
        {
            this.jsonService = val;
            return this;
        }

        public Builder withJwKeysetService(final IJWKeysetService val)
        {
            this.jwKeysetService = val;
            return this;
        }

        public Builder withIMobileConnectEncodeDecoder(final IMobileConnectEncodeDecoder val)
        {
            this.iMobileConnectEncodeDecoder = val;
            return this;
        }

        public Builder withConfig(final MobileConnectConfig val)
        {
            this.config = val;
            return this;
        }

        @Override
        public MobileConnectWebInterface build()
        {
            ObjectUtils.requireNonNull(this.discoveryService, "discoveryService");
            ObjectUtils.requireNonNull(this.authnService, "authnService");
            ObjectUtils.requireNonNull(this.identityService, "identityService");
            ObjectUtils.requireNonNull(this.config, "config");

            return new MobileConnectWebInterface(this);
        }
    }

    /** Allows an application to create discovery object manually without call to discovery service
     *
     * @param clientSecret The registered application secretKey (Required)
     * @param clientKey The registered application clientKey (consumer key) (Required)
     * @param name application name (Required)
     * @param operatorUrls operator specific urls returned from a successful discovery process call
     * @throws JsonDeserializationException on failure to process response from DiscoveryResponse, ProviderMetadata
     */
    public DiscoveryResponse generateDiscoveryManually(String clientSecret, String clientKey, String name,
                                                       OperatorUrls operatorUrls) throws JsonDeserializationException {
        return authnService.makeDiscoveryForAuthorization(clientSecret, clientKey, name, operatorUrls);
    }
}
