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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gsma.mobileconnect.r2.encoding.IMobileConnectEncodeDecoder;
import com.gsma.mobileconnect.r2.exceptions.MobileConnectInvalidJWKException;
import com.gsma.mobileconnect.r2.utils.ByteUtils;
import com.gsma.mobileconnect.r2.utils.StringUtils;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.RsaSignatureValidator;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

/**
 * Represents a cryptographic key that belongs to a JWKeyset
 *
 * @since 2.0
 */
class JWKey
{
    private String keyType;
    private String use;
    private String keyOps;
    private String algorithm;
    private String keyId;
    private String key;
    private String eccCurve;
    private String eccX;
    private String eccY;
    private String rsaN;
    private String rsaE;

    private JWKey()
    {
    }

    /**
     * @return The "kty" (key type) parameter identifies the cryptographic algorithm family used
     * with the key, such as "RSA" or "EC"
     */
    @JsonProperty("kty")
    public String getKeyType()
    {
        return keyType;
    }

    /**
     * @return The "use" (public key use) parameter identifies the intended use of the public
     * key.The "use" parameter is employed to indicate whether a public key is used for encrypting
     * data or verifying the signature on data.
     */
    @JsonProperty("use")
    public String getUse()
    {
        return use;
    }

    /**
     * @return The "key_ops" (key operations) parameter identifies the operation(s) for which the
     * key is intended to be used.The "key_ops" parameter is intended for use cases in which public,
     * private, or symmetric keys may be present.
     */
    @JsonProperty("key_ops")
    public String getKeyOps()
    {
        return keyOps;
    }

    /**
     * @return The "alg" (algorithm) parameter identifies the algorithm intended for use with the
     * key.
     */
    @JsonProperty("alg")
    public String getAlgorithm()
    {
        return algorithm;
    }

    /**
     * @return The "kid" (key ID) parameter is used to match a specific key. This is used, for
     * instance, to choose among a set of keys within a JWK Set during key rollover.
     */
    @JsonProperty("kid")
    public String getKeyId()
    {
        return keyId;
    }

    @JsonIgnore
    Boolean isSymmetric()
    {
        return "OCT".equalsIgnoreCase(this.keyType);
    }

    /**
     * @return The "k" (key value) parameter contains the value of the symmetric (or other
     * single-valued) key. It is represented as the base64url encoding of the octet sequence
     * containing the key value.
     */
    @JsonProperty("k")
    public String getKey()
    {
        return key;
    }

    @JsonIgnore
    Boolean isEcc()
    {
        return "EC".equalsIgnoreCase(this.keyType);
    }

    /**
     * @return The "crv" (curve) parameter identifies the cryptographic curve used with the key
     */
    @JsonProperty("crv")
    public String getEccCurve()
    {
        return eccCurve;
    }

    /**
     * @return The "x" (x coordinate) parameter contains the x coordinate for the Elliptic Curve
     * point
     */
    @JsonProperty("x")
    public String getEccX()
    {
        return eccX;
    }

    /**
     * @return The "y" (y coordinate) parameter contains the y coordinate for the Elliptic Curve
     * point
     */
    @JsonProperty("y")
    public String getEccY()
    {
        return eccY;
    }

    @JsonIgnore
    Boolean isRsa()
    {
        return "RSA".equalsIgnoreCase(this.keyType);
    }

    /**
     * @return The "n" (modulus) parameter contains the modulus value for the RSA public key. It is
     * represented as a Base64urlUInt-encoded value.
     */
    @JsonProperty("n")
    public String getRsaN()
    {
        return rsaN;
    }

    /**
     * @return The "e" (exponent) parameter contains the exponent value for the RSA public key. It
     * is represented as a Base64urlUInt-encoded value.
     */
    @JsonProperty("e")
    public String getRsaE()
    {
        return rsaE;
    }

    public boolean verify(final String input, final String expected, final String algorithm,
        final IMobileConnectEncodeDecoder mobileConnectEncodeDecoder)
        throws MobileConnectInvalidJWKException, NoSuchAlgorithmException, InvalidKeySpecException
    {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.forName(algorithm);

        boolean isValid = false;
        if (isRsa())
        {

            isValid = verifyRsa(input, expected, signatureAlgorithm, mobileConnectEncodeDecoder);
        }
        return isValid;
    }

    private boolean verifyRsa(final String input, final String expected,
        final SignatureAlgorithm signatureAlgorithm,
        final IMobileConnectEncodeDecoder mobileConnectEncodeDecoder)
        throws NoSuchAlgorithmException, InvalidKeySpecException, MobileConnectInvalidJWKException
    {
        if (StringUtils.isNullOrEmpty(this.getRsaN()) || StringUtils.isNullOrEmpty(this.getRsaE()))
        {
            throw new MobileConnectInvalidJWKException(
                "RSA key does not have required Modulus and Exponent components");
        }

        byte[] mod =
            ByteUtils.addZeroPrefix(mobileConnectEncodeDecoder.decodeFromBase64(this.getRsaN()));
        byte[] exp =
            ByteUtils.addZeroPrefix(mobileConnectEncodeDecoder.decodeFromBase64(this.getRsaE()));

        final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        final Key rsaKey = keyFactory.generatePublic(
            new RSAPublicKeySpec(new BigInteger(mod), new BigInteger(exp)));

        return new RsaSignatureValidator(signatureAlgorithm, rsaKey).isValid(input.getBytes(),
            mobileConnectEncodeDecoder.decodeFromBase64(expected));
    }

}
