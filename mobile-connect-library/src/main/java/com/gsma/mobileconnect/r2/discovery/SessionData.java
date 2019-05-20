package com.gsma.mobileconnect.r2.discovery;

import com.gsma.mobileconnect.r2.cache.AbstractCacheable;

public class SessionData extends AbstractCacheable {
    private String nonce;
    private DiscoveryResponse discoveryResponse;
    private String scope;
    private String version;

    public SessionData() {

    }

    public SessionData(DiscoveryResponse discoveryResponse, String nonce) {
        this.nonce = nonce;
        this.discoveryResponse = discoveryResponse;
    }

    public SessionData(DiscoveryResponse discoveryResponse, String nonce, String scope, String version) {
        this.nonce = nonce;
        this.discoveryResponse = discoveryResponse;
        this.scope = scope;
        this.version = version;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public DiscoveryResponse getDiscoveryResponse() {
        return discoveryResponse;
    }

    public void setDiscoveryResponse(DiscoveryResponse discoveryResponse) {
        this.discoveryResponse = discoveryResponse;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
