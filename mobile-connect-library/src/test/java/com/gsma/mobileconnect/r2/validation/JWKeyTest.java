package com.gsma.mobileconnect.r2.validation;

import com.gsma.mobileconnect.r2.json.JacksonJsonService;
import com.gsma.mobileconnect.r2.json.JsonDeserializationException;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @since 2.0
 */
public class JWKeyTest
{
    @Test
    public void testIsSymmetric() throws JsonDeserializationException
    {
        final JacksonJsonService jacksonJsonService = new JacksonJsonService();

        final String jwkJsonHmac =
            "{\"alg\":\"RS256\",\"e\":\"AQAB\",\"n\":\"hzr2li5ABVbbQ4BvdDskl6hejaVw0tIDYO\",\"kty\":\"OCT\",\"use\":\"sig\"}";
        final JWKey jwKeyHmac = jacksonJsonService.deserialize(jwkJsonHmac, JWKey.class);

        assertTrue(jwKeyHmac.isSymmetric());

        final String jwkRsaJson =
            "{\"alg\":\"RS256\",\"e\":\"AQAB\",\"n\":\"hzr2li5ABVbbQ4BvdDskl6hejaVw0tIDYO\",\"kty\":\"RSA\",\"use\":\"sig\"}";
        final JWKey jwKeyRsa = jacksonJsonService.deserialize(jwkRsaJson, JWKey.class);

        assertFalse(jwKeyRsa.isSymmetric());
    }

    @Test
    public void testIsEcc() throws JsonDeserializationException
    {
        final JacksonJsonService jacksonJsonService = new JacksonJsonService();

        final String jwkJsonHmac =
            "{\"alg\":\"RS256\",\"e\":\"AQAB\",\"n\":\"hzr2li5ABVbbQ4BvdDskl6hejaVw0tIDYO\",\"kty\":\"EC\",\"use\":\"sig\"}";
        final JWKey jwKeyHmac = jacksonJsonService.deserialize(jwkJsonHmac, JWKey.class);

        assertTrue(jwKeyHmac.isEcc());

        final String jwkRsaJson =
            "{\"alg\":\"RS256\",\"e\":\"AQAB\",\"n\":\"hzr2li5ABVbbQ4BvdDskl6hejaVw0tIDYO\",\"kty\":\"RSA\",\"use\":\"sig\"}";
        final JWKey jwKeyRsa = jacksonJsonService.deserialize(jwkRsaJson, JWKey.class);

        assertFalse(jwKeyRsa.isEcc());
    }

    @Test
    public void testIsRsa() throws JsonDeserializationException
    {
        final JacksonJsonService jacksonJsonService = new JacksonJsonService();
        final String jwkRsaJson =
            "{\"alg\":\"RS256\",\"e\":\"AQAB\",\"n\":\"hzr2li5ABVbbQ4BvdDskl6hejaVw0tIDYO\",\"kty\":\"RSA\",\"use\":\"sig\"}";
        final JWKey jwKeyRsa = jacksonJsonService.deserialize(jwkRsaJson, JWKey.class);

        assertTrue(jwKeyRsa.isRsa());

        final String jwkJsonHmac =
            "{\"alg\":\"RS256\",\"e\":\"AQAB\",\"n\":\"hzr2li5ABVbbQ4BvdDskl6hejaVw0tIDYO\",\"kty\":\"OCT\",\"use\":\"sig\"}";
        final JWKey jwKeyHmac = jacksonJsonService.deserialize(jwkJsonHmac, JWKey.class);

        assertFalse(jwKeyHmac.isRsa());
    }

}