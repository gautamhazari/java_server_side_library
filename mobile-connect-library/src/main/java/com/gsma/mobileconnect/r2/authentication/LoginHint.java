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
import com.gsma.mobileconnect.r2.discovery.ProviderMetadata;
import com.gsma.mobileconnect.r2.discovery.SupportedVersions;
import com.gsma.mobileconnect.r2.utils.ListUtils;
import com.gsma.mobileconnect.r2.utils.Predicate;
import com.gsma.mobileconnect.r2.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for working with login hints for the auth login hint parameter
 *
 * @since 2.0
 */
@SuppressWarnings("WeakerAccess")
public class LoginHint
{
    private static final SupportedVersions DEFAULT_VERSIONS = new SupportedVersions.Builder().build();
    private static final List<String> RECOGNISED_HINTS = new ArrayList<String>();

    static
    {
        RECOGNISED_HINTS.add(LoginHintPrefixes.MSISDN.getName());
        RECOGNISED_HINTS.add(LoginHintPrefixes.ENCRYPTED_MSISDN.getName());
        RECOGNISED_HINTS.add(LoginHintPrefixes.PCR.getName());
    }

    private LoginHint() {
        /*
        Empty Private Constructor since all methods are static
         */
    }

    /**
     * Is login hint with MSISDN supported by the target provider
     * @param providerMetadata Provider Metadata received during the discovery phase
     * @return True if format MSISDN:xxxxxxxxxx is supported
     */
    public static boolean isSupportedForMsisdn(final ProviderMetadata providerMetadata)
    {
        return isSupportedFor(providerMetadata, LoginHintPrefixes.MSISDN.getName());
    }

    /**
     * Is login hint with Encrypted MSISDN (SubscriberId) supported by the target provider
     * @param providerMetadata Provider Metadata received during the discovery phase
     * @return True if format ENCRYPTED_MSISDN:xxxxxxxxxx is supported
     */
    public static boolean isSupportedForEncryptedMsisdn(final ProviderMetadata providerMetadata)
    {
        return isSupportedFor(providerMetadata, LoginHintPrefixes.ENCRYPTED_MSISDN.getName());
    }

    /**
     * Is login hint with PCR (Pseudonymous Customer Reference) supported by the target provider
     * @param providerMetadata Provider Metadata received during the discovery phase
     * @return True if format PCR:xxxxxxxxxx is supported
     */
    public static boolean isSupportedForPcr(final ProviderMetadata providerMetadata)
    {
        return isSupportedFor(providerMetadata, LoginHintPrefixes.PCR.getName());
    }

    /**
     * Is login hint with specified prefix supported by the target provider
     * @param providerMetadata Provider Metadata received during the discovery phase
     * @param prefix Prefix to check for login hint support
     * @return True if format ${prefix}:xxxxxxxxxx is supported
     */
    public static boolean isSupportedFor(final ProviderMetadata providerMetadata, final String prefix)
    {
        if (providerMetadata == null ||
            providerMetadata.getLoginHintMethodsSupported() == null ||
            providerMetadata.getLoginHintMethodsSupported().isEmpty())
        {
            final SupportedVersions supportedVersions = getSupportedVersions(providerMetadata);

            // if not a recognised prefix, then it is not supported if no data to state it is supported
            if (getFirstMatch(RECOGNISED_HINTS, prefix) == null)
            {
                return false;
            }

            // If we are on 1.2 or greater then currently all recognised prefixes are assumed supported
            if (supportedVersions.isVersionSupported("1.2"))
            {
                return true;
            }

            // If we aren't at 1.2 or greater then we must be on 1.1 and therefore only MSISDN and encrypted are supported
            return !(!LoginHintPrefixes.ENCRYPTED_MSISDN.getName().equalsIgnoreCase(prefix)
                && !LoginHintPrefixes.MSISDN.getName().equalsIgnoreCase(prefix));

        }
        return getFirstMatch(providerMetadata.getLoginHintMethodsSupported(), prefix) != null;
    }

    private static SupportedVersions getSupportedVersions(final ProviderMetadata providerMetadata)
    {
        return (providerMetadata != null)
               ? providerMetadata.getMobileConnectVersionSupported()
               : DEFAULT_VERSIONS;
    }

    private static String getFirstMatch(final List<String> list, final String searchFor)
    {
        return ListUtils.firstMatch(list, new Predicate<String>()
        {
            @Override
            public boolean apply(final String input)
            {
                return input.equalsIgnoreCase(searchFor);
            }
        });
    }

    /**
     * Generates login hint for MSISDN value
     * @param msisdn MSISDN value
     * @return Correctly formatted login hint parameter for MSISDN
     */
    public static String generateForMsisdn(final String msisdn)
    {
        return generateFor(LoginHintPrefixes.MSISDN.getName(), msisdn.replaceAll("\\+",""));
    }

    /**
     * Generates login hint for Encrypted MSISDN (SubscriberId) value
     * @param encryptedMsisdn Encrypted MSISDN value
     * @return Correctly formatted login hint parameter for Encrypted MSISDN
     */
    public static String generateForEncryptedMsisdn(final String encryptedMsisdn)
    {
        return generateFor(LoginHintPrefixes.ENCRYPTED_MSISDN.getName(), encryptedMsisdn);
    }

    /**
     * Generates login hint for PCR (Pseudonymous Customer Reference) value
     * @param pcr PCR (Pseudonymous Customer Reference)
     * @return Correctly formatted login hint parameter for PCR (Pseudonymous Customer Reference)
     */
    public static String generateForPcr(final String pcr)
    {
        return generateFor(LoginHintPrefixes.PCR.getName(), pcr);
    }

    /**
     * Generates a login hint for the specified prefix with the specified value.
     * This method will not check that the prefix is recognised or supported, it is assumed that it is supported.
     * @param prefix Prefix to use
     * @param value Value to use
     * @return Correctly formatted login hint for prefix and value
     */
    public static String generateFor(final String prefix, final String value)
    {
        if (StringUtils.isNullOrEmpty(prefix) || StringUtils.isNullOrEmpty(value))
        {
            return null;
        }
        return String.format("%s:%s", prefix, value);
    }
}
