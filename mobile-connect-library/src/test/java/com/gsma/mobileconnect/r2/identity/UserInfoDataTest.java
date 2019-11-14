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

import com.gsma.mobileconnect.r2.model.json.IJsonService;
import com.gsma.mobileconnect.r2.model.json.GsonJsonService;
import com.gsma.mobileconnect.r2.model.json.JsonDeserializationException;
import com.gsma.mobileconnect.r2.model.json.JsonSerializationException;
import com.gsma.mobileconnect.r2.service.identity.AddressData;
import com.gsma.mobileconnect.r2.service.identity.UserInfoData;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Tests {@link UserInfoData}
 *
 * @since 2.0
 */
public class UserInfoDataTest
{
    private final IJsonService jsonService = new GsonJsonService();

    @Test
    public void userInfoDataShouldSerializeAndDeserialize()
        throws JsonDeserializationException, JsonSerializationException
    {
        final String responseJson =
            "{\"sub\":\"aaaaaaa-bbbb-aaaaa-bbbbbbb\",\"name\":\"David Andrew Smith\",\"family_name\":\"Smith\",\"given_name\":\"David\",\"middle_name\":\"Andrew\",\"nickname\":\"Dave\",\"preferred_username\":\"testname\",\"profile\":\"http://profile.com/profile\",\"picture\":\"http://picture.com/picture\",\"website\":\"http://website.com/\",\"gender\":\"Male\",\"birthdate\":\"1990-11-04\",\"zoneinfo\":\"Europe/London\",\"locale\":\"en-GB\",\"updated_at\":1472136214,\"email\":\"test@test.com\",\"email_verified\":true,\"address\":{\"formatted\":\"123 Fake Street Formatted\",\"street_address\":\"123 Fake Street\",\"locality\":\"Manchester\",\"region\":\"Greater Manchester\",\"postal_code\":\"M1 1AB\",\"country\":\"England\"},\"phone_number\":\"+447700900250\",\"phone_number_verified\":true}";

        final UserInfoData userInfoData =
            this.jsonService.deserialize(responseJson, UserInfoData.class);

        assertEquals(userInfoData.getSub(), "aaaaaaa-bbbb-aaaaa-bbbbbbb");
        assertEquals(userInfoData.getName(), "David Andrew Smith");
        assertEquals(userInfoData.getFamilyName(), "Smith");
        assertEquals(userInfoData.getGivenName(), "David");
        assertEquals(userInfoData.getMiddleName(), "Andrew");
        assertEquals(userInfoData.getNickname(), "Dave");
        assertEquals(userInfoData.getPreferredUsername(), "testname");
        assertEquals(userInfoData.getProfile(), "http://profile.com/profile");
        assertEquals(userInfoData.getPicture(), "http://picture.com/picture");
        assertEquals(userInfoData.getWebsite(), "http://website.com/");
        assertEquals(userInfoData.getGender(), "Male");
        assertEquals(userInfoData.getBirthdate(), "1990-11-04");
        assertEquals(userInfoData.getZoneinfo(), "Europe/London");
        assertEquals(userInfoData.getLocale(), "en-GB");
        assertEquals(userInfoData.getUpdatedAt(), new Long(1472136214L));
        assertEquals(userInfoData.getEmail(), "test@test.com");
        assertEquals(userInfoData.getEmailVerified(), Boolean.TRUE);

        final AddressData addressData = userInfoData.getAddress();
        assertEquals(addressData.getFormatted(), "123 Fake Street Formatted");
        assertEquals(addressData.getStreetAddress(), "123 Fake Street");
        assertEquals(addressData.getLocality(), "Manchester");
        assertEquals(addressData.getRegion(), "Greater Manchester");
        assertEquals(addressData.getPostalCode(), "M1 1AB");
        assertEquals(addressData.getCountry(), "England");

        assertEquals(userInfoData.getPhoneNumber(), "+447700900250");
        assertEquals(userInfoData.getPhoneNumberVerified(), Boolean.TRUE);

        final String actual = this.jsonService.serialize(userInfoData);

        assertEquals(actual, responseJson);
    }
}
