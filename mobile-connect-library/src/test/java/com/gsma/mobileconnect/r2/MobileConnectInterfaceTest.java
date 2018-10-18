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

import com.gsma.mobileconnect.r2.authentication.AuthenticationService;
import com.gsma.mobileconnect.r2.cache.DiscoveryCache;
import com.gsma.mobileconnect.r2.constants.Parameters;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.discovery.DiscoveryService;
import com.gsma.mobileconnect.r2.encoding.DefaultEncodeDecoder;
import com.gsma.mobileconnect.r2.identity.IdentityService;
import com.gsma.mobileconnect.r2.json.IJsonService;
import com.gsma.mobileconnect.r2.json.JacksonJsonService;
import com.gsma.mobileconnect.r2.json.JsonDeserializationException;
import com.gsma.mobileconnect.r2.rest.MockRestClient;
import com.gsma.mobileconnect.r2.utils.TestUtils;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.AssertJUnit.assertNotNull;

/**
 * Tests {@link MobileConnectInterface}
 *
 * @since 2.0
 */
public class MobileConnectInterfaceTest
{
    private final MobileConnectConfig config = new MobileConnectConfig.Builder()
        .withClientId("zxcvbnm")
        .withClientSecret("asdfghjkl")
        .withDiscoveryUrl(URI.create("http://discovery/test"))
        .withRedirectUrl(URI.create("http://redirect/test"))
        .build();
    private final MockRestClient restClient = new MockRestClient();
    private final IJsonService jsonService = new JacksonJsonService();

    private final MobileConnectInterface mobileConnectInterface = MobileConnect
        .builder(this.config, new DefaultEncodeDecoder(),new DiscoveryCache.Builder().withJsonService(jsonService).withMaxCacheSize(999999999).build(),
                new DiscoveryCache.Builder().withJsonService(jsonService).withMaxCacheSize(999999999).build())
        .withRestClient(this.restClient)
        .build()
        .getMobileConnectInterface();

    @Test
    public void requestUserInfoReturnsUserInfo() throws JsonDeserializationException
    {
        final DiscoveryResponse discoveryResponse =
            DiscoveryResponse.fromRestResponse(TestUtils.AUTHENTICATION_RESPONSE, this.jsonService);

        this.restClient.addResponse(TestUtils.USERINFO_RESPONSE);

        final MobileConnectStatus response =
            this.mobileConnectInterface.requestUserInfo(discoveryResponse,
                "zaqwsxcderfvbgtyhnmjukilop");

        assertNotNull(response);
        assertEquals(response.getResponseType(), MobileConnectStatus.ResponseType.USER_INFO);
    }

    @Test
    public void requestUserInfoReturnsErrorWhenNoUserInfoUrl() throws JsonDeserializationException
    {
        final DiscoveryResponse discoveryResponse =
            DiscoveryResponse.fromRestResponse(TestUtils.AUTHENTICATION_NO_URI_RESPONSE,
                this.jsonService);

        final MobileConnectStatus response =
            this.mobileConnectInterface.requestUserInfo(discoveryResponse,
                "zaqwsxcderfvbgtyhnmjukilop");

        assertNotNull(response);
        assertEquals(response.getResponseType(), MobileConnectStatus.ResponseType.ERROR);
        assertNull(response.getIdentityResponse());
        assertNotNull(response.getErrorCode());
        assertNotNull(response.getErrorMessage());
    }

    @Test
    public void testRefreshToken() throws JsonDeserializationException
    {
        final DiscoveryResponse discoveryResponse =
            DiscoveryResponse.fromRestResponse(TestUtils.AUTHENTICATION_RESPONSE, this.jsonService);
        this.restClient.addResponse(TestUtils.VALIDATED_TOKEN_RESPONSE);

        final MobileConnectStatus status =
            this.mobileConnectInterface.refreshToken("RefreshToken", discoveryResponse);

        assertNotNull(status);

        assertEquals(status.getResponseType(), MobileConnectStatus.ResponseType.COMPLETE);

        assertEquals(status.getRequestTokenResponse().getResponseCode(), 202);
        assertEquals(status.getRequestTokenResponse().getResponseData().getAccessToken(),
            "966ad150-16c5-11e6-944f-43079d13e2f3");
    }

    @Test
    public void testRevokeToken() throws JsonDeserializationException
    {
        final DiscoveryResponse discoveryResponse =
            DiscoveryResponse.fromRestResponse(TestUtils.AUTHENTICATION_RESPONSE, this.jsonService);
        this.restClient.addResponse(TestUtils.REVOKE_TOKEN_SUCCESS_RESPONSE);

        final MobileConnectStatus status =
            this.mobileConnectInterface.revokeToken("AccessToken", Parameters.ACCESS_TOKEN_HINT,
                discoveryResponse);

        assertNotNull(status);

        assertEquals(status.getResponseType(), MobileConnectStatus.ResponseType.COMPLETE);
        assertEquals(status.getOutcome(), AuthenticationService.REVOKE_TOKEN_SUCCESS);
    }

    @Test
    public void testBuilder()
    {
        final MobileConnectInterface mobileConnectInterface = new MobileConnectInterface.Builder()
            .withDiscoveryService(Mockito.mock(DiscoveryService.class))
            .withAuthnService(Mockito.mock(AuthenticationService.class))
            .withIdentityService(Mockito.mock(IdentityService.class))
            .withConfig(this.config)
            .build();
        assertNotNull(mobileConnectInterface);
    }
}
