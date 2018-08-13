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

import com.fasterxml.jackson.databind.JsonNode;
import com.gsma.mobileconnect.r2.encoding.DefaultEncodeDecoder;
import com.gsma.mobileconnect.r2.ErrorResponse;
import com.gsma.mobileconnect.r2.json.JacksonJsonService;
import com.gsma.mobileconnect.r2.json.JsonDeserializationException;
import com.gsma.mobileconnect.r2.rest.RestResponse;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

/**
 * Tests {@link IdentityResponse}
 *
 * @since 2.0
 */
public class IdentityResponseTest
{
    private final JacksonJsonService jsonService = new JacksonJsonService();

    @Test
    public void builderShouldSetResponseJson()
    {
        final String responseJson =
            "{\"sub\":\"411421B0-38D6-6568-A53A-DF99691B7EB6\",\"email\":\"test2@example.com\",\"email_verified\":true}";

        final RestResponse restResponse = new RestResponse.Builder()
            .withStatusCode(HttpStatus.SC_ACCEPTED)
            .withContent(responseJson)
            .build();

        final IdentityResponse identityResponse =
            IdentityResponse.fromRestResponse(restResponse, this.jsonService, new DefaultEncodeDecoder());

        assertEquals(identityResponse.getResponseJson(), responseJson);
    }

    @Test
    public void builderShouldSetResponseWithDecodedJwtPayload() throws IOException
    {
        final String responseJwt =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI0MTE0MjFCMC0zOEQ2LTY1NjgtQTUzQS1ERjk5NjkxQjdFQjYiLCJlbWFpbCI6InRlc3QyQGV4YW1wbGUuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWV9.AcpILNH2Uvok99MQWwxP6X7x3OwtVmTOw0t9Hq00gmQ";

        final RestResponse restResponse = new RestResponse.Builder()
            .withStatusCode(HttpStatus.SC_ACCEPTED)
            .withContent(responseJwt)
            .build();

        final IdentityResponse identityResponse =
            IdentityResponse.fromRestResponse(restResponse, this.jsonService, new DefaultEncodeDecoder());

        final JsonNode jsonNode =
            this.jsonService.getObjectMapper().readTree(identityResponse.getResponseJson());

        assertNotNull(jsonNode);
        assertEquals(jsonNode.get("sub").asText(), "411421B0-38D6-6568-A53A-DF99691B7EB6");
        assertEquals(jsonNode.get("email").asText(), "test2@example.com");
        assertTrue(jsonNode.get("email_verified").asBoolean());
    }

    @Test
    public void builderShouldSetResponseWithNullContent()
    {
        final RestResponse restResponse =
            new RestResponse.Builder().withStatusCode(HttpStatus.SC_ACCEPTED).build();

        final IdentityResponse identityResponse =
            IdentityResponse.fromRestResponse(restResponse, this.jsonService, new DefaultEncodeDecoder());

        assertNull(identityResponse.getResponseJson());
    }

    @Test
    public void builderShouldSetErrorForInvalidFormatResponseData()
    {
        final String responseJson = "<html>not valid</html>";
        final RestResponse restResponse = new RestResponse.Builder()
            .withStatusCode(HttpStatus.SC_ACCEPTED)
            .withContent(responseJson)
            .build();

        final IdentityResponse identityResponse =
            IdentityResponse.fromRestResponse(restResponse, this.jsonService, new DefaultEncodeDecoder());

        assertNotNull(identityResponse.getErrorResponse());
        assertEquals(identityResponse.getErrorResponse().getError(), "invalid_format");
    }

    @Test
    public void responseDataAsShouldDeserialiseToUserInfoData() throws JsonDeserializationException
    {
        final String responseJson =
            "{\"sub\":\"411421B0-38D6-6568-A53A-DF99691B7EB6\",\"email\":\"test2@example.com\",\"email_verified\":true,\"phone_number\":\"+447700200200\",\"phone_number_verified\":true,\"birthdate\":\"1990-04-11\",\"updated_at\":\"1460779506\",\"address\":{\"formatted\":\"123 Fake Street \\r\\n Manchester\",\"postal_code\":\"M1 1AB\"}}";
        final RestResponse restResponse = new RestResponse.Builder()
            .withStatusCode(HttpStatus.SC_ACCEPTED)
            .withContent(responseJson)
            .build();

        final IdentityResponse identityResponse =
            IdentityResponse.fromRestResponse(restResponse, this.jsonService, new DefaultEncodeDecoder());
        final UserInfoData userInfoData =
            identityResponse.getResponseAs(UserInfoData.class, this.jsonService);

        assertNotNull(userInfoData);
        assertEquals(userInfoData.getSub(), "411421B0-38D6-6568-A53A-DF99691B7EB6");
        assertEquals(userInfoData.getEmail(), "test2@example.com");
        assertEquals(userInfoData.getEmailVerified(), Boolean.TRUE);
        assertEquals(userInfoData.getPhoneNumber(), "+447700200200");
        assertEquals(userInfoData.getPhoneNumberVerified(), Boolean.TRUE);

        final AddressData addressData = userInfoData.getAddress();

        assertNotNull(addressData);
        assertEquals(addressData.getFormatted(), "123 Fake Street \r\n Manchester");
        assertEquals(addressData.getPostalCode(), "M1 1AB");

        assertEquals(userInfoData.getBirthdate(), "1990-04-11");
        assertEquals(userInfoData.getUpdatedAt(), new Long(1460779506L));
    }

    @Test
    public void responseDataAsShouldReuseCachedResponse() throws JsonDeserializationException
    {
        final String responseJson =
            "{\"sub\":\"411421B0-38D6-6568-A53A-DF99691B7EB6\",\"email\":\"test2@example.com\",\"email_verified\":true,\"phone_number\":\"+447700200200\",\"phone_number_verified\":true,\"birthdate\":\"1990-04-11\",\"updated_at\":\"1460779506\",\"address\":{\"formatted\":\"123 Fake Street \\r\\n Manchester\",\"postal_code\":\"M1 1AB\"}}";
        final RestResponse restResponse = new RestResponse.Builder()
            .withStatusCode(HttpStatus.SC_ACCEPTED)
            .withContent(responseJson)
            .build();

        final IdentityResponse identityResponse =
            IdentityResponse.fromRestResponse(restResponse, this.jsonService, new DefaultEncodeDecoder());
        final UserInfoData first =
            identityResponse.getResponseAs(UserInfoData.class, this.jsonService);
        final UserInfoData second =
            identityResponse.getResponseAs(UserInfoData.class, this.jsonService);

        assertSame(first, second);
    }

    @Test
    public void responseDataAsShouldReturnNullIfResponseJsonNull()
        throws JsonDeserializationException
    {
        final RestResponse restResponse =
            new RestResponse.Builder().withStatusCode(HttpStatus.SC_ACCEPTED).build();

        final IdentityResponse identityResponse =
            IdentityResponse.fromRestResponse(restResponse, this.jsonService, new DefaultEncodeDecoder());
        final UserInfoData userInfoData =
            identityResponse.getResponseAs(UserInfoData.class, this.jsonService);

        assertNull(userInfoData);
    }

    @Test
    public void responseDataAsShouldReturnErrorResponseIfInJson()
    {
        final String responseJson =
            "{\"error\":\"test_error\", \"error_description\":\"test_description\", \"error_uri\":\"http://test\"}";
        final RestResponse restResponse = new RestResponse.Builder()
            .withStatusCode(HttpStatus.SC_OK)
            .withContent(responseJson)
            .build();

        final IdentityResponse identityResponse =
            IdentityResponse.fromRestResponse(restResponse, this.jsonService, new DefaultEncodeDecoder());

        assertNotNull(identityResponse);

        final ErrorResponse errorResponse = identityResponse.getErrorResponse();

        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), "test_error");
        assertEquals(errorResponse.getErrorDescription(), "test_description");
        assertEquals(errorResponse.getErrorUri(), "http://test");
    }
}
