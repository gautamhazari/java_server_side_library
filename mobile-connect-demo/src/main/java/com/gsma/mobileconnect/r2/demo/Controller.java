package com.gsma.mobileconnect.r2.demo;

import com.gsma.mobileconnect.r2.MobileConnectConfig;
import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.MobileConnectWebInterface;
import com.gsma.mobileconnect.r2.cache.CacheAccessException;
import com.gsma.mobileconnect.r2.cache.ConcurrentCache;
import com.gsma.mobileconnect.r2.constants.Headers;
import com.gsma.mobileconnect.r2.constants.Scope;
import com.gsma.mobileconnect.r2.demo.objects.OperatorParameters;
import com.gsma.mobileconnect.r2.demo.utils.Constants;
import com.gsma.mobileconnect.r2.discovery.DiscoveryOptions;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.discovery.OperatorUrls;
import com.gsma.mobileconnect.r2.discovery.SessionData;
import com.gsma.mobileconnect.r2.json.IJsonService;
import com.gsma.mobileconnect.r2.json.GsonJsonService;
import com.gsma.mobileconnect.r2.rest.RestClient;
import com.gsma.mobileconnect.r2.utils.ObjectUtils;
import com.gsma.mobileconnect.r2.utils.StringUtils;
import com.gsma.mobileconnect.r2.web.MobileConnectWebResponse;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@org.springframework.stereotype.Controller
@EnableAutoConfiguration
@RequestMapping(path = "server_side_api"/*, produces = MediaType.APPLICATION_JSON_UTF8_VALUE*/)
public class Controller {
    protected ConcurrentCache sessionCache;
    protected MobileConnectConfig mobileConnectConfig;
    protected final IJsonService jsonService;
    protected MobileConnectWebInterface mobileConnectWebInterface;
    protected String clientName;
    protected String apiVersion;
    protected OperatorUrls operatorUrls;
    protected boolean includeRequestIP;
    protected boolean loginHintTokenPreference;
    protected ConcurrentCache discoveryCache;
    protected RestClient restClient;
    protected OperatorParameters operatorParams = new OperatorParameters();
    protected static final String[] identityScopes = {Scope.IDENTITY_PHONE, Scope.IDENTITY_SIGNUP,
            Scope.IDENTITY_NATIONALID, Scope.MC_PHONE, Scope.MC_SIGNUP,
            Scope.MC_NATIONALID, Scope.IDENTITY_SIGNUPPLUS, Scope.KYC_HASHED, Scope.KYC_PLAIN};
    protected static final String[] userinfoScopes = {Scope.PROFILE, Scope.EMAIL, Scope.ADDRESS,
            Scope.PHONE, Scope.OFFLINE_ACCESS};
    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);

    protected Controller() {
        this.jsonService = new GsonJsonService();
        restClient = new RestClient.Builder().withJsonService(jsonService).withHttpClient(HttpClientBuilder.create().build()).build();
    }


    protected ModelAndView redirectToView(MobileConnectStatus status, String operationStatus) {
        Map<String, Object> modelMap = new HashMap<>();
        LOGGER.info(ObjectUtils.convertToJsonString(status));
        modelMap.put("operation", operationStatus);
        if (status.getErrorCode() != null) {
            modelMap.put("status", new MobileConnectWebResponse(status));
            return new ModelAndView(Constants.FAIL_HTML_PAGE, modelMap);
        }
        return new ModelAndView(Constants.SUCCESS_HTML_PAGE, modelMap);
    }

    protected MobileConnectStatus attemptDiscovery(String msisdn, String mcc, String mnc, String sourceIp, HttpServletRequest request) {
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

    protected void setDiscoveryCache(final String msisdn, final String mcc, final String mnc, final String sourceIp,
                                   final DiscoveryResponse discoveryResponse) {
        try {
            discoveryCache.add(StringUtils.formatKey(msisdn, mcc, mnc, sourceIp), discoveryResponse);
        } catch (CacheAccessException e) {
            LOGGER.error("Unable to access cache");
        }
    }

    protected DiscoveryResponse getDiscoveryCache(String msisdn, String mcc, String mnc, String sourceIp) {
        return discoveryCache.get(StringUtils.formatKey(msisdn, mcc, mnc, sourceIp));
    }

    protected void setSessionCache(MobileConnectStatus status, String msisdn, String mcc, String mnc, String sourceIp) {
        try {
            sessionCache.add(status.getState(), new SessionData(discoveryCache.get(StringUtils.formatKey(msisdn, mcc, mnc, sourceIp)),
                    status.getNonce()));
        } catch (CacheAccessException e) {
            LOGGER.error("Unable to access cache");
        }
    }


}
