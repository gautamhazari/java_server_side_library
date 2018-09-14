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
package com.gsma.mobileconnect.r2;

import com.gsma.mobileconnect.r2.authentication.AuthenticationOptions;
import com.gsma.mobileconnect.r2.authentication.AuthenticationService;
import com.gsma.mobileconnect.r2.cache.CacheAccessException;
import com.gsma.mobileconnect.r2.cache.DiscoveryCache;
import com.gsma.mobileconnect.r2.constants.Parameters;
import com.gsma.mobileconnect.r2.discovery.*;
import com.gsma.mobileconnect.r2.encoding.DefaultEncodeDecoder;
import com.gsma.mobileconnect.r2.exceptions.InvalidResponseException;
import com.gsma.mobileconnect.r2.exceptions.RequestFailedException;
import com.gsma.mobileconnect.r2.json.IJsonService;
import com.gsma.mobileconnect.r2.json.JacksonJsonService;
import com.gsma.mobileconnect.r2.json.JsonDeserializationException;
import com.gsma.mobileconnect.r2.rest.*;
import com.gsma.mobileconnect.r2.utils.HttpUtils;
import com.gsma.mobileconnect.r2.utils.KeyValuePair;
import com.gsma.mobileconnect.r2.utils.TestUtils;
import junit.framework.Assert;
import org.mockito.Mockito;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

/**
 * Tests {@link MobileConnectWebInterface}
 *
 * @since 2.0
 */
@SuppressWarnings("UnusedParameters")
public class MobileConnectWebInterfaceTest
{
    private final MobileConnectConfig config = new MobileConnectConfig.Builder()
        .withClientId("zxcvbnm")
        .withClientSecret("asdfghjkl")
        .withDiscoveryUrl(URI.create("http://discovery/test"))
        .withRedirectUrl(URI.create("http://redirect/test"))
        .build();
    private final IJsonService jsonService = new JacksonJsonService();
    private final MockRestClient restClient = new MockRestClient();
    private final MobileConnect mobileConnect = MobileConnect
        .builder(this.config, new DefaultEncodeDecoder(), new DiscoveryCache.Builder().withJsonService(jsonService).withMaxCacheSize(999999999).build(),
                new DiscoveryCache.Builder().withJsonService(jsonService).withMaxCacheSize(999999999).build())
        .withRestClient(this.restClient)
        .build();

    private final IDiscoveryService discoveryService = this.mobileConnect.getDiscoveryService();
    private final MobileConnectWebInterface mcWebInterface =
        this.mobileConnect.getMobileConnectWebInterface();

    private final HttpServletRequest request = mock(HttpServletRequest.class);

    private DiscoveryResponse completeDiscovery()
        throws RequestFailedException, InvalidResponseException
    {
        this.restClient
            .addResponse(TestUtils.AUTHENTICATION_RESPONSE)
            .addResponse(TestUtils.PROVIDER_METADATA_RESPONSE);

        return this.discoveryService.completeSelectedOperatorDiscovery(this.config,
            this.config.getRedirectUrl(), "111", "11");
    }

    @BeforeMethod
    public void beforeMethod() throws CacheAccessException
    {
        this.discoveryService.getCache().clear();
    }

    @AfterMethod
    public void afterMethod()
    {
        assertEquals(this.restClient.reset().size(), 0);
    }

    @DataProvider
    public Object[][] startAuthnData()
    {
        return new Object[][] {
            //
            {null, new String[] {"openid"}, "mc_authz"},
            //
            {new AuthenticationOptions.Builder().withContext("context").build(),
             new String[] {"mc_authz"}, "mc_authn"},
            //
            {new AuthenticationOptions.Builder()
                 .withScope("mc_authz")
                 .withContext("context")
                 .withBindingMessage("message").build(), new String[] {"mc_authz"}, "mc_authn"},
            //
            {new AuthenticationOptions.Builder()
                 .withScope("mc_identity_phone")
                 .withContext("context")
                 .withBindingMessage("message").build(),
             new String[] {"mc_authz", "mc_identity_phone"}, "mc_authn"}};
    }

    @Test(dataProvider = "startAuthnData")
    public void startAuthenticationScopes(final AuthenticationOptions authnOptions,
        final String[] includes, final String exclude)
        throws RequestFailedException, InvalidResponseException
    {
        final DiscoveryResponse discoveryResponse = this.completeDiscovery();

        final MobileConnectRequestOptions options = authnOptions == null
                                                    ? null
                                                    : new MobileConnectRequestOptions.Builder()
                                                        .withAuthenticationOptions(authnOptions)
                                                        .build();

        final MobileConnectStatus status =
            this.mcWebInterface.startAuthentication(this.request, discoveryResponse,
                "1111222233334444", "state", "nonce", options, "mc_v1.1");

        assertNotNull(status);
        assertEquals(status.getResponseType(), MobileConnectStatus.ResponseType.AUTHENTICATION);

        final String scope = HttpUtils.extractQueryValue(URI.create(status.getUrl()), "scope");

        assertNotNull(scope);
        for (final String include : includes)
        {
            assertTrue(scope.contains(include));
        }
        assertFalse(scope.contains(exclude));
    }

    @Test
    public void startAuthenticationShouldSetClientNameWhenAuthz()
        throws RequestFailedException, InvalidResponseException
    {
        final DiscoveryResponse discoveryResponse = this.completeDiscovery();

        final MobileConnectRequestOptions options = new MobileConnectRequestOptions.Builder()
            .withAuthenticationOptions(new AuthenticationOptions.Builder()
                .withScope("mc_identity_phone")
                .withContext("context")
                .withBindingMessage("message")
                .build())
            .build();

        final MobileConnectStatus status =
            this.mcWebInterface.startAuthentication(this.request, discoveryResponse,
                "1111222233334444", "state", "nonce", options, "mc_v1.1");

        final String clientName =
            HttpUtils.extractQueryValue(URI.create(status.getUrl()), "client_name");

        assertEquals(clientName, "test1"); // set in the response under TestUtils
    }

    @Test
    public void requestUserInfoReturnsUserInfo() throws JsonDeserializationException
    {
        this.restClient.addResponse(TestUtils.USERINFO_RESPONSE);
        final DiscoveryResponse discoveryResponse =
            DiscoveryResponse.fromRestResponse(TestUtils.AUTHENTICATION_RESPONSE, this.jsonService);

        final MobileConnectStatus status =
            this.mcWebInterface.requestUserInfo(this.request, discoveryResponse,
                "zaqwsxcderfvbgtyhnmjukilop");

        assertEquals(status.getResponseType(), MobileConnectStatus.ResponseType.USER_INFO);
        assertNotNull(status.getIdentityResponse());
    }

    @Test
    public void requestUserInfoReturnsErrorWhenNoUserInfoUrl() throws JsonDeserializationException
    {
        final DiscoveryResponse discoveryResponse =
            DiscoveryResponse.fromRestResponse(TestUtils.AUTHENTICATION_NO_URI_RESPONSE,
                this.jsonService);

        final MobileConnectStatus status =
            this.mcWebInterface.requestUserInfo(this.request, discoveryResponse,
                "zaqwsxcderfvbgtyhnmjukilop");

        assertEquals(status.getResponseType(), MobileConnectStatus.ResponseType.ERROR);
        assertNull(status.getIdentityResponse());
        assertNotNull(status.getErrorCode());
        assertNotNull(status.getErrorMessage());
    }

    @Test
    public void requestUserInfoShouldUseSdkSessionCache()
        throws JsonDeserializationException, CacheAccessException
    {
        this.restClient.addResponse(TestUtils.USERINFO_RESPONSE);

        final DiscoveryResponse discoveryResponse =
            DiscoveryResponse.fromRestResponse(TestUtils.AUTHENTICATION_RESPONSE, this.jsonService);
        this.discoveryService.getCache().add("sessionid", discoveryResponse);

        final MobileConnectStatus status =
            this.mcWebInterface.requestUserInfo(this.request, "sessionid",
                "zaqwsxcderfvbgtyhnmjukilop");

        assertEquals(status.getResponseType(), MobileConnectStatus.ResponseType.USER_INFO);
        assertNotNull(status.getIdentityResponse());
    }

    @Test
    public void requestTokenShouldReturnErrorForInvalidSession()
    {
        final MobileConnectStatus status =
            this.mcWebInterface.requestToken(this.request, "invalidid", URI.create("http://test"),
                "state", "nonce", null, "mc_v1.1");

        assertEquals(status.getResponseType(), MobileConnectStatus.ResponseType.ERROR);
        assertEquals(status.getErrorCode(), "sdksession_not_found");
    }

    @Test
    public void requestTokenShouldReturnErrorForCacheDisabled()
    {
        final MobileConnectConfig config = new MobileConnectConfig.Builder()
            .withCacheResponsesWithSessionId(false)
            .withClientId("id")
            .withClientSecret("secret")
            .withDiscoveryUrl(URI.create("http://discovery"))
            .withRedirectUrl(URI.create("http://redirect"))
            .build();

        final MobileConnectWebInterface mcWebInterface =
            MobileConnect.buildWebInterface(config, new DefaultEncodeDecoder(), new DiscoveryCache.Builder().withJsonService(jsonService).withMaxCacheSize(999999999).build(),
                    new DiscoveryCache.Builder().withJsonService(jsonService).withMaxCacheSize(999999999).build());

        final MobileConnectStatus status =
            mcWebInterface.requestToken(this.request, "invalidid", URI.create("http://test"),
                "state", "nonce", null, "mc_v1.1");

        assertEquals(status.getResponseType(), MobileConnectStatus.ResponseType.ERROR);
        assertEquals(status.getErrorCode(), "cache_disabled");
    }

    @Test(dataProvider = "startAuthnData")
    public void testHeadlessAuthenticationGetTokenButValidationFails(final AuthenticationOptions authnOptions,
        final String[] includes, final String exclude)
        throws RequestFailedException, InvalidResponseException, URISyntaxException
    {
         final DiscoveryResponse discoveryResponse = this.completeDiscovery();
         this.restClient.addResponse(TestUtils.VALIDATED_TOKEN_RESPONSE);
         this.restClient.addResponse(TestUtils.JWKS_RESPONSE);

         final MobileConnectRequestOptions options = authnOptions == null
         ? null
         : new MobileConnectRequestOptions.Builder()
         .withAuthenticationOptions(authnOptions)
         .build();

         final MobileConnectStatus status =
         this.mcWebInterface.requestHeadlessAuthentication(this.request, discoveryResponse,
         "1111222233334444", "state", "81991496-48bb-4d13-bd0c-117d994411a6", options, "mc_v1.1");

        assertNotNull(status);

        // Since the token validation fails as the token is an old token
        assertEquals(status.getResponseType(), MobileConnectStatus.ResponseType.ERROR);

        assertEquals(status.getErrorCode(), "Invalid Id Token");
        assertEquals(status.getErrorMessage(), "Token validation failed");

        assertEquals(status.getRequestTokenResponse().getResponseCode(), 202);

        assertEquals(status.getRequestTokenResponse().getResponseData().getAccessToken(),
            "966ad150-16c5-11e6-944f-43079d13e2f3");
        assertEquals(status.getRequestTokenResponse().getResponseData().getIdToken(),
            "eyJhbGciOiJSUzI1NiIsImtpZCI6IlBIUE9QLTAwIn0.eyJpc3MiOiJodHRwczpcL1wvcmVmZXJlbmNlLm1vYmlsZWNvbm5lY3QuaW9cL21vYmlsZWNvbm5lY3QiLCJzdWIiOiI0MTE0MjFCMC0zOEQ2LTY1NjgtQTUzQS1ERjk5NjkxQjdFQjYiLCJhdWQiOlsieC1aV1JoTmpVM09XSTNNR0l3WVRSaCJdLCJleHAiOjE0NzQ2MjYzMzAsImlhdCI6MTQ3NDYyNjAzMCwibm9uY2UiOiI4MTk5MTQ5Ni00OGJiLTRkMTMtYmQwYy0xMTdkOTk0NDExYTYiLCJhdF9oYXNoIjoiNTZGMXo3RjZ3eWhUYUhVY1ZGY0xJQSIsImF1dGhfdGltZSI6MTQ3NDYyNjAyMCwiYWNyIjoiMiIsImFtciI6WyJTSU1fUElOIl0sImF6cCI6IngtWldSaE5qVTNPV0kzTUdJd1lUUmgifQ.TYcvfIHeKigkvjYta6fy90EffiA6u6NFCSIPlPM2WxEUi8Kxc5JIrjXnM8l0rFJOLmgNFUBpSqIRhuxwZkUV52KWf8jzswi3jTI8wEjonbjgviz7c6WzlZdb0Pw5kUEWy2xMam7VprESphPaIkHCDor2yR2g6Uq3Wtqyg7MCqek");

        assertEquals(status.getRequestTokenResponse().getDecodedIdTokenPayload(),
            "{\"iss\":\"https:\\/\\/reference.mobileconnect.io\\/mobileconnect\",\"sub\":\"411421B0-38D6-6568-A53A-DF99691B7EB6\",\"aud\":[\"x-ZWRhNjU3OWI3MGIwYTRh\"],\"exp\":1474626330,\"iat\":1474626030,\"nonce\":\"81991496-48bb-4d13-bd0c-117d994411a6\",\"at_hash\":\"56F1z7F6wyhTaHUcVFcLIA\",\"auth_time\":1474626020,\"acr\":\"2\",\"amr\":[\"SIM_PIN\"],\"azp\":\"x-ZWRhNjU3OWI3MGIwYTRh\"}");
    }

    @Test(dataProvider = "startAuthnData")
    public void testHeadlessAuthenticationGetTokenAccessTokenNull(final AuthenticationOptions authnOptions,
        final String[] includes, final String exclude)
        throws RequestFailedException, InvalidResponseException, URISyntaxException
    {
        final DiscoveryResponse discoveryResponse = this.completeDiscovery();
        this.restClient.addResponse(TestUtils.INVALID_TOKEN_RESPONSE_ACCESS_TOKEN_MISSING);
        this.restClient.addResponse(TestUtils.JWKS_RESPONSE);

        final MobileConnectRequestOptions options = authnOptions == null
                                                    ? null
                                                    : new MobileConnectRequestOptions.Builder()
                                                        .withAuthenticationOptions(authnOptions)
                                                        .build();

        final MobileConnectStatus status =
            this.mcWebInterface.requestHeadlessAuthentication(this.request, discoveryResponse,
                "1111222233334444", "state", "81991496-48bb-4d13-bd0c-117d994411a6", options, "mc_v1.1");

        assertNotNull(status);

        // Since the token validation fails as the token is an old token
        assertEquals(status.getResponseType(), MobileConnectStatus.ResponseType.ERROR);

        assertEquals(status.getErrorCode(), "Invalid Access Token");
        assertEquals(status.getErrorMessage(), "Access Token validation failed");

        assertEquals(status.getRequestTokenResponse().getResponseCode(), 202);

        assertNull(status.getRequestTokenResponse().getResponseData().getAccessToken());
        assertEquals(status.getRequestTokenResponse().getResponseData().getIdToken(),
            "eyJhbGciOiJSUzI1NiIsImtpZCI6IlBIUE9QLTAwIn0.eyJpc3MiOiJodHRwczpcL1wvcmVmZXJlbmNlLm1vYmlsZWNvbm5lY3QuaW9cL21vYmlsZWNvbm5lY3QiLCJzdWIiOiI0MTE0MjFCMC0zOEQ2LTY1NjgtQTUzQS1ERjk5NjkxQjdFQjYiLCJhdWQiOlsieC1aV1JoTmpVM09XSTNNR0l3WVRSaCJdLCJleHAiOjE0NzQ2MjYzMzAsImlhdCI6MTQ3NDYyNjAzMCwibm9uY2UiOiI4MTk5MTQ5Ni00OGJiLTRkMTMtYmQwYy0xMTdkOTk0NDExYTYiLCJhdF9oYXNoIjoiNTZGMXo3RjZ3eWhUYUhVY1ZGY0xJQSIsImF1dGhfdGltZSI6MTQ3NDYyNjAyMCwiYWNyIjoiMiIsImFtciI6WyJTSU1fUElOIl0sImF6cCI6IngtWldSaE5qVTNPV0kzTUdJd1lUUmgifQ.TYcvfIHeKigkvjYta6fy90EffiA6u6NFCSIPlPM2WxEUi8Kxc5JIrjXnM8l0rFJOLmgNFUBpSqIRhuxwZkUV52KWf8jzswi3jTI8wEjonbjgviz7c6WzlZdb0Pw5kUEWy2xMam7VprESphPaIkHCDor2yR2g6Uq3Wtqyg7MCqek");

        assertEquals(status.getRequestTokenResponse().getDecodedIdTokenPayload(),
            "{\"iss\":\"https:\\/\\/reference.mobileconnect.io\\/mobileconnect\",\"sub\":\"411421B0-38D6-6568-A53A-DF99691B7EB6\",\"aud\":[\"x-ZWRhNjU3OWI3MGIwYTRh\"],\"exp\":1474626330,\"iat\":1474626030,\"nonce\":\"81991496-48bb-4d13-bd0c-117d994411a6\",\"at_hash\":\"56F1z7F6wyhTaHUcVFcLIA\",\"auth_time\":1474626020,\"acr\":\"2\",\"amr\":[\"SIM_PIN\"],\"azp\":\"x-ZWRhNjU3OWI3MGIwYTRh\"}");
    }

    @Test(dataProvider = "startAuthnData")
    public void testHeadlessAuthenticationGetTokenButNonceDoesNotMatch(final AuthenticationOptions authnOptions,
        final String[] includes, final String exclude)
        throws RequestFailedException, InvalidResponseException, URISyntaxException
    {
        final DiscoveryResponse discoveryResponse = this.completeDiscovery();
        this.restClient.addResponse(TestUtils.VALIDATED_TOKEN_RESPONSE);

        final MobileConnectRequestOptions options = authnOptions == null
                                                    ? null
                                                    : new MobileConnectRequestOptions.Builder()
                                                        .withAuthenticationOptions(authnOptions)
                                                        .build();

        final MobileConnectStatus status =
            this.mcWebInterface.requestHeadlessAuthentication(this.request, discoveryResponse,
                "1111222233334444", "state", "81991496-48bb-4d13-bd0c-117d994411a7", options, "mc_v1.1");

        assertNotNull(status);

        // Since the token validation fails as the token is an old token
        assertEquals(status.getResponseType(), MobileConnectStatus.ResponseType.ERROR);

        assertEquals(status.getErrorCode(), "unknown_error");
       // assertEquals(status.getErrorMessage(), "nonce values do not match, possible replay attack");

    }

    @Test(dataProvider = "startAuthnData")
    public void testHeadlessAuthenticationWithoutDiscoveryResponse(final AuthenticationOptions authnOptions,
        final String[] includes, final String exclude)
        throws RequestFailedException, InvalidResponseException, URISyntaxException
    {
        final DiscoveryResponse discoveryResponse = this.completeDiscovery();
        DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder()
            .withIdentifiedMcc("111")
            .withIdentifiedMnc("11")
            .build();
        ((DiscoveryService) this.discoveryService).addCachedDiscoveryResponse(discoveryOptions,
            discoveryResponse);

        this.restClient.addResponse(TestUtils.VALIDATED_TOKEN_RESPONSE);
        this.restClient.addResponse(TestUtils.JWKS_RESPONSE);

        final MobileConnectRequestOptions options = authnOptions == null
                                                    ? null
                                                    : new MobileConnectRequestOptions.Builder()
                                                        .withAuthenticationOptions(authnOptions)
                                                        .build();

        final MobileConnectStatus status =
            this.mcWebInterface.requestHeadlessAuthentication(this.request,
                "111_11",
                "1111222233334444", "state", "81991496-48bb-4d13-bd0c-117d994411a6", options, "mc_v1.1");

        assertNotNull(status);

        // Since the token validation fails as the token is an old token
        assertEquals(status.getResponseType(), MobileConnectStatus.ResponseType.ERROR);

        assertEquals(status.getErrorCode(), "Invalid Id Token");
        assertEquals(status.getErrorMessage(), "Token validation failed");

        assertEquals(status.getRequestTokenResponse().getResponseCode(), 202);

        assertEquals(status.getRequestTokenResponse().getResponseData().getAccessToken(),
            "966ad150-16c5-11e6-944f-43079d13e2f3");
        assertEquals(status.getRequestTokenResponse().getResponseData().getIdToken(),
            "eyJhbGciOiJSUzI1NiIsImtpZCI6IlBIUE9QLTAwIn0.eyJpc3MiOiJodHRwczpcL1wvcmVmZXJlbmNlLm1vYmlsZWNvbm5lY3QuaW9cL21vYmlsZWNvbm5lY3QiLCJzdWIiOiI0MTE0MjFCMC0zOEQ2LTY1NjgtQTUzQS1ERjk5NjkxQjdFQjYiLCJhdWQiOlsieC1aV1JoTmpVM09XSTNNR0l3WVRSaCJdLCJleHAiOjE0NzQ2MjYzMzAsImlhdCI6MTQ3NDYyNjAzMCwibm9uY2UiOiI4MTk5MTQ5Ni00OGJiLTRkMTMtYmQwYy0xMTdkOTk0NDExYTYiLCJhdF9oYXNoIjoiNTZGMXo3RjZ3eWhUYUhVY1ZGY0xJQSIsImF1dGhfdGltZSI6MTQ3NDYyNjAyMCwiYWNyIjoiMiIsImFtciI6WyJTSU1fUElOIl0sImF6cCI6IngtWldSaE5qVTNPV0kzTUdJd1lUUmgifQ.TYcvfIHeKigkvjYta6fy90EffiA6u6NFCSIPlPM2WxEUi8Kxc5JIrjXnM8l0rFJOLmgNFUBpSqIRhuxwZkUV52KWf8jzswi3jTI8wEjonbjgviz7c6WzlZdb0Pw5kUEWy2xMam7VprESphPaIkHCDor2yR2g6Uq3Wtqyg7MCqek");

        assertEquals(status.getRequestTokenResponse().getDecodedIdTokenPayload(),
            "{\"iss\":\"https:\\/\\/reference.mobileconnect.io\\/mobileconnect\",\"sub\":\"411421B0-38D6-6568-A53A-DF99691B7EB6\",\"aud\":[\"x-ZWRhNjU3OWI3MGIwYTRh\"],\"exp\":1474626330,\"iat\":1474626030,\"nonce\":\"81991496-48bb-4d13-bd0c-117d994411a6\",\"at_hash\":\"56F1z7F6wyhTaHUcVFcLIA\",\"auth_time\":1474626020,\"acr\":\"2\",\"amr\":[\"SIM_PIN\"],\"azp\":\"x-ZWRhNjU3OWI3MGIwYTRh\"}");
    }

    @Test
    public void testRefreshTokenShouldReturnCompleteStatus()
        throws RequestFailedException, InvalidResponseException
    {
        final DiscoveryResponse discoveryResponse = this.completeDiscovery();
        this.restClient.addResponse(TestUtils.VALIDATED_TOKEN_RESPONSE);

        final MobileConnectStatus status =
            this.mcWebInterface.refreshToken(this.request, "RefreshToken", discoveryResponse);

        assertNotNull(status);

        assertEquals(status.getResponseType(), MobileConnectStatus.ResponseType.COMPLETE);

        assertEquals(status.getRequestTokenResponse().getResponseCode(), 202);
        assertEquals(status.getRequestTokenResponse().getResponseData().getAccessToken(),
            "966ad150-16c5-11e6-944f-43079d13e2f3");
    }

    @Test
    public void testRefreshTokenWithCachedDiscoveryResponse()
        throws RequestFailedException, InvalidResponseException
    {
        final DiscoveryResponse discoveryResponse = this.completeDiscovery();
        DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder()
            .withIdentifiedMcc("111")
            .withIdentifiedMnc("11")
            .build();
        ((DiscoveryService) this.discoveryService).addCachedDiscoveryResponse(discoveryOptions,
            discoveryResponse);

        this.restClient.addResponse(TestUtils.VALIDATED_TOKEN_RESPONSE);

        final MobileConnectStatus status =
            this.mcWebInterface.refreshToken(this.request, "RefreshToken", "111_11");

        assertNotNull(status);

        assertEquals(status.getResponseType(), MobileConnectStatus.ResponseType.COMPLETE);

        assertEquals(status.getRequestTokenResponse().getResponseCode(), 202);
        assertEquals(status.getRequestTokenResponse().getResponseData().getAccessToken(),
            "966ad150-16c5-11e6-944f-43079d13e2f3");
    }

    @Test
    public void testRevokeTokenShouldReturnCompleteStatus()
        throws RequestFailedException, InvalidResponseException
    {
        final DiscoveryResponse discoveryResponse = this.completeDiscovery();
        this.restClient.addResponse(TestUtils.REVOKE_TOKEN_SUCCESS_RESPONSE);

        final MobileConnectStatus status =
            this.mcWebInterface.revokeToken(this.request, "AccessToken",
                Parameters.ACCESS_TOKEN_HINT, discoveryResponse);

        assertNotNull(status);

        assertEquals(status.getResponseType(), MobileConnectStatus.ResponseType.COMPLETE);
        assertEquals(status.getOutcome(), AuthenticationService.REVOKE_TOKEN_SUCCESS);
    }

    @Test
    public void testRevokeTokenWithCachedDiscoveryResponse()
        throws RequestFailedException, InvalidResponseException
    {
        final DiscoveryResponse discoveryResponse = this.completeDiscovery();
        DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder()
            .withIdentifiedMcc("111")
            .withIdentifiedMnc("11")
            .build();
        ((DiscoveryService) this.discoveryService).addCachedDiscoveryResponse(discoveryOptions,
            discoveryResponse);
        this.restClient.addResponse(TestUtils.REVOKE_TOKEN_SUCCESS_RESPONSE);

        final MobileConnectStatus status =
            this.mcWebInterface.revokeToken(this.request, "AccessToken",
                Parameters.ACCESS_TOKEN_HINT, "111_11");

        assertNotNull(status);

        assertEquals(status.getResponseType(), MobileConnectStatus.ResponseType.COMPLETE);
        assertEquals(status.getOutcome(), AuthenticationService.REVOKE_TOKEN_SUCCESS);
    }

    @Test
    private void attemptDiscoveryManuallyTest() throws JsonDeserializationException, RequestFailedException {
        IRestClient restClientLocal = Mockito.mock(RestClient.class);

        MobileConnect mobileConnectLocal = MobileConnect
                .builder(this.config, new DefaultEncodeDecoder(), new DiscoveryCache.Builder().withJsonService(jsonService).withMaxCacheSize(999999999).build(),
                        new DiscoveryCache.Builder().withJsonService(jsonService).withMaxCacheSize(999999999).build())
                .withRestClient(restClientLocal)
                .build();

        MobileConnectWebInterface mobileConnectWebInterface =
                mobileConnectLocal.getMobileConnectWebInterface();

        String secretKey = "secret";
        String subscriberId = "subid";
        String name = "AppShortName";
        String clientKey = "clientKey";

        OperatorUrls operatorUrls = new OperatorUrls.Builder()
                .withAuthorizationUrl("https://authorize")
                .withRequestTokenUrl("https://accesstoken")
                .withUserInfoUrl("https://userinfo")
                .withRevokeTokenUrl("https://revoke")
                .withPremiumInfoUri("https://premiuminfo")
                .withScopeUri("openid profile email")
                .withProviderMetadataUri("https://providemetadata")
                .withJwksUri("https://jwks").build();

        String providerMetadata = "{}";
        RestResponse response = new RestResponse.Builder()
                .withStatusCode(200)
                .withMethod("GET")
                .withContent(providerMetadata).build();

        when(restClientLocal.get(any(URI.class), (RestAuthentication) eq(null), anyString(), (String) eq(null),
                (List<KeyValuePair>) eq(null), (Iterable<KeyValuePair>) eq(null)))
                .thenReturn(response).thenReturn(response);

        DiscoveryResponse discoveryResponse = mobileConnectWebInterface.generateDiscoveryManually(secretKey, clientKey, subscriberId, name, operatorUrls);
        MobileConnectStatus status = mobileConnectWebInterface.attemptManuallyDiscovery(discoveryResponse);

        Assert.assertNotNull(status.getSdkSession(), "sdk session is null");
    }
}
