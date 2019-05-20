package com.gsma.mobileconnect.r2.cache;

import com.gsma.mobileconnect.r2.discovery.ClientSessionData;
import com.gsma.mobileconnect.r2.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScopeSessionCache extends ConcurrentCache {
    protected ScopeSessionCache(Builder builder) {
        super(builder);
    }
    private static final Logger LOGGER = LoggerFactory.getLogger(ScopeSessionCache.class);

    public ClientSessionData get(String key) {
        ClientSessionData scope = null;
        if (!hasKey(key)) {
            return null;
        }
        try {
            scope = this.get(key, ClientSessionData.class);
        } catch (CacheAccessException e) {
            LOGGER.warn(e.getMessage());
        }
//        this.remove(key);

        return scope;
    }

    @Override
    protected boolean hasKey(String key) {
        try {
            return this.get(key, ClientSessionData.class) != null;
        } catch (CacheAccessException e) {
            LOGGER.warn(e.getMessage());
            return false;
        }
    }

    public static final class Builder extends ConcurrentCache.Builder {

        @Override
        public ConcurrentCache build() {
            ObjectUtils.requireNonNull(this.jsonService, "jsonService");
            return new ScopeSessionCache(this);
        }
    }
}
