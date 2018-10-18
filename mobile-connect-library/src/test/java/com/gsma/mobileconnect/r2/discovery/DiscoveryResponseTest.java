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

import com.gsma.mobileconnect.r2.json.IJsonService;
import com.gsma.mobileconnect.r2.json.JacksonJsonService;
import com.gsma.mobileconnect.r2.json.JsonDeserializationException;
import com.gsma.mobileconnect.r2.json.JsonSerializationException;
import com.gsma.mobileconnect.r2.rest.RestResponse;
import edu.emory.mathcs.backport.java.util.Collections;
import org.apache.http.HttpStatus;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static java.util.Arrays.asList;
import static org.testng.Assert.*;

/**
 * Tests {@link DiscoveryResponse}
 *
 * @since 2.0
 */
public class DiscoveryResponseTest
{
    private final IJsonService jsonService = new JacksonJsonService();

    @Test
    public void operatorUrlsShouldBeOverridenByProviderMetadataOnSet()
        throws JsonDeserializationException
    {
        final String responseJson =
            "{\"ttl\":1461169322705,\"subscriber_id\":\"e06a09de399ae6c6798c2126e531775ddf3cfe00367af1842534be709fef25e199157c49cc44adf661d286a29afa09c017747fb4383db22b2eaf33db5f878b3ea261c8f342b234e998757e83de23f4a637ce2390453d5d578c76cd65aae99332ee7fbdbd4a140c99babc4e700eae6aa44d3e17ac050771c1fd784fef0214bf770cd0854ea6f4cff87b3ea1e4b25dccd1d340f00eb66c0f041f90596f5236c1017b2541606fff5165320fc4b3381ebfe1fdb848ab04fbedc550bc575ca385b44695a0a9917a368552ee9f8e2178553318a17c32284197631f74f293f30fe6c04f7a77115ec0d2e8ab2a522db88c60263ec1b690ca22540b916e8a9d2c3d820ec1\",\"response\":{\"serving_operator\":\"Example Operator A\",\"country\":\"US\",\"currency\":\"USD\",\"apis\":{\"operatorid\":{\"link\":[{\"href\":\"http://operator_a.sandbox2.mobileconnect.io/oidc/authorize\",\"rel\":\"authorization\"},{\"href\":\"http://operator_a.sandbox2.mobileconnect.io/oidc/accesstoken\",\"rel\":\"token\"},{\"href\":\"http://operator_a.sandbox2.mobileconnect.io/oidc/userinfo\",\"rel\":\"userinfo\"},{\"href\":\"openid profile email\",\"rel\":\"scope\"}]}},\"client_id\":\"66742a85-2282-4747-881d-ed5b7bd74d2d\",\"client_secret\":\"f15199f4-b658-4e58-8bb3-e40998873392\",\"subscriber_id\":\"e06a09de399ae6c6798c2126e531775ddf3cfe00367af1842534be709fef25e199157c49cc44adf661d286a29afa09c017747fb4383db22b2eaf33db5f878b3ea261c8f342b234e998757e83de23f4a637ce2390453d5d578c76cd65aae99332ee7fbdbd4a140c99babc4e700eae6aa44d3e17ac050771c1fd784fef0214bf770cd0854ea6f4cff87b3ea1e4b25dccd1d340f00eb66c0f041f90596f5236c1017b2541606fff5165320fc4b3381ebfe1fdb848ab04fbedc550bc575ca385b44695a0a9917a368552ee9f8e2178553318a17c32284197631f74f293f30fe6c04f7a77115ec0d2e8ab2a522db88c60263ec1b690ca22540b916e8a9d2c3d820ec1\"}}";
        final RestResponse restResponse = new RestResponse.Builder()
            .withStatusCode(HttpStatus.SC_OK)
            .withContent(responseJson)
            .build();

        final String authzEndpoint = "test authz";
        final String tokenEndpoint = "test token";
        final String userInfoEndpoint = "test userinfo";
        final String jwksEndpoint = "test jwks";

        final ProviderMetadata providerMetadata = new ProviderMetadata.Builder()
            .withAuthorizationEndpoint(authzEndpoint)
            .withTokenEndpoint(tokenEndpoint)
            .withUserinfoEndpoint(userInfoEndpoint)
            .withJwksUri(jwksEndpoint)
            .build();

        final DiscoveryResponse discoveryResponse =
            DiscoveryResponse.fromRestResponse(restResponse, this.jsonService);
        discoveryResponse.setProviderMetadata(providerMetadata);

        assertEquals(discoveryResponse.getOperatorUrls().getAuthorizationUrl(), authzEndpoint);
        assertEquals(discoveryResponse.getOperatorUrls().getRequestTokenUrl(), tokenEndpoint);
        assertEquals(discoveryResponse.getOperatorUrls().getUserInfoUrl(), userInfoEndpoint);
        assertEquals(discoveryResponse.getOperatorUrls().getJwksUri(), jwksEndpoint);
    }

    @Test
    public void operatorURLsShouldBeOverriddenByProviderMetadataOnDeserialize()
        throws JsonSerializationException, JsonDeserializationException
    {
        final String responseJson =
            "{\"ttl\":1461169322705,\"subscriber_id\":\"e06a09de399ae6c6798c2126e531775ddf3cfe00367af1842534be709fef25e199157c49cc44adf661d286a29afa09c017747fb4383db22b2eaf33db5f878b3ea261c8f342b234e998757e83de23f4a637ce2390453d5d578c76cd65aae99332ee7fbdbd4a140c99babc4e700eae6aa44d3e17ac050771c1fd784fef0214bf770cd0854ea6f4cff87b3ea1e4b25dccd1d340f00eb66c0f041f90596f5236c1017b2541606fff5165320fc4b3381ebfe1fdb848ab04fbedc550bc575ca385b44695a0a9917a368552ee9f8e2178553318a17c32284197631f74f293f30fe6c04f7a77115ec0d2e8ab2a522db88c60263ec1b690ca22540b916e8a9d2c3d820ec1\",\"response\":{\"serving_operator\":\"Example Operator A\",\"country\":\"US\",\"currency\":\"USD\",\"apis\":{\"operatorid\":{\"link\":[{\"href\":\"http://operator_a.sandbox2.mobileconnect.io/oidc/authorize\",\"rel\":\"authorization\"},{\"href\":\"http://operator_a.sandbox2.mobileconnect.io/oidc/accesstoken\",\"rel\":\"token\"},{\"href\":\"http://operator_a.sandbox2.mobileconnect.io/oidc/userinfo\",\"rel\":\"userinfo\"},{\"href\":\"openid profile email\",\"rel\":\"scope\"}]}},\"client_id\":\"66742a85-2282-4747-881d-ed5b7bd74d2d\",\"client_secret\":\"f15199f4-b658-4e58-8bb3-e40998873392\",\"subscriber_id\":\"e06a09de399ae6c6798c2126e531775ddf3cfe00367af1842534be709fef25e199157c49cc44adf661d286a29afa09c017747fb4383db22b2eaf33db5f878b3ea261c8f342b234e998757e83de23f4a637ce2390453d5d578c76cd65aae99332ee7fbdbd4a140c99babc4e700eae6aa44d3e17ac050771c1fd784fef0214bf770cd0854ea6f4cff87b3ea1e4b25dccd1d340f00eb66c0f041f90596f5236c1017b2541606fff5165320fc4b3381ebfe1fdb848ab04fbedc550bc575ca385b44695a0a9917a368552ee9f8e2178553318a17c32284197631f74f293f30fe6c04f7a77115ec0d2e8ab2a522db88c60263ec1b690ca22540b916e8a9d2c3d820ec1\"}}";
        final RestResponse restResponse = new RestResponse.Builder()
            .withStatusCode(HttpStatus.SC_OK)
            .withContent(responseJson)
            .build();

        final String authzEndpoint = "test authz";
        final String tokenEndpoint = "test token";
        final String userInfoEndpoint = "test userinfo";
        final String jwksEndpoint = "test jwks";
        final String refreshTokenEndpoint = "test refresh token";
        final String revokeTokenEndpoint = "test revoke token";

        final ProviderMetadata providerMetadata = new ProviderMetadata.Builder()
            .withAuthorizationEndpoint(authzEndpoint)
            .withTokenEndpoint(tokenEndpoint)
            .withUserinfoEndpoint(userInfoEndpoint)
            .withJwksUri(jwksEndpoint)
            .withRefreshEndpoint(refreshTokenEndpoint)
            .withRevocationEndpoint(revokeTokenEndpoint)
            .build();

        final DiscoveryResponse discoveryResponse =
            DiscoveryResponse.fromRestResponse(restResponse, this.jsonService);
        discoveryResponse.setProviderMetadata(providerMetadata);

        final IJsonService jsonService = new JacksonJsonService();
        final String serialized = jsonService.serialize(discoveryResponse);
        final DiscoveryResponse actual =
            jsonService.deserialize(serialized, DiscoveryResponse.class);

        assertEquals(actual.getOperatorUrls().getAuthorizationUrl(), authzEndpoint);
        assertEquals(actual.getOperatorUrls().getRequestTokenUrl(), tokenEndpoint);
        assertEquals(actual.getOperatorUrls().getUserInfoUrl(), userInfoEndpoint);
        assertEquals(actual.getOperatorUrls().getJwksUri(), jwksEndpoint);
        assertEquals(actual.getOperatorUrls().getRefreshTokenUrl(), refreshTokenEndpoint);
        assertEquals(actual.getOperatorUrls().getRevokeTokenUrl(), revokeTokenEndpoint);
    }

    @Test
    public void clientNameShouldBePopulatedIfAvailable()
        throws JsonDeserializationException
    {
        final String responseJson =
            "{\"ttl\":1466082848000,\"response\":{\"client_id\":\"x-ZWRhNjU3OWI3MGIwYTRh\",\"client_secret\":\"x-NjQzZTBhZWM0YmQ4ZDQ5\",\"serving_operator\":\"demo_unitedkingdom\",\"country\":\"UnitedKingdom\",\"currency\":\"GBP\",\"apis\":{\"operatorid\":{\"link\":[{\"rel\":\"authorization\",\"href\":\"https://reference.mobileconnect.io/mobileconnect/index.php/auth\"},{\"rel\":\"token\",\"href\":\"https://reference.mobileconnect.io/mobileconnect/index.php/token\"},{\"rel\":\"userinfo\",\"href\":\"https://reference.mobileconnect.io/mobileconnect/index.php/userinfo\"},{\"rel\":\"jwks\",\"href\":\"https://reference.mobileconnect.io/mobileconnect/cert.jwk\"},{\"rel\":\"applicationShortName\",\"href\":\"test1\"},{\"rel\":\"openid-configuration\",\"href\":\"https://reference.mobileconnect.io/mobileconnect/discovery.php/openid-configuration\"}]}}},\"subscriber_id\":\"6c483ef529a86e5aa808f9cfdcb78ac3ec9f24aba27ea1a003476b0693751d89c3feacd3d2ff00c0e1e1cb683ff7de9ea87bdd775d4e79b7da5a4fbec509d918c1f804fdaf1fcaa9d1aae572bd19a12de7de2d695d004a3b2828be9b79e5f13a5c70a35adebedef138ab11440f8573fff53e59c8348caaf458716dbb53b4162d27737f290a8a759a4eab409af27685b3667659ce1f5b2194ab68953c0381126fc941eb0043c17647021d1e47a07cfde2e5e18c9e29ca01af1a8d2b3558d9853ffeed1cd9c8545e0d4c609db4ca318c02d10cddaf83bab927f81c4ca8bbb04da4dba273a4f76d3962e5a31a59f806067393823ae6702850726281352849209fe4\"}";
        final RestResponse restResponse = new RestResponse.Builder()
            .withStatusCode(HttpStatus.SC_OK)
            .withContent(responseJson)
            .build();
        final DiscoveryResponse discoveryResponse =
            DiscoveryResponse.fromRestResponse(restResponse, this.jsonService);

        assertNotNull(discoveryResponse.getClientName());
    }

    @Test
    public void clientNamePopulationShouldHandleNonExistentClientName()
        throws JsonDeserializationException
    {
        final String responseJson =
            "{\"ttl\":1461169322705,\"subscriber_id\":\"e06a09de399ae6c6798c2126e531775ddf3cfe00367af1842534be709fef25e199157c49cc44adf661d286a29afa09c017747fb4383db22b2eaf33db5f878b3ea261c8f342b234e998757e83de23f4a637ce2390453d5d578c76cd65aae99332ee7fbdbd4a140c99babc4e700eae6aa44d3e17ac050771c1fd784fef0214bf770cd0854ea6f4cff87b3ea1e4b25dccd1d340f00eb66c0f041f90596f5236c1017b2541606fff5165320fc4b3381ebfe1fdb848ab04fbedc550bc575ca385b44695a0a9917a368552ee9f8e2178553318a17c32284197631f74f293f30fe6c04f7a77115ec0d2e8ab2a522db88c60263ec1b690ca22540b916e8a9d2c3d820ec1\",\"response\":{\"serving_operator\":\"Example Operator A\",\"country\":\"US\",\"currency\":\"USD\",\"apis\":{\"operatorid\":{\"link\":[{\"href\":\"http://operator_a.sandbox2.mobileconnect.io/oidc/authorize\",\"rel\":\"authorization\"},{\"href\":\"http://operator_a.sandbox2.mobileconnect.io/oidc/accesstoken\",\"rel\":\"token\"},{\"href\":\"http://operator_a.sandbox2.mobileconnect.io/oidc/userinfo\",\"rel\":\"userinfo\"},{\"href\":\"openid profile email\",\"rel\":\"scope\"}]}},\"client_id\":\"66742a85-2282-4747-881d-ed5b7bd74d2d\",\"client_secret\":\"f15199f4-b658-4e58-8bb3-e40998873392\",\"subscriber_id\":\"e06a09de399ae6c6798c2126e531775ddf3cfe00367af1842534be709fef25e199157c49cc44adf661d286a29afa09c017747fb4383db22b2eaf33db5f878b3ea261c8f342b234e998757e83de23f4a637ce2390453d5d578c76cd65aae99332ee7fbdbd4a140c99babc4e700eae6aa44d3e17ac050771c1fd784fef0214bf770cd0854ea6f4cff87b3ea1e4b25dccd1d340f00eb66c0f041f90596f5236c1017b2541606fff5165320fc4b3381ebfe1fdb848ab04fbedc550bc575ca385b44695a0a9917a368552ee9f8e2178553318a17c32284197631f74f293f30fe6c04f7a77115ec0d2e8ab2a522db88c60263ec1b690ca22540b916e8a9d2c3d820ec1\"}}";

        final RestResponse restResponse = new RestResponse.Builder()
            .withStatusCode(HttpStatus.SC_OK)
            .withContent(responseJson)
            .build();
        final DiscoveryResponse discoveryResponse =
            DiscoveryResponse.fromRestResponse(restResponse, this.jsonService);

        assertNull(discoveryResponse.getClientName());
    }

    @DataProvider(name = "mobileConnectServiceSupportedData")
    public Object[][] createMobileConnectServiceSupportedData()
    {
        return new Object[][] {
            {"ShouldReturnTrueIfAllScopesAreSupported", ServiceSupportedTestMode.SUPPORTED_SCOPES,
             "openid mc_authz email", Boolean.TRUE}, {"ShouldReturnFalseIfNotAllScopesAreSupported",
                                                      ServiceSupportedTestMode.SUPPORTED_SCOPES,
                                                      "openid mc_authz email notsupported",
                                                      Boolean.FALSE},
            {"ShouldHandleSingleScope", ServiceSupportedTestMode.SUPPORTED_SCOPES, "openid",
             Boolean.TRUE},
            {"ShouldHandleEmptyScope", ServiceSupportedTestMode.SUPPORTED_SCOPES, "", Boolean.TRUE},
            {"ShouldIgnoreCase", ServiceSupportedTestMode.SUPPORTED_SCOPES, "OPENID mC_AUthz eMaIl",
             Boolean.TRUE}, {"ShouldThrowIfProviderMetadataUnavailable",
                             ServiceSupportedTestMode.NO_PROVIDER_METADATA, "openid mc_authz",
                             null}, {"ShouldThrowIfScopesSupportedUnavailable",
                                     ServiceSupportedTestMode.NO_SUPPORTED_SCOPES,
                                     "openid mc_authz", null},
            {"ShouldThrowIfScopesSupportedEmpty", ServiceSupportedTestMode.EMPTY_SUPPORTED_SCOPES,
             "openid mc_authz", null}};
    }

    @SuppressWarnings("unchecked")
    @Test(dataProvider = "mobileConnectServiceSupportedData")
    void isMobileConnectServiceSupported(final String name, final ServiceSupportedTestMode mode,
        final String scopesToTest, final Boolean expected) throws JsonDeserializationException
    {
        final String responseJson =
            "{\"ttl\":1461169322705,\"subscriber_id\":\"e06a09de399ae6c6798c2126e531775ddf3cfe00367af1842534be709fef25e199157c49cc44adf661d286a29afa09c017747fb4383db22b2eaf33db5f878b3ea261c8f342b234e998757e83de23f4a637ce2390453d5d578c76cd65aae99332ee7fbdbd4a140c99babc4e700eae6aa44d3e17ac050771c1fd784fef0214bf770cd0854ea6f4cff87b3ea1e4b25dccd1d340f00eb66c0f041f90596f5236c1017b2541606fff5165320fc4b3381ebfe1fdb848ab04fbedc550bc575ca385b44695a0a9917a368552ee9f8e2178553318a17c32284197631f74f293f30fe6c04f7a77115ec0d2e8ab2a522db88c60263ec1b690ca22540b916e8a9d2c3d820ec1\",\"response\":{\"serving_operator\":\"Example Operator A\",\"country\":\"US\",\"currency\":\"USD\",\"apis\":{\"operatorid\":{\"link\":[{\"href\":\"http://operator_a.sandbox2.mobileconnect.io/oidc/authorize\",\"rel\":\"authorization\"},{\"href\":\"http://operator_a.sandbox2.mobileconnect.io/oidc/accesstoken\",\"rel\":\"token\"},{\"href\":\"http://operator_a.sandbox2.mobileconnect.io/oidc/userinfo\",\"rel\":\"userinfo\"},{\"href\":\"openid profile email\",\"rel\":\"scope\"}]}},\"client_id\":\"66742a85-2282-4747-881d-ed5b7bd74d2d\",\"client_secret\":\"f15199f4-b658-4e58-8bb3-e40998873392\",\"subscriber_id\":\"e06a09de399ae6c6798c2126e531775ddf3cfe00367af1842534be709fef25e199157c49cc44adf661d286a29afa09c017747fb4383db22b2eaf33db5f878b3ea261c8f342b234e998757e83de23f4a637ce2390453d5d578c76cd65aae99332ee7fbdbd4a140c99babc4e700eae6aa44d3e17ac050771c1fd784fef0214bf770cd0854ea6f4cff87b3ea1e4b25dccd1d340f00eb66c0f041f90596f5236c1017b2541606fff5165320fc4b3381ebfe1fdb848ab04fbedc550bc575ca385b44695a0a9917a368552ee9f8e2178553318a17c32284197631f74f293f30fe6c04f7a77115ec0d2e8ab2a522db88c60263ec1b690ca22540b916e8a9d2c3d820ec1\"}}";

        final RestResponse restResponse = new RestResponse.Builder()
            .withStatusCode(HttpStatus.SC_OK)
            .withContent(responseJson)
            .build();
        final DiscoveryResponse discoveryResponse =
            DiscoveryResponse.fromRestResponse(restResponse, this.jsonService);

        final ProviderMetadata providerMetadata;

        switch (mode)
        {
            case NO_SUPPORTED_SCOPES:
                providerMetadata = new ProviderMetadata.Builder().build();
                break;
            case EMPTY_SUPPORTED_SCOPES:
                providerMetadata = new ProviderMetadata.Builder()
                    .withScopesSupported(Collections.emptyList())
                    .build();
                break;
            case SUPPORTED_SCOPES:
                providerMetadata = new ProviderMetadata.Builder()
                    .withScopesSupported(
                        asList("openid", "mc_authn", "mc_authz", "profile", "email"))
                    .build();
                break;
            case NO_PROVIDER_METADATA:
            default:
                providerMetadata = null;
                break;
        }

        discoveryResponse.setProviderMetadata(providerMetadata);

        try
        {
            final boolean actual = discoveryResponse.isMobileConnectServiceSupported(scopesToTest);

            assertTrue(mode == ServiceSupportedTestMode.SUPPORTED_SCOPES,
                "expect an exception to be thrown");
            assertEquals(actual, expected.booleanValue(), name);
        }
        catch (ProviderMetadataUnavailableException pmue)
        {
            assertTrue(mode != ServiceSupportedTestMode.SUPPORTED_SCOPES,
                "unexpected exception was thrown");
        }
    }

    private enum ServiceSupportedTestMode
    {
        NO_PROVIDER_METADATA, NO_SUPPORTED_SCOPES, EMPTY_SUPPORTED_SCOPES, SUPPORTED_SCOPES
    }
}
