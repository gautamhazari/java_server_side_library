package com.gsma.mobileconnect.r2.web.rest;

import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.model.exceptions.HeadlessOperationFailedException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Tests {@link HeadlessOperationFailedException}
 *
 * @since 2.0
 */
public class HeadlessOperationFailedExceptionTest
{
    private HeadlessOperationFailedException exception;
    private String errorMsg = "error message";

    @BeforeTest
    public void init()
    {
        exception = new HeadlessOperationFailedException(errorMsg);
    }

    @AfterTest
    public void teardown()
    {
        exception = null;
    }

    @Test
    public void testGetMesage() throws Exception
    {
        assertEquals(exception.getMesage(), errorMsg, "Check error message");
    }

    @Test
    public void testToMobileConnectStatus() throws Exception
    {
        String task = "auth";
        MobileConnectStatus mobileConnectStatus = exception.toMobileConnectStatus(task);

        assertEquals(mobileConnectStatus.getErrorCode(), "http_failure", "Check error code");
        assertEquals(mobileConnectStatus.getErrorMessage(),
            String.format("%s headless operation either had too many redirects or it timed out",
                task), "Check error message");
    }

}