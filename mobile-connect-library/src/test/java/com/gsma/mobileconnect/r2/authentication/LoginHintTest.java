/*
* SOFTWARE USE PERMISSION
*
* By downloading and accessing this software and associated documentation files ("Software") you are granted the
* unrestricted right to deal in the Software, including, without limitation the right to use, copy, modify, publish,
* sublicense and grant such rights to third parties, subject to the following conditions:
*
* The following copyright notice and this permission notice shall be included in all copies, modifications or
* substantial portions of this Software: Copyright © 2016 GSM Association.
*
* THE SOFTWARE IS PROVIDED "AS IS," WITHOUT WARRANTY OF ANY KIND, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
* MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
* ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. YOU AGREE TO
* INDEMNIFY AND HOLD HARMLESS THE AUTHORS AND COPYRIGHT HOLDERS FROM AND AGAINST ANY SUCH LIABILITY.
*/
package com.gsma.mobileconnect.r2.authentication;

import com.gsma.mobileconnect.r2.constants.LoginHintPrefixes;
import com.gsma.mobileconnect.r2.constants.Scopes;
import com.gsma.mobileconnect.r2.discovery.ProviderMetadata;
import com.gsma.mobileconnect.r2.discovery.SupportedVersions;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.testng.Assert.*;

/**
 * @since 2.0
 */
public class LoginHintTest
{
    @Test
    public void isSupportedMSISDNShouldReturnTrueIfMSISDNIncluded()
    {
        final ProviderMetadata providerMetadata = new ProviderMetadata.Builder()
            .withLoginHintMethodsSupported(new ArrayList<String>(){{
                add(LoginHintPrefixes.MSISDN.getName());
            }})
            .build();

        final boolean supportedForMsisdn = LoginHint.isSupportedForMsisdn(providerMetadata);

        assertTrue(supportedForMsisdn);
    }

    @Test
    public void isSupportedMSISDNShouldReturnFalseIfMSISDNNotIncluded()
    {
        final ProviderMetadata providerMetadata = new ProviderMetadata.Builder()
            .withLoginHintMethodsSupported(new ArrayList<String>(){{
                add(LoginHintPrefixes.PCR.getName());
            }})
            .build();

        final boolean supportedForMsisdn = LoginHint.isSupportedForMsisdn(providerMetadata);

        assertFalse(supportedForMsisdn);
    }

    @Test
    public void isSupportedEncryptedMSISDNShouldReturnTrueIfEncryptedMSISDNIncluded()
    {
        final ProviderMetadata providerMetadata = new ProviderMetadata.Builder()
            .withLoginHintMethodsSupported(new ArrayList<String>(){{
                add(LoginHintPrefixes.ENCRYPTED_MSISDN.getName());
            }})
            .build();

        final boolean supportedForEncryptedMsisdn = LoginHint.isSupportedForEncryptedMsisdn(providerMetadata);

        assertTrue(supportedForEncryptedMsisdn);
    }

    @Test
    public void isSupportedEncryptedMSISDNShouldReturnFalseIfEncryptedMSISDNNotIncluded()
    {
        final ProviderMetadata providerMetadata = new ProviderMetadata.Builder()
            .withLoginHintMethodsSupported(new ArrayList<String>(){{
                add(LoginHintPrefixes.PCR.getName());
            }})
            .build();

        final boolean supportedForEncryptedMsisdn = LoginHint.isSupportedForEncryptedMsisdn(providerMetadata);

        assertFalse(supportedForEncryptedMsisdn);
    }

    @Test
    public void isSupportedPCRShouldReturnTrueIfPCRIncluded()
    {
        final ProviderMetadata providerMetadata = new ProviderMetadata.Builder()
            .withLoginHintMethodsSupported(new ArrayList<String>(){{
                add(LoginHintPrefixes.PCR.getName());
            }})
            .build();

        final boolean supportedForPcr = LoginHint.isSupportedForPcr(providerMetadata);

        assertTrue(supportedForPcr);
    }

    @Test
    public void isSupportedPCRShouldReturnFalseIfPCRNotIncluded()
    {
        final ProviderMetadata providerMetadata = new ProviderMetadata.Builder()
            .withLoginHintMethodsSupported(new ArrayList<String>(){{
                add(LoginHintPrefixes.MSISDN.getName());
            }})
            .build();

        final boolean supportedForPcr = LoginHint.isSupportedForPcr(providerMetadata);

        assertFalse(supportedForPcr);
    }

    @Test
    public void isSupportedMSISDNShouldReturnTrueIfMissingMetadata()
    {
        final boolean supportedForMsisdn = LoginHint.isSupportedForMsisdn(null);

        assertTrue(supportedForMsisdn);
    }

    @Test
    public void isSupportedEncryptedMSISDNShouldReturnTrueIfMissingMetadata()
    {
        final boolean supportedForEncryptedMsisdn = LoginHint.isSupportedForEncryptedMsisdn(null);

        assertTrue(supportedForEncryptedMsisdn);
    }

    @Test
    public void isSupportedPCRShouldReturnTrueIfMissingMetadata()
    {
        final boolean supportedForPcr = LoginHint.isSupportedForPcr(null);

        assertTrue(supportedForPcr);
    }

    @Test
    public void isSupportedPCRShouldReturnTrueIfSupportedVersionIs1_2()
    {
        final ProviderMetadata providerMetadata = new ProviderMetadata.Builder()
            .withMobileConnectVersionSupported(new SupportedVersions.Builder()
                .addSupportedVersion(Scopes.MOBILECONNECT, "mc_v1.2")
                .build())
            .withLoginHintMethodsSupported(null)
            .build();

        final boolean supportedForPcr = LoginHint.isSupportedForPcr(providerMetadata);

        assertTrue(supportedForPcr);
    }

    @Test
    public void isSupportedForShouldReturnFalseIfUnrecognisedPrefixAndMissingMetadata()
    {
        final boolean supportedFor = LoginHint.isSupportedFor(null, "testprefix");

        assertFalse(supportedFor);
    }

    @Test
    public void isSupportedForShouldBeCaseInsensitive()
    {
        final ProviderMetadata providerMetadata = new ProviderMetadata.Builder()
            .withLoginHintMethodsSupported(new ArrayList<String>(){{
                add(LoginHintPrefixes.MSISDN.getName());
            }})
            .build();

        final boolean supportedForMsisdn = LoginHint.isSupportedFor(providerMetadata, "msiSDN");

        assertTrue(supportedForMsisdn);
    }

    @Test
    public void generateForMSISDNShouldGenerateCorrectFormat()
    {
        final String msisdnLoginHint = LoginHint.generateForMsisdn("+447700900250");

        assertEquals(msisdnLoginHint, "MSISDN:447700900250");
    }

    @Test
    public void generateForEncryptedMSISDNShouldGenerateCorrectFormat()
    {
        final String encryptedMsisdnLoginHint = LoginHint.generateForEncryptedMsisdn("zmalqpwoeirutyfhdjskaslxzmxncbv");

        assertEquals(encryptedMsisdnLoginHint, "ENCR_MSISDN:zmalqpwoeirutyfhdjskaslxzmxncbv");
    }

    @Test
    public void generateForPCRShouldGenerateCorrectFormat()
    {
        final String pcrLoginHint = LoginHint.generateForPcr("zmalqpwoeirutyfhdjskaslxzmxncbv");

        assertEquals(pcrLoginHint, "PCR:zmalqpwoeirutyfhdjskaslxzmxncbv");
    }

    @Test
    public void generateForShouldReturnNullWhenValueNull()
    {
        assertNull(LoginHint.generateFor(LoginHintPrefixes.PCR.getName(), null));
    }

    @Test
    public void generateForShouldReturnNullWhenValueEmpty()
    {
        assertNull(LoginHint.generateFor(LoginHintPrefixes.PCR.getName(), ""));
    }

    @Test
    public void generateForShouldReturnNullWhenPrefixNull()
    {
        assertNull(LoginHint.generateFor(null, "testValue"));
    }

    @Test
    public void generateForShouldReturnNullWhenPrefixEmpty()
    {
        assertNull(LoginHint.generateFor("", "testValue"));
    }
}
