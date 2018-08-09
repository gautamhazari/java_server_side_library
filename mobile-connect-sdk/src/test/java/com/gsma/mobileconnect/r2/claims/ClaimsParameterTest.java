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

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Tests {@link ClaimsParameter}
 *
 * @since 2.0
 */
public class ClaimsParameterTest
{
    @Test
    public void userInfoShouldBeSet()
    {
        final Claims claims =
            new Claims.Builder().addEssential("test").add("test2", false, "1000").build();

        final ClaimsParameter claimsParameter =
            new ClaimsParameter.Builder().withUserinfo(claims).build();

        assertEquals(claimsParameter.getUserinfo(), claims);
    }

    @Test
    public void idTokenShouldBeSet()
    {
        final Claims claims =
            new Claims.Builder().addEssential("test").add("test2", false, "1000").build();

        final ClaimsParameter claimsParameter =
            new ClaimsParameter.Builder().withIdToken(claims).build();

        assertEquals(claimsParameter.getIdToken(), claims);
    }

    @Test
    public void emptyClaimsParameterShouldBeEmpty()
    {
        assertTrue(new ClaimsParameter.Builder().build().isEmpty());
    }

    @Test
    public void testIsEmptyWithEmptyUserInfo()
    {
        assertTrue(new ClaimsParameter.Builder()
            .withUserinfo(new Claims.Builder().build())
            .build()
            .isEmpty());
    }

    @Test
    public void testIsEmptyWithEmptyIdentityInfo()
    {
        assertTrue(new ClaimsParameter.Builder()
            .withIdToken(new Claims.Builder().build())
            .build()
            .isEmpty());
    }

    @Test
    public void testIsEmptyOnlyUserInfo()
    {
        assertFalse(new ClaimsParameter.Builder()
            .withUserinfo(new Claims.Builder().addEssential("key").build())
            .build()
            .isEmpty());
    }

    @Test
    public void testIsEmptyOnlyIdentity()
    {
        assertFalse(new ClaimsParameter.Builder()
            .withIdToken(new Claims.Builder().addEssential("key").build())
            .build()
            .isEmpty());
    }

    @Test
    public void testIsEmptyFalse()
    {
        assertFalse(new ClaimsParameter.Builder()
            .withIdToken(new Claims.Builder().addEssential("key").build())
            .withUserinfo(new Claims.Builder().addEssential("key").build())
            .build()
            .isEmpty());
    }
}
