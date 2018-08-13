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
package com.gsma.mobileconnect.r2.web;

import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.authentication.RequestTokenResponse;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.json.DiscoveryResponseData;
import com.gsma.mobileconnect.r2.json.Link;
import com.gsma.mobileconnect.r2.json.Response;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

/**
 * Tests {@link MobileConnectWebResponse}
 *
 * @since 2.0
 */
public class MobileConnectWebResponseTest
{
    @Test
    public void webResponseWithErrorStatus()
    {
        final MobileConnectStatus status = MobileConnectStatus.error("error", "message",
                                                                     new Exception("test exception"));
        final MobileConnectWebResponse mobileConnectWebResponse = new MobileConnectWebResponse(status);

        assertEquals(mobileConnectWebResponse.getStatus(), "failure");
        assertEquals(mobileConnectWebResponse.getAction(), "error");
        assertEquals(mobileConnectWebResponse.getError(), "error");
        assertEquals(mobileConnectWebResponse.getDescription(), "message");
        assertEquals(status.getException().getMessage(), "test exception");
    }

    @Test
    public void webResponseWithErrorAndDiscoveryResponseStatus()
    {
        final List<Link> links = new ArrayList<Link>();
        final Link link = new Link.Builder().withHref("href").withRel("applicationShortName").build();
        links.add(link);
        final Response response = new Response.Builder().build();
        final DiscoveryResponseData discoveryResponseData = new DiscoveryResponseData.Builder()
                                                                                     .withResponse(response)
                                                                                     .withLinks(links)
                                                                                     .withSubscriberId("subscriberId")
                                                                                     .build();
        final DiscoveryResponse discoveryResponse = new DiscoveryResponse.Builder()
                                                                         .withResponseData(discoveryResponseData)
                                                                         .build();
        final MobileConnectStatus status = MobileConnectStatus.error("error", "message",
                                                                     new Exception("test exception"),
                                                                     discoveryResponse);
        final MobileConnectWebResponse mobileConnectWebResponse = new MobileConnectWebResponse(status);

        assertEquals(mobileConnectWebResponse.getClientName(), "href");
        assertEquals(mobileConnectWebResponse.getSubscriberId(), "subscriberId");
        assertEquals(mobileConnectWebResponse.getStatus(), "failure");
        assertEquals(mobileConnectWebResponse.getAction(), "error");
        assertEquals(mobileConnectWebResponse.getError(), "error");
        assertEquals(mobileConnectWebResponse.getDescription(), "message");
        assertEquals(status.getException().getMessage(), "test exception");
    }

    @Test
    public void webResponseWithErrorAndRequestTokenResponseStatus()
    {
        final RequestTokenResponse requestTokenResponse = new RequestTokenResponse.Builder().build();
        final MobileConnectStatus status = MobileConnectStatus.error("error", "message",
                                                                     new Exception("test exception"),
                                                                     requestTokenResponse);
        final MobileConnectWebResponse mobileConnectWebResponse = new MobileConnectWebResponse(status);

        assertEquals(mobileConnectWebResponse.getToken(), null);
        assertEquals(mobileConnectWebResponse.getAction(), "error");
        assertEquals(mobileConnectWebResponse.getError(), "error");
        assertEquals(mobileConnectWebResponse.getDescription(), "message");
        assertEquals(mobileConnectWebResponse.getAction(), "error");
        assertEquals(status.getException().getMessage(), "test exception");
    }

    @Test
    public void webResponseWithOperatorSelectionStatus()
    {
        final MobileConnectStatus status = MobileConnectStatus.operatorSelection("url");
        final MobileConnectWebResponse mobileConnectWebResponse = new MobileConnectWebResponse(status);

        assertNull(mobileConnectWebResponse.getDescription());
        assertNull(mobileConnectWebResponse.getError());
        assertEquals(mobileConnectWebResponse.getStatus(), "success");
        assertEquals(mobileConnectWebResponse.getUrl(), "url");
        assertEquals(mobileConnectWebResponse.getAction(), "operator_selection");
    }

    @Test
    public void webResponseWithStartAuthenticationStatus()
    {
        final List<Link> links = new ArrayList<Link>();
        final Link link = new Link.Builder().withHref("href").withRel("applicationShortName").build();
        links.add(link);
        final Response response = new Response.Builder().build();
        final DiscoveryResponseData discoveryResponseData = new DiscoveryResponseData.Builder()
                                                                                     .withResponse(response)
                                                                                     .withLinks(links)
                                                                                     .withSubscriberId("subscriberId")
                                                                                     .build();
        final DiscoveryResponse discoveryResponse = new DiscoveryResponse.Builder()
                                                                         .withResponseData(discoveryResponseData)
                                                                         .build();
        final MobileConnectStatus status = MobileConnectStatus.startAuthentication(discoveryResponse);
        final MobileConnectWebResponse mobileConnectWebResponse = new MobileConnectWebResponse(status);

        assertEquals(mobileConnectWebResponse.getClientName(), "href");
        assertEquals(mobileConnectWebResponse.getSubscriberId(), "subscriberId");
        assertEquals(mobileConnectWebResponse.getStatus(), "success");
        assertEquals(mobileConnectWebResponse.getAction(), "start_authentication");
    }

    @Test
    public void webResponseWithStartDiscoveryStatus()
    {
        final MobileConnectStatus status = MobileConnectStatus.startDiscovery();
        final MobileConnectWebResponse mobileConnectWebResponse = new MobileConnectWebResponse(status);

        assertNull(mobileConnectWebResponse.getDescription());
        assertNull(mobileConnectWebResponse.getError());
        assertEquals(mobileConnectWebResponse.getStatus(), "success");
        assertEquals(mobileConnectWebResponse.getAction(), "start_discovery");
    }

    @Test
    public void webResponseWithAuthenticationStatus()
    {
        final MobileConnectStatus status = MobileConnectStatus.authentication("url", "state", "nonce");
        final MobileConnectWebResponse mobileConnectWebResponse = new MobileConnectWebResponse(status);

        assertNull(mobileConnectWebResponse.getDescription());
        assertNull(mobileConnectWebResponse.getError());
        assertEquals(mobileConnectWebResponse.getUrl(), "url");
        assertEquals(mobileConnectWebResponse.getState(), "state");
        assertEquals(mobileConnectWebResponse.getNonce(), "nonce");
        assertEquals(mobileConnectWebResponse.getStatus(), "success");
        assertEquals(mobileConnectWebResponse.getAction(), "authentication");
    }

    @Test
    public void webResponseWithCompleteStatusWithTokenNotValidated()
    {
        final RequestTokenResponse requestTokenResponse = new RequestTokenResponse.Builder().build();
        final MobileConnectStatus status = MobileConnectStatus.complete(requestTokenResponse);
        final MobileConnectWebResponse mobileConnectWebResponse = new MobileConnectWebResponse(status);

        assertEquals(mobileConnectWebResponse.getToken(), null);
        assertEquals(mobileConnectWebResponse.getStatus(), "success");
        assertEquals(mobileConnectWebResponse.getAction(), "complete");
        assertFalse(mobileConnectWebResponse.isTokenValidated());
    }

    @Test
    public void webResponseWithCompleteStatusWithTokenValidated()
    {
        final RequestTokenResponse requestTokenResponse = new RequestTokenResponse.Builder()
            .withTokenValidated(true).build();
        final MobileConnectStatus status = MobileConnectStatus.complete(requestTokenResponse);
        final MobileConnectWebResponse mobileConnectWebResponse = new MobileConnectWebResponse(status);

        assertEquals(mobileConnectWebResponse.getToken(), null);
        assertEquals(mobileConnectWebResponse.getStatus(), "success");
        assertEquals(mobileConnectWebResponse.getAction(), "complete");
        assertTrue(mobileConnectWebResponse.isTokenValidated());
    }

    @Test
    public void webResponseWithCompleteStatusWithOutcome()
    {
        final MobileConnectStatus status = MobileConnectStatus.complete("Operation outcome");
        final MobileConnectWebResponse mobileConnectWebResponse = new MobileConnectWebResponse(status);

        assertEquals(mobileConnectWebResponse.getOutcome(), "Operation outcome");
        assertEquals(mobileConnectWebResponse.getAction(), "complete");
    }

    @Test
    public void webResponseWithErrorStatusWithoutMessage()
    {
        final MobileConnectStatus status = MobileConnectStatus.error("task", new Exception("test exception"));
        final MobileConnectWebResponse mobileConnectWebResponse = new MobileConnectWebResponse(status);

        assertEquals(mobileConnectWebResponse.getStatus(), "failure");
        assertEquals(mobileConnectWebResponse.getAction(), "error");
        assertEquals(mobileConnectWebResponse.getError(), "unknown_error");
        assertEquals(mobileConnectWebResponse.getDescription(), "An unknown error occurred while performing task 'task'");
        assertEquals(status.getException().getMessage(), "test exception");
    }
}
