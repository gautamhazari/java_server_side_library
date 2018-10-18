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

import com.gsma.mobileconnect.r2.encoding.DefaultEncodeDecoder;
import com.gsma.mobileconnect.r2.exceptions.InvalidArgumentException;
import com.gsma.mobileconnect.r2.exceptions.InvalidResponseException;
import com.gsma.mobileconnect.r2.json.JacksonJsonService;
import com.gsma.mobileconnect.r2.rest.MockRestClient;
import com.gsma.mobileconnect.r2.exceptions.RequestFailedException;
import com.gsma.mobileconnect.r2.rest.RestResponse;
import com.gsma.mobileconnect.r2.utils.HttpUtils;
import com.gsma.mobileconnect.r2.utils.KeyValuePair;
import com.gsma.mobileconnect.r2.utils.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

/**
 * Tests {@link IdentityService}
 *
 * @since 2.0
 */
public class IdentityServiceTest
{
    private static final RestResponse UNAUTHORIZED_RESPONSE = new RestResponse.Builder()
        .withStatusCode(HttpStatus.SC_UNAUTHORIZED)
        .withHeaders(new KeyValuePair.ListBuilder()
            .add(HttpHeaders.WWW_AUTHENTICATE,
                "Bearer error=\"invalid_request\", error_description=\"No Access Token\"")
            .build())
        .build();

    private static final RestResponse USERINFO_RESPONSE = new RestResponse.Builder()
        .withStatusCode(HttpStatus.SC_OK)
        .withContent(
            "{\"sub\":\"411421B0-38D6-6568-A53A-DF99691B7EB6\",\"email\":\"test2@example.com\",\"email_verified\":true}")
        .build();

    private static final URI USERINFO_URL = URI.create("http://userinfo");

    private final MockRestClient restClient = new MockRestClient();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final IIdentityService identityService = new IdentityService.Builder()
        .withJsonService(new JacksonJsonService())
        .withRestClient(this.restClient)
        .build();

    @AfterClass
    public void afterClass() throws InterruptedException
    {
        this.executorService.shutdown();
        assertTrue(this.executorService.awaitTermination(5L, TimeUnit.SECONDS));
    }

    @AfterMethod
    public void afterMethod()
    {
        assertEquals(this.restClient.reset().size(), 0, "restClient contains responses");
    }

    @Test
    public void requestUserInfoShouldHandleUserInfoResponse()
        throws RequestFailedException, InvalidResponseException
    {
        this.restClient.addResponse(USERINFO_RESPONSE);

        final IdentityResponse response =
            this.identityService.requestInfo(USERINFO_URL, "zmalqpxnskwocbdjeivbfhru",
                new DefaultEncodeDecoder());

        assertNotNull(response);
        assertEquals(response.getResponseCode(), HttpStatus.SC_OK);
        assertNotNull(response.getResponseJson());
    }

    @Test
    public void requestUserInfoShouldHandleUnauthorizedResponse()
        throws RequestFailedException, InvalidResponseException
    {
        this.restClient.addResponse(UNAUTHORIZED_RESPONSE);

        final IdentityResponse response =
            this.identityService.requestInfo(USERINFO_URL, "zmalqpxnskwocbdjeivbfhru",
                new DefaultEncodeDecoder());

        assertNotNull(response);
        assertEquals(response.getResponseCode(), HttpStatus.SC_UNAUTHORIZED);
        assertNull(response.getResponseJson());
        assertNotNull(response.getErrorResponse());
        assertFalse(StringUtils.isNullOrEmpty(response.getErrorResponse().getError()));
        assertFalse(StringUtils.isNullOrEmpty(response.getErrorResponse().getErrorDescription()));
    }

    @Test(expectedExceptions = RequestFailedException.class)
    public void requestUserInfoShouldHandleHttpRequestException() throws RequestFailedException
    {
        this.restClient.addResponse(
            new RequestFailedException(HttpUtils.HttpMethod.POST, USERINFO_URL, null));

        this.identityService.requestInfo(USERINFO_URL, "zmalqpxnskwocbdjeivbfhru",
            new DefaultEncodeDecoder());
    }

    @Test(expectedExceptions = InvalidArgumentException.class)
    public void requestUserInfoShouldThrowInvalidArgumentExceptionWhenUriNull()
        throws RequestFailedException
    {
        this.identityService.requestInfo(null, "zmalqpxnskwocbdjeivbfhru",
            new DefaultEncodeDecoder());
    }

    @Test(expectedExceptions = InvalidArgumentException.class)
    public void requestUserInfoShouldThrowInvalidArgumentExceptionWhenTokenEmpty()
        throws RequestFailedException
    {
        this.identityService.requestInfo(USERINFO_URL, "", new DefaultEncodeDecoder());
    }
}
