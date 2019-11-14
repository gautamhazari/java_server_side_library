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
package com.gsma.mobileconnect.r2;

import com.gsma.mobileconnect.r2.model.constants.DefaultOptions;
import com.gsma.mobileconnect.r2.model.constants.Scopes;
import com.gsma.mobileconnect.r2.utils.ObjectUtils;
import com.gsma.mobileconnect.r2.utils.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Class with static helpers relating to the difference in Mobile Connect Service versions.
 *
 * @since 2.0
 */
public final class MobileConnectVersions
{
    /**
     * Immutable map of scopes mapped to versions.
     */
    public static final Map<String, String> DEFAULT_SUPPORTED_VERSIONS;

    static
    {
        final Map<String, String> versions = new HashMap<String, String>();
        versions.put(Scopes.MOBILE_CONNECT, DefaultOptions.VERSION_MOBILECONNECT);
        versions.put(Scopes.MOBILE_CONNECT_AUTHENTICATION, DefaultOptions.VERSION_MOBILECONNECTAUTHN);
        versions.put(Scopes.MOBILE_CONNECT_AUTHORIZATION, DefaultOptions.VERSION_MOBILECONNECTAUTHZ);
        versions.put(Scopes.MOBILE_CONNECT_IDENTITY_NATIONALID,
            DefaultOptions.VERSION_MOBILECONNECTIDENTITY);
        versions.put(Scopes.MOBILE_CONNECT_IDENTITY_PHONE,
            DefaultOptions.VERSION_MOBILECONNECTIDENTITY);
        versions.put(Scopes.MOBILE_CONNECT_IDENTITY_SIGNUP,
            DefaultOptions.VERSION_MOBILECONNECTIDENTITY);
        versions.put(Scopes.MOBILE_CONNECT_IDENTITY_SIGNUP_PLUS,
            DefaultOptions.VERSION_MOBILECONNECTIDENTITY);

        DEFAULT_SUPPORTED_VERSIONS = Collections.unmodifiableMap(versions);
    }

    private MobileConnectVersions()
    {
    }

    /**
     * Coerces a version to the valid default for that version if null or empty.
     *
     * @param version to coerce if required.
     * @param scope   scope to use for retrieving default value.
     * @return a coerced version value.
     */
    public static String coerceVersion(final String version, final String scope)
    {
        ObjectUtils.requireNonNull(scope, "scope");

        String retval = version;

        if (StringUtils.isNullOrEmpty(version))
        {
            retval = ObjectUtils.defaultIfNull(DEFAULT_SUPPORTED_VERSIONS.get(scope),
                DEFAULT_SUPPORTED_VERSIONS.get(Scopes.MOBILE_CONNECT));
        }

        return retval;
    }
}
