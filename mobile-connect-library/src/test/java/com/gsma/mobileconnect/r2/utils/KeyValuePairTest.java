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

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Tests {@link KeyValuePair}
 *
 * @since 2.0
 */
public class KeyValuePairTest
{
    private final KeyValuePair pairAA = new KeyValuePair("a", "a");
    private final KeyValuePair pairAB = new KeyValuePair("a", "b");
    private final KeyValuePair pairBA = new KeyValuePair("a", "a");
    private final KeyValuePair pairBB = new KeyValuePair("a", "b");

    @DataProvider
    public Object[][] equalsData()
    {
        return new Object[][] {{this.pairAA, this.pairAA, true}, {this.pairAA, this.pairAB, false},
                               {this.pairAA, this.pairBA, true}, {this.pairAA, this.pairBB, false},
                               {this.pairBA, this.pairAA, true}, {this.pairBA, this.pairAB, false},
                               {this.pairBA, this.pairBA, true}, {this.pairBA, this.pairBB, false}};
    }

    @Test(dataProvider = "equalsData")
    public void equals(final KeyValuePair first, final KeyValuePair second, final boolean equals)
    {
        assertEquals(first.equals(second), equals);
        assertEquals(first.hashCode() == second.hashCode(), equals);
    }

    @Test
    public void toString_()
    {
        assertEquals(this.pairAA.toString(), "KeyValuePair(key=a,value=a)");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void constructor_nullKey()
    {
        new KeyValuePair(null, "");
    }
}
