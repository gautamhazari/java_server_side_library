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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertEqualsNoOrder;

/**
 * Tests {@link ClaimsValue}
 *
 * @since 2.0
 */
public class ClaimsValueTest
{
    private final IJsonService jsonService = new JacksonJsonService();

    @DataProvider
    public Object[][] claimsValueSerialization()
    {
        return new Object[][] {{Claims.ESSENTIAL_CLAIM, "{\"essential\":true}"},
                               {new ClaimsValue.Builder()
                                    .withEssential(Boolean.TRUE)
                                    .withValue("1263746").build(),
                                "{\"essential\":true,\"value\":\"1263746\"}"},
                               {new ClaimsValue.Builder()
                                    .withEssential(Boolean.FALSE)
                                    .withValue("1263746").build(), "{\"value\":\"1263746\"}"},
                               {new ClaimsValue.Builder()
                                    .withEssential(Boolean.TRUE)
                                    .withValues("1263746", "1263746").build(),
                                "{\"essential\":true,\"values\":[\"1263746\",\"1263746\"]}"},
                               {new ClaimsValue.Builder().withValue("1263746").build(),
                                "{\"value\":\"1263746\"}"},
                               {new ClaimsValue.Builder().withValues("1263746", "1263746").build(),
                                "{\"values\":[\"1263746\",\"1263746\"]}"}};
    }

    @Test(dataProvider = "claimsValueSerialization")
    public void claimsValueShouldSerialiseToJson(final ClaimsValue claimsValue,
        final String expected) throws JsonSerializationException
    {
        final String actual = new JacksonJsonService().serialize(claimsValue);

        assertEquals(actual, expected);
    }

    @Test(dataProvider = "claimsValueSerialization")
    public void claimsValueShouldDeserializeFromJson(final ClaimsValue expected, final String json)
        throws JsonDeserializationException
    {
        final ClaimsValue actual = this.jsonService.deserialize(json, ClaimsValue.class);

        assertEquals(actual.isEssential(), expected.isEssential());
        assertEquals(actual.getValue(), expected.getValue());
        assertEqualsNoOrder(actual.getValues(), expected.getValues());
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void claimsValueWithValueAndValuesShouldThrowException()
    {
        new ClaimsValue.Builder().withValue("test").withValues("test1", "test2").build();
    }
}
