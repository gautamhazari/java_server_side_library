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

import java.util.concurrent.TimeUnit;

/**
 * @since 2.0
 */
@SuppressWarnings("unused")
public final class DefaultOptions
{
    private static final String MC_V1_1 = "mc_v1.1";
    private static final String MC_V1_2 = "mc_v1.2";
    public static final String MC_V2_3 = "mc_di_r2_v2.3";

    public static final long TIMEOUT_MS = TimeUnit.SECONDS.toMillis(300L);
    public static final boolean MANUAL_SELECT = false;
    public static final boolean COOKIES_ENABLED = true;
    public static final String DISPLAY = "page";
    public static final boolean CHECK_ID_TOKEN_SIGNATURE = true;
    public static final long MIN_TTL_MS = TimeUnit.SECONDS.toMillis(300L);
    public static final long MAX_TTL_MS = TimeUnit.DAYS.toMillis(180L);
    public static final String AUTHENTICATION_ACR_VALUES = "2";
    public static final String AUTHENTICATION_SCOPE = Scope.OPENID;
    public static final long AUTHENTICATION_MAX_AGE = TimeUnit.HOURS.toSeconds(1L);
    public static final String AUTHENTICATION_RESPONSE_TYPE = "code";
    public static final String AUTHENTICATION_DEFAULT_VERSION = MC_V1_1;
    public static final String GRANT_TYPE_AUTH_CODE = "authorization_code";
    public static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";
    public static final long PROVIDER_METADATA_TTL_MS = TimeUnit.SECONDS.toMillis(9L);
    public static final String VERSION_MOBILECONNECT = MC_V1_1;
    public static final String VERSION_MOBILECONNECTAUTHN = MC_V1_1;
    public static final String VERSION_MOBILECONNECTAUTHZ = MC_V1_2;
    public static final String VERSION_MOBILECONNECTIDENTITY = MC_V1_2;
    public static final int THREAD_POOL_SIZE = 100;

    public static final String PROMPT = "mobile";
    public static final String X_REDIRECT_VALUE = "APP";

    // Since the wait time is 5 seconds & the maximum timeout = 2 mins
    public static final long MAX_REDIRECTS = 24;
    public static final long WAIT_TIME = 5000L; // 5 seconds

    private DefaultOptions()
    {
        /*
        Private default constructor
         */
    }
}
