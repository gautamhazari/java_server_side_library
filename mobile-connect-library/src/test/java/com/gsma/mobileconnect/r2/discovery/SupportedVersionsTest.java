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

import com.gsma.mobileconnect.r2.constants.Scopes;
import com.gsma.mobileconnect.r2.json.IJsonService;
import com.gsma.mobileconnect.r2.json.JacksonJsonService;
import com.gsma.mobileconnect.r2.json.JsonDeserializationException;
import com.gsma.mobileconnect.r2.json.JsonSerializationException;
import com.gsma.mobileconnect.r2.utils.TestUtils;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Tests {@link SupportedVersions}.
 *
 * @since 2.0
 */
public class SupportedVersionsTest
{
    private final IJsonService jsonService = new JacksonJsonService();

    @Test
    public void getSupportedVersionShouldReturnVersionForScope()
    {
        String mcAuthenticationVersion = "2.0";
        final SupportedVersions supportedVersions = new SupportedVersions.Builder()
            .addSupportedVersion("openid", "1.2")
            .addSupportedVersion(Scopes.MOBILE_CONNECT_AUTHENTICATION, mcAuthenticationVersion)
            .build();

        final String actual = supportedVersions.getSupportedVersion(Scopes.MOBILE_CONNECT_AUTHENTICATION);

        assertEquals(actual, mcAuthenticationVersion);
    }

    @Test
    public void getSupportedVersionShouldReturnVersionForOpenidIfScopeNotFound()
    {
        final SupportedVersions supportedVersions = new SupportedVersions.Builder()
            .addSupportedVersion("openid", "1.2")
            .addSupportedVersion(Scopes.MOBILE_CONNECT_AUTHENTICATION, "2.0")
            .build();

        final String actual = supportedVersions.getSupportedVersion(Scopes.MOBILE_CONNECT_AUTHORIZATION);

        assertEquals(actual, "1.2");
    }

    @Test
    public void getSupportedVersionShouldReturnVersionFromDefaultVersionsIfOpenidScopeNotFound()
    {
        final SupportedVersions supportedVersions = new SupportedVersions.Builder().build();

        final String actual = supportedVersions.getSupportedVersion("openid mc_authz");

        assertEquals(actual, "mc_v1.2");
    }

    @Test
    public void supportedVersionsRoundTripJsonSerialization()
        throws JsonDeserializationException, JsonSerializationException
    {
        final String json =
            "[{\"openid\":\"mc_v1.1\"},{\"openid mc_authn\":\"mc_v1.2\"},{\"openid mc_authz\":\"mc_v1.2\"}]";

        final SupportedVersions versions =
            this.jsonService.deserialize(json, SupportedVersions.class);

        assertEquals(versions.getSupportedVersion("openid"), "mc_v1.1");
        assertEquals(versions.getSupportedVersion("openid mc_authn"), "mc_v1.2");
        assertEquals(versions.getSupportedVersion("openid mc_authz"), "mc_v1.2");

        final String actual = this.jsonService.serialize(versions);

        assertEqualsNoOrder(TestUtils.splitArray(actual), TestUtils.splitArray(json));
    }

    @Test
    public void getSupportedVersionShouldReturnNullIfScopeNotRecognised()
    {
        final SupportedVersions supportedVersions = new SupportedVersions.Builder().build();

        final String supportedVersion = supportedVersions.getSupportedVersion("testest");

        assertNull(supportedVersion);
    }

    @Test
    public void isVersionSupportedShouldReturnFalseIfVersionNull()
    {
        final SupportedVersions supportedVersions = new SupportedVersions.Builder().build();
        final String version = null;

        final boolean versionSupported = supportedVersions.isVersionSupported(version);

        assertFalse(versionSupported);
    }

    @Test
    public void isVersionSupportedShouldReturnFalseIfVersionEmpty()
    {
        final SupportedVersions supportedVersions = new SupportedVersions.Builder().build();
        final String version = "";

        final boolean versionSupported = supportedVersions.isVersionSupported(version);

        assertFalse(versionSupported);
    }

    @Test
    public void isVersionSupportedShouldReturnTrueIfMaxVersionSupported()
    {
        final SupportedVersions supportedVersions = new SupportedVersions.Builder().build();
        final String version = "mc_v1.1";

        final boolean versionSupported = supportedVersions.isVersionSupported(version);

        assertTrue(versionSupported);
    }

    @Test
    public void isVersionSupportedShouldReturnTrueIfLowerThanMaxVersionSupported()
    {
        final SupportedVersions supportedVersions = new SupportedVersions.Builder()
            .addSupportedVersion(Scopes.MOBILE_CONNECT, "mc_v1.2").build();
        final String version = "mc_v1.1";

        final boolean versionSupported  = supportedVersions.isVersionSupported(version);

        assertTrue(versionSupported);
    }

    @Test
    public void isVersionSupportedShouldReturnFalseIfHigherThanMaxVersionSupported()
    {
        final SupportedVersions supportedVersions = new SupportedVersions.Builder()
            .addSupportedVersion(Scopes.MOBILE_CONNECT, "mc_v1.2").build();
        final String version = "mc_v1.3";

        final boolean versionSupported  = supportedVersions.isVersionSupported(version);

        assertFalse(versionSupported);
    }
}
