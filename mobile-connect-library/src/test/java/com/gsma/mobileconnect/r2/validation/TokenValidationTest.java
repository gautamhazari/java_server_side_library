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

import com.gsma.mobileconnect.r2.service.authentication.RequestTokenResponseData;
import com.gsma.mobileconnect.r2.utils.encoding.DefaultEncodeDecoder;
import com.gsma.mobileconnect.r2.utils.encoding.IMobileConnectEncodeDecoder;
import com.gsma.mobileconnect.r2.model.json.GsonJsonService;
import com.gsma.mobileconnect.r2.model.json.JsonDeserializationException;
import com.gsma.mobileconnect.r2.service.validation.JWKeyset;
import com.gsma.mobileconnect.r2.service.validation.TokenValidation;
import com.gsma.mobileconnect.r2.service.validation.TokenValidationResult;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @since 2.0
 */
public class TokenValidationTest
{
    private String nonce = "1234567890";
    private String clientId = "x-clientid-x";
    private String issuer = "http://mobileconnect.io";
    private static final long MAX_AGE = 300L * 24 * 60 * 60; //seconds
    private final IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder =
        new DefaultEncodeDecoder();
    private final GsonJsonService gsonJsonService = new GsonJsonService();

    @Test
    public void validateIdTokenShouldReturnValidSignature() throws JsonDeserializationException
    {
        final String jwksJson =
            "{\"keys\":[{\"alg\":\"RS256\",\"e\":\"AQAB\",\"n\":\"hzr2li5ABVbbQ4BvdDskl6hejaVw0tIDYO-C0GBr5lRA-AXtmCO7bh0CEC9-R6mqctkzUhVnU22Vrj-B1J0JtJoaya9VTC3DdhzI_-7kxtIc5vrHq-ss5wo8-tK7UqtKLSRf9DcyZA0H9FEABbO5Qfvh-cfK4EI_ytA5UBZgO322RVYgQ9Do0D_-jf90dcuUgoxz_JTAOpVNc0u_m9LxGnGL3GhMbxLaX3eUublD40aK0nS2k37dOYOpQHxuAS8BZxLvS6900qqaZ6z0kwZ2WFq-hhk3Imd6fweS724fzqVslY7rHpM5n7z5m7s1ArurU1dBC1Dxw1Hzn6ZeJkEaZQ\",\"kty\":\"RSA\",\"use\":\"sig\"}]}";
        final String idToken =
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJub25jZSI6IjEyMzQ1Njc4OTAiLCJhdWQiOiJ4LWNsaWVudGlkLXgiLCJhenAiOiJ4LWNsaWVudGlkLXgiLCJpc3MiOiJodHRwOi8vbW9iaWxlY29ubmVjdC5pbyIsImV4cCI6MjE0NzQ4MzY0NywiYXV0aF90aW1lIjoyMTQ3NDgzNjQ3LCJpYXQiOjE0NzEwMDczMjd9.U9c5iuybG4GIvrbQH5BT9AgllRbPL6SuIzL4Y3MW7VlCVIQOc_HFfkiLa0LNvqZiP-kFlADmnkzuuQxPq7IyaOILVYct20mrcOb_U_zMli4jg-t9P3BxHaq3ds9JlLBjz0oewd01ZQtWHgRnrGymfKAIojzHlde-aePuL1M26Eld5zoKQvCLcKAynZsjKsWF_6YdLk-uhlC5ofMOaOoPirPSPAxYvbj91z3o9XIgSHoU-umN7AJ6UQ4H-ulfftlRGK8hz0Yzpf2MHOy9OHg1u3ayfCaaf8g5zKGngcz0LgK9VAw2B31xJw-RHkPPh0Hz82FgBc4588oEFC1c22GGTw";

        final JWKeyset jwKeyset = gsonJsonService.deserialize(jwksJson, JWKeyset.class);
        final TokenValidationResult tokenValidationResult =
            TokenValidation.validateIdToken(idToken, clientId, issuer, nonce, MAX_AGE, jwKeyset,
                    gsonJsonService, iMobileConnectEncodeDecoder, "mc_v1.1");

        assertEquals(tokenValidationResult, TokenValidationResult.MAX_AGE_PASSED);
    }

    @Test
    public void validateIdTokenShouldReturnInValidSignature() throws JsonDeserializationException
    {
        final String jwksJson =
            "{\"keys\":[{\"alg\":\"RS256\",\"e\":\"AQAB\",\"n\":\"hzr2li5ABVbbQ4BvdDskl6hejaVw0tIDYO-C0GBr5lRA-AXtmCO7bh0CEC9-R6mqctkzUhVnU22Vrj-B1J0JtJoaya9VTC3DdhzI_-7kxtIc5vrHq-ss5wo8-tK7UqtKLSRf9DcyZA0H9FEABbO5Qfvh-cfK4EI_ytA5UBZgO322RVYgQ9Do0D_-jf90dcuUgoxz_JTAOpVNc0u_m9LxGnGL3GhMbxLaX3eUublD40aK0nS2k37dOYOpQHxuAS8BZxLvS6900qqaZ6z0kwZ2WFq-hhk3Imd6fweS724fzqVslY7rHpM5n7z5m7s1ArurU1dBC1Dxw1Hzn6ZeJkEaZQ\",\"kty\":\"RSA\",\"use\":\"sig\"}]}";
        final String idToken =
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJub25jZSI6IjEyMzQ1Njc4OTAiLCJhdWQiOiJ4LWNsaWVudGlkLXgiLCJhenAiOiJ4LWNsaWVudGlkLXgiLCJpc3MiOiJodHRwOi8vbW9iaWxlY29ubmVjdC5pbyIsImV4cCI6MjE0NzQ4MzY0NywiYXV0aF90aW1lIjoyMTQ3NDgzNjQ3LCJpYXQiOjE0NzEwMDczMjd9.U9c5iuybG4GIvrbQH5BT9AgllRbPL6SuIzL4Y3MW7VlCVIQOc_HFfkiLa0LNvqZiP-kFlADmnkzuuQxPq7IyaOILVYct20mrcOb_U_zMli4jg-t9P3BxHaq3ds9JlLBjz0oewd01ZQtWHgRnrGymfKAIojzHlde-aePuL1M26Eld5zoKQvCLcKAynZsjKsWF_6YdLk-uhlC5ofMOaOoPirPSPAxYvbj91z3o9XIgSHoU-umN7AJ6UQ4H-ulfftlRGK8hz0Yzpf2MHOy9OHg1u3ayfCaaf8g5zKGngcz0LgK9VAw2B31xJw-RHkPPh0Hz82FgBcabd8oEFC1c22GGT1";

        final JWKeyset jwKeyset = gsonJsonService.deserialize(jwksJson, JWKeyset.class);
        final TokenValidationResult tokenValidationResult =
            TokenValidation.validateIdToken(idToken, clientId, issuer, nonce, MAX_AGE, jwKeyset,
                    gsonJsonService, iMobileConnectEncodeDecoder, "mc_v1.1");

        assertEquals(tokenValidationResult, TokenValidationResult.MAX_AGE_PASSED);
    }

    @Test
    public void validateIdTokenShouldReturnTvrWhenClaimsResultNotValid()
        throws JsonDeserializationException
    {
        final String invalidNonce = "invalid nonce";
        final String jwksJson =
            "{\"keys\":[{\"alg\":\"RS256\",\"e\":\"AQAB\",\"n\":\"hzr2li5ABVbbQ4BvdDskl6hejaVw0tIDYO-C0GBr5lRA-AXtmCO7bh0CEC9-R6mqctkzUhVnU22Vrj-B1J0JtJoaya9VTC3DdhzI_-7kxtIc5vrHq-ss5wo8-tK7UqtKLSRf9DcyZA0H9FEABbO5Qfvh-cfK4EI_ytA5UBZgO322RVYgQ9Do0D_-jf90dcuUgoxz_JTAOpVNc0u_m9LxGnGL3GhMbxLaX3eUublD40aK0nS2k37dOYOpQHxuAS8BZxLvS6900qqaZ6z0kwZ2WFq-hhk3Imd6fweS724fzqVslY7rHpM5n7z5m7s1ArurU1dBC1Dxw1Hzn6ZeJkEaZQ\",\"kty\":\"RSA\",\"use\":\"sig\"}]}";
        final String idToken =
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJub25jZSI6IjEyMzQ1Njc4OTAiLCJhdWQiOiJ4LWNsaWVudGlkLXgiLCJhenAiOiJ4LWNsaWVudGlkLXgiLCJpc3MiOiJodHRwOi8vbW9iaWxlY29ubmVjdC5pbyIsImV4cCI6MjE0NzQ4MzY0NywiYXV0aF90aW1lIjoyMTQ3NDgzNjQ3LCJpYXQiOjE0NzEwMDczMjd9.U9c5iuybG4GIvrbQH5BT9AgllRbPL6SuIzL4Y3MW7VlCVIQOc_HFfkiLa0LNvqZiP-kFlADmnkzuuQxPq7IyaOILVYct20mrcOb_U_zMli4jg-t9P3BxHaq3ds9JlLBjz0oewd01ZQtWHgRnrGymfKAIojzHlde-aePuL1M26Eld5zoKQvCLcKAynZsjKsWF_6YdLk-uhlC5ofMOaOoPirPSPAxYvbj91z3o9XIgSHoU-umN7AJ6UQ4H-ulfftlRGK8hz0Yzpf2MHOy9OHg1u3ayfCaaf8g5zKGngcz0LgK9VAw2B31xJw-RHkPPh0Hz82FgBc4588oEFC1c22GGTw";

        final JWKeyset jwKeyset = gsonJsonService.deserialize(jwksJson, JWKeyset.class);
        final TokenValidationResult tokenValidationResult =
            TokenValidation.validateIdToken(idToken, clientId, issuer, invalidNonce, MAX_AGE,
                jwKeyset, gsonJsonService, iMobileConnectEncodeDecoder, "mc_v1.1");

        assertEquals(tokenValidationResult, TokenValidationResult.INVALID_NONCE);
    }

    @Test
    public void validateIdTokenShouldReturnIdTokenMissing() throws JsonDeserializationException
    {
        final String jwksJson =
            "{\"keys\":[{\"alg\":\"RS256\",\"e\":\"AQAB\",\"n\":\"hzr2li5ABVbbQ4BvdDskl6hejaVw0tIDYO-C0GBr5lRA-AXtmCO7bh0CEC9-R6mqctkzUhVnU22Vrj-B1J0JtJoaya9VTC3DdhzI_-7kxtIc5vrHq-ss5wo8-tK7UqtKLSRf9DcyZA0H9FEABbO5Qfvh-cfK4EI_ytA5UBZgO322RVYgQ9Do0D_-jf90dcuUgoxz_JTAOpVNc0u_m9LxGnGL3GhMbxLaX3eUublD40aK0nS2k37dOYOpQHxuAS8BZxLvS6900qqaZ6z0kwZ2WFq-hhk3Imd6fweS724fzqVslY7rHpM5n7z5m7s1ArurU1dBC1Dxw1Hzn6ZeJkEaZQ\",\"kty\":\"RSA\",\"use\":\"sig\"}]}";
        final JWKeyset jwKeyset = gsonJsonService.deserialize(jwksJson, JWKeyset.class);

        final String idTokenEmpty = "";
        final TokenValidationResult tokenValidationResultEmpty =
            TokenValidation.validateIdToken(idTokenEmpty, clientId, issuer, nonce, MAX_AGE, jwKeyset,
                    gsonJsonService, iMobileConnectEncodeDecoder, "mc_v1.1");
        assertEquals(tokenValidationResultEmpty, TokenValidationResult.ID_TOKEN_MISSING);

        final String idTokenNull = null;
        final TokenValidationResult tokenValidationResultNull =
            TokenValidation.validateIdToken(idTokenNull, clientId, issuer, nonce, MAX_AGE, jwKeyset,
                    gsonJsonService, iMobileConnectEncodeDecoder, "mc_v1.1");
        assertEquals(tokenValidationResultNull, TokenValidationResult.ID_TOKEN_MISSING);
    }

    @Test
    public void validMatchingRSAAlgorithmShouldVerifySignature() throws JsonDeserializationException
    {
        final String jwksJson =
            "{\"keys\":[{\"kty\":\"RSA\",\"use\":\"sig\",\"n\":\"ALyIC8vj1tqEIvAvpDMQfgosw13LpBS9Z2lsMmuaLDNJjN_FKIb-HVR2qtMj7AYC0-wYJhGxJpTXJTVRRDz-zLN7uredNxuhVj76vmU1tfvEN0Xq2INYoWeJ3d9fZtkBgKl7Enfkgz858DLAfZuJzDycOzuZXR5r29zXMDstT5F5\",\"e\":\"AQAB\",\"kid\":\"PHPOP-00\"}]}";
        final String idToken =
            "eyJhbGciOiJSUzI1NiIsImtpZCI6IlBIUE9QLTAwIn0.eyJpc3MiOiJodHRwczpcL1wvcmVmZXJlbmNlLm1vYmlsZWNvbm5lY3QuaW9cL21vYmlsZWNvbm5lY3QiLCJzdWIiOiI0MTE0MjFCMC0zOEQ2LTY1NjgtQTUzQS1ERjk5NjkxQjdFQjYiLCJhdWQiOlsieC1aV1JoTmpVM09XSTNNR0l3WVRSaCJdLCJleHAiOjE0NzQ2MjYzMzAsImlhdCI6MTQ3NDYyNjAzMCwibm9uY2UiOiI4MTk5MTQ5Ni00OGJiLTRkMTMtYmQwYy0xMTdkOTk0NDExYTYiLCJhdF9oYXNoIjoiNTZGMXo3RjZ3eWhUYUhVY1ZGY0xJQSIsImF1dGhfdGltZSI6MTQ3NDYyNjAyMCwiYWNyIjoiMiIsImFtciI6WyJTSU1fUElOIl0sImF6cCI6IngtWldSaE5qVTNPV0kzTUdJd1lUUmgifQ.TYcvfIHeKigkvjYta6fy90EffiA6u6NFCSIPlPM2WxEUi8Kxc5JIrjXnM8l0rFJOLmgNFUBpSqIRhuxwZkUV52KWf8jzswi3jTI8wEjonbjgviz7c6WzlZdb0Pw5kUEWy2xMam7VprESphPaIkHCDor2yR2g6Uq3Wtqyg7MCqek";

        final JWKeyset jwKeyset = gsonJsonService.deserialize(jwksJson, JWKeyset.class);
        final TokenValidationResult tokenValidationResult =
            TokenValidation.validateIdTokenSignature(idToken, jwKeyset, gsonJsonService,
                iMobileConnectEncodeDecoder);

        assertEquals(tokenValidationResult, TokenValidationResult.VALID);
    }

    @Test
    public void invalidMatchingRSAAlgorithmShouldThrowInvalidSignature()
        throws JsonDeserializationException
    {
        final String jwksJson =
            "{\"keys\":[{\"kty\":\"RSA\",\"use\":\"sig\",\"n\":\"LyIC8vj1tqEIvAvpDMQfgosw13LpBS9Z2lsMmuaLDNJjN_FKIb-HVR2qtMj7AYC0-wYJhGxJpTXJTVRRDz-zLN7uredNxuhVj76vmU1tfvEN0Xq2INYoWeJ3d9fZtkBgKl7Enfkgz858DLAfZuJzDycOzuZXR5r29zXMDstT5F5\",\"e\":\"AQAB\",\"kid\":\"PHPOP-00\"}]}";
        final String idToken =
            "eyJhbGciOiJSUzI1NiIsImtpZCI6IlBIUE9QLTAwIn0.eyJpc3MiOiJodHRwczpcL1wvcmVmZXJlbmNlLm1vYmlsZWNvbm5lY3QuaW9cL21vYmlsZWNvbm5lY3QiLCJzdWIiOiI0MTE0MjFCMC0zOEQ2LTY1NjgtQTUzQS1ERjk5NjkxQjdFQjYiLCJhdWQiOlsieC1aV1JoTmpVM09XSTNNR0l3WVRSaCJdLCJleHAiOjE0NzQ2MjYzMzAsImlhdCI6MTQ3NDYyNjAzMCwibm9uY2UiOiI4MTk5MTQ5Ni00OGJiLTRkMTMtYmQwYy0xMTdkOTk0NDExYTYiLCJhdF9oYXNoIjoiNTZGMXo3RjZ3eWhUYUhVY1ZGY0xJQSIsImF1dGhfdGltZSI6MTQ3NDYyNjAyMCwiYWNyIjoiMiIsImFtciI6WyJTSU1fUElOIl0sImF6cCI6IngtWldSaE5qVTNPV0kzTUdJd1lUUmgifQ.TYcvfIHeKigkvjYta6fy90EffiA6u6NFCSIPlPM2WxEUi8Kxc5JIrjXnM8l0rFJOLmgNFUBpSqIRhuxwZkUV52KWf8jzswi3jTI8wEjonbjgviz7c6WzlZdb0Pw5kUEWy2xMam7VprESphPaIkHCDor2yR2g6Uq3Wtqyg7MCqek";

        final JWKeyset jwKeyset = gsonJsonService.deserialize(jwksJson, JWKeyset.class);
        final TokenValidationResult tokenValidationResult =
            TokenValidation.validateIdTokenSignature(idToken, jwKeyset, gsonJsonService,
                iMobileConnectEncodeDecoder);

        assertEquals(tokenValidationResult, TokenValidationResult.INVALID_SIGNATURE);
    }

    @Test
    public void validateIdTokenSignatureShouldNotValidateWhenKeysetNull()
        throws JsonDeserializationException
    {
        final String jwksJson = null;
        final String idToken =
            "eyJhbGciOiJSUzI1NiIsImtpZCI6IlBIUE9QLTAwIn0.eyJpc3MiOiJodHRwczpcL1wvcmVmZXJlbmNlLm1vYmlsZWNvbm5lY3QuaW9cL21vYmlsZWNvbm5lY3QiLCJzdWIiOiI0MTE0MjFCMC0zOEQ2LTY1NjgtQTUzQS1ERjk5NjkxQjdFQjYiLCJhdWQiOlsieC1aV1JoTmpVM09XSTNNR0l3WVRSaCJdLCJleHAiOjE0NzQ2MjYzMzAsImlhdCI6MTQ3NDYyNjAzMCwibm9uY2UiOiI4MTk5MTQ5Ni00OGJiLTRkMTMtYmQwYy0xMTdkOTk0NDExYTYiLCJhdF9oYXNoIjoiNTZGMXo3RjZ3eWhUYUhVY1ZGY0xJQSIsImF1dGhfdGltZSI6MTQ3NDYyNjAyMCwiYWNyIjoiMiIsImFtciI6WyJTSU1fUElOIl0sImF6cCI6IngtWldSaE5qVTNPV0kzTUdJd1lUUmgifQ.TYcvfIHeKigkvjYta6fy90EffiA6u6NFCSIPlPM2WxEUi8Kxc5JIrjXnM8l0rFJOLmgNFUBpSqIRhuxwZkUV52KWf8jzswi3jTI8wEjonbjgviz7c6WzlZdb0Pw5kUEWy2xMam7VprESphPaIkHCDor2yR2g6Uq3Wtqyg7MCqek";

        final JWKeyset jwKeyset = gsonJsonService.deserialize(jwksJson, JWKeyset.class);
        final TokenValidationResult tokenValidationResult =
            TokenValidation.validateIdTokenSignature(idToken, jwKeyset, gsonJsonService,
                iMobileConnectEncodeDecoder);

        assertEquals(tokenValidationResult, TokenValidationResult.JWKS_ERROR);
    }

    @Test
    public void validateIdTokenSignatureShouldNotValidateWhenNoMatchingKey()
        throws JsonDeserializationException
    {
        final String jwksJson =
            "{\"keys\":[{\"kty\":\"AAA\",\"use\":\"sig\",\"n\":\"ALyIC8vj1tqEIvAvpDMQfgosw13LpBS9Z2lsMmuaLDNJjN_FKIb-HVR2qtMj7AYC0-wYJhGxJpTXJTVRRDz-zLN7uredNxuhVj76vmU1tfvEN0Xq2INYoWeJ3d9fZtkBgKl7Enfkgz858DLAfZuJzDycOzuZXR5r29zXMDstT5F5\",\"e\":\"AQAB\",\"kid\":\"PHPOP-99\"}]}";
        final String idToken =
            "eyJhbGciOiJSUzI1NiIsImtpZCI6IlBIUE9QLTAwIn0.eyJpc3MiOiJodHRwczpcL1wvcmVmZXJlbmNlLm1vYmlsZWNvbm5lY3QuaW9cL21vYmlsZWNvbm5lY3QiLCJzdWIiOiI0MTE0MjFCMC0zOEQ2LTY1NjgtQTUzQS1ERjk5NjkxQjdFQjYiLCJhdWQiOlsieC1aV1JoTmpVM09XSTNNR0l3WVRSaCJdLCJleHAiOjE0NzQ2MjYzMzAsImlhdCI6MTQ3NDYyNjAzMCwibm9uY2UiOiI4MTk5MTQ5Ni00OGJiLTRkMTMtYmQwYy0xMTdkOTk0NDExYTYiLCJhdF9oYXNoIjoiNTZGMXo3RjZ3eWhUYUhVY1ZGY0xJQSIsImF1dGhfdGltZSI6MTQ3NDYyNjAyMCwiYWNyIjoiMiIsImFtciI6WyJTSU1fUElOIl0sImF6cCI6IngtWldSaE5qVTNPV0kzTUdJd1lUUmgifQ.TYcvfIHeKigkvjYta6fy90EffiA6u6NFCSIPlPM2WxEUi8Kxc5JIrjXnM8l0rFJOLmgNFUBpSqIRhuxwZkUV52KWf8jzswi3jTI8wEjonbjgviz7c6WzlZdb0Pw5kUEWy2xMam7VprESphPaIkHCDor2yR2g6Uq3Wtqyg7MCqek";

        final JWKeyset jwKeyset = gsonJsonService.deserialize(jwksJson, JWKeyset.class);
        final TokenValidationResult tokenValidationResult =
            TokenValidation.validateIdTokenSignature(idToken, jwKeyset, gsonJsonService,
                iMobileConnectEncodeDecoder);

        assertEquals(tokenValidationResult, TokenValidationResult.NO_MATCHING_KEY);
    }

    @Test
    public void validateIdTokenSignatureShouldNotValidateWhenSignatureMissing()
        throws JsonDeserializationException
    {
        final String jwksJson =
            "{\"keys\":[{\"kty\":\"RSA\",\"use\":\"sig\",\"n\":\"ALyIC8vj1tqEIvAvpDMQfgosw13LpBS9Z2lsMmuaLDNJjN_FKIb-HVR2qtMj7AYC0-wYJhGxJpTXJTVRRDz-zLN7uredNxuhVj76vmU1tfvEN0Xq2INYoWeJ3d9fZtkBgKl7Enfkgz858DLAfZuJzDycOzuZXR5r29zXMDstT5F5\",\"e\":\"AQAB\",\"kid\":\"PHPOP-00\"}]}";
        final String idToken =
            "eyJhbGciOiJSUzI1NiIsImtpZCI6IlBIUE9QLTAwIn0.eyJpc3MiOiJodHRwczpcL1wvcmVmZXJlbmNlLm1vYmlsZWNvbm5lY3QuaW9cL21vYmlsZWNvbm5lY3QiLCJzdWIiOiI0MTE0MjFCMC0zOEQ2LTY1NjgtQTUzQS1ERjk5NjkxQjdFQjYiLCJhdWQiOlsieC1aV1JoTmpVM09XSTNNR0l3WVRSaCJdLCJleHAiOjE0NzQ2MjYzMzAsImlhdCI6MTQ3NDYyNjAzMCwibm9uY2UiOiI4MTk5MTQ5Ni00OGJiLTRkMTMtYmQwYy0xMTdkOTk0NDExYTYiLCJhdF9oYXNoIjoiNTZGMXo3RjZ3eWhUYUhVY1ZGY0xJQSIsImF1dGhfdGltZSI6MTQ3NDYyNjAyMCwiYWNyIjoiMiIsImFtciI6WyJTSU1fUElOIl0sImF6cCI6IngtWldSaE5qVTNPV0kzTUdJd1lUUmgifQ.";

        final JWKeyset jwKeyset = gsonJsonService.deserialize(jwksJson, JWKeyset.class);
        final TokenValidationResult tokenValidationResult =
            TokenValidation.validateIdTokenSignature(idToken, jwKeyset, gsonJsonService,
                iMobileConnectEncodeDecoder);

        assertEquals(tokenValidationResult, TokenValidationResult.INVALID_SIGNATURE);
    }

    @Test
    public void validateIdTokenSignatureShouldReturnKeyMisformedWhenMissingModOrExp()
        throws JsonDeserializationException
    {
        final String jwksJson =
            "{\"keys\":[{\"kty\":\"RSA\",\"use\":\"sig\",\"kid\":\"PHPOP-00\"}]}";
        final String idToken =
            "eyJhbGciOiJSUzI1NiIsImtpZCI6IlBIUE9QLTAwIn0.eyJpc3MiOiJodHRwczpcL1wvcmVmZXJlbmNlLm1vYmlsZWNvbm5lY3QuaW9cL21vYmlsZWNvbm5lY3QiLCJzdWIiOiI0MTE0MjFCMC0zOEQ2LTY1NjgtQTUzQS1ERjk5NjkxQjdFQjYiLCJhdWQiOlsieC1aV1JoTmpVM09XSTNNR0l3WVRSaCJdLCJleHAiOjE0NzQ2MjYzMzAsImlhdCI6MTQ3NDYyNjAzMCwibm9uY2UiOiI4MTk5MTQ5Ni00OGJiLTRkMTMtYmQwYy0xMTdkOTk0NDExYTYiLCJhdF9oYXNoIjoiNTZGMXo3RjZ3eWhUYUhVY1ZGY0xJQSIsImF1dGhfdGltZSI6MTQ3NDYyNjAyMCwiYWNyIjoiMiIsImFtciI6WyJTSU1fUElOIl0sImF6cCI6IngtWldSaE5qVTNPV0kzTUdJd1lUUmgifQ.TYcvfIHeKigkvjYta6fy90EffiA6u6NFCSIPlPM2WxEUi8Kxc5JIrjXnM8l0rFJOLmgNFUBpSqIRhuxwZkUV52KWf8jzswi3jTI8wEjonbjgviz7c6WzlZdb0Pw5kUEWy2xMam7VprESphPaIkHCDor2yR2g6Uq3Wtqyg7MCqek";

        final JWKeyset jwKeyset = gsonJsonService.deserialize(jwksJson, JWKeyset.class);
        final TokenValidationResult tokenValidationResult =
            TokenValidation.validateIdTokenSignature(idToken, jwKeyset, gsonJsonService,
                iMobileConnectEncodeDecoder);

        assertEquals(tokenValidationResult, TokenValidationResult.KEY_MISFORMED);

    }

    @Test
    public void validateIdTokenClaimsShouldReturnValid() throws JsonDeserializationException
    {
        final String idToken =
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJub25jZSI6IjEyMzQ1Njc4OTAiLCJhdWQiOiJ4LWNsaWVudGlkLXgiLCJhenAiOiJ4LWNsaWVudGlkLXgiLCJpc3MiOiJodHRwOi8vbW9iaWxlY29ubmVjdC5pbyIsImV4cCI6MjE0NzQ4MzY0NywiYXV0aF90aW1lIjoyMTQ3NDgzNjQ3LCJpYXQiOjE0NzEwMDczMjd9.U9c5iuybG4GIvrbQH5BT9AgllRbPL6SuIzL4Y3MW7VlCVIQOc_HFfkiLa0LNvqZiP-kFlADmnkzuuQxPq7IyaOILVYct20mrcOb_U_zMli4jg-t9P3BxHaq3ds9JlLBjz0oewd01ZQtWHgRnrGymfKAIojzHlde-aePuL1M26Eld5zoKQvCLcKAynZsjKsWF_6YdLk-uhlC5ofMOaOoPirPSPAxYvbj91z3o9XIgSHoU-umN7AJ6UQ4H-ulfftlRGK8hz0Yzpf2MHOy9OHg1u3ayfCaaf8g5zKGngcz0LgK9VAw2B31xJw-RHkPPh0Hz82FgBcabd8oEFC1c22GGT1";


        final TokenValidationResult tokenValidationResult =
            TokenValidation.validateIdTokenClaims(idToken, clientId, issuer, nonce, MAX_AGE,
                    gsonJsonService, iMobileConnectEncodeDecoder, "mc_v1.1");

        assertEquals(tokenValidationResult, TokenValidationResult.MAX_AGE_PASSED);

    }

    @Test
    public void validateIdTokenClaimsShouldReturnInvalidNonce() throws JsonDeserializationException
    {
        final String invalidNonce = "invalid_nonce";
        final String idToken =
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJub25jZSI6IjEyMzQ1Njc4OTAiLCJhdWQiOiJ4LWNsaWVudGlkLXgiLCJhenAiOiJ4LWNsaWVudGlkLXgiLCJpc3MiOiJodHRwOi8vbW9iaWxlY29ubmVjdC5pbyIsImV4cCI6MjE0NzQ4MzY0NywiYXV0aF90aW1lIjoyMTQ3NDgzNjQ3LCJpYXQiOjE0NzEwMDczMjd9.U9c5iuybG4GIvrbQH5BT9AgllRbPL6SuIzL4Y3MW7VlCVIQOc_HFfkiLa0LNvqZiP-kFlADmnkzuuQxPq7IyaOILVYct20mrcOb_U_zMli4jg-t9P3BxHaq3ds9JlLBjz0oewd01ZQtWHgRnrGymfKAIojzHlde-aePuL1M26Eld5zoKQvCLcKAynZsjKsWF_6YdLk-uhlC5ofMOaOoPirPSPAxYvbj91z3o9XIgSHoU-umN7AJ6UQ4H-ulfftlRGK8hz0Yzpf2MHOy9OHg1u3ayfCaaf8g5zKGngcz0LgK9VAw2B31xJw-RHkPPh0Hz82FgBcabd8oEFC1c22GGT1";


        final TokenValidationResult tokenValidationResult =
            TokenValidation.validateIdTokenClaims(idToken, clientId, issuer, invalidNonce, MAX_AGE,
                    gsonJsonService, iMobileConnectEncodeDecoder, "mc_v1.1");

        assertEquals(tokenValidationResult, TokenValidationResult.INVALID_NONCE);

    }

    @Test
    public void validateIdTokenClaimsShouldReturnInvalidIssuer() throws JsonDeserializationException
    {
        final String invalidIssuer = "invalid_issuer";
        final String idToken =
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJub25jZSI6IjEyMzQ1Njc4OTAiLCJhdWQiOiJ4LWNsaWVudGlkLXgiLCJhenAiOiJ4LWNsaWVudGlkLXgiLCJpc3MiOiJodHRwOi8vbW9iaWxlY29ubmVjdC5pbyIsImV4cCI6MjE0NzQ4MzY0NywiYXV0aF90aW1lIjoyMTQ3NDgzNjQ3LCJpYXQiOjE0NzEwMDczMjd9.U9c5iuybG4GIvrbQH5BT9AgllRbPL6SuIzL4Y3MW7VlCVIQOc_HFfkiLa0LNvqZiP-kFlADmnkzuuQxPq7IyaOILVYct20mrcOb_U_zMli4jg-t9P3BxHaq3ds9JlLBjz0oewd01ZQtWHgRnrGymfKAIojzHlde-aePuL1M26Eld5zoKQvCLcKAynZsjKsWF_6YdLk-uhlC5ofMOaOoPirPSPAxYvbj91z3o9XIgSHoU-umN7AJ6UQ4H-ulfftlRGK8hz0Yzpf2MHOy9OHg1u3ayfCaaf8g5zKGngcz0LgK9VAw2B31xJw-RHkPPh0Hz82FgBcabd8oEFC1c22GGT1";


        final TokenValidationResult tokenValidationResult =
            TokenValidation.validateIdTokenClaims(idToken, clientId, invalidIssuer, nonce, MAX_AGE,
                    gsonJsonService, iMobileConnectEncodeDecoder, "mc_v1.1");

        assertEquals(tokenValidationResult, TokenValidationResult.INVALID_ISSUER);
    }

    @Test
    public void validateIdTokenClaimsShouldReturnInvalidAudAndAzp() throws JsonDeserializationException
    {
        final String idToken =
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJub25jZSI6IjEyMzQ1Njc4OTAiLCJhdWQiOiJ4LWNsaWVudGlkLXgiLCJhenAiOiJ4LWNsaWVudGlkLXgiLCJpc3MiOiJodHRwOi8vbW9iaWxlY29ubmVjdC5pbyIsImV4cCI6MjE0NzQ4MzY0NywiYXV0aF90aW1lIjoyMTQ3NDgzNjQ3LCJpYXQiOjE0NzEwMDczMjd9.U9c5iuybG4GIvrbQH5BT9AgllRbPL6SuIzL4Y3MW7VlCVIQOc_HFfkiLa0LNvqZiP-kFlADmnkzuuQxPq7IyaOILVYct20mrcOb_U_zMli4jg-t9P3BxHaq3ds9JlLBjz0oewd01ZQtWHgRnrGymfKAIojzHlde-aePuL1M26Eld5zoKQvCLcKAynZsjKsWF_6YdLk-uhlC5ofMOaOoPirPSPAxYvbj91z3o9XIgSHoU-umN7AJ6UQ4H-ulfftlRGK8hz0Yzpf2MHOy9OHg1u3ayfCaaf8g5zKGngcz0LgK9VAw2B31xJw-RHkPPh0Hz82FgBcabd8oEFC1c22GGT1";

        final String invalid_clientid = "invalid_clientid";
        final TokenValidationResult tokenValidationResult =
            TokenValidation.validateIdTokenClaims(idToken, invalid_clientid, issuer, nonce, MAX_AGE,
                    gsonJsonService, iMobileConnectEncodeDecoder, "mc_v1.1");

        assertEquals(tokenValidationResult, TokenValidationResult.INVALID_AUD_AND_AZP);
    }

    @Test
    public void validateIdTokenClaimsShouldReturnMaxAgePassed() throws JsonDeserializationException
    {
        final int maxAgeExpired = 0;
        final String idToken =
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJub25jZSI6IjEyMzQ1Njc4OTAiLCJhdWQiOiJ4LWNsaWVudGlkLXgiLCJhenAiOiJ4LWNsaWVudGlkLXgiLCJpc3MiOiJodHRwOi8vbW9iaWxlY29ubmVjdC5pbyIsImV4cCI6MjE0NzQ4MzY0NywiYXV0aF90aW1lIjoyMTQ3NDgzNjQ3LCJpYXQiOjE0NzEwMDczMjd9.U9c5iuybG4GIvrbQH5BT9AgllRbPL6SuIzL4Y3MW7VlCVIQOc_HFfkiLa0LNvqZiP-kFlADmnkzuuQxPq7IyaOILVYct20mrcOb_U_zMli4jg-t9P3BxHaq3ds9JlLBjz0oewd01ZQtWHgRnrGymfKAIojzHlde-aePuL1M26Eld5zoKQvCLcKAynZsjKsWF_6YdLk-uhlC5ofMOaOoPirPSPAxYvbj91z3o9XIgSHoU-umN7AJ6UQ4H-ulfftlRGK8hz0Yzpf2MHOy9OHg1u3ayfCaaf8g5zKGngcz0LgK9VAw2B31xJw-RHkPPh0Hz82FgBcabd8oEFC1c22GGT1";


        final TokenValidationResult tokenValidationResult =
            TokenValidation.validateIdTokenClaims(idToken, clientId, issuer, nonce, maxAgeExpired,
                    gsonJsonService, iMobileConnectEncodeDecoder, "mc_v1.1");

        assertEquals(tokenValidationResult, TokenValidationResult.MAX_AGE_PASSED);

    }

    @Test
    public void validateAccessTokenShouldReturnValid()
    {
        RequestTokenResponseData requestTokenResponseData = new RequestTokenResponseData.Builder()
            .withAccessToken("accessToken")
            .withExpiresIn(10000L)
            .build();

        final TokenValidationResult tokenValidationResultNull =
            TokenValidation.validateAccessToken(requestTokenResponseData);

        assertEquals(tokenValidationResultNull, TokenValidationResult.VALID);

    }

    @Test
    public void validateAccessTokenShouldReturnAccessTokenMissing()
    {
        RequestTokenResponseData rtrdNullAccessToken =
            new RequestTokenResponseData.Builder().withAccessToken(null).build();

        final TokenValidationResult tokenValidationResultNull =
            TokenValidation.validateAccessToken(rtrdNullAccessToken);

        assertEquals(tokenValidationResultNull, TokenValidationResult.ACCESS_TOKEN_MISSING);

        RequestTokenResponseData rtrdEmptyAccessToken =
            new RequestTokenResponseData.Builder().withAccessToken("").build();

        final TokenValidationResult tokenValidationResultEmpty =
            TokenValidation.validateAccessToken(rtrdEmptyAccessToken);

        assertEquals(tokenValidationResultEmpty, TokenValidationResult.ACCESS_TOKEN_MISSING);
    }

    @Test
    public void validateAccessTokenShouldReturnAccessTokenExpired() throws InterruptedException
    {
        RequestTokenResponseData requestTokenResponseData = new RequestTokenResponseData.Builder()
            .withAccessToken("accessToken")
            .withExpiresIn(0L)
            .build();

        Thread.sleep(1000);

        final TokenValidationResult tokenValidationResultNull =
            TokenValidation.validateAccessToken(requestTokenResponseData);

        assertEquals(tokenValidationResultNull, TokenValidationResult.ACCESS_TOKEN_EXPIRED);

    }
}
