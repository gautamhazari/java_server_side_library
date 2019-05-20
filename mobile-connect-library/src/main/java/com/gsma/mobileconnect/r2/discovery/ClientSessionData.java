package com.gsma.mobileconnect.r2.discovery;

import com.gsma.mobileconnect.r2.cache.AbstractCacheable;

public class ClientSessionData extends AbstractCacheable {
    private String scope;
    private String version;

    public ClientSessionData(String scope, String version) {
        this.scope = scope;
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
