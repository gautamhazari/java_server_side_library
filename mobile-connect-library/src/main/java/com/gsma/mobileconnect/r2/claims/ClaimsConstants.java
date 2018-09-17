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
package com.gsma.mobileconnect.r2.claims;

/**
 * Constants relating to claims
 *
 * @since 2.0
 */
public class ClaimsConstants
{
    /**
     * Key for NONCE
     */
    public static final String NONCE = "nonce";

    /**
     * Key for AUD
     */
    public static final String AUD = "aud";

    /**
     * Key for AZP
     */
    public static final String AZP = "azp";

    /**
     * Key for at_hash
     */
    public static final String AT_HASH = "at_hash";

    /**
     * Key for acr
     */
    public static final String ACR = "acr";

    /**
     * Key for amr
     */
    public static final String AMR = "amr";

    /**
     * Key for hashed_login_hint
     */
    public static final String HASHED_LOGIN_HINT = "hashed_login_hint";

    /**
     * Key for Issuer
     */
    public static final String ISSUER = "iss";

    /**
     * Key for expiry time
     */
    public static final String EXPIRED = "exp";

    /**
     * Key for issued at tme
     */
    public static final String ISSUED_AT_TIME = "iat";

    private ClaimsConstants()
    {
        /*
        Private default constructor
         */
    }
}
