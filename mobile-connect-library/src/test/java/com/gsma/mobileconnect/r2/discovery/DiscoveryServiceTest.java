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
package com.gsma.mobileconnect.r2.discovery;

import com.gsma.mobileconnect.r2.MobileConnectConfig;
import com.gsma.mobileconnect.r2.cache.CacheAccessException;
import com.gsma.mobileconnect.r2.cache.ConcurrentCache;
import com.gsma.mobileconnect.r2.cache.DiscoveryCache;
import com.gsma.mobileconnect.r2.exceptions.InvalidArgumentException;
import com.gsma.mobileconnect.r2.exceptions.InvalidResponseException;
import com.gsma.mobileconnect.r2.exceptions.RequestFailedException;
import com.gsma.mobileconnect.r2.json.IJsonService;
import com.gsma.mobileconnect.r2.json.JacksonJsonService;
import com.gsma.mobileconnect.r2.rest.MockRestClient;
import com.gsma.mobileconnect.r2.rest.RestResponse;
import com.gsma.mobileconnect.r2.utils.HttpUtils;
import com.gsma.mobileconnect.r2.utils.TestUtils;
import org.apache.http.HttpStatus;
import org.testng.annotations.*;

import java.net.URI;
import java.util.concurrent.*;

import static org.testng.Assert.*;

/**
 * Tests {@link DiscoveryService}
 *
 * @since 2.0
 */
public class DiscoveryServiceTest
{
    private static final URI DISCOVERY_URL = URI.create("http://localhost:8080/v2/discovery");
    private static final URI REDIRECT_URL = URI.create("http://localhost:8080/");
    private static final DiscoveryOptions DISCOVERY_OPTIONS =
        new DiscoveryOptions.Builder().withIdentifiedMnc("100").withIdentifiedMcc("10").build();

    private static final IJsonService jsonService = new JacksonJsonService();
    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    private static final MockRestClient restClient = new MockRestClient();
    private static MobileConnectConfig config;
    private static IDiscoveryService discoveryService;
    private static ConcurrentCache discoveryCache;

    @BeforeClass
    public static void beforeClass()
    {
        config = new MobileConnectConfig.Builder()
            .withClientId("1234567890")
            .withClientSecret("1234567890")
            .withDiscoveryUrl(DISCOVERY_URL)
            .withRedirectUrl(REDIRECT_URL)
            .build();

        discoveryCache = new DiscoveryCache.Builder().withJsonService(jsonService).build();

        discoveryService = new DiscoveryService.Builder()
            .withJsonService(jsonService)
            .withCache(discoveryCache)
            .withRestClient(restClient)
            .build();
    }

    @AfterClass
    public static void afterClass() throws InterruptedException
    {
        executorService.shutdown();
        assertTrue(executorService.awaitTermination(5L, TimeUnit.SECONDS));
    }

    @AfterMethod
    public void afterMethod() throws CacheAccessException
    {
        assertEquals(restClient.reset().size(), 0);
        discoveryCache.clear();
    }

    @Test
    public void automatedOperatorDiscoveryShouldHandleOperatorSelectionResponse()
        throws RequestFailedException, InvalidResponseException
    {
        restClient.addResponse(TestUtils.OPERATOR_SELECTION_RESPONSE);

        final DiscoveryResponse response =
            discoveryService.startAutomatedOperatorDiscovery(config, DISCOVERY_URL, REDIRECT_URL,
                DISCOVERY_OPTIONS, null);

        assertNotNull(response);
        assertEquals(response.getResponseCode(), HttpStatus.SC_ACCEPTED);
        assertNotNull(response.getResponseData());
        assertNotNull(response.getResponseData().getLinks());
        assertFalse(response.isCached());
    }

    @Test
    public void automatedOperatorDiscoveryShouldHandleAuthenticationResponse()
        throws RequestFailedException, InvalidResponseException
    {
        restClient
            .addResponse(TestUtils.AUTHENTICATION_RESPONSE)
            .addResponse(TestUtils.PROVIDER_METADATA_RESPONSE);

        final DiscoveryResponse response =
            discoveryService.startAutomatedOperatorDiscovery(config, DISCOVERY_URL, REDIRECT_URL,
                DISCOVERY_OPTIONS, null);

        assertNotNull(response);
        assertEquals(response.getResponseCode(), HttpStatus.SC_OK);
        assertNotNull(response.getResponseData());
        assertNotNull(response.getResponseData().getResponse());
        assertEquals(response.getResponseData().getTtl(), 1466082848000L);
        assertNotNull(response.getProviderMetadata());
        assertEquals(response.getProviderMetadata().getAuthorizationEndpoint(),
            "https://reference.mobileconnect.io/mobileconnect/index.php/auth");
        assertFalse(response.isCached());
        assertFalse(response.getProviderMetadata().isCached());
    }

    @Test(expectedExceptions = RequestFailedException.class)
    public void automatedOperatorDiscoveryShouldHandleFailedRequest()
        throws RequestFailedException, InvalidResponseException
    {
        restClient.addResponse(
            new RequestFailedException(HttpUtils.HttpMethod.GET, URI.create("http://error"), null));

        discoveryService.startAutomatedOperatorDiscovery(config, DISCOVERY_URL, REDIRECT_URL, DISCOVERY_OPTIONS,
            null);
    }

    @Test(expectedExceptions = InvalidResponseException.class)
    public void automatedOperatorDiscoveryShouldHandleInvalidResponse()
        throws RequestFailedException, InvalidResponseException
    {
        restClient.addResponse(TestUtils.INVALID_RESPONSE);

        discoveryService.startAutomatedOperatorDiscovery(config, DISCOVERY_URL, REDIRECT_URL, DISCOVERY_OPTIONS,
            null);
    }

    @Test
    public void getOperatorSelectionURLShouldHandleOperatorSelectionResponse()
        throws RequestFailedException, InvalidResponseException
    {
        restClient.addResponse(TestUtils.OPERATOR_SELECTION_RESPONSE);

        final DiscoveryResponse response =
            discoveryService.getOperatorSelectionURL(config, DISCOVERY_URL, REDIRECT_URL);

        assertNotNull(response);
        assertEquals(response.getResponseCode(), HttpStatus.SC_ACCEPTED);
        assertNotNull(response.getResponseData());
        assertNotNull(response.getOperatorUrls());
        assertFalse(response.isCached());
    }

    @Test(expectedExceptions = RequestFailedException.class)
    public void getOperatorSelectionUrlShouldHandleFailedRequest()
        throws RequestFailedException, InvalidResponseException
    {
        restClient.addResponse(
            new RequestFailedException(HttpUtils.HttpMethod.GET, URI.create("http://error"), null));

        discoveryService.getOperatorSelectionURL(config, DISCOVERY_URL, REDIRECT_URL);
    }

    @Test(expectedExceptions = InvalidResponseException.class)
    public void getOperatorSelectionUrlShouldHandleInvalidResponse()
        throws RequestFailedException, InvalidResponseException
    {
        restClient.addResponse(TestUtils.INVALID_RESPONSE);

        discoveryService.getOperatorSelectionURL(config, DISCOVERY_URL, REDIRECT_URL);
    }

    @Test
    public void completeSelectedOperatorDiscoveryShouldHandleOperatorSelectionResponse()
        throws RequestFailedException, InvalidResponseException
    {
        restClient.addResponse(TestUtils.OPERATOR_SELECTION_RESPONSE);

        final DiscoveryResponse response =
            discoveryService.completeSelectedOperatorDiscovery(config, DISCOVERY_URL, REDIRECT_URL, "901", "01");

        assertNotNull(response);
        assertEquals(response.getResponseCode(), HttpStatus.SC_ACCEPTED);
        assertNotNull(response.getResponseData());
        assertNotNull(response.getResponseData().getLinks());
        assertNotNull(response.getProviderMetadata());
        assertFalse(response.isCached());
    }

    @Test
    public void completeSelectedOperatorDiscoveryShouldHandleAuthenticationResponse()
        throws RequestFailedException, InvalidResponseException
    {
        restClient
            .addResponse(TestUtils.AUTHENTICATION_RESPONSE)
            .addResponse(TestUtils.PROVIDER_METADATA_RESPONSE);

        final DiscoveryResponse response =
            discoveryService.completeSelectedOperatorDiscovery(config, DISCOVERY_URL, REDIRECT_URL, "901", "01");

        assertNotNull(response);
        assertEquals(response.getResponseCode(), HttpStatus.SC_OK);
        assertNotNull(response.getResponseData());
        assertNotNull(response.getResponseData().getResponse());
        assertEquals(response.getResponseData().getTtl(), 1466082848000L);
        assertNotNull(response.getProviderMetadata());
        assertEquals(response.getProviderMetadata().getAuthorizationEndpoint(),
            "https://reference.mobileconnect.io/mobileconnect/index.php/auth");
        assertFalse(response.isCached());
        assertFalse(response.getProviderMetadata().isCached());
    }

//    @Test
//    public void completeSelectedOperatorDiscoveryShouldHandleErrorResponse()
//        throws RequestFailedException, InvalidResponseException
//    {
//        restClient.addResponse(TestUtils.NOT_FOUND_RESPONSE);
//
//        final DiscoveryResponse discoveryResponse =
//            discoveryService.completeSelectedOperatorDiscovery(config, DISCOVERY_URL, REDIRECT_URL, "901", "01");
//
//        assertNotNull(discoveryResponse);
//        assertEquals(discoveryResponse.getResponseCode(), HttpStatus.SC_OK);
//        assertNotNull(discoveryResponse.getResponseData());
//        assertNotNull(discoveryResponse.getErrorResponse());
//        assertFalse(discoveryResponse.isCached());
//    }

//    @Test
//    public void completeSelectedOperatorDiscoveryShouldUseCachedResponsesIfCacheSupplied()
//        throws RequestFailedException, InvalidResponseException
//    {
//        restClient
//            .addResponse(TestUtils.AUTHENTICATION_RESPONSE)
//            .addResponse(TestUtils.PROVIDER_METADATA_RESPONSE);
//
//        discoveryService.completeSelectedOperatorDiscovery(config, DISCOVERY_URL, REDIRECT_URL, "901", "01");
//        final DiscoveryResponse cached =
//            discoveryService.completeSelectedOperatorDiscovery(config, DISCOVERY_URL, REDIRECT_URL, "901", "01");
//
//        assertNotNull(cached);
//        assertTrue(cached.isCached());
//        assertNotNull(cached.getProviderMetadata());
//        assertTrue(cached.getProviderMetadata().isCached());
//    }

    @Test(expectedExceptions = RequestFailedException.class)
    public void completeSelectedSelectedOperatorDiscoveryShouldHandleRequestFailedException()
        throws RequestFailedException, InvalidResponseException
    {
        restClient.addResponse(
            new RequestFailedException(HttpUtils.HttpMethod.POST, URI.create("http://error"),
                null));

        discoveryService.completeSelectedOperatorDiscovery(config, DISCOVERY_URL, REDIRECT_URL, "901", "01");
    }

    @Test
    public void getCachedDiscoveryResultShouldReturnCachedResponse()
        throws RequestFailedException, InvalidResponseException, CacheAccessException
    {
        restClient
            .addResponse(TestUtils.AUTHENTICATION_RESPONSE)
            .addResponse(TestUtils.PROVIDER_METADATA_RESPONSE);

        discoveryService.completeSelectedOperatorDiscovery(config, DISCOVERY_URL, REDIRECT_URL, "901", "01");
        final DiscoveryResponse second = discoveryService.getCachedDiscoveryResponse("901", "01");

        assertNotNull(second);
        assertTrue(second.isCached());
        assertNotNull(second.getProviderMetadata());
        assertTrue(second.getProviderMetadata().isCached());
    }

//    @Test
//    public void clearDiscoveryCacheShouldEmptyCacheWithEmptyArguments()
//        throws RequestFailedException, InvalidResponseException, CacheAccessException
//    {
//        this.primeCache();
//
//        discoveryService.clearCache(null, null);
//
//        assertTrue(discoveryCache.isEmpty());
//    }

//    @Test
//    public void clearDiscoveryCacheShouldClearSingleEntryIfSuppliedValidMccMnc()
//        throws RequestFailedException, InvalidResponseException, CacheAccessException
//    {
//        this.primeCache();
//
//        discoveryService.clearCache("903", "01");
//
//        final DiscoveryResponse response = discoveryService.getCachedDiscoveryResponse("902", "01");
//
//        assertNotNull(response);
//        assertFalse(discoveryCache.isEmpty());
//    }

    private void primeCache() throws RequestFailedException, InvalidResponseException
    {
        for (int i = 0; i < 10; i++)
        {
            restClient.addResponse(TestUtils.AUTHENTICATION_RESPONSE);
            if (i == 0)
            {
                restClient.addResponse(TestUtils.PROVIDER_METADATA_RESPONSE);
            }

            final DiscoveryResponse response =
                discoveryService.completeSelectedOperatorDiscovery(config, DISCOVERY_URL, REDIRECT_URL, "90" + i,
                    "01");

            assertNotNull(response);
            assertNotNull(response.getProviderMetadata());
            assertFalse(response.isCached());
            assertEquals(response.getProviderMetadata().isCached(), i != 0);
        }
    }

    @Test
    public void getProviderMetadataShouldBypassCacheIfRequested()
        throws RequestFailedException, InvalidResponseException, InterruptedException,
        ExecutionException, TimeoutException
    {
        final RestResponse updatedMetadata = new RestResponse.Builder()
            .withStatusCode(HttpStatus.SC_OK)
            .withContent(
                "{\"version\":\"4.0\",\"issuer\":\"https://reference.mobileconnect.io/mobileconnect\",\"authorization_endpoint\":\"https://reference.mobileconnect.io/mobileconnect/index.php/auth\",\"token_endpoint\":\"https://reference.mobileconnect.io/mobileconnect/index.php/token\",\"userinfo_endpoint\":\"https://reference.mobileconnect.io/mobileconnect/index.php/userinfo\",\"check_session_iframe\":\"https://reference.mobileconnect.io/mobileconnect/opframe.php\",\"end_session_endpoint\":\"https://reference.mobileconnect.io/mobileconnect/index.php/endsession\",\"jwks_uri\":\"https://reference.mobileconnect.io/mobileconnect/op.jwk\",\"scopes_supported\":[\"openid\",\"mc_authn\",\"mc_authz\",\"profile\",\"email\",\"address\"],\"response_types_supported\":[\"code\",\"code token\",\"code id_token\",\"token\",\"token id_token\",\"code token id_token\",\"id_token\"],\"grant_types_supported\":[\"authorization_code\"],\"acr_values_supported\":[\"2\",\"3\"],\"subject_types_supported\":[\"public\",\"pairwise\"],\"userinfo_signing_alg_values_supported\":[\"HS256\",\"HS384\",\"HS512\",\"RS256\",\"RS384\",\"RS512\"],\"userinfo_encryption_alg_values_supported\":[\"RSA1_5\",\"RSA-OAEP\"],\"userinfo_encryption_enc_values_supported\":[\"A128CBC-HS256\",\"A256CBC-HS512\",\"A128GCM\",\"A256GCM\"],\"id_token_signing_alg_values_supported\":[\"HS256\",\"HS384\",\"HS512\",\"RS256\",\"RS384\",\"RS512\"],\"id_token_encryption_alg_values_supported\":[\"RSA1_5\",\"RSA-OAEP\"],\"id_token_encryption_enc_values_supported\":[\"A128CBC-HS256\",\"A256CBC-HS512\",\"A128GCM\",\"A256GCM\"],\"request_object_signing_alg_values_supported\":[\"HS256\",\"HS384\",\"HS512\",\"RS256\",\"RS384\",\"RS512\"],\"request_object_encryption_alg_values_supported\":[\"RSA1_5\",\"RSA-OAEP\"],\"request_object_encryption_enc_values_supported\":[\"A128CBC-HS256\",\"A256CBC-HS512\",\"A128GCM\",\"A256GCM\"],\"token_endpoint_auth_methods_supported\":[\"client_secret_post\",\"client_secret_basic\",\"client_secret_jwt\",\"private_key_jwt\"],\"token_endpoint_auth_signing_alg_values_supported\":[\"HS256\",\"HS384\",\"HS512\",\"RS256\",\"RS384\",\"RS512\"],\"display_values_supported\":[\"page\"],\"claim_types_supported\":[\"normal\"],\"claims_supported\":[\"name\",\"given_name\",\"family_name\",\"middle_name\",\"nickname\",\"preferred_username\",\"profile\",\"picture\",\"website\",\"email\",\"email_verified\",\"gender\",\"birthdate\",\"zoneinfo\",\"locale\",\"phone_number\",\"phone_number_verified\",\"address\",\"updated_at\"],\"service_documentation\":\"https://reference.mobileconnect.io/mobileconnect/index.php/servicedocs\",\"claims_locales_supported\":[\"en-US\"],\"ui_locales_supported\":[\"en-US\"],\"require_request_uri_registration\":false,\"op_policy_uri\":\"https://reference.mobileconnect.io/mobileconnect/index.php/op_policy\",\"op_tos_uri\":\"https://reference.mobileconnect.io/mobileconnect/index.php/op_tos\",\"claims_parameter_supported\":true,\"request_parameter_supported\":true,\"request_uri_parameter_supported\":true,\"mobile_connect_version_supported\":[{\"openid\":\"mc_v1.1\"},{\"openid mc_authn\":\"mc_v1.2\"},{\"openid mc_authz\":\"mc_v1.2\"}],\"login_hint_methods_supported\":[\"MSISDN\",\"ENCRYPTED_MSISDN\",\"PCR\"]} ")
            .build();

        restClient
            .addResponse(TestUtils.AUTHENTICATION_RESPONSE)
            .addResponse(TestUtils.PROVIDER_METADATA_RESPONSE)
            .addResponse(updatedMetadata);

        final DiscoveryResponse original =
            discoveryService.startAutomatedOperatorDiscovery(config, DISCOVERY_URL, REDIRECT_URL,
                DISCOVERY_OPTIONS, null);

        final Future<ProviderMetadata> future =
            discoveryService.getProviderMetadata(original, true);

        final ProviderMetadata metadata = future.get(5L, TimeUnit.SECONDS);

        assertEquals(metadata.getVersion(), "4.0");
        assertEquals(original.getProviderMetadata(), metadata);
    }

    @DataProvider
    public Object[][] argValidationData()
    {
        return new Object[][] {{null, "secret", DISCOVERY_URL, REDIRECT_URL},
                               {"id", null, DISCOVERY_URL, REDIRECT_URL},
                               {"id", "secret", null, REDIRECT_URL},
                               {"id", "secret", DISCOVERY_URL, null},};
    }

    @Test(dataProvider = "argValidationData", expectedExceptions = InvalidArgumentException.class)
    public void automatedOperatorDiscoveryArgumentValidation(final String clientId,
        final String clientSecret, final URI discoveryUrl, final URI redirectUrl)
        throws RequestFailedException, InvalidResponseException
    {
        discoveryService.startAutomatedOperatorDiscovery(clientId, clientSecret, discoveryUrl,
            redirectUrl, DISCOVERY_OPTIONS, null);
    }

    @Test(expectedExceptions = InvalidArgumentException.class)
    public void automatedOperatorDiscoveryArgumentValidationPrefs()
        throws RequestFailedException, InvalidResponseException
    {
        discoveryService.startAutomatedOperatorDiscovery(null, DISCOVERY_URL, REDIRECT_URL, DISCOVERY_OPTIONS,
            null);
    }

    @Test(dataProvider = "argValidationData", expectedExceptions = InvalidArgumentException.class)
    public void getOperatorSelectionUrlArgumentValidation(final String clientId,
        final String clientSecret, final URI discoveryUrl, final URI redirectUrl)
        throws RequestFailedException, InvalidResponseException
    {
        discoveryService.getOperatorSelectionURL(clientId, clientSecret, discoveryUrl, redirectUrl);
    }

    @Test(expectedExceptions = InvalidArgumentException.class)
    public void getOperatorSelectionUrlArgumentValidationPrefs()
        throws RequestFailedException, InvalidResponseException
    {
        discoveryService.getOperatorSelectionURL(null, DISCOVERY_URL, REDIRECT_URL);
    }

    @DataProvider
    public Object[][] completeSelectedOperatorArgValidationData()
    {
        return new Object[][] {{null, "secret", DISCOVERY_URL, REDIRECT_URL, "mcc", "mnc"},
                               {"id", null, DISCOVERY_URL, REDIRECT_URL, "mcc", "mnc"},
                               {"id", "secret", null, REDIRECT_URL, "mcc", "mnc"},
                               {"id", "secret", DISCOVERY_URL, null, "mcc", "mnc"},
                               {"id", "secret", DISCOVERY_URL, REDIRECT_URL, null, "mnc"},
                               {"id", "secret", DISCOVERY_URL, REDIRECT_URL, "mcc", null}};
    }

    @Test(dataProvider = "completeSelectedOperatorArgValidationData", expectedExceptions = InvalidArgumentException.class)
    public void completeSelectedOperatorArgumentValidation(final String clientId,
        final String clientSecret, final URI discoveryUrl, final URI redirectUrl, final String mcc,
        final String mnc) throws RequestFailedException, InvalidResponseException
    {
        discoveryService.completeSelectedOperatorDiscovery(clientId, clientSecret, discoveryUrl,
            redirectUrl, mcc, mnc);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void completeSelectedOperatorArgumentValidationPrefs()
        throws RequestFailedException, InvalidResponseException
    {
        discoveryService.completeSelectedOperatorDiscovery(null, DISCOVERY_URL, REDIRECT_URL, "mcc", "mnc");
    }

    @DataProvider
    public Object[][] parseDiscoveryRedirectData()
    {
        return new Object[][] {{"subscriber_id=abc123&mcc_mnc=456_789", "abc123", "456", "789"},
                               {"subscriber_id=abc123&mcc_mnc=456789", "abc123", null, null},
                               {"mcc_mnc=456_789", null, "456", "789"}, {"", null, null, null}};
    }

    @Test(dataProvider = "parseDiscoveryRedirectData")
    public void parseDiscoveryRedirect(final String query, final String subscriberId,
        final String mcc, final String mnc)
    {
        final URI redirectUri = URI.create("http://test?" + query);

        final ParsedDiscoveryRedirect parsed = discoveryService.parseDiscoveryRedirect(redirectUri);

        assertNotNull(parsed);
        assertEquals(parsed.getEncryptedMsisdn(), subscriberId);
        assertEquals(parsed.getSelectedMcc(), mcc);
        assertEquals(parsed.getSelectedMnc(), mnc);
    }
}

