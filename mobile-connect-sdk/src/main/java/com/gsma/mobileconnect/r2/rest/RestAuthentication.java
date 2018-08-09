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
package com.gsma.mobileconnect.r2.rest;

import com.gsma.mobileconnect.r2.encoding.IMobileConnectEncodeDecoder;
import com.gsma.mobileconnect.r2.utils.LogUtils;
import com.gsma.mobileconnect.r2.utils.ObjectUtils;
import com.gsma.mobileconnect.r2.utils.StringUtils;

import java.io.UnsupportedEncodingException;

/**
 * Helper class for holding authentication values for calling rest endpoints using {@link
 * RestClient}.
 *
 * @since 2.0
 */
public class RestAuthentication
{
    private final Scheme scheme;
    private final String parameter;

    private RestAuthentication(final Scheme scheme, final String parameter)
    {
        this.scheme = ObjectUtils.requireNonNull(scheme, "schemeName");
        this.parameter = StringUtils.requireNonEmpty(parameter, "parameter");
    }

    /**
     * Createa a new instance of the RestAuthentication class for basic authentication.
     *
     * @param key    key/user value
     * @param secret to be encoded
     * @return a new instance of RestAuthentication configured for basic auth.
     */
    public static RestAuthentication basic(final String key, final String secret,
        IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder)
    {
        StringUtils.requireNonEmpty(key, "key");
        StringUtils.requireNonEmpty(secret, "secret");

        try
        {
            final byte[] authentication = String.format("%s:%s", key, secret).getBytes("UTF-8");
            final String encoded = iMobileConnectEncodeDecoder.encodeToBase64(authentication);

            return new RestAuthentication(Scheme.BASIC, encoded);
        }
        catch (final UnsupportedEncodingException uee)
        {
            throw new UnsupportedOperationException("Unable to decode UTF-8", uee);
        }
    }

    /**
     * Creates a new instance of RestAuthentication class for Bearer authentication.
     *
     * @param token bearer token.
     * @return new instance of RestAuthentication configured for bearer auth.
     */
    public static RestAuthentication bearer(final String token)
    {
        return new RestAuthentication(Scheme.BEARER, StringUtils.requireNonEmpty(token, "token"));
    }

    /**
     * @return the schemeName to be used.
     */
    public String getScheme()
    {
        return this.scheme.getSchemeName();
    }

    /**
     * @return return the authentication parameter.
     */
    public String getParameter()
    {
        return this.parameter;
    }

    @Override
    public String toString()
    {
        return "RestAuthentication(scheme=" + this.scheme + ", parameter=" + LogUtils.mask(
            this.parameter) + ")";
    }

    public enum Scheme
    {
        BASIC("Basic"), BEARER("Bearer");

        private final String schemeName;

        Scheme(final String schemeName)
        {
            this.schemeName = schemeName;
        }

        public String getSchemeName()
        {
            return this.schemeName;
        }
    }
}
