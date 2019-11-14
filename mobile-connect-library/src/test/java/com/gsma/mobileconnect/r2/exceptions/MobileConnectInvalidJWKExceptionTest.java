package com.gsma.mobileconnect.r2.exceptions;

import com.gsma.mobileconnect.r2.model.exceptions.MobileConnectInvalidJWKException;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @since 2.0
 */
public class MobileConnectInvalidJWKExceptionTest
{
    @Test
    public void testExceptionCreation()
    {
        MobileConnectInvalidJWKException exception =
            new MobileConnectInvalidJWKException("Error desc");
        Assert.assertNotNull(exception);
        Assert.assertEquals(exception.getMessage(), "Error desc");

        RuntimeException runtimeException = new RuntimeException("Test");
        MobileConnectInvalidJWKException exceptionThrowable =
            new MobileConnectInvalidJWKException("Error desc with throwable", runtimeException);
        Assert.assertNotNull(exceptionThrowable);
        Assert.assertEquals(exceptionThrowable.getMessage(), "Error desc with throwable");
        Assert.assertEquals(exceptionThrowable.getCause(), runtimeException);
    }
}