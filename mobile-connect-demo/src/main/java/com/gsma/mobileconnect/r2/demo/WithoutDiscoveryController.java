package com.gsma.mobileconnect.r2.demo;

import com.gsma.mobileconnect.r2.MobileConnect;
import com.gsma.mobileconnect.r2.MobileConnectConfig;
import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.authentication.AuthenticationOptions;
import com.gsma.mobileconnect.r2.cache.CacheAccessException;
import com.gsma.mobileconnect.r2.cache.DiscoveryCache;
import com.gsma.mobileconnect.r2.cache.SessionCache;
import com.gsma.mobileconnect.r2.constants.Parameters;
import com.gsma.mobileconnect.r2.constants.Scope;
import com.gsma.mobileconnect.r2.demo.utils.Constants;
import com.gsma.mobileconnect.r2.demo.utils.ReadAndParseFiles;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.discovery.SessionData;
import com.gsma.mobileconnect.r2.encoding.DefaultEncodeDecoder;
import com.gsma.mobileconnect.r2.json.JsonDeserializationException;
import com.gsma.mobileconnect.r2.utils.LogUtils;
import com.gsma.mobileconnect.r2.utils.StringUtils;
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
public class WithoutDiscoveryController extends com.gsma.mobileconnect.r2.demo.Controller {
    private static final Logger LOGGER = LoggerFactory.getLogger(WithoutDiscoveryController.class);
    private String clientId;
    private String clientSecret;

    private WithoutDiscoveryController() {
        this.getWDParameters();
        if (this.mobileConnectWebInterface == null) {
            this.mobileConnectWebInterface = MobileConnect.buildWebInterface(mobileConnectConfig, new DefaultEncodeDecoder(), this.sessionCache, this.discoveryCache);
        }
    }

    @GetMapping("start_discovery_manually")
    @ResponseBody
    @ResponseStatus(HttpStatus.FOUND)
    public RedirectView startAuthenticationWithoutDiscovery(
            @RequestParam(required = false) final String msisdn,
            final HttpServletRequest request)
    {

        LOGGER.info("* Attempting discovery for msisdn={}", LogUtils.mask(msisdn, LOGGER, Level.INFO));

        DiscoveryResponse discoveryResponse = null;
        try {
            discoveryResponse = this.mobileConnectWebInterface.generateDiscoveryManually(clientSecret, clientId,
                    clientName, operatorUrls);
        } catch (JsonDeserializationException e) {
            LOGGER.warn("Can't create discoveryResponse");
        }

        String url;

        if (operatorParams.getScope().contains(Scope.AUTHZ)) {
            url = startAuthorize(
                    discoveryResponse,
                    msisdn,
                    request);
        } else {
            url = startAuthentication(
                    discoveryResponse,
                    msisdn,
                    request);
        }

        return new RedirectView(url);
    }

    @GetMapping({"start_wd_authentication"})
    @ResponseBody
    @ResponseStatus(HttpStatus.FOUND)
    public String startAuthentication(
            @RequestParam(required = false) final DiscoveryResponse discoveryResponse,
            @RequestParam(required = false) final String msisdn, final HttpServletRequest request)
    {
        LOGGER.info("* Starting authentication for discoveryResponse={}, msisdn={}, scope={}",
                discoveryResponse, LogUtils.mask(msisdn, LOGGER, Level.INFO));

        return startAuth(discoveryResponse, msisdn, request);
    }

    @GetMapping({"start_wd_authorization"})
    @ResponseBody
    @ResponseStatus(HttpStatus.FOUND)
    public String startAuthorize(
            @RequestParam(required = false) final DiscoveryResponse discoveryResponse,
            @RequestParam(required = false) final String msisdn, final HttpServletRequest request)
    {
        LOGGER.info("* Starting authorization for discoveryResponse={}, msisdn={}, scope={}",
                discoveryResponse, LogUtils.mask(msisdn, LOGGER, Level.INFO));

        return startAuth(discoveryResponse, msisdn, request);
    }

    @GetMapping({"start_wd_authentication", "start_wd_authorization"})
    @ResponseBody
    @ResponseStatus(HttpStatus.FOUND)
    public String startAuth(
            @RequestParam(required = false) final DiscoveryResponse discoveryResponse,
            @RequestParam(required = false) final String msisdn, final HttpServletRequest request)
    {
        String scope = operatorParams.getScope();
        String loginHint = null;

        if (!StringUtils.isNullOrEmpty(msisdn)) {
           loginHint = String.format("%s:%s", Parameters.MSISDN, msisdn);
        }

        final MobileConnectRequestOptions options = new MobileConnectRequestOptions.Builder()
                .withAuthenticationOptions(new AuthenticationOptions.Builder()
                        .withScope(scope)
                        .withContext((apiVersion.equals(Constants.Version2_0) || apiVersion.equals(Constants.Version2_3)) ? Constants.ContextBindingMsg : null)
                        .withBindingMessage((apiVersion.equals(Constants.Version2_0) || apiVersion.equals(Constants.Version2_3)) ? Constants.BindingMsg : null)
                        .withClientName(clientName)
                        .withLoginHint(loginHint)
                        .build())
                .build();
        final MobileConnectStatus status =
                this.mobileConnectWebInterface.startAuthentication(request, discoveryResponse, null,
                        null, null, options, apiVersion);

        if (status.getErrorMessage() != null) {
            return null;
        }
        setSessionCache(status, discoveryResponse, status.getNonce());

        return status.getUrl();
    }

    private void setSessionCache(MobileConnectStatus status, DiscoveryResponse discoveryResponse, String nonce) {
        try {
            sessionCache.add(status.getState(), new SessionData(discoveryResponse, nonce));
        } catch (CacheAccessException e) {
            LOGGER.error("Unable to access cache");
        }
    }

    private void getWDParameters() {
        operatorParams = ReadAndParseFiles.ReadFile(Constants.WDConfigFilePath);
        if(operatorParams == null) {
            operatorParams = ReadAndParseFiles.ReadFile(Constants.WDConfigFilePath.replace("file:/", ""));
        }
        if(operatorParams == null) {
            operatorParams = ReadAndParseFiles.ReadFile(Constants.WDConfigFilePath.replace("file:", ""));
        }

        apiVersion = operatorParams.getApiVersion();
        includeRequestIP = operatorParams.getIncludeRequestIP().equals("True");
        operatorUrls = operatorParams.getOperatorUrls();
        sessionCache = new SessionCache.Builder()
                .withJsonService(this.jsonService)
                .withMaxCacheSize(operatorParams.getMaxDiscoveryCacheSize())
                .build();
        clientName = operatorParams.getClientName();
        clientId = operatorParams.getClientID();
        clientSecret = operatorParams.getClientSecret();

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
