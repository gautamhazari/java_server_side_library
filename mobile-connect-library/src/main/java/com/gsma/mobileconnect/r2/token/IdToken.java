package com.gsma.mobileconnect.r2.token;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.gsma.mobileconnect.r2.claims.ClaimsConstants;
import com.gsma.mobileconnect.r2.json.ListJsonAdapter;

import java.util.ArrayList;

public class IdToken {
    @SerializedName(ClaimsConstants.ISS)
    private Object iss;

    @SerializedName(ClaimsConstants.SUB)
    private Object sub;

    @SerializedName(ClaimsConstants.AUD)
    @JsonAdapter(ListJsonAdapter.class)
    private ArrayList<Object> aud;

    @SerializedName(ClaimsConstants.EXP)
    private Long exp;

    @SerializedName(ClaimsConstants.IAT)
    private Long iat;

    @SerializedName(ClaimsConstants.AUTH_TIME)
    private Object authTime;

    @SerializedName(ClaimsConstants.NONCE)
    private Object nonce;

    @SerializedName(ClaimsConstants.AT_HASH)
    private Object atHash;

    @SerializedName(ClaimsConstants.ACR)
    private Object acr;

    @SerializedName(ClaimsConstants.AMR)
    private Object amr;

    @SerializedName(ClaimsConstants.AZP)
    private Object azp;

    @SerializedName(ClaimsConstants.DISPLAYED_DATA)
    private Object displayedData;

    @SerializedName(ClaimsConstants.DTS)
    private Object dts;

    @SerializedName(ClaimsConstants.UPK)
    private Object upk;

    @SerializedName(ClaimsConstants.DTS_TIME)
    private Object dtsTime;

    @SerializedName(ClaimsConstants.HASHED_LOGIN_HINT)
    private Object hashedLoginHint;

    private IdToken(Builder builder) {
        this.iss = builder.iss;
        this.sub = builder.sub;
        this.aud = builder.aud;
        this.exp = builder.exp;
        this.iat = builder.iat;
        this.authTime = builder.authTime;
        this.nonce = builder.nonce;
        this.atHash = builder.atHash;
        this.acr = builder.acr;
        this.amr = builder.amr;
        this.azp = builder.azp;
        this.displayedData = builder.displayedData;
        this.dts = builder.dts;
        this.upk = builder.upk;
        this.dtsTime = builder.dtsTime;
        this.hashedLoginHint = builder.hashedLoginHint;
    }

    public Object getIss() {
        return iss;
    }

    public Object getSub() {
        return sub;
    }

    public ArrayList<Object> getAud() {
        return aud;
    }

    public Long getExp() {
        return exp;
    }

    public Long getIat() {
        return iat;
    }

    public Object getAuthTime() {
        return authTime;
    }

    public Object getNonce() {
        return nonce;
    }

    public Object getAtHash() {
        return atHash;
    }

    public Object getAcr() {
        return acr;
    }

    public Object getAmr() {
        return amr;
    }

    public Object getAzp() {
        return azp;
    }

    public Object getDisplayedData() {
        return displayedData;
    }

    public Object getDts() {
        return dts;
    }

    public Object getUpk() {
        return upk;
    }

    public Object getDtsTime() {
        return dtsTime;
    }

    public Object getHashedLoginHint() {
        return hashedLoginHint;
    }

    public static final class Builder {
        private Object iss;
        private Object sub;
        private ArrayList<Object> aud;
        private Long exp;
        private Long iat;
        private Object authTime;
        private Object nonce;
        private Object atHash;
        private Object acr;
        private Object amr;
        private Object azp;
        private Object displayedData;
        private Object dts;
        private Object upk;
        private Object dtsTime;
        private Object hashedLoginHint;

        public void withIss(Object iss) {
            this.iss = iss;
        }

        public void withSub(Object sub) {
            this.sub = sub;
        }

        public void withAud(ArrayList<Object> aud) {
            this.aud = aud;
        }

        public void withExp(Long exp) {
            this.exp = exp;
        }

        public void withIat(Long iat) {
            this.iat = iat;
        }

        public void withAuthTime(Object authTime) {
            this.authTime = authTime;
        }

        public void withNonce(Object nonce) {
            this.nonce = nonce;
        }

        public void withAtHash(Object atHash) {
            this.atHash = atHash;
        }

        public void withAcr(Object acr) {
            this.acr = acr;
        }

        public void withAmr(Object amr) {
            this.amr = amr;
        }

        public void withAzp(Object azp) {
            this.azp = azp;
        }

        public void withDisplayedData(Object displayedData) {
            this.displayedData = displayedData;
        }

        public void withDts(Object dts) {
            this.dts = dts;
        }

        public void withUpk(Object upk) {
            this.upk = upk;
        }

        public void withDtsTime(Object dtsTime) {
            this.dtsTime = dtsTime;
        }

        public void withHashedLoginHint(Object hashedLoginHint) {
            this.hashedLoginHint = hashedLoginHint;
        }

        public IdToken build()
        {
            return new IdToken(this);
        }
    }
}
