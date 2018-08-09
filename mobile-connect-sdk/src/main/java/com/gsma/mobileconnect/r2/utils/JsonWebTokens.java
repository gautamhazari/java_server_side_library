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
package com.gsma.mobileconnect.r2.utils;

import com.gsma.mobileconnect.r2.encoding.IMobileConnectEncodeDecoder;

import java.nio.charset.Charset;

/**
 * Helper class for interacting with JSON web tokens.
 *
 * @since 2.0
 */
public final class JsonWebTokens
{
    private static final String FIELD_SEPARATOR_REGEX = "\\.";

    private JsonWebTokens()
    {
    }

    /**
     * Check if the token is in the valid JWT format.
     *
     * @param token to check
     * @return true if the token contains three parts split by commas.  The last part may be empty.
     */
    public static boolean isValidFormat(final String token)
    {
        StringUtils.requireNonEmpty(token, "token");

        return token.split(FIELD_SEPARATOR_REGEX).length == Part.values().length;
    }

    /**
     * Defines the parts of a JWT token, and mechanism for decoding them.
     */
    public enum Part
    {
        /**
         * First part of the JWT containing information about the algorithm and token type.
         */
        HEADER(0),

        /**
         * Second part of the JWT containing data and required claims.
         */
        CLAIMS(1),

        /**
         * Third part of the JWT use to verify the token authenticity.
         */
        SIGNATURE(2);

        final int index;

        Part(final int index)
        {
            this.index = index;
        }

        /**
         * Decode the specified part of the token.
         *
         * @param token to extract part from.
         * @param iMobileConnectEncodeDecoder
         * @return decoded part.
         */
        public String decode(final String token, IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder)
        {
            StringUtils.requireNonEmpty(token, "token");

            final String part = token.split(FIELD_SEPARATOR_REGEX)[this.index];

            if (this == SIGNATURE || StringUtils.isNullOrEmpty(part))
            {
                return token;
            }
            else
            {
                String base64 = part.replace('-', '+').replace('_', '/');
                final int padding = 4 - (base64.length() % 4);
                final StringBuilder builder = new StringBuilder(base64);
                for (int i = 0; i < padding; i++)
                {
                    builder.append("=");
                }
                final byte[] decoded = iMobileConnectEncodeDecoder.decodeFromBase64(builder.toString());
                return new String(decoded, Charset.forName("UTF-8"));
            }
        }
    }
}
