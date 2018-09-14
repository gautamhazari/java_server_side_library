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
package com.gsma.mobileconnect.r2.authentication;

import com.gsma.mobileconnect.r2.MobileConnectConfig;
import com.gsma.mobileconnect.r2.claims.Claims;
import com.gsma.mobileconnect.r2.claims.ClaimsParameter;
import com.gsma.mobileconnect.r2.constants.Parameters;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.discovery.OperatorUrls;
import com.gsma.mobileconnect.r2.discovery.SupportedVersions;
import com.gsma.mobileconnect.r2.exceptions.HeadlessOperationFailedException;
import com.gsma.mobileconnect.r2.exceptions.InvalidResponseException;
import com.gsma.mobileconnect.r2.exceptions.RequestFailedException;
import com.gsma.mobileconnect.r2.json.IJsonService;
import com.gsma.mobileconnect.r2.json.JacksonJsonService;
import com.gsma.mobileconnect.r2.json.JsonDeserializationException;
import com.gsma.mobileconnect.r2.json.JsonSerializationException;
import com.gsma.mobileconnect.r2.rest.IRestClient;
import com.gsma.mobileconnect.r2.rest.RestAuthentication;
import com.gsma.mobileconnect.r2.rest.RestClient;
import com.gsma.mobileconnect.r2.rest.RestResponse;
import com.gsma.mobileconnect.r2.utils.HttpUtils;
import com.gsma.mobileconnect.r2.utils.KeyValuePair;
import com.gsma.mobileconnect.r2.utils.TestUtils;
import org.apache.http.HttpStatus;
import org.mockito.Mockito;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

/**
 * Tests {@link AuthenticationService}
 *
 * @since 2.0
 */
@SuppressWarnings("unchecked")
public class AuthenticationServiceTest
{
    private final static URI REDIRECT_URL = URI.create("http://localhost:8080/");
    private final static URI AUTHORIZE_URL = URI.create("http://localhost:8080/authorize");
    private final static URI TOKEN_URL = URI.create("http://localhost:8080/token");
    private final IJsonService jsonService = new JacksonJsonService();
    private final IRestClient restClient = Mockito.mock(RestClient.class);

    private final SupportedVersions defaultVersions =
        new SupportedVersions.Builder().addSupportedVersion("openid", "mc_v1.2").build();

    private final IAuthenticationService authentication = new AuthenticationService.Builder()
            .withRestClient(this.restClient)
            .withJsonService(this.jsonService)
            .build();

    private final MobileConnectConfig config = new MobileConnectConfig.Builder()
        .withClientId("1234567890")
        .withClientSecret("1234567890")
        .withDiscoveryUrl(URI.create("http://localhost:8080/v2/discovery/"))
        .withRedirectUrl(REDIRECT_URL)
        .build();

    @Test
    public void startAuthenticationReturnsUrlWhenArgumentsValid()
    {
        final StartAuthenticationResponse response =
            this.authentication.startAuthentication(this.config.getClientId(), null, AUTHORIZE_URL,
                REDIRECT_URL, "state", "nonce", null, null, null, "mc_v1.1");

        assertNotNull(response);
        assertTrue(response.getUrl().toString().contains(AUTHORIZE_URL.toString()));
    }

    @DataProvider
    public Object[][] startAuthenticationCoercesScopeData()
    {
        return new Object[][] {{"mc_v1.1", "openid mc_authn", "openid"},
                               {"mc_v1.2", "openid mc_authn", "openid mc_authn"},
                               {"mc_v1.2", "openid", "openid mc_authz"}};
    }

    @Test(dataProvider = "startAuthenticationCoercesScopeData")
    public void startAuthenticationCoercesScope(final String version, final String initialScope,
        final String expectedScope)
    {
        final SupportedVersions versions =
            new SupportedVersions.Builder().addSupportedVersion("openid", version).build();
        final AuthenticationOptions.Builder optionsBuilder =
            new AuthenticationOptions.Builder().withScope(initialScope);

        if(version.equals("mc_v1.2")){
            optionsBuilder.withContext("context").withBindingMessage("msg").withClientName("client_name");
        }

        final AuthenticationOptions options = optionsBuilder.build();

        final StartAuthenticationResponse response =
            this.authentication.startAuthentication(this.config.getClientId(), null, AUTHORIZE_URL,
                REDIRECT_URL, "state", "nonce", null, versions, options, "mc_v1.1");
        final String actualScope = HttpUtils.extractQueryValue(response.getUrl(), "scope");

        assertNotNull(actualScope);
        assertEqualsNoOrder(actualScope.split("\\s"), expectedScope.split("\\s"));
    }

    @Test
    public void startAuthenticationWithMc_AuthzScopeShouldAddAuthorizationArguments()
    {
        final AuthenticationOptions options = new AuthenticationOptions.Builder()
            .withScope("openid mc_authz")
            .withClientName("test")
            .withContext("context-val")
            .withBindingMessage("binding-val")
            .build();

        final StartAuthenticationResponse response =
            this.authentication.startAuthentication(this.config.getClientId(), null, AUTHORIZE_URL,
                REDIRECT_URL, "state", "nonce", null, this.defaultVersions, options, "mc_v1.1");

        assertEquals(HttpUtils.extractQueryValue(response.getUrl(), "context"), "context-val");
        assertEquals(HttpUtils.extractQueryValue(response.getUrl(), "client_name"), "test");
        assertEquals(HttpUtils.extractQueryValue(response.getUrl(), "binding_message"),
            "binding-val");
    }

    @Test
    public void startAuthenticationWithContextShouldUseAuthorizationScope()
    {
        final String initialScope = "openid";
        final String expectedScope = "openid mc_authz";
        final AuthenticationOptions options = new AuthenticationOptions.Builder()
            .withScope(initialScope)
            .withClientName("clientName-val")
            .withContext("context-val")
            .build();

        final StartAuthenticationResponse response =
            this.authentication.startAuthentication(this.config.getClientId(), null, AUTHORIZE_URL,
                REDIRECT_URL, "state", "nonce", null, this.defaultVersions, options, "mc_v1.1");

        assertEquals(HttpUtils.extractQueryValue(response.getUrl(), "scope"), expectedScope);
    }

    @Test()
    public void startAuthenticationWithMobileConnectProductScopeShouldUseAuthorization()
    {
        final String initialScope = "openid mc_authn mc_identity_phone";
        final AuthenticationOptions options =
            new AuthenticationOptions.Builder().withScope(initialScope).build();

        this.authentication.startAuthentication(this.config.getClientId(), null, AUTHORIZE_URL,
            REDIRECT_URL, "state", "nonce", null, this.defaultVersions, options, "mc_v1.1");
    }

    @Test
    public void startAuthenticationWithClaimsShouldEncodeAndIncludeClaims()
        throws JsonSerializationException
    {
        final ClaimsParameter claimsParameter = new ClaimsParameter.Builder()
            .withIdToken(new Claims.Builder().addEssential("test1"))
            .withUserinfo(new Claims.Builder().add("test2", false, "testvalue"))
            .build();

        final AuthenticationOptions options =
            new AuthenticationOptions.Builder().withClaims(claimsParameter).build();
        final String expectedClaims = this.jsonService.serialize(claimsParameter);

        final StartAuthenticationResponse response =
            this.authentication.startAuthentication(this.config.getClientId(), null, AUTHORIZE_URL,
                REDIRECT_URL, "state", "nonce", null, this.defaultVersions, options, "mc_v1.1");

        assertEquals(HttpUtils.extractQueryValue(response.getUrl(), "claims"), expectedClaims);
    }

    @Test
    public void startAuthenticationWithClaimsShouldEncodeAndIncludeClaimsJson()
    {
        final String claims = null;
        final AuthenticationOptions options =
                new AuthenticationOptions.Builder().withClaimsJson(claims).build();

        final StartAuthenticationResponse response =
                this.authentication.startAuthentication(this.config.getClientId(), null, AUTHORIZE_URL,
                        REDIRECT_URL, "state", "nonce", null, this.defaultVersions, options, "mc_v1.1");

        assertEquals(HttpUtils.extractQueryValue(response.getUrl(), "claims"), claims);
    }

    @Test
    public void requestTokenShouldHandleTokenResponse()
        throws RequestFailedException, InvalidResponseException
    {
        when(this.restClient.postFormData(eq(TOKEN_URL), isA(RestAuthentication.class), anyString(),
            anyListOf(KeyValuePair.class), isNull(String.class),
            isNull(Iterable.class))).thenReturn(TestUtils.TOKEN_RESPONSE);

        final RequestTokenResponse response =
            this.authentication.requestToken(this.config.getClientId(), this.config.getClientSecret(), null,
                    TOKEN_URL, REDIRECT_URL, "code");

        assertNotNull(response);
        assertEquals(response.getResponseCode(), HttpStatus.SC_ACCEPTED);
        assertNotNull(response.getResponseData());
        assertEquals(response.getResponseData().getAccessToken(),
            "966ad150-16c5-11e6-944f-43079d13e2f3");
    }

    @Test
    public void requestTokenShouldHandleInvalidCodeResponse()
        throws RequestFailedException, InvalidResponseException
    {
        when(this.restClient.postFormData(eq(TOKEN_URL), isA(RestAuthentication.class),anyString(),
            anyListOf(KeyValuePair.class), isNull(String.class),
            isNull(Iterable.class))).thenReturn(TestUtils.INVALID_CODE_RESPONSE);

        final RequestTokenResponse response =
            this.authentication.requestToken(this.config.getClientId(),
                this.config.getClientSecret(), null, TOKEN_URL, REDIRECT_URL, "code");

        assertNotNull(response);
        assertEquals(response.getResponseCode(), HttpStatus.SC_BAD_REQUEST);
        assertNotNull(response.getErrorResponse());
        assertEquals(response.getErrorResponse().getError(), "invalid_grant");
        assertEquals(response.getErrorResponse().getErrorDescription(),
            "Authorization code doesn't exist or is invalid for the client");
    }

    @Test(expectedExceptions = RequestFailedException.class)
    public void requestTokenShouldHandleHttpRequestException()
        throws RequestFailedException, InvalidResponseException
    {
        when(this.restClient.postFormData(eq(TOKEN_URL), isA(RestAuthentication.class), anyString(),
            anyListOf(KeyValuePair.class), isNull(String.class), isNull(Iterable.class))).thenThrow(
            new RequestFailedException(HttpUtils.HttpMethod.POST, TOKEN_URL,
                new Exception("test")));

        this.authentication.requestToken(this.config.getClientId(), this.config.getClientSecret(),null,
            TOKEN_URL, REDIRECT_URL, "code");
    }

    @DataProvider
    public Object[][] startAuthenticationRequiredArgsData()
    {
        return new Object[][] {{null, AUTHORIZE_URL, REDIRECT_URL, "state", "nonce"},
                               {this.config.getClientId(), null, REDIRECT_URL, "state", "nonce"},
                               {this.config.getClientId(), AUTHORIZE_URL, null, "state", "nonce"},
                               {this.config.getClientId(), AUTHORIZE_URL, REDIRECT_URL, null,
                                "nonce"},
                               {this.config.getClientId(), AUTHORIZE_URL, REDIRECT_URL, "state",
                                null}};
    }

    @Test(dataProvider = "startAuthenticationRequiredArgsData", expectedExceptions = {IllegalArgumentException.class, NullPointerException.class})
    public void startAuthenticationShouldThrowWhenRequiredArgIsNull(final String clientId,
        final URI authorizeUrl, final URI redirectUrl, final String state, final String nonce)
    {
        this.authentication.startAuthentication(clientId, null, authorizeUrl, redirectUrl, state, nonce,
            null, null, null, "mc_v1.1");
    }

    @DataProvider
    public Object[][] startAuthenticationRequiredOptionsData()
    {
        return new Object[][] {{"context", null}, {null, "client"}};
    }

    @Test(dataProvider = "startAuthenticationRequiredOptionsData", expectedExceptions = IllegalArgumentException.class)
    public void startAuthenticationShouldThrowWhenRequiredOptionsNullAndShouldUseAuthz(
        final String context, final String clientId)
    {
        final AuthenticationOptions options =
            new AuthenticationOptions.Builder().withContext(context).withClientId(clientId).build();
        this.authentication.startAuthentication(this.config.getClientId(), null, AUTHORIZE_URL,
            REDIRECT_URL, "state", null, null, null, options, "mc_v1.1");
    }

    @DataProvider
    public Object[][] requestTokenRequiredArgsData()
    {
        final String clientId = this.config.getClientId();
        final String clientSecret = this.config.getClientSecret();

        return new Object[][] {{"", clientSecret, TOKEN_URL, REDIRECT_URL, "code"},
                               {clientId, "", TOKEN_URL, REDIRECT_URL, "code"},
                               {clientId, clientSecret, null, REDIRECT_URL, "code"},
                               {clientId, clientSecret, TOKEN_URL, null, "code"},
                               {clientId, clientSecret, TOKEN_URL, REDIRECT_URL, ""},};
    }

    @Test(dataProvider = "requestTokenRequiredArgsData", expectedExceptions = IllegalArgumentException.class)
    public void requestTokenShouldThrowWhenRequiredArgIsNull(final String clientId,
        final String clientSecret, final URI requestTokenUrl, final URI redirectUrl,
        final String code) throws RequestFailedException, InvalidResponseException
    {
        this.authentication.requestToken(clientId, null, clientSecret, requestTokenUrl, redirectUrl,
            code);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void headlessAuthenticationTest()
        throws RequestFailedException, HeadlessOperationFailedException, ExecutionException,
        InterruptedException, URISyntaxException
    {
        // Given
        when(this.restClient.postFormData(isA(URI.class), isA(RestAuthentication.class), anyString(),
            anyListOf(KeyValuePair.class), isNull(String.class),
            isNull(Iterable.class))).thenReturn(TestUtils.TOKEN_RESPONSE);

        when(this.restClient.getFinalRedirect(isA(URI.class), isA(URI.class),
            isA(RestAuthentication.class))).thenReturn(new URI(REDIRECT_URL + "?code=code"));

        // When
        final Future<RequestTokenResponse> response =
            this.authentication.requestHeadlessAuthentication(this.config.getClientId(),
                this.config.getClientSecret(), null, AUTHORIZE_URL, REDIRECT_URL, TOKEN_URL, "state",
                "nonce", null, null, null, "mc_v1.1");

        // Then
        assertNotNull(response);
        RequestTokenResponse requestTokenResponse = response.get();
        assertNotNull(response.get());

        assertNotNull(requestTokenResponse);
        assertEquals(requestTokenResponse.getResponseCode(), HttpStatus.SC_ACCEPTED);
        assertNotNull(requestTokenResponse.getResponseData());
        assertEquals(requestTokenResponse.getResponseData().getAccessToken(),
            "966ad150-16c5-11e6-944f-43079d13e2f3");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void headlessAuthorizationTest()
        throws RequestFailedException, HeadlessOperationFailedException, ExecutionException,
        InterruptedException, URISyntaxException
    {
        // Given
        when(this.restClient.postFormData(isA(URI.class), isA(RestAuthentication.class), anyString(),
            anyListOf(KeyValuePair.class), isNull(String.class),
            isNull(Iterable.class))).thenReturn(TestUtils.TOKEN_RESPONSE);

        when(this.restClient.getFinalRedirect(isA(URI.class), isA(URI.class),
            isA(RestAuthentication.class))).thenReturn(new URI(REDIRECT_URL + "?code=code"));

        final AuthenticationOptions options = new AuthenticationOptions.Builder()
            .withScope("openid mc_authz")
            .withClientName("test")
            .withContext("context-val")
            .withBindingMessage("binding-val")
            .build();

        // When
        final Future<RequestTokenResponse> response =
            this.authentication.requestHeadlessAuthentication(this.config.getClientId(),
                this.config.getClientSecret(), null, AUTHORIZE_URL, REDIRECT_URL, TOKEN_URL, "state",
                "nonce", null, null, options, "mc_v1.1");


        // Then
        assertNotNull(response);

        RequestTokenResponse requestTokenResponse = response.get();
        assertNotNull(requestTokenResponse);
        assertEquals(requestTokenResponse.getResponseCode(), HttpStatus.SC_ACCEPTED);
        assertNotNull(requestTokenResponse.getResponseData());
        assertEquals(requestTokenResponse.getResponseData().getAccessToken(),
            "966ad150-16c5-11e6-944f-43079d13e2f3");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void refreshTokenTestForSuccessfulRefresh()
        throws RequestFailedException, HeadlessOperationFailedException, ExecutionException,
        InterruptedException, URISyntaxException, InvalidResponseException
    {
        // Given
        when(this.restClient.postFormData(isA(URI.class), isA(RestAuthentication.class), anyString(),
            anyListOf(KeyValuePair.class), isNull(String.class),
            isNull(Iterable.class))).thenReturn(TestUtils.TOKEN_RESPONSE);

        // When
        final RequestTokenResponse requestTokenResponse =
            this.authentication.refreshToken(this.config.getClientId(),
                this.config.getClientSecret(), TOKEN_URL, "RefreshToken");

        // Then
        assertNotNull(requestTokenResponse);
        assertEquals(requestTokenResponse.getResponseCode(), HttpStatus.SC_ACCEPTED);
        assertNotNull(requestTokenResponse.getResponseData());
        assertEquals(requestTokenResponse.getResponseData().getAccessToken(),
            "966ad150-16c5-11e6-944f-43079d13e2f3");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void revokeTokenSuccessTest()
        throws RequestFailedException, HeadlessOperationFailedException, ExecutionException,
        InterruptedException, URISyntaxException, InvalidResponseException,
        JsonDeserializationException
    {
        // Given
        when(this.restClient.postFormData(isA(URI.class), isA(RestAuthentication.class), anyString(),
            anyListOf(KeyValuePair.class), isNull(String.class),
            isNull(Iterable.class))).thenReturn(TestUtils.REVOKE_TOKEN_SUCCESS_RESPONSE);

        // When
        final String outcome =
            this.authentication.revokeToken(this.config.getClientId(),
                this.config.getClientSecret(), TOKEN_URL, "AccessToken",
                Parameters.ACCESS_TOKEN_HINT);

        // Then
        assertNotNull(outcome);
        assertEquals(outcome, AuthenticationService.REVOKE_TOKEN_SUCCESS);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void revokeTokenFailureTestWithError()
        throws RequestFailedException, HeadlessOperationFailedException, ExecutionException,
        InterruptedException, URISyntaxException, InvalidResponseException,
        JsonDeserializationException
    {
        // Given
        when(this.restClient.postFormData(isA(URI.class), isA(RestAuthentication.class), anyString(),
            anyListOf(KeyValuePair.class), isNull(String.class),
            isNull(Iterable.class))).thenReturn(TestUtils.REVOKE_TOKEN_ERROR_RESPONSE);

        // When
        final String outcome =
            this.authentication.revokeToken(this.config.getClientId(),
                this.config.getClientSecret(), TOKEN_URL, "AccessToken", "xyz");

        // Then
        assertNotNull(outcome);
        assertEquals(outcome, "unsupported_token_type");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void revokeTokenFailureTestDefaultError()
        throws RequestFailedException, HeadlessOperationFailedException, ExecutionException,
        InterruptedException, URISyntaxException, InvalidResponseException,
        JsonDeserializationException
    {
        // Given
        when(this.restClient.postFormData(isA(URI.class), isA(RestAuthentication.class), anyString(),
            anyListOf(KeyValuePair.class), isNull(String.class),
            isNull(Iterable.class))).thenReturn(TestUtils.REVOKE_TOKEN_NON_ERROR_RESPONSE);

        // When
        final String outcome =
            this.authentication.revokeToken(this.config.getClientId(),
                this.config.getClientSecret(), TOKEN_URL, "AccessToken", "xyz");

        // Then
        assertNotNull(outcome);
        assertEquals(outcome, AuthenticationService.UNSUPPORTED_TOKEN_TYPE_ERROR);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void revokeTokenWithNoHintTest()
        throws RequestFailedException, HeadlessOperationFailedException, ExecutionException,
        InterruptedException, URISyntaxException, InvalidResponseException,
        JsonDeserializationException
    {
        // Given
        when(this.restClient.postFormData(isA(URI.class), isA(RestAuthentication.class), anyString(),
            anyListOf(KeyValuePair.class), isNull(String.class),
            isNull(Iterable.class))).thenReturn(TestUtils.REVOKE_TOKEN_SUCCESS_RESPONSE);

        // When
        final String outcome =
            this.authentication.revokeToken(this.config.getClientId(),
                this.config.getClientSecret(), TOKEN_URL, "AccessToken", null);

        // Then
        assertNotNull(outcome);
        assertEquals(outcome, AuthenticationService.REVOKE_TOKEN_SUCCESS);
    }
    @Test
    public void makeDiscoveryForAuthenticationTest() throws JsonDeserializationException, RequestFailedException {

        // Given
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

        when(restClient.get(any(URI.class), (RestAuthentication) eq(null), anyString(), (String) eq(null), (List<KeyValuePair>) eq(null), (Iterable<KeyValuePair>) eq(null))).thenReturn(response).thenReturn(response);

        //When
        final DiscoveryResponse discoveryResponse =
                this.authentication.makeDiscoveryForAuthorization(secretKey, clientKey,
                        subscriberId, name, operatorUrls);

        //Then
        assertNotNull(discoveryResponse.getResponseData(), "response data is null");
    }
}
