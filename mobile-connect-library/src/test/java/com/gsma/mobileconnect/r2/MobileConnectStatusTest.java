package com.gsma.mobileconnect.r2;

import com.gsma.mobileconnect.r2.authentication.RequestTokenResponse;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.identity.IdentityResponse;
import com.gsma.mobileconnect.r2.json.DiscoveryResponseData;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

public class MobileConnectStatusTest
{
    private MobileConnectStatus mobileConnectStatus;
    private MobileConnectStatus mobileConnectStatusCopy;
    private DiscoveryResponse discoveryResponse;
    private IdentityResponse identityResponse;
    private RequestTokenResponse requestTokenResponse;
    private Exception exception;
    private List<String> cookies;

    @BeforeMethod
    public void setUp() throws Exception
    {
        discoveryResponse = new DiscoveryResponse.Builder()
            .withResponseData(new DiscoveryResponseData.Builder().build())
            .build();
        identityResponse = new IdentityResponse.Builder().build();
        requestTokenResponse = new RequestTokenResponse.Builder().build();
        exception = new RuntimeException("Test");
        cookies = new ArrayList<String>();
        cookies.add("a=1");
        cookies.add("b=2");

        mobileConnectStatus = new MobileConnectStatus.Builder()
            .withDiscoveryResponse(discoveryResponse)
            .withErrorCode("errorCode")
            .withErrorMessage("errorMsg")
            .withException(exception)
            .withIdentityResponse(identityResponse)
            .withNonce("nonce")
            .withState("state")
            .withSdkSession("sdkSession")
            .withOutcome("outcome")
            .withUrl("url")
            .withRequestTokenResponse(requestTokenResponse)
            .withResponseType(MobileConnectStatus.ResponseType.COMPLETE)
            .withSetCookie(cookies)
            .build();

        mobileConnectStatusCopy = new MobileConnectStatus.Builder(mobileConnectStatus)
            .withResponseType(MobileConnectStatus.ResponseType.ERROR).build();
    }

    @AfterMethod
    public void tearDown() throws Exception
    {
        discoveryResponse = null;
        identityResponse = null;
        requestTokenResponse = null;
        exception = null;
        cookies = null;
        mobileConnectStatus = null;
    }

    @Test
    public void testWithSdkSession() throws Exception
    {
        MobileConnectStatus status = mobileConnectStatus.withSdkSession("sdkSession123");
        assertNotNull(status);
        assertEquals(status.getSdkSession(), "sdkSession123");
    }

    @Test
    public void testBuildWithNullStatus() throws Exception
    {
        MobileConnectStatus status = new MobileConnectStatus.Builder(null).withResponseType(
            MobileConnectStatus.ResponseType.START_DISCOVERY).build();
        assertNotNull(status);
        assertEquals(status.getResponseType(), MobileConnectStatus.ResponseType.START_DISCOVERY);
    }

    @Test
    public void testGetResponseType() throws Exception
    {
        assertEquals(mobileConnectStatus.getResponseType(),
            MobileConnectStatus.ResponseType.COMPLETE);
        assertEquals(mobileConnectStatusCopy.getResponseType(),
            MobileConnectStatus.ResponseType.ERROR);
    }

    @Test
    public void testGetErrorCode() throws Exception
    {
        assertEquals(mobileConnectStatus.getErrorCode(), "errorCode");
        assertEquals(mobileConnectStatusCopy.getErrorCode(), "errorCode");
    }

    @Test
    public void testGetErrorMessage() throws Exception
    {
        assertEquals(mobileConnectStatus.getErrorMessage(), "errorMsg");
        assertEquals(mobileConnectStatusCopy.getErrorMessage(), "errorMsg");
    }

    @Test
    public void testGetUrl() throws Exception
    {
        assertEquals(mobileConnectStatus.getUrl(), "url");
        assertEquals(mobileConnectStatusCopy.getUrl(), "url");
    }

    @Test
    public void testGetState() throws Exception
    {
        assertEquals(mobileConnectStatus.getState(), "state");
        assertEquals(mobileConnectStatusCopy.getState(), "state");
    }

    @Test
    public void testGetNonce() throws Exception
    {
        assertEquals(mobileConnectStatus.getNonce(), "nonce");
        assertEquals(mobileConnectStatusCopy.getNonce(), "nonce");
    }

    @Test
    public void testGetSetCookie() throws Exception
    {
        assertEquals(mobileConnectStatus.getSetCookie(), cookies);
        assertEquals(mobileConnectStatusCopy.getSetCookie(), cookies);
    }

    @Test
    public void testGetSdkSession() throws Exception
    {
        assertEquals(mobileConnectStatus.getSdkSession(), "sdkSession");
        assertEquals(mobileConnectStatusCopy.getSdkSession(), "sdkSession");
    }

    @Test
    public void testGetDiscoveryResponse() throws Exception
    {
        assertEquals(mobileConnectStatus.getDiscoveryResponse(), discoveryResponse);
        assertEquals(mobileConnectStatusCopy.getDiscoveryResponse(), discoveryResponse);
    }

    @Test
    public void testGetRequestTokenResponse() throws Exception
    {
        assertEquals(mobileConnectStatus.getRequestTokenResponse(), requestTokenResponse);
        assertEquals(mobileConnectStatusCopy.getRequestTokenResponse(), requestTokenResponse);
    }

    @Test
    public void testGetIdentityResponse() throws Exception
    {
        assertEquals(mobileConnectStatus.getIdentityResponse(), identityResponse);
        assertEquals(mobileConnectStatusCopy.getIdentityResponse(), identityResponse);
    }

    @Test
    public void testGetException() throws Exception
    {
        assertEquals(mobileConnectStatus.getException(), exception);
        assertEquals(mobileConnectStatusCopy.getException(), exception);
    }

    @Test
    public void testGetOutcome() throws Exception
    {
        assertEquals(mobileConnectStatus.getOutcome(), "outcome");
        assertEquals(mobileConnectStatusCopy.getOutcome(), "outcome");
    }
}