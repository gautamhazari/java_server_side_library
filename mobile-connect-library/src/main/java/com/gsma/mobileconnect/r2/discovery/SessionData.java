package com.gsma.mobileconnect.r2.discovery;

import com.gsma.mobileconnect.r2.cache.AbstractCacheable;

public class SessionData extends AbstractCacheable {
    private String nonce;
    private DiscoveryResponse discoveryResponse;

    public SessionData() {

    }

    public SessionData(DiscoveryResponse discoveryResponse, String nonce) {
        this.nonce = nonce;
        this.discoveryResponse = discoveryResponse;
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
}
