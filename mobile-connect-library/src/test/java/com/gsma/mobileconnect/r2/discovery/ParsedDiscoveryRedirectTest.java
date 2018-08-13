package com.gsma.mobileconnect.r2.discovery;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ParsedDiscoveryRedirectTest
{
    private ParsedDiscoveryRedirect parsedDiscoveryRedirect;

    @BeforeMethod
    public void setUp() throws Exception
    {
        parsedDiscoveryRedirect = new ParsedDiscoveryRedirect.Builder()
            .withSelectedMnc("mnc")
            .withSelectedMcc("mcc")
            .withEncryptedMsisdn("encryptedMsisdn")
            .build();
    }

    @AfterMethod
    public void tearDown() throws Exception
    {
        parsedDiscoveryRedirect = null;
    }
    @Test
    public void testGetSelectedMcc() throws Exception
    {
        assertEquals(parsedDiscoveryRedirect.getSelectedMcc(), "mcc");
    }

    @Test
    public void testGetSelectedMnc() throws Exception
    {
        assertEquals(parsedDiscoveryRedirect.getSelectedMnc(), "mnc");
    }

    @Test
    public void testGetEncryptedMsisdn() throws Exception
    {
        assertEquals(parsedDiscoveryRedirect.getEncryptedMsisdn(), "encryptedMsisdn");
    }

    @Test
    public void testHasMccAndMncBothPresent() throws Exception
    {
        assertTrue(parsedDiscoveryRedirect.hasMccAndMnc());
    }

    @Test
    public void testHasMccAndMncBothAbsent() throws Exception
    {
        final ParsedDiscoveryRedirect parsedDiscoveryRedirect = new ParsedDiscoveryRedirect.Builder()
            .withEncryptedMsisdn("encryptedMsisdn")
            .build();

        assertFalse(parsedDiscoveryRedirect.hasMccAndMnc());
    }

    @Test
    public void testHasMccAndMncOnlyMncPresent() throws Exception
    {
        final ParsedDiscoveryRedirect parsedDiscoveryRedirect = new ParsedDiscoveryRedirect.Builder()
            .withEncryptedMsisdn("encryptedMsisdn")
            .withSelectedMnc("mcn")
            .build();
        assertFalse(parsedDiscoveryRedirect.hasMccAndMnc());
    }

    @Test
    public void testHasMccAndMncOnlyMccPresent() throws Exception
    {
        final ParsedDiscoveryRedirect parsedDiscoveryRedirect = new ParsedDiscoveryRedirect.Builder()
            .withEncryptedMsisdn("encryptedMsisdn")
            .withSelectedMcc("mcc")
            .build();
        assertFalse(parsedDiscoveryRedirect.hasMccAndMnc());
    }

}