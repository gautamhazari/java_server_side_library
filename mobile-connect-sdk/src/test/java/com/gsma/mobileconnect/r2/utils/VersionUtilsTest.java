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
package com.gsma.mobileconnect.r2.utils;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

/**
 * Tests {@link VersionUtils}
 *
 * @since 2.0
 */
public class VersionUtilsTest
{
    @Test
    public void testVersionNumbers()
    {
        assertEquals(VersionUtils.versionCompare("2", "1"), 1, "Comparing 2 and 1");
        assertEquals(VersionUtils.versionCompare("1", "1"), 0, "Comparing 1 and 1");
        assertEquals(VersionUtils.versionCompare("0", "1"), -1, "Comparing 0 and 1");

        assertEquals(VersionUtils.versionCompare("1.2", "1.1"), 1, "Comparing 1.2 and 1.1");
        assertEquals(VersionUtils.versionCompare("1.1", "1.1"), 0 , "Comparing 1.1 and 1.1");
        assertEquals(VersionUtils.versionCompare("1.0", "1.1"), -1, "Comparing 1.0 and 1.1");

        assertEquals(VersionUtils.versionCompare("1.1.2", "1.1.1"), 1, "Comparing 1.1.2 and 1.1.1");
        assertEquals(VersionUtils.versionCompare("1.1.1", "1.1.1"), 0, "Comparing 1.1.1 and 1.1.1");
        assertEquals(VersionUtils.versionCompare("1.1.0", "1.1.1"), -1, "Comparing 1.1.0 and 1.1.1");

        assertEquals(VersionUtils.versionCompare("1.1.1.2", "1.1.1.1"), 1, "Comparing 1.1.1.2 and 1.1.1.1");
        assertEquals(VersionUtils.versionCompare("1.1.1.1", "1.1.1.1"), 0, "Comparing 1.1.1.1 and 1.1.1.1");
        assertEquals(VersionUtils.versionCompare("1.1.1.0", "1.1.1.1"), -1, "Comparing 1.1.1.0 and 1.1.1.1");

        assertEquals(VersionUtils.versionCompare("1.1.1", "2"), -1, "Comparing 1.1 and 2");
        assertEquals(VersionUtils.versionCompare("2.1", "1.3"), 1, "Comparing 2.1 and 1.2");
        assertEquals(VersionUtils.versionCompare("2", "1.1.1"), 1, "Comparing 2 and 1.1.1");
        assertEquals(VersionUtils.versionCompare("1.1.0", "1.1"), 0, "Comparing 1.1.0 and 1.1");

        assertEquals(VersionUtils.versionCompare("mc_v1.1.0", "mc_v1.1"), 0, "Comparing mc_v1.1.0 and mc_v1.1");
    }
}
