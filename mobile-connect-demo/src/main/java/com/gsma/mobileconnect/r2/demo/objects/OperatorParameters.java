package com.gsma.mobileconnect.r2.demo.objects;

public class OperatorParameters {
    private String msisdn;
    private String clientID;
    private String clientSecret;
    private String discoveryURL;
    private String redirectURL;
    private String xRedirect;
    private String includeRequestIP;
    private String apiVersion;
    private String scope;
    private long MaxDiscoveryCacheSize;

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

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
        return MaxDiscoveryCacheSize;
    }

    public void setMaxDiscoveryCacheSize(long MaxDiscoveryCacheSize) {
        this.MaxDiscoveryCacheSize = MaxDiscoveryCacheSize;
    }

}


