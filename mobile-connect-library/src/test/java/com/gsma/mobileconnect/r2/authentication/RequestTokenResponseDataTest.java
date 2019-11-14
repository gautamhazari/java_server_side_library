package com.gsma.mobileconnect.r2.authentication;

import com.gsma.mobileconnect.r2.service.authentication.RequestTokenResponseData;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

/**
 * @since 2.0
 */
public class RequestTokenResponseDataTest
{
    private RequestTokenResponseData requestTokenResponseData;
    private Date timeReceived;
    private Date expiry;
    private Long expiresIn;

    @BeforeMethod
    public void setUp() throws Exception
    {
        expiresIn = 1000L;

        timeReceived = Calendar.getInstance().getTime();

        expiry = new Date(this.timeReceived.getTime() + TimeUnit.SECONDS.toMillis(expiresIn));

        requestTokenResponseData = new RequestTokenResponseData.Builder()
            .withAccessToken("accessToken")
            .withExpiresIn(expiresIn)
            .withIdToken("idToken")
            .withRefreshToken("refreshToken")
            .withTokenType("tokenType")
            .withTimeReceived(timeReceived)
            .build();
    }

    @AfterMethod
    public void tearDown() throws Exception
    {
        requestTokenResponseData = null;
    }

    @Test
    public void testGetTimeReceived() throws Exception
    {
        assertEquals(requestTokenResponseData.getTimeReceived(), timeReceived);
    }

    @Test
    public void testGetAccessToken() throws Exception
    {
        assertEquals(requestTokenResponseData.getAccessToken(), "accessToken");
    }

    @Test
    public void testGetTokenType() throws Exception
    {
        assertEquals(requestTokenResponseData.getTokenType(), "tokenType");
    }

    @Test
    public void testGetIdToken() throws Exception
    {
        assertEquals(requestTokenResponseData.getIdToken(), "idToken");
    }

    @Test
    public void testGetRefreshToken() throws Exception
    {
        assertEquals(requestTokenResponseData.getRefreshToken(), "refreshToken");
    }

    @Test
    public void testGetExpiry() throws Exception
    {
        assertEquals(requestTokenResponseData.getExpiry(), expiry);
    }

    @Test
    public void testGetExpiresIn() throws Exception
    {
        assertEquals(requestTokenResponseData.getExpiresIn(), expiresIn);
    }

}