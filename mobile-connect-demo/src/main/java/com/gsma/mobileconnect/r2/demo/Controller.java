package com.gsma.mobileconnect.r2.demo;

import com.gsma.mobileconnect.r2.MobileConnectConfig;
import com.gsma.mobileconnect.r2.MobileConnectWebInterface;
import com.gsma.mobileconnect.r2.cache.ConcurrentCache;
import com.gsma.mobileconnect.r2.constants.Scope;
import com.gsma.mobileconnect.r2.demo.objects.OperatorParameters;
import com.gsma.mobileconnect.r2.discovery.OperatorUrls;
import com.gsma.mobileconnect.r2.json.IJsonService;
import com.gsma.mobileconnect.r2.json.JacksonJsonService;
import com.gsma.mobileconnect.r2.rest.RestClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;

@org.springframework.stereotype.Controller
@EnableAutoConfiguration
@RequestMapping(path = "server_side_api"/*, produces = MediaType.APPLICATION_JSON_UTF8_VALUE*/)
public class Controller {
    protected ConcurrentCache sessionCache;
    protected ConcurrentCache scopeSessionCache;
    protected MobileConnectConfig mobileConnectConfig;
    protected final IJsonService jsonService;
    protected MobileConnectWebInterface mobileConnectWebInterface;
    protected String clientName;
    protected OperatorUrls operatorUrls;
    protected boolean includeRequestIP;
    protected ConcurrentCache discoveryCache;
    protected RestClient restClient;
    protected OperatorParameters operatorParams = new OperatorParameters();
    protected static final String[] identityScopes = {Scope.IDENTITY_PHONE, Scope.IDENTITY_SIGNUP,
            Scope.IDENTITY_NATIONALID, Scope.IDENTITY_SIGNUPPLUS, Scope.KYC_HASHED, Scope.KYC_PLAIN};
    protected static final String[] userinfoScopes = {Scope.PROFILE, Scope.EMAIL, Scope.ADDRESS,
            Scope.PHONE, Scope.OFFLINE_ACCESS};

    protected Controller() {
        this.jsonService = new JacksonJsonService();
        restClient = new RestClient.Builder().withJsonService(jsonService).withHttpClient(HttpClientBuilder.create().build()).build();
    }



}
