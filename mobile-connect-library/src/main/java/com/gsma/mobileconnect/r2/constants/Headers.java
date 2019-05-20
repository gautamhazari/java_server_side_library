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
package com.gsma.mobileconnect.r2.constants;

/**
 * Constants relating to headers such as possible Header keys.
 *
 * @since 2.0
 */
public final class Headers
{
    /**
     * Key for Source Ip Header
     */
    public static final String X_SOURCE_IP = "X-Source-IP";
    /**
     * Key for Source Ip Header
     */
    public static final String X_REQUEST_ID = "X-Request-ID";

    /**
     * Key for Set Cookie Header
     */
    public static final String SET_COOKIE = "Set-Cookie";

    /**
     * Key for Forwarded For Header
     */
    public static final String X_FORWARDED_FOR = "X-Forwarded-For";

    /**
     * Key for cookie header.
     */
    public static final String COOKIE = "Cookie";

    /**
     * Key for version Header.
     */
    public static final String VERSION_SDK = "SDK-Version";

    /**
     * Key for version Header.
     */
    public static final String CLIENT_SIDE_VERSION = "Client-Side-Version";

    /**
     * Key for version Header.
     */
    public static final String SERVER_SIDE_VERSION = "Server-Side-Version";

    /**
     * Default value for version Header.
     */
    public static final String NONE = "none";

    private Headers()
    {
        /*
        Private default constructor
         */
    }
}
