package com.gsma.mobileconnect.r2.demo.objects;
import com.gsma.mobileconnect.r2.cache.ICacheable;
import com.gsma.mobileconnect.r2.discovery.DiscoveryOptions;

public class CachedParameters implements ICacheable {

    private boolean cached = false;
    private boolean expired = false;
    private String nonce;
    private String sdkSession;
    private String accessToken;
    private DiscoveryOptions discoveryOptions;


    @Override
    public boolean isCached() {
        return this.cached;
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }

    @Override
    public boolean hasExpired() {
        return this.expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public String getNonce() {
        return this.nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getSdkSession() {
        return this.sdkSession;
    }

    public void setSdkSession(String sdkSession) {
        this.sdkSession = sdkSession;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public DiscoveryOptions getDiscoveryOptions() {
        return discoveryOptions;
    }

    public void setDiscoveryOptions(DiscoveryOptions discoveryOptions) {
        this.discoveryOptions = discoveryOptions;
    }
}