package com.gsma.mobileconnect.r2.authentication;

import com.gsma.mobileconnect.r2.model.ErrorResponse;
import com.gsma.mobileconnect.r2.service.authentication.RequestTokenResponse;
import com.gsma.mobileconnect.r2.service.authentication.RequestTokenResponseData;
import com.gsma.mobileconnect.r2.utils.KeyValuePair;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.testng.Assert.*;

public class RequestTokenResponseTest
{
    private RequestTokenResponse requestTokenResponse;
    private ErrorResponse errorResponse;
    private List<KeyValuePair> headers;
    private RequestTokenResponseData requestTokenResponseData;

    @BeforeMethod
    public void setUp() throws Exception
    {
        errorResponse = new ErrorResponse.Builder()
            .withError("error")
            .withDescription("desc")
            .withErrorUri("errorUri")
            .build();

        headers = new ArrayList<KeyValuePair>();
        headers.add(new KeyValuePair("key1", "value1"));
        headers.add(new KeyValuePair("key2", "value2"));

        requestTokenResponseData = new RequestTokenResponseData.Builder()
            .withAccessToken("accessToken")
            .withExpiresIn(1000L)
            .withIdToken("idToken")
            .withRefreshToken("refreshToken")
            .withTokenType("tokenType")
            .withTimeReceived(Calendar.getInstance().getTime())
            .build();

        requestTokenResponse = new RequestTokenResponse.Builder()
            .withDecodedIdTokenPayload("decodedIdTokenPayload")
            .withErrorResponse(errorResponse)
            .withHeaders(headers)
            .withResponseCode(200)
            .withResponseData(requestTokenResponseData)
            .build();
    }

    @AfterMethod
    public void tearDown() throws Exception
    {
        errorResponse = null;
        headers = null;
        requestTokenResponseData = null;
        requestTokenResponse = null;
    }

    @Test
    public void testGetResponseCode() throws Exception
    {
        assertEquals(requestTokenResponse.getResponseCode(), 200);
    }

    @Test
    public void testGetHeaders() throws Exception
    {
        assertEquals(requestTokenResponse.getHeaders(), headers);
    }

    @Test
    public void testGetResponseData() throws Exception
    {
        assertEquals(requestTokenResponse.getResponseData(), requestTokenResponseData);
    }

    @Test
    public void testGetDecodedIdTokenPayload() throws Exception
    {
        assertEquals(requestTokenResponse.getDecodedIdTokenPayload(), "decodedIdTokenPayload");
    }

    @Test
    public void testGetErrorResponse() throws Exception
    {
        assertEquals(requestTokenResponse.getErrorResponse(), errorResponse);
    }

    @Test
    public void testIsTokenValidated() throws Exception
    {
        final RequestTokenResponse validatedTokenResponse =
            new RequestTokenResponse.Builder(requestTokenResponse).withTokenValidated(true).build();
        assertFalse(requestTokenResponse.isTokenValidated());
        assertTrue(validatedTokenResponse.isTokenValidated());
    }
}