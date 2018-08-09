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
package com.gsma.mobileconnect.r2.validation;

import com.gsma.mobileconnect.r2.authentication.RequestTokenResponseData;
import com.gsma.mobileconnect.r2.claims.Claims;
import com.gsma.mobileconnect.r2.claims.ClaimsConstants;
import com.gsma.mobileconnect.r2.encoding.IMobileConnectEncodeDecoder;
import com.gsma.mobileconnect.r2.exceptions.MobileConnectInvalidJWKException;
import com.gsma.mobileconnect.r2.json.IJsonService;
import com.gsma.mobileconnect.r2.json.JsonDeserializationException;
import com.gsma.mobileconnect.r2.utils.JsonWebTokens;
import com.gsma.mobileconnect.r2.utils.ListUtils;
import com.gsma.mobileconnect.r2.utils.Predicate;
import com.gsma.mobileconnect.r2.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Calendar;
import java.util.Date;

/**
 * Utility methods for token validation
 *
 * @since 2.0
 */
@SuppressWarnings("WeakerAccess")
public class TokenValidation
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenValidation.class);

    private TokenValidation()
    {
    }

    /**
     * Validates an id token against the mobile connect validation requirements, this includes
     * validation of some claims and validation of the signature
     *
     * @param idToken                     IDToken to validate
     * @param clientId                    ClientId that is validated against the aud and azp claims
     * @param issuer                      Issuer that is validated against the iss claim
     * @param nonce                       Nonce that is validated against the nonce claim
     * @param maxAge                      MaxAge that is used to validate the auth_time claim (if
     *                                    supplied)
     * @param keyset                      Keyset retrieved from the jwks url, used to validate the
     *                                    token signature
     * @param mobileConnectEncodeDecoder  Class used to encode/decode
     * @return TokenValidationResult that specifies if the token is valid, or if not why it is not
     * valid
     */
    public static TokenValidationResult validateIdToken(final String idToken, //NOSONAR
        final String clientId, final String issuer, final String nonce, final long maxAge,
        final JWKeyset keyset, final IJsonService jsonService,
        final IMobileConnectEncodeDecoder mobileConnectEncodeDecoder)
        throws JsonDeserializationException
    {
        if (StringUtils.isNullOrEmpty(idToken))
        {
            LOGGER.warn("Id token is missing");
            return TokenValidationResult.ID_TOKEN_MISSING;
        }

        TokenValidationResult result =
            validateIdTokenClaims(idToken, clientId, issuer, nonce, maxAge, jsonService,
                mobileConnectEncodeDecoder);
        if (result != TokenValidationResult.VALID)
        {
            return result;
        }

        return validateIdTokenSignature(idToken, keyset, jsonService, mobileConnectEncodeDecoder);
    }

    /**
     * Validates an id token signature by signing the id token payload and comparing the result with
     * the signature
     *
     * @param idToken                     IDToken to validate
     * @param keyset                      Keyset retrieved from the jwks url, used to validate the
     *                                    token signature. If null the token will not be validated
     *                                    and {@link TokenValidationResult#JWKS_ERROR}
     * @param jsonService                 Json service to be used deserialising strings to com.gsma.mobileconnect.r2.demo.objects
     * @param mobileConnectEncodeDecoder  Class used to encode/decode
     * @return TokenValidationResult that specifies if the token signature is valid, or if not why
     * it is not valid
     */
    public static TokenValidationResult validateIdTokenSignature(final String idToken,
        final JWKeyset keyset, final IJsonService jsonService,
        final IMobileConnectEncodeDecoder mobileConnectEncodeDecoder)
        throws JsonDeserializationException
    {
        if (keyset == null)
        {
            LOGGER.warn("Keyset not found");
            return TokenValidationResult.JWKS_ERROR;
        }

        try
        {
            return validateSignature(idToken, keyset, jsonService, mobileConnectEncodeDecoder);
        }
        catch (JsonDeserializationException e)
        {
            LOGGER.warn("Error deserializing idToken");
            throw new JsonDeserializationException(JWKey.class, idToken, e);
        }
    }

    private static TokenValidationResult validateSignature(final String idToken,
        final JWKeyset keyset, final IJsonService jsonService,
        final IMobileConnectEncodeDecoder mobileConnectEncodeDecoder)
        throws JsonDeserializationException
    {
        final JWKey jwKeyDeserialized = jsonService.deserialize(
            JsonWebTokens.Part.HEADER.decode(idToken, mobileConnectEncodeDecoder),
            JWKey.class);
        final String alg = jwKeyDeserialized.getAlgorithm();
        final String keyId = jwKeyDeserialized.getKeyId();

        final JWKey jwKey = extractJwKey(keyset, alg, keyId);

        if (jwKey == null)
        {
            LOGGER.warn("No key found in keyset matching idtoken header");
            return TokenValidationResult.NO_MATCHING_KEY;
        }

        final int lastSplitIndex = idToken.lastIndexOf('.');
        if (lastSplitIndex < 0 || lastSplitIndex == idToken.length() - 1)
        {
            LOGGER.warn("Error discovering signature");
            return TokenValidationResult.INVALID_SIGNATURE;
        }

        final String dataToSign = idToken.substring(0, lastSplitIndex);
        final String signature = idToken.substring(lastSplitIndex + 1);

        return verifySignature(jwKey, dataToSign, signature, alg, mobileConnectEncodeDecoder);
    }

    private static JWKey extractJwKey(final JWKeyset keyset, final String alg, final String keyId)
    {
        return ListUtils.firstMatch(keyset.getKeys(), new Predicate<JWKey>()
        {
            @Override
            public boolean apply(final JWKey input)
            {
                if (input.getKeyId() == null)
                {
                    return keyId == null && (StringUtils.isNullOrEmpty(input.getAlgorithm())
                        || input.getAlgorithm().equals(alg));
                }
                return input.getKeyId().equals(keyId) && (StringUtils.isNullOrEmpty(
                    input.getAlgorithm()) || input.getAlgorithm().equals(alg));
            }
        });
    }

    private static TokenValidationResult verifySignature(final JWKey jwKey, final String dataToSign,
        final String signature, final String alg,
        final IMobileConnectEncodeDecoder mobileConnectEncodeDecoder)
    {
        boolean isValid;
        try
        {
            isValid = jwKey.verify(dataToSign, signature, alg, mobileConnectEncodeDecoder);
            return isValid ? TokenValidationResult.VALID : TokenValidationResult.INVALID_SIGNATURE;
        }
        catch (MobileConnectInvalidJWKException e)
        {
            LOGGER.warn("Id Token validation failed", e);
            return TokenValidationResult.KEY_MISFORMED;
        }
        catch (InvalidKeySpecException e)
        {
            LOGGER.warn("Key specification invalid", e);
            return TokenValidationResult.KEY_MISFORMED;
        }
        catch (NoSuchAlgorithmException e)
        {
            LOGGER.warn("No matching algorithm found", e);
            return TokenValidationResult.INCORRECT_ALGORITHM;
        }
    }

    /**
     * Validates an id tokens claims using validation requirements from the mobile connect and open
     * id connect specification
     *
     * @param idToken                     IDToken to validate
     * @param clientId                    ClientId that is validated against the aud and azp claims
     * @param issuer                      Issuer that is validated against the iss claim
     * @param expectedNonce               Nonce that is validated against the nonce claim
     * @param maxAge                      MaxAge that is used to validate the auth_time claim (if
     *                                    supplied)
     * @param jsonService                 Json service used to serialize/deserialize com.gsma.mobileconnect.r2.demo.objects
     * @param mobileConnectEncodeDecoder  Encoder used to serialize com.gsma.mobileconnect.r2.demo.objects
     * @return TokenValidationResult that specifies if the token claims are valid, or if not why
     * they are not valid
     */
    public static TokenValidationResult validateIdTokenClaims(final String idToken,
        final String clientId, final String issuer, final String expectedNonce, final long maxAge,
        final IJsonService jsonService,
        final IMobileConnectEncodeDecoder mobileConnectEncodeDecoder)
        throws JsonDeserializationException
    {
        final Claims claims = extractClaims(idToken, jsonService, mobileConnectEncodeDecoder);

        if (isNonceInvalid(claims, expectedNonce))
        {
            LOGGER.warn("Invalid Nonce");
            return TokenValidationResult.INVALID_NONCE;
        }

        if (isIssuerInvalid(claims, issuer))
        {
            LOGGER.warn("Issuer does not match expected");
            return TokenValidationResult.INVALID_ISSUER;
        }

        if (!doesAudOrAzpClaimMatchClientId(claims, clientId))
        {
            LOGGER.warn("Audience or Authorized party does not match client id");
            return TokenValidationResult.INVALID_AUD_AND_AZP;
        }

        return validateTokenExpiry(claims, maxAge);
    }

    private static Claims extractClaims(final String idToken, final IJsonService jsonService,
        final IMobileConnectEncodeDecoder mobileConnectEncodeDecoder)
        throws JsonDeserializationException
    {
        String claimsJson = JsonWebTokens.Part.CLAIMS.decode(idToken, mobileConnectEncodeDecoder);
        return jsonService.deserialize(claimsJson, Claims.class);
    }

    private static boolean isNonceInvalid(final Claims claims, final String expectedNonce)
    {
        return expectedNonce != null && !expectedNonce.equals(
            claims.get(ClaimsConstants.NONCE).getValue().toString());
    }

    private static boolean isIssuerInvalid(final Claims claims, final String issuer)
    {
        return !claims.get(ClaimsConstants.ISSUER).getValue().toString().equals(issuer);
    }

    private static boolean doesAudOrAzpClaimMatchClientId(final Claims claims,
        final String clientId)
    {
        return clientId.equals(claims.get(ClaimsConstants.AUD).getValue()) || clientId.equals(
            claims.get(ClaimsConstants.AZP).getValue());
    }

    private static TokenValidationResult validateTokenExpiry(Claims claims, long maxAge)
    {
        if (tokenHasExpired(claims))
        {
            LOGGER.warn("Id token has expired");
            return TokenValidationResult.ID_TOKEN_EXPIRED;
        }

        if (maxAgeHasPassed(claims, maxAge))
        {
            LOGGER.warn("Id token has passed max age");
            return TokenValidationResult.MAX_AGE_PASSED;
        }

        return TokenValidationResult.VALID;
    }

    private static boolean tokenHasExpired(final Claims claims)
    {
        return (Long.valueOf(claims.get(ClaimsConstants.EXPIRED).getValue().toString()) * 1000)
            < Calendar.getInstance().getTimeInMillis();
    }

    private static boolean maxAgeHasPassed(final Claims claims, final long maxAge)
    {
        return (Long.valueOf(claims.get(ClaimsConstants.ISSUED_AT_TIME).getValue().toString())
            * 1000) + (maxAge * 1000) < Calendar.getInstance().getTimeInMillis();
    }

    /**
     * Validates the access token contained in the token response data
     *
     * @param tokenResponse Response data containing the access token and accompanying parameters
     * @return TokenValidationResult that specifies if the access token is valid, or if not why it
     * is not valid
     */
    public static TokenValidationResult validateAccessToken(
        final RequestTokenResponseData tokenResponse)
    {
        if (StringUtils.isNullOrEmpty(tokenResponse.getAccessToken()))
        {
            LOGGER.warn("Access token is missing");
            return TokenValidationResult.ACCESS_TOKEN_MISSING;
        }

        if (tokenResponse.getExpiry() != null && tokenResponse
            .getExpiry()
            .before(new Date(Calendar.getInstance().getTimeInMillis())))
        {
            LOGGER.warn("Access token has expired");
            return TokenValidationResult.ACCESS_TOKEN_EXPIRED;
        }
        return TokenValidationResult.VALID;
    }
}
