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
package com.gsma.mobileconnect.r2.identity;

import com.gsma.mobileconnect.r2.json.IJsonService;
import com.gsma.mobileconnect.r2.json.JacksonJsonService;
import com.gsma.mobileconnect.r2.json.JsonDeserializationException;
import com.gsma.mobileconnect.r2.json.JsonSerializationException;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Tests {@link IdentityData}
 *
 * @since 2.0
 */
public class IdentityDataTest
{
    private final IJsonService jsonService = new JacksonJsonService();

    @Test
    public void identityDataShouldSerializeAndDeserialize()
        throws JsonDeserializationException, JsonSerializationException
    {
        final String responseJson =
            "{\"sub\":\"411421B0-38D6-6568-A53A-DF99691B7EB6\",\"phone_number_alternate\":\"447700100100\",\"title\":\"Mr\",\"given_name\":\"David\",\"family_name\":\"Smith\",\"middle_name\":\"Andrew\",\"street_address\":\"123 Fake Street\",\"city\":\"Manchester\",\"state\":\"Greater Manchester\",\"postal_code\":\"M1 1AB\",\"country\":\"England\",\"email\":\"test@test.com\",\"phone_number\":\"447700200200\",\"birthdate\":\"1990-11-04\",\"national_identifier\":\"XXXXXXXXXX\"}";

        final IdentityData identityData =
            this.jsonService.deserialize(responseJson, IdentityData.class);

        assertEquals(identityData.getSub(), "411421B0-38D6-6568-A53A-DF99691B7EB6");
        assertEquals(identityData.getPhoneNumberAlternate(), "447700100100");
        assertEquals(identityData.getTitle(), "Mr");
        assertEquals(identityData.getGivenName(), "David");
        assertEquals(identityData.getFamilyName(), "Smith");
        assertEquals(identityData.getMiddleName(), "Andrew");
        assertEquals(identityData.getStreetAddress(), "123 Fake Street");
        assertEquals(identityData.getCity(), "Manchester");
        assertEquals(identityData.getState(), "Greater Manchester");
        assertEquals(identityData.getPostalCode(), "M1 1AB");
        assertEquals(identityData.getCountry(), "England");
        assertEquals(identityData.getEmail(), "test@test.com");
        assertEquals(identityData.getPhoneNumber(), "447700200200");
        assertEquals(identityData.getBirthdate(), "1990-11-04");
        assertEquals(identityData.getNationalIdentifier(), "XXXXXXXXXX");

        final String actual = this.jsonService.serialize(identityData);

        assertEquals(actual, responseJson);
    }
}
