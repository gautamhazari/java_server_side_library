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
 * @since 2.0
 */
public final class Parameters
{
    // Required param for discovery
    public static final String REDIRECT_URL = "Redirect_URL";

    // Optional params for discovery
    public static final String MANUALLY_SELECT = "Manually-Select";
    public static final String IDENTIFIED_MCC = "Identified-MCC";
    public static final String IDENTIFIED_MNC = "Identified-MNC";
    public static final String SELECTED_MCC = "Selected-MCC";
    public static final String SELECTED_MNC = "Selected-MNC";
    public static final String USING_MOBILE_DATA = "Using-Mobile-Data";
    public static final String LOCAL_CLIENT_IP = "Local-Client-IP";
    public static final String MSISDN = "MSISDN";
    public static final String X_REDIRECT = "X-Redirect";

    public static final String MCC_MNC = "mcc_mnc";
    public static final String DISCOVERY_CALLBACK = "discovery_callback";
    public static final String SUBSCRIBER_ID = "subscriber_id";
    public static final String CORRELATION_ID = "correlation_id";

    // Required params for authentication
    public static final String CLIENT_ID = "client_id";
    public static final String RESPONSE_TYPE = "response_type";
    public static final String AUTHENTICATION_REDIRECT_URI = "redirect_uri";
    public static final String SCOPE = "scope";
    public static final String ACR_VALUES = "acr_values";
    public static final String STATE = "state";
    public static final String NONCE = "nonce";
    public static final String VERSION = "version";

    // Optional params for authentication
    public static final String DISPLAY = "display";
    public static final String PROMPT = "prompt";
    public static final String MAX_AGE = "max-age";
    public static final String UI_LOCALES = "ui_locales";
    public static final String CLAIMS_LOCALES = "claims_locales";
    public static final String ID_TOKEN_HINT = "id_token_hint";
    public static final String LOGIN_HINT = "login_hint";
    public static final String LOGIN_HINT_TOKEN = "login_hint_token";
    public static final String DTBS = "dtbs";
    public static final String CLAIMS = "claims";
    public static final String SECTOR_IDENTIFIER_URI = "claims";

    // Required params for authorization
    public static final String CLIENT_NAME = "client_name";
    public static final String CONTEXT = "context";
    public static final String BINDING_MESSAGE = "binding_message";

    // Params for AuthorizationResponse
    public static final String ERROR = "error";
    public static final String ERROR_DESCRIPTION = "error_description";
    public static final String DESCRIPTION = "description";
    public static final String ERROR_URI = "error_uri";
    public static final String CODE = "code";

    // Params for Token
    public static final String GRANT_TYPE = "grant_type";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String TOKEN = "token";
    public static final String TOKEN_TYPE_HINT = "token_type_hint";

    public static final String ACCESS_TOKEN_HINT = "access_token";
    public static final String REFRESH_TOKEN_HINT = "refresh_token";

    //Version of current SDK
    public static final String SDK_VERSION = "Java-2.4.7";

    private Parameters()
    {
        /*
        Private default constructor
         */
    }
}
