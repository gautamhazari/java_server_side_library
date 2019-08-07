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
import com.gsma.mobileconnect.r2.constants.DefaultOptions;
import com.gsma.mobileconnect.r2.constants.Headers;
import com.gsma.mobileconnect.r2.constants.Parameters;
import com.gsma.mobileconnect.r2.constants.Scope;
import com.gsma.mobileconnect.r2.demo.objects.Status;
import com.gsma.mobileconnect.r2.demo.utils.Constants;
import com.gsma.mobileconnect.r2.demo.utils.ReadAndParseFiles;
import com.gsma.mobileconnect.r2.discovery.DiscoveryOptions;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.discovery.SessionData;
import com.gsma.mobileconnect.r2.encoding.DefaultEncodeDecoder;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

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

        if (StringUtils.isNullOrEmpty(sourceIp) && !ignoreIp) {
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
                        .withContext((apiVersion.equals(Constants.VERSION_2_0) || apiVersion.equals(Constants.VERSION_2_3)) ? Constants.CONTEXT_BINDING_MSG : null)
                        .withBindingMessage((apiVersion.equals(Constants.VERSION_2_0) || apiVersion.equals(Constants.VERSION_2_3)) ? Constants.BINDING_MSG : null)
                        .withClientName(clientName)
                        .withLoginHintToken(discoveryResponse.getResponseData().getSubscriberIdToken())
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

    @GetMapping(value = "discovery_callback", params = "mcc_mnc")
    @ResponseBody
    @ResponseStatus(HttpStatus.FOUND)
    public RedirectView mccMncDiscoveryCallback(@RequestParam(required = false) final String mcc_mnc,
                                                @RequestParam(required = false) final String subscriber_id,
                                                final HttpServletRequest request)
    {

        final MobileConnectRequestOptions options = new MobileConnectRequestOptions.Builder()
                .withAuthenticationOptions(new AuthenticationOptions.Builder()
                        .withContext((apiVersion.equals(Constants.VERSION_2_0) || apiVersion.equals(Constants.VERSION_2_3)) ? Constants.CONTEXT_BINDING_MSG : null)
                        .withBindingMessage((apiVersion.equals(Constants.VERSION_2_0) || apiVersion.equals(Constants.VERSION_2_3)) ? Constants.CONTEXT_BINDING_MSG : null)
                        .withClientName(clientName)
                        .build())
                .build();

        String[] mccMncArray = mcc_mnc.split("_");
        String mcc = mccMncArray[0];
        String mnc = mccMncArray[1];

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
    @ResponseStatus(HttpStatus.FOUND)
    public ModelAndView stateDiscoveryCallback(@RequestParam(required = false) String state,
                                               @RequestParam(required = false) final String error,
                                               @RequestParam(required = false) final String error_description,
                                               @RequestParam(required = false) final String description,
                                               final HttpServletRequest request)
    {
        String operationStatus;
        if (error != null)
        {
            if (operatorParams.getScope().contains(Scope.AUTHN) || operatorParams.getScope().equals(Scope.OPENID)) {
                operationStatus = Status.AUTHENTICATION;
            } else {
                operationStatus = Status.AUTHORISATION;
            }
            return redirectToView(MobileConnectStatus.error(error,
                    ObjectUtils.defaultIfNull(description, error_description), new Exception()), operationStatus);
        }

        final MobileConnectRequestOptions options = new MobileConnectRequestOptions.Builder()
                .withAuthenticationOptions(new AuthenticationOptions.Builder()
                        .withContext((apiVersion.equals(Constants.VERSION_2_0) || apiVersion.equals(Constants.VERSION_2_3)) ? Constants.CONTEXT_BINDING_MSG : null)
                        .withBindingMessage((apiVersion.equals(Constants.VERSION_2_0) || apiVersion.equals(Constants.VERSION_2_3)) ? Constants.CONTEXT_BINDING_MSG : null)
                        .withClientName(clientName)
                        .build())
                .build();

        URI requestUri = HttpUtils.extractCompleteUrl(request);
        SessionData sessionData = sessionCache.get(state);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MobileConnectStatus status = this.mobileConnectWebInterface.handleUrlRedirect(request, requestUri,
                sessionData.getDiscoveryResponse(), state, sessionData.getNonce(), options, apiVersion);

        if (apiVersion.equals(DefaultOptions.VERSION_MOBILECONNECT) && !StringUtils.isNullOrEmpty(sessionData.getDiscoveryResponse().getOperatorUrls().getUserInfoUrl())) {
            for (String userInfoScope : userinfoScopes) {
                if (operatorParams.getScope().contains(userInfoScope)) {
                    final MobileConnectStatus statusUserInfo =
                            this.mobileConnectWebInterface.requestUserInfo(request, sessionData.getDiscoveryResponse(),
                                    status.getRequestTokenResponse().getResponseData().getAccessToken());
                    status = status.withIdentityResponse(statusUserInfo.getIdentityResponse());
                    break;
                }
            }

        } else if ((apiVersion.equals(DefaultOptions.MC_V2_3) || apiVersion.equals(DefaultOptions.MC_V2_0))
                && !StringUtils.isNullOrEmpty(sessionData.getDiscoveryResponse().getOperatorUrls().getPremiumInfoUri())) {
            for (String identityScope : identityScopes) {
                if (operatorParams.getScope().contains(identityScope)) {
                    final MobileConnectStatus statusIdentity =
                            this.mobileConnectWebInterface.requestIdentity(request, sessionData.getDiscoveryResponse(),
                                    status.getRequestTokenResponse().getResponseData().getAccessToken());
                    status = status.withIdentityResponse(statusIdentity.getIdentityResponse());
                    break;
                }
            }
        } else {
            return redirectToView(status, Status.TOKEN);
        }

        return redirectToView(status, Status.PREMIUMINFO);
    }

    private ModelAndView redirectToView(MobileConnectStatus status, String operationStatus) {
        Map<String, Object> modelMap = new HashMap<>();
        LOGGER.info(ObjectUtils.convertToJsonString(status));
        modelMap.put("operation", operationStatus);
        if (status.getErrorCode() != null) {
            modelMap.put("status", new MobileConnectWebResponse(status));
            return new ModelAndView(Constants.FAIL_HTML_PAGE, modelMap);
        }
        return new ModelAndView(Constants.SUCCESS_HTML_PAGE, modelMap);
    }

    @GetMapping({"sector_identifier_uri", "sector_identifier_uri.json"})
    @ResponseBody
    @ResponseStatus(HttpStatus.FOUND)
    public JSONArray getSectorIdentifierUri()   {
        JSONArray jsonArray;
        jsonArray = ReadAndParseFiles.readJsonArray(Constants.SECTOR_IDENTIFIER_PATH);
        if(jsonArray == null) {
            jsonArray = ReadAndParseFiles.readJsonArray(Constants.SECTOR_IDENTIFIER_PATH.replace("file:/", ""));
        }
        if(jsonArray == null) {
            jsonArray = ReadAndParseFiles.readJsonArray(Constants.SECTOR_IDENTIFIER_PATH.replace("file:", ""));
        }
        return jsonArray;
    }


    private void getParameters() {
        operatorParams = ReadAndParseFiles.readFile(Constants.CONFIG_FILE_PATH);
        if(operatorParams == null) {
            operatorParams = ReadAndParseFiles.readFile(Constants.CONFIG_FILE_PATH.replace("file:/", ""));
        }
        if(operatorParams == null) {
            operatorParams = ReadAndParseFiles.readFile(Constants.CONFIG_FILE_PATH.replace("file:", ""));
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
