package com.gsma.mobileconnect.r2;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ErrorResponseTest
{
    private ErrorResponse errorResponse;
    private String error = "error";
    private String errorDesc = "errorDesc";
    private String desc = "desc";
    private String errorUri = "errorUri";

    @BeforeMethod
    public void setUp() throws Exception
    {
        errorResponse = new ErrorResponse.Builder()
            .withError(error)
            .withErrorDescription(errorDesc)
            .withErrorUri(errorUri)
            .build();
    }

    @AfterMethod
    public void tearDown() throws Exception
    {
        errorResponse = null;
    }

    @Test
    public void testGetError() throws Exception
    {
        assertEquals(errorResponse.getError(), error);
    }

    @Test
    public void testGetErrorDescription() throws Exception
    {
        assertEquals(errorResponse.getErrorDescription(), errorDesc);
    }

    @Test
    public void testDescription() throws Exception
    {
        ErrorResponse response = new ErrorResponse.Builder()
            .withError(error)
            .withDescription(desc)
            .withErrorDescription(null)
            .build();
        assertEquals(response.getErrorDescription(), desc);
    }

    @Test
    public void testGetErrorUri() throws Exception
    {
        assertEquals(errorResponse.getErrorUri(), errorUri);
    }

    @Test
    public void testToString() throws Exception
    {
        assertNotNull(errorResponse.toString());
    }

}