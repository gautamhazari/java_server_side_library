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

import com.google.common.collect.ImmutableList;
import com.gsma.mobileconnect.r2.model.exceptions.InvalidArgumentException;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

/**
 * Tests {@link StringUtils}
 *
 * @since 2.0
 */
public final class StringUtilsTest
{
    @Test
    public void isNullOrEmpty_empty()
    {
        assertTrue(StringUtils.isNullOrEmpty(""));
    }

    @Test
    public void isNullOrEmpty_notEmpty()
    {
        assertFalse(StringUtils.isNullOrEmpty(" "));
    }

    @Test(expectedExceptions = InvalidArgumentException.class, expectedExceptionsMessageRegExp = "Required parameter 'test' was null or empty")
    public void requireNonEmpty_null()
    {
        StringUtils.requireNonEmpty(null, "test");
    }

    @Test(expectedExceptions = InvalidArgumentException.class, expectedExceptionsMessageRegExp = "Required parameter 'test' was null or empty")
    public void requireNonEmpty_empty()
    {
        StringUtils.requireNonEmpty("", "test");
    }

    @Test
    public void requireNonEmpty_notEmpty()
    {
        StringUtils.requireNonEmpty(" ", "test");
    }

    @Test
    public void join_empty()
    {
        assertEquals(StringUtils.join(ImmutableList.<String>of(), ","), "");
    }

    @Test
    public void join_values()
    {
        assertEquals(StringUtils.join(ImmutableList.of("a", "b", "c"), ","), "a,b,c");
    }

    @Test
    public void trimLeading_empty()
    {
        assertEquals(StringUtils.trimLeading("", 'x'), "");
    }

    @Test
    public void trimLeading_null()
    {
        assertEquals(StringUtils.trimLeading(null, 'x'), null);
    }

    @Test
    public void trimLeading_noMatch()
    {
        assertEquals(StringUtils.trimLeading("abcdef", 'x'), "abcdef");
    }

    @Test
    public void trimLeading_match()
    {
        assertEquals(StringUtils.trimLeading("xxxxabcxxxxabcxxx", 'x'), "abcxxxxabcxxx");
    }
}
