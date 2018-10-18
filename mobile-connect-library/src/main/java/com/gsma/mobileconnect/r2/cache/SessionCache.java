package com.gsma.mobileconnect.r2.cache;

import com.gsma.mobileconnect.r2.discovery.SessionData;
import com.gsma.mobileconnect.r2.utils.ObjectUtils;

public class SessionCache extends ConcurrentCache {
    protected SessionCache(Builder builder) {
        super(builder);
    }

    public SessionData get(String key) {
        SessionData sessionData = null;
        if (!hasKey(key)) {
            return null;
        }
        try {
            sessionData = this.get(key, SessionData.class);
        } catch (CacheAccessException e) {
            e.printStackTrace();
        }
        this.remove(key);

//        if (sessionData.hasExpired()) {
//            this.remove(key);
//            return null;
//        }
        return sessionData;
    }

    @Override
    protected boolean hasKey(String key) {
        try {
            return this.get(key, SessionData.class) != null;
        } catch (CacheAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static final class Builder extends ConcurrentCache.Builder {

        @Override
        public ConcurrentCache build() {
            ObjectUtils.requireNonNull(this.jsonService, "jsonService");
            return new SessionCache(this);
        }
    }
}
