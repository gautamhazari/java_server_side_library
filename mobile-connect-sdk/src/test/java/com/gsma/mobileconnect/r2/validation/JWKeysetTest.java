package com.gsma.mobileconnect.r2.validation;

import com.gsma.mobileconnect.r2.json.JacksonJsonService;
import com.gsma.mobileconnect.r2.utils.Predicate;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Iterator;

import static org.testng.Assert.*;

/**
 * @since 2.0
 */
public class JWKeysetTest
{
    private JacksonJsonService jacksonJsonService;

    @BeforeClass
    public void setUp()
    {
        jacksonJsonService = new JacksonJsonService();
    }

    @Test
    public void testGetKeys() throws Exception
    {
        final String jwksJson =
            "{\"keys\":[{\"alg\":\"RS256\",\"e\":\"AQAB\",\"n\":\"hzr2li5ABVbbQ4BvdDskl6hejaVw0tIDYO\",\"kty\":\"RSA\",\"use\":\"sig\"}]}";
        final JWKeyset jwKeyset = jacksonJsonService.deserialize(jwksJson, JWKeyset.class);

        assertEquals(jwKeyset.getKeys().size(), 1);

        final String jwksJsonEmpty = "";
        final JWKeyset jwKeysetEmpty =
            jacksonJsonService.deserialize(jwksJsonEmpty, JWKeyset.class);

        assertNull(jwKeysetEmpty);
    }

    @Test
    public void testGetMatchingWithSingleMatching() throws Exception
    {
        final String jwksJson =
            "{\"keys\":[{\"alg\":\"RS256\",\"e\":\"AQAB\",\"n\":\"hzr2li5ABVbbQ4BvdDskl6hejaVw0tIDYO\",\"kty\":\"RSA\",\"use\":\"sig\"}]}";
        final JWKeyset jwKeyset = jacksonJsonService.deserialize(jwksJson, JWKeyset.class);

        //predicate to get RSA keys only
        Predicate<JWKey> predicate = new Predicate<JWKey>()
        {
            @Override
            public boolean apply(JWKey input)
            {
                return input.isRsa();
            }
        };

        Iterator<JWKey> jwKeysetMatching = jwKeyset.getMatching(predicate).iterator();
        assertTrue(jwKeysetMatching.hasNext());
        // should assert what next is actually going to be
        jwKeysetMatching.next();
        assertFalse(jwKeysetMatching.hasNext());
    }

    @Test
    public void testGetMatchingWithNoMatching() throws Exception
    {
        //predicate to get RSA keys only
        Predicate<JWKey> predicate = new Predicate<JWKey>()
        {
            @Override
            public boolean apply(JWKey input)
            {
                return input.isRsa();
            }
        };

        final String jwksJsonMac =
            "{\"keys\":[{\"alg\":\"RS256\",\"e\":\"AQAB\",\"n\":\"hzr2li5ABVbbQ4BvdDskl6hejaVw0tIDYO\",\"kty\":\"OCT\",\"use\":\"sig\"}]}";
        final JWKeyset jwKeysetMac = jacksonJsonService.deserialize(jwksJsonMac, JWKeyset.class);

        Iterator<JWKey> jwKeysetMatchingMac = jwKeysetMac.getMatching(predicate).iterator();
        assertFalse(jwKeysetMatchingMac.hasNext());

        final String jwksJsonEmpty = "";
        final JWKeyset jwKeysetEmpty =
            jacksonJsonService.deserialize(jwksJsonEmpty, JWKeyset.class);

        assertNull(jwKeysetEmpty);
    }

    @Test
    public void testGetMatchingWithNullPredicateShouldReturnAllKeys() throws Exception
    {
        final String jwksJson =
            "{\"keys\":[{\"alg\":\"RS256\",\"e\":\"AQAB\",\"n\":\"hzr2li5ABVbbQ4BvdDskl6hejaVw0tIDYO\",\"kty\":\"OCT\",\"use\":\"sig\"}]}";
        final JWKeyset jwKeyset = jacksonJsonService.deserialize(jwksJson, JWKeyset.class);

        Iterator<JWKey> jwKeysetMatching = jwKeyset.getMatching(null).iterator();
        assertTrue(jwKeysetMatching.hasNext());
        // should assert what next is actually going to be
        jwKeysetMatching.next();
        assertFalse(jwKeysetMatching.hasNext());
    }
}