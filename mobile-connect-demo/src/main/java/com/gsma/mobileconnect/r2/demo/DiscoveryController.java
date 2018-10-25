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
package com.gsma.mobileconnect.r2.demo;

import com.gsma.mobileconnect.r2.MobileConnect;
import com.gsma.mobileconnect.r2.MobileConnectConfig;
import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.authentication.AuthenticationOptions;
import com.gsma.mobileconnect.r2.cache.CacheAccessException;
import com.gsma.mobileconnect.r2.cache.DiscoveryCache;
import com.gsma.mobileconnect.r2.cache.SessionCache;
import com.gsma.mobileconnect.r2.constants.*;
import com.gsma.mobileconnect.r2.demo.utils.Constants;
import com.gsma.mobileconnect.r2.demo.utils.ReadAndParseFiles;
import com.gsma.mobileconnect.r2.discovery.DiscoveryOptions;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.discovery.OperatorUrls;
import com.gsma.mobileconnect.r2.discovery.SessionData;
import com.gsma.mobileconnect.r2.encoding.DefaultEncodeDecoder;
import com.gsma.mobileconnect.r2.json.JsonDeserializationException;
import com.gsma.mobileconnect.r2.utils.HttpUtils;
import com.gsma.mobileconnect.r2.utils.LogUtils;
import com.gsma.mobileconnect.r2.utils.ObjectUtils;
import com.gsma.mobileconnect.r2.utils.StringUtils;
import com.gsma.mobileconnect.r2.web.MobileConnectWebResponse;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @since 2.0
 */
@Controller
@EnableAutoConfiguration
@RequestMapping(path = "server_side_api"/*, produces = MediaType.APPLICATION_JSON_UTF8_VALUE*/)
public class DiscoveryController extends com.gsma.mobileconnect.r2.demo.Controller {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoveryController.class);

    public DiscoveryController() {
        this.getParameters();
        if (this.mobileConnectWebInterface == null) {
            this.mobileConnectWebInterface = MobileConnect.buildWebInterface(mobileConnectConfig, new DefaultEncodeDecoder(), this.sessionCache, this.discoveryCache);
        }
    }

    @GetMapping("start_discovery")
    @ResponseBody
    @ResponseStatus(HttpStatus.FOUND)
    public RedirectView startDiscovery(
            @RequestParam(required = false) final String msisdn,
            @RequestParam(required = false) final String mcc,
            @RequestParam(required = false) final String mnc,
            @RequestParam(required = false) String sourceIp,
            @RequestParam(required = false) boolean ignoreIp,
            final HttpServletRequest request)
    {
        LOGGER.info("* Attempting discovery for msisdn={}, mcc={}, mnc={}, sourceIp={}",
                LogUtils.mask(msisdn, LOGGER, Level.INFO), mcc, mnc, sourceIp);
        this.mobileConnectWebInterface = MobileConnect.buildWebInterface(mobileConnectConfig, new DefaultEncodeDecoder(), this.sessionCache, this.discoveryCache);
        this.getParameters();

        if (StringUtils.isNullOrEmpty(sourceIp) & !ignoreIp) {
            sourceIp = includeRequestIP ? HttpUtils.extractClientIp(request) : null;
        }

        DiscoveryResponse discoveryResponse = getDiscoveryCache(msisdn, mcc, mnc, sourceIp);
        MobileConnectStatus status;

        if (discoveryResponse == null) {
            status = attemptDiscovery(msisdn, mcc, mnc, sourceIp, request);
            discoveryResponse = status.getDiscoveryResponse();

            if (discoveryResponse == null  || discoveryResponse.getResponseCode() !=
                    org.apache.http.HttpStatus.SC_OK) {
                if (status.getUrl() != null) {
                    return new RedirectView(status.getUrl(), true);
                }
                else {
                    return startDiscovery(null, null, null, null, true, request);
                }
            }
        }

        setDiscoveryCache(msisdn, mcc, mnc, sourceIp, discoveryResponse);

        String url;
        if (operatorParams.getScope().contains(Scope.AUTHZ)) {
            url = startAuthorize(
                    discoveryResponse,
                    discoveryResponse.getResponseData().getSubscriberId(),
                    request,
                    msisdn, mcc, mnc, sourceIp);
        } else {
            url = startAuthentication(
                    discoveryResponse,
                    discoveryResponse.getResponseData().getSubscriberId(),
                    request,
                    msisdn, mcc, mnc, sourceIp);
        }

        if (url == null) {
            return startDiscovery(null, null, null, null, true, request);
        }

        return new RedirectView(url);
    }

    private MobileConnectStatus attemptDiscovery(String msisdn, String mcc, String mnc, String sourceIp, HttpServletRequest request) {
        MobileConnectRequestOptions requestOptions =
                new MobileConnectRequestOptions.Builder()
                        .withDiscoveryOptions(new DiscoveryOptions.Builder()
                                .withClientIp(sourceIp)
                                .withClientSideVersion(request.getHeader(Headers.CLIENT_SIDE_VERSION))
                                .withServerSideVersion(Constants.SERVER_SIDE_VERSION)
                                .build())
                        .build();
        MobileConnectStatus status =
                this.mobileConnectWebInterface.attemptDiscovery(request, msisdn, mcc, mnc, true,
                        mobileConnectConfig.getIncludeRequestIp(), requestOptions);

        if (status.getErrorMessage() != null) {
            status = this.mobileConnectWebInterface.attemptDiscovery(request, null, null, null,
                    false, includeRequestIP, requestOptions);
        }
        return status;
    }

    private void setDiscoveryCache(final String msisdn, final String mcc, final String mnc, final String sourceIp,
                                   final DiscoveryResponse discoveryResponse) {
        try {
            discoveryCache.add(StringUtils.formatKey(msisdn, mcc, mnc, sourceIp), discoveryResponse);
        } catch (CacheAccessException e) {
            LOGGER.error("Unable to access cache");
        }
    }

    private DiscoveryResponse getDiscoveryCache(String msisdn, String mcc, String mnc, String sourceIp) {
        return discoveryCache.get(StringUtils.formatKey(msisdn, mcc, mnc, sourceIp));
    }

    @GetMapping("start_manual_discovery_no_metadata")
    @ResponseBody
    @ResponseStatus(HttpStatus.FOUND)
    public MobileConnectWebResponse startAuthenticationNoMetadata(
            @RequestParam(required = false) final String subId,
            @RequestParam(required = false) final String clientId,
            @RequestParam(required = false) final String clientSecret,
            final HttpServletRequest request) throws JsonDeserializationException {

        LOGGER.info("* Starting authentication for sdkSession={}, subscriberId={}, scope={}",
                LogUtils.mask(subId, LOGGER, Level.INFO), Scopes.MOBILE_CONNECT);
        OperatorUrls operatorUrlsWD = new OperatorUrls.Builder()
                .withAuthorizationUrl(operatorUrls.getAuthorizationUrl())
                .withRequestTokenUrl(operatorUrls.getRequestTokenUrl())
                .withUserInfoUrl(operatorUrls.getUserInfoUrl())
                .build();
        DiscoveryResponse discoveryResponse = this.mobileConnectWebInterface.generateDiscoveryManually(clientSecret,
                clientId, subId, "appName", operatorUrlsWD);

        final MobileConnectStatus status =
                this.mobileConnectWebInterface.attemptManuallyDiscovery(discoveryResponse);

        return new MobileConnectWebResponse(status);
    }

    @GetMapping({"start_authentication"})
    @ResponseBody
    @ResponseStatus(HttpStatus.FOUND)
    public String startAuthentication(
            @RequestParam(required = false) final DiscoveryResponse discoveryResponse,
            @RequestParam(required = false) final String subscriberId, final HttpServletRequest request, final String msisdn,
            final String mcc, final String mnc, final String sourceIp)
    {
        LOGGER.info("* Starting authentication for discoveryResponse={}, subscriberId={}, scope={}",
                discoveryResponse, LogUtils.mask(subscriberId, LOGGER, Level.INFO));

        return startAuth(discoveryResponse, subscriberId, request, msisdn, mcc, mnc, sourceIp);
    }

    @GetMapping({"start_authorization"})
    @ResponseBody
    @ResponseStatus(HttpStatus.FOUND)
    public String startAuthorize(
            @RequestParam(required = false) final DiscoveryResponse discoveryResponse,
            @RequestParam(required = false) final String subscriberId, final HttpServletRequest request, final String msisdn,
            final String mcc, final String mnc, final String sourceIp)
    {
        LOGGER.info("* Starting authorization for discoveryResponse={}, subscriberId={}, scope={}",
                discoveryResponse, LogUtils.mask(subscriberId, LOGGER, Level.INFO));

        return startAuth(discoveryResponse, subscriberId, request, msisdn, mcc, mnc, sourceIp);
    }

    @GetMapping({"start_authentication", "start_authorization"})
    @ResponseBody
    @ResponseStatus(HttpStatus.FOUND)
    public String startAuth(
            @RequestParam(required = false) final DiscoveryResponse discoveryResponse,
            @RequestParam(required = false) final String subscriberId, final HttpServletRequest request, final String msisdn,
            final String mcc, final String mnc, final String sourceIp)
    {
        String scope = operatorParams.getScope();

        final MobileConnectRequestOptions options = new MobileConnectRequestOptions.Builder()
                .withAuthenticationOptions(new AuthenticationOptions.Builder()
                        .withScope(scope)
                        .withContext((apiVersion.equals(Constants.Version2_0) || apiVersion.equals(Constants.Version2_3)) ? Constants.ContextBindingMsg : null)
                        .withBindingMessage((apiVersion.equals(Constants.Version2_0) || apiVersion.equals(Constants.Version2_3)) ? Constants.BindingMsg : null)
                        .withClientName(clientName)
                        .build())
                .build();
        final MobileConnectStatus status =
                this.mobileConnectWebInterface.startAuthentication(request, discoveryResponse, subscriberId,
                        null, null, options, apiVersion);

        if (status.getErrorMessage() != null) {
            return null;
        }
        setSessionCache(status, msisdn, mcc, mnc, sourceIp);

        return status.getUrl();
    }

    private void setSessionCache(MobileConnectStatus status, String msisdn, String mcc, String mnc, String sourceIp) {
        try {
            sessionCache.add(status.getState(), new SessionData(discoveryCache.get(StringUtils.formatKey(msisdn, mcc, mnc, sourceIp)),
                    status.getNonce()));
        } catch (CacheAccessException e) {
            LOGGER.error("Unable to access cache");
        }
    }

    @GetMapping("headless_authentication")
    @ResponseBody
    @ResponseStatus(HttpStatus.FOUND)
    public MobileConnectWebResponse headlessAuthentication(
            @RequestParam(required = false) final String sdkSession,
            @RequestParam(required = false) final String subscriberId,
            @RequestParam(required = false) final String scope, final HttpServletRequest request)
    {
        LOGGER.info("* Starting authentication for sdkSession={}, subscriberId={}, scope={}",
                sdkSession, LogUtils.mask(subscriberId, LOGGER, Level.INFO), scope);

        final MobileConnectRequestOptions options = new MobileConnectRequestOptions.Builder()
                .withAuthenticationOptions(new AuthenticationOptions.Builder()
                        .withScope(scope)
                        .withContext(apiVersion.equals(DefaultOptions.VERSION_MOBILECONNECTAUTHZ) ? "headless" : null)
                        .withBindingMessage(apiVersion.equals(DefaultOptions.VERSION_MOBILECONNECTAUTHZ) ? "demo headless" : null)
                        .build())
                .witAutoRetrieveIdentitySet(true)
                .build();

        final MobileConnectStatus status =
                this.mobileConnectWebInterface.requestHeadlessAuthentication(request, sdkSession,
                        subscriberId, null, null, options, apiVersion);

        return new MobileConnectWebResponse(status);
    }

    @GetMapping("refresh_token")
    @ResponseBody
    public MobileConnectWebResponse refreshToken(
            @RequestParam(required = false) final String sdkSession,
            @RequestParam(required = false) final String refreshToken, final HttpServletRequest request) {
        LOGGER.info("* Calling refresh token for sdkSession={}, refreshToken={}", sdkSession,
                LogUtils.mask(refreshToken, LOGGER, Level.INFO));

        final MobileConnectStatus status =
                this.mobileConnectWebInterface.refreshToken(request, refreshToken, sdkSession);

        return new MobileConnectWebResponse(status);
    }

    @GetMapping("revoke_token")
    @ResponseBody
    public MobileConnectWebResponse revokeToken(
            @RequestParam(required = false) final String sdkSession,
            @RequestParam(required = false) final String accessToken, final HttpServletRequest request) {
        LOGGER.info("* Calling revoke token for sdkSession={}, accessToken={}", sdkSession,
                LogUtils.mask(accessToken, LOGGER, Level.INFO));

        final MobileConnectStatus status =
                this.mobileConnectWebInterface.revokeToken(request, accessToken,
                        Parameters.ACCESS_TOKEN_HINT, sdkSession);

        return new MobileConnectWebResponse(status);
    }

    @GetMapping(value = "discovery_callback", params = "mcc_mnc")
    @ResponseBody
    @ResponseStatus(HttpStatus.FOUND)
    public RedirectView mccMncDiscoveryCallback(@RequestParam(required = false) final String mcc_mnc,
                                                @RequestParam(required = false) final String subscriber_id,
                                                final HttpServletRequest request)
    {

        final MobileConnectRequestOptions options = new MobileConnectRequestOptions.Builder()
                .withAuthenticationOptions(new AuthenticationOptions.Builder()
                        .withContext((apiVersion.equals(Constants.Version2_0) || apiVersion.equals(Constants.Version2_3)) ? Constants.ContextBindingMsg : null)
                        .withBindingMessage((apiVersion.equals(Constants.Version2_0) || apiVersion.equals(Constants.Version2_3)) ? Constants.ContextBindingMsg : null)
                        .withClientName(clientName)
                        .build())
                .build();

        String[] mcc_mncArray = mcc_mnc.split("_");
        String mcc = mcc_mncArray[0];
        String mnc = mcc_mncArray[1];

        MobileConnectStatus status = this.mobileConnectWebInterface.attemptDiscovery(request, null, mcc, mnc, true, mobileConnectConfig.getIncludeRequestIp(), options);

        if (status.getDiscoveryResponse() != null) {
            setDiscoveryCache(null, mcc, mnc, null, status.getDiscoveryResponse());
            String url;
            if (operatorParams.getScope().contains(Scope.AUTHZ)) {
                url = startAuthorize(
                        status.getDiscoveryResponse(),
                        subscriber_id,
                        request, null, mcc, mnc, null);
            } else {
                url = startAuthentication(
                        status.getDiscoveryResponse(),
                        subscriber_id,
                        request, null, mcc, mnc, null);
            }
            return new RedirectView(url);
        }
        else {
            return new RedirectView(status.getUrl(), true);
        }
    }

    @GetMapping(value = "discovery_callback")
    @ResponseBody
    @ResponseStatus(HttpStatus.FOUND)
    public MobileConnectWebResponse StateDiscoveryCallback(@RequestParam(required = false) String state,
                                          @RequestParam(required = false) final String error,
                                          @RequestParam(required = false) final String error_description,
                                          @RequestParam(required = false) final String description,
                                          final HttpServletRequest request)
    {
        if (error != null)
        {
            return new MobileConnectWebResponse(MobileConnectStatus.error(error, ObjectUtils.defaultIfNull(description, error_description), new Exception()));
        }
        final MobileConnectRequestOptions options = new MobileConnectRequestOptions.Builder()
                .withAuthenticationOptions(new AuthenticationOptions.Builder()
                        .withContext((apiVersion.equals(Constants.Version2_0) || apiVersion.equals(Constants.Version2_3)) ? Constants.ContextBindingMsg : null)
                        .withBindingMessage((apiVersion.equals(Constants.Version2_0) || apiVersion.equals(Constants.Version2_3)) ? Constants.ContextBindingMsg : null)
                        .withClientName(clientName)
                        .build())
                .build();

        URI requestUri = HttpUtils.extractCompleteUrl(request);
        SessionData sessionData = sessionCache.get(state);
        MobileConnectStatus status = this.mobileConnectWebInterface.handleUrlRedirect(request, requestUri,
                sessionData.getDiscoveryResponse(), state, sessionData.getNonce(), options, apiVersion);

        if (apiVersion.equals(DefaultOptions.VERSION_MOBILECONNECT) & !StringUtils.isNullOrEmpty(sessionData.getDiscoveryResponse().getOperatorUrls().getUserInfoUrl())) {
            for (String userInfoScope : USERINFO_SCOPES) {
                if (operatorParams.getScope().contains(userInfoScope)) {
                    final MobileConnectStatus statusUserInfo =
                            this.mobileConnectWebInterface.requestUserInfo(request, sessionData.getDiscoveryResponse(),
                                    status.getRequestTokenResponse().getResponseData().getAccessToken());
                    status = status.withIdentityResponse(statusUserInfo.getIdentityResponse());
                }
            }

        } else if (apiVersion.equals(DefaultOptions.MC_V2_3) & !StringUtils.isNullOrEmpty(sessionData.getDiscoveryResponse().getOperatorUrls().getPremiumInfoUri())) {
            for (String identityScope : IDENTITY_SCOPES) {
                if (operatorParams.getScope().contains(identityScope)) {
                    final MobileConnectStatus statusIdentity =
                            this.mobileConnectWebInterface.requestIdentity(request, sessionData.getDiscoveryResponse(),
                                    status.getRequestTokenResponse().getResponseData().getAccessToken());
                    status = status.withIdentityResponse(statusIdentity.getIdentityResponse());
                    break;
                }
            }
        }

        return new MobileConnectWebResponse(status);
    }

    @GetMapping({"sector_identifier_uri", "sector_identifier_uri.json"})
    @ResponseBody
    @ResponseStatus(HttpStatus.FOUND)
    public JSONArray getSectorIdentifierUri()   {
        JSONArray jsonArray;
        jsonArray = ReadAndParseFiles.readJsonArray(Constants.SectorIdentifierPath);
        if(jsonArray == null) {
            jsonArray = ReadAndParseFiles.readJsonArray(Constants.SectorIdentifierPath.replace("file:/", ""));
        }
        if(jsonArray == null) {
            jsonArray = ReadAndParseFiles.readJsonArray(Constants.SectorIdentifierPath.replace("file:", ""));
        }
        return jsonArray;
    }


    private void getParameters() {
        operatorParams = ReadAndParseFiles.ReadFile(Constants.ConfigFilePath);
        if(operatorParams == null) {
            operatorParams = ReadAndParseFiles.ReadFile(Constants.ConfigFilePath.replace("file:/", ""));
        }
        if(operatorParams == null) {
            operatorParams = ReadAndParseFiles.ReadFile(Constants.ConfigFilePath.replace("file:", ""));
        }

        apiVersion = operatorParams.getApiVersion();
        includeRequestIP = operatorParams.getIncludeRequestIP().equals("True");
        sessionCache = new SessionCache.Builder()
                .withJsonService(this.jsonService)
                .withMaxCacheSize(operatorParams.getMaxDiscoveryCacheSize())
                .build();
        clientName = operatorParams.getClientName();

        discoveryCache = new DiscoveryCache.Builder().withJsonService(this.jsonService).withMaxCacheSize(operatorParams.getMaxDiscoveryCacheSize()).build();
        try {
            mobileConnectConfig = new MobileConnectConfig.Builder()
                    .withClientId(operatorParams.getClientID())
                    .withClientSecret(operatorParams.getClientSecret())
                    .withClientName(operatorParams.getClientName())
                    .withDiscoveryUrl(new URI(operatorParams.getDiscoveryURL()))
                    .withRedirectUrl(new URI(operatorParams.getRedirectURL()))
                    .withXRedirect(operatorParams.getXRedirect().equals("True") ? "APP" : "False")
                    .withIncludeRequestIP(includeRequestIP)
                    .build();
        } catch (URISyntaxException e) {
            LOGGER.error("Wrong URI provided");
        }
    }
}
