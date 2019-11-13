package com.gsma.mobileconnect.r2.validation;

import com.gsma.mobileconnect.r2.service.validation.TokenValidationOptions;
import com.gsma.mobileconnect.r2.service.validation.TokenValidationResult;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @since 2.0
 */
public class TokenValidationOptionsTest
{
    @Test
    public void testGetAcceptedValidationResults() throws Exception
    {
        TokenValidationOptions tokenValidationOptions = new TokenValidationOptions.Builder()
            .withAcceptedValidationResults(TokenValidationResult.VALID)
            .build();

        assertEquals(tokenValidationOptions.getAcceptedValidationResults(),
            TokenValidationResult.VALID);
    }

}