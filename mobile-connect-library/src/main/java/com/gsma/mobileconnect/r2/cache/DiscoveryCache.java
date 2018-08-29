package com.gsma.mobileconnect.r2.cache;

import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.utils.ObjectUtils;

public class DiscoveryCache extends ConcurrentCache {
    protected DiscoveryCache(Builder builder) {
        super(builder);
    }

    public DiscoveryResponse get(String key) {
        DiscoveryResponse discoveryResp = null;
        if (!hasKey(key)) {
            return null;
        }
        try {
            discoveryResp = this.get(key, DiscoveryResponse.class);
        } catch (CacheAccessException e) {
            e.printStackTrace();
        }

        if (discoveryResp.hasExpired()) {
            this.remove(key);
            return null;
        }
        return discoveryResp;
    }

    public static final class Builder extends ConcurrentCache.Builder {

        @Override
        public ConcurrentCache build() {
            ObjectUtils.requireNonNull(this.jsonService, "jsonService");
            return new DiscoveryCache(this);
        }
    }
}
