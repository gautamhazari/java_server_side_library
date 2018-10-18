package com.gsma.mobileconnect.r2.constants;

import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @since 2.0
 */
public class ScopesTest
{
    @Test
    public void testCoerceOpenIdScope() throws Exception
    {
        String[] scopes = String
            .format("%s %s", Scopes.MOBILE_CONNECT_IDENTITY_SIGNUP, Scopes.MOBILE_CONNECT_IDENTITY_PHONE)
            .split(" ");

        List<String> scopeList =
            Scopes.coerceOpenIdScope(Arrays.asList(scopes), DefaultOptions.AUTHENTICATION_SCOPE);

        assertEquals(scopeList.size(), 3);
        assertTrue(scopeList.contains("openid"));
        assertTrue(scopeList.contains("mc_identity_signup"));
        assertTrue(scopeList.contains("mc_identity_phonenumber"));
    }
}