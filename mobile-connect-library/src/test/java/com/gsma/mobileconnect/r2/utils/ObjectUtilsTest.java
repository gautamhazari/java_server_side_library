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

import com.gsma.mobileconnect.r2.model.exceptions.InvalidArgumentException;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @since 2.0
 */
public class ObjectUtilsTest
{
    @Test
    public void requireNonNull_nonNull()
    {
        final String expected = "expected";
        final String actual = ObjectUtils.requireNonNull(expected, "expected");

        assertEquals(actual, expected);
    }

    @Test(expectedExceptions = InvalidArgumentException.class, expectedExceptionsMessageRegExp = "Required parameter 'obj' was null")
    public void requireNonNull_null()
    {
        ObjectUtils.requireNonNull(null, "obj");
    }

    @Test
    public void defaultIfNull_nonNull()
    {
        final String expected = "expected";
        final String actual = ObjectUtils.defaultIfNull(expected, "unexpected");

        assertEquals(actual, expected);
    }

    @Test
    public void defaultIfNull_null()
    {
        final String expected = "expected";
        final String actual = ObjectUtils.defaultIfNull(null, expected);

        assertEquals(actual, expected);
    }
}
