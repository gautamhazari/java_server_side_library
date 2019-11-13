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
package com.gsma.mobileconnect.r2.service.validation;

/**
 * Enum for available token validation results
 *
 * @since 2.0
 */
public enum TokenValidationResult
{
    /**
     * No validation has occurred
     */
    NONE,

    /**
     * Token when signed does not match signature
     */
    INVALID_SIGNATURE,

    /**
     * Token passed all validation steps
     */
    VALID,

    /**
     * Key was not retrieved from the jwks url or a jwks url was not present
     */
    JWKS_ERROR,

    /**
     * The alg claim in the id token header does not match the alg requested or the default alg of
     * RS256
     */
    INCORRECT_ALGORITHM,

    /**
     * Neither the azp nor the aud claim in the id token match the client id used to make the auth
     * request
     */
    INVALID_AUD_AND_AZP,

    /**
     * The iss claim in the id token does not match the expected issuer
     */
    INVALID_ISSUER,

    /**
     * The IdToken has expired
     */
    ID_TOKEN_EXPIRED,

    /**
     * No key matching the requested key id was found
     */
    NO_MATCHING_KEY,

    /**
     * Key does not contain the required information to validate against the requested algorithm
     */
    KEY_MISFORMED,

    /**
     * Algorithm is unsupported for validation
     */
    UNSUPPORTED_ALGORITHM,

    /**
     * The access token has expired
     */
    ACCESS_TOKEN_EXPIRED,


    /**
     * The access token is null or empty in the token response
     */
    ACCESS_TOKEN_MISSING,

    /**
     * The id token is null or empty in the token response
     */
    ID_TOKEN_MISSING,

    /**
     * The id token is older than the max age specified in the auth stage
     */
    MAX_AGE_PASSED,

    /**
     * A longer time than the configured limit has passed since the token was issued
     */
    TOKEN_ISSUE_TIME_LIMIT_PASSED,

    /**
     * The nonce in the id token claims does not match the nonce specified in the auth stage
     */
    INVALID_NONCE,

    /**
     * The at_hash in the id token claims does not match the nonce specified in the auth stage
     */
    INVALID_AT_HASH,

    /**
     * The acr in the id token claims does not match the nonce specified in the auth stage
     */
    INVALID_ACR,

    /**
     * The amr in the id token claims does not match the nonce specified in the auth stage
     */
    INVALID_AMR,

    /**
     * The hashed_login_hint in the id token claims does not match the nonce specified in the auth stage
     */
    INVALID_HASHED_LOGIN_HINT,

    /**
     * The token response is null or missing required data
     */
    INCOMPLETE_TOKEN_RESPONSE
}
