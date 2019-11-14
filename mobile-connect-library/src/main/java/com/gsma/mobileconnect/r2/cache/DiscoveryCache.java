package com.gsma.mobileconnect.r2.cache;

import com.gsma.mobileconnect.r2.service.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscoveryCache extends ConcurrentCache {
    protected DiscoveryCache(Builder builder) {
        super(builder);
    }
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoveryCache.class);

    public DiscoveryResponse get(String key) {
        DiscoveryResponse discoveryResp = null;
        if (!hasKey(key)) {
            return null;
        }
        try {
            discoveryResp = this.get(key, DiscoveryResponse.class);
        } catch (CacheAccessException e) {
            LOGGER.warn(e.getMessage());
        }

        if (discoveryResp != null && discoveryResp.hasExpired()) {
            this.remove(key);
            return null;
        }
        return discoveryResp;
    }

    @Override
    protected boolean hasKey(String key) {
        try {
            return this.get(key, DiscoveryResponse.class) != null;
        } catch (CacheAccessException e) {
            LOGGER.warn(e.getMessage());
            return false;
        }
    }

    public static final class Builder extends ConcurrentCache.Builder {

        @Override
        public ConcurrentCache build() {
            ObjectUtils.requireNonNull(this.jsonService, "jsonService");
            return new DiscoveryCache(this);
        }
    }
}
