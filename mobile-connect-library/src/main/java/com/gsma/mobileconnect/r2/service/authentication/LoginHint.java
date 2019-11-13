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
package com.gsma.mobileconnect.r2.service.authentication;

import com.gsma.mobileconnect.r2.model.constants.LoginHintPrefixes;
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
