package com.gsma.mobileconnect.r2.demo.objects;

import com.gsma.mobileconnect.r2.service.discovery.OperatorUrls;

public class OperatorParameters {
    private String clientID;
    private String clientSecret;
    private String clientName;
    private String discoveryURL;
    private String redirectURL;
    private String xRedirect;
    private String includeRequestIP;
    private String apiVersion;
    private String scope;
    private String arcValues;
    private long maxDiscoveryCacheSize;
    private String loginHintTokenPreference;
    private OperatorUrls operatorUrls;

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getDiscoveryURL() {
        return discoveryURL;
    }

    public void setDiscoveryURL(String discoveryURL) {
        this.discoveryURL = discoveryURL;
    }

    public String getRedirectURL() {
        return redirectURL;
    }

    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }

    public String getXRedirect() {
        return xRedirect;
    }

    public void setXRedirect(String xRedirect) {
        this.xRedirect = xRedirect;
    }

    public String getIncludeRequestIP() {
        return includeRequestIP;
    }

    public void setIncludeRequestIP(String includeRequestIP) {
        this.includeRequestIP = includeRequestIP;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public long getMaxDiscoveryCacheSize() {
        return maxDiscoveryCacheSize;
    }

    public void setMaxDiscoveryCacheSize(long maxDiscoveryCacheSize) {
        this.maxDiscoveryCacheSize = maxDiscoveryCacheSize;
    }

    public String getArcValues() {
        return arcValues;
    }

    public void setArcValues(String arcValues) {
        this.arcValues = arcValues;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public OperatorUrls getOperatorUrls() {
        return operatorUrls;
    }

    public void setOperatorUrls(OperatorUrls operatorUrls) {
        this.operatorUrls = operatorUrls;
    }

    public String getLoginHintTokenPreference() {
        return loginHintTokenPreference;
    }

    public void setLoginHintTokenPreference(String loginHintTokenPreference) {
        this.loginHintTokenPreference = loginHintTokenPreference;
    }
}


