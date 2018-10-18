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
package com.gsma.mobileconnect.r2.claims;

import com.gsma.mobileconnect.r2.json.IJsonService;
import com.gsma.mobileconnect.r2.json.JacksonJsonService;
import com.gsma.mobileconnect.r2.json.JsonDeserializationException;
import com.gsma.mobileconnect.r2.json.JsonSerializationException;
import com.gsma.mobileconnect.r2.utils.TestUtils;
import org.testng.annotations.Test;

import static org.testng.Assert.*;
import static org.testng.AssertJUnit.assertNull;

/**
 * Tests {@link Claims}
 *
 * @since 2.0
 */
public class ClaimsTest
{
    private final IJsonService jsonService = new JacksonJsonService();

    @Test
    public void claimsShouldSerialize() throws JsonSerializationException
    {
        final Claims claims = new Claims.Builder()
            .addVoluntary("test")
            .addEssential("test2")
            .add("test3", false, "1634")
            .add("test4", false, new Object[] {"123", "456"})
            .build();

        final String expected =
            "{\"test\":null,\"test2\":{\"essential\":true},\"test3\":{\"value\":\"1634\"},\"test4\":{\"values\":[\"123\",\"456\"]}}";

        final String actual = this.jsonService.serialize(claims);

        assertEqualsNoOrder(TestUtils.splitArray(actual), TestUtils.splitArray(expected));
    }

    @SuppressWarnings("AssertEqualsBetweenInconvertibleTypesTestNG")
    @Test
    public void claimsShouldDeserialize() throws JsonDeserializationException
    {
        final String expected =
            "{\"nonce\":\"1234567890\",\"aud\":\"x-clientid-x\",\"azp\":\"x-clientid-x\",\"iss\":\"http://mobileconnect.io\",\"exp\":2147483647,\"auth_time\":2147483647,\"iat\":1471007327}";

        final Claims claims = this.jsonService.deserialize(expected, Claims.class);

        assertFalse(claims.isEmpty());
        assertNull(claims.get("test"));
        assertEquals(claims.get("nonce").getValue(), "1234567890");
        assertEquals(claims.get("aud").getValue(), "x-clientid-x");
        assertEquals(claims.get("azp").getValue(), "x-clientid-x");
        assertEquals(claims.get("iss").getValue(), "http://mobileconnect.io");
        assertEquals(claims.get("exp").getValue(), 2147483647L);
        assertEquals(claims.get("auth_time").getValue(), 2147483647L);
        assertEquals(claims.get("iat").getValue(), 1471007327L);
    }
}
