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
package com.gsma.mobileconnect.r2.web.rest;

import com.gsma.mobileconnect.r2.model.exceptions.RequestFailedException;
import com.gsma.mobileconnect.r2.utils.KeyValuePair;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

/**
 * Test implementation of {@link IRestClient}.
 *
 * @since 2.0
 */
public class MockRestClient implements IRestClient
{
    private final Queue<Object> queue = new ConcurrentLinkedQueue<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(MockRestClient.class);

    /**
     * Queue a response.
     *
     * @param restResponse to queue.
     */
    public MockRestClient addResponse(final RestResponse restResponse)
    {
        this.queue.add(restResponse);
        return this;
    }

    /**
     * Queue a failure.
     *
     * @param rfe to queue.
     */
    public MockRestClient addResponse(final RequestFailedException rfe)
    {
        this.queue.add(rfe);
        return this;
    }

    private RestResponse getNext() throws RequestFailedException
    {
        final Object next = this.queue.poll();
        assertNotNull(next, "no queued response");
        if (next instanceof RestResponse)
        {
            return (RestResponse) next;
        }
        else if (next instanceof RequestFailedException)
        {
            throw (RequestFailedException) next;
        }
        else
        {
            fail("unexpected entry in queue " + next.getClass().getName());
            return null;
        }
    }

    /**
     * Flushes the queue, returning any remaining items.
     *
     * @return list of any items remaining on the queue.
     */
    public List<Object> reset()
    {
        final List<Object> remaining = new ArrayList<>();
        while (!this.queue.isEmpty())
        {
            remaining.add(this.queue.poll());
        }
        return remaining;
    }

    @Override
    public RestResponse get(URI uri, RestAuthentication authentication, String xRedirect, String sourceIp,
        List<KeyValuePair> queryParams, Iterable<KeyValuePair> cookies)
        throws RequestFailedException
    {
        return this.getNext();
    }

    @Override
    public RestResponse getDiscovery(URI uri, RestAuthentication authentication, String xRedirect, String sourceIp,
                                     String clientSideVersion, String serverSideVersion, List<KeyValuePair> queryParams,
                                     Iterable<KeyValuePair> cookies) throws RequestFailedException {
        return this.getNext();
    }

    @Override
    public RestResponse postDiscoveryFormData(URI uri, RestAuthentication authentication, String xRedirect, List<KeyValuePair> formData, String sourceIp,
                                              final String clientSideVersion, final String serverSideVersion, Iterable<KeyValuePair> cookies) throws RequestFailedException {
        return this.getNext();
    }

    @Override
    public RestResponse postFormData(URI uri, RestAuthentication authentication, String xRedirect,
                                     List<KeyValuePair> formData, String sourceIp, Iterable<KeyValuePair> cookies)
        throws RequestFailedException
    {
        return this.getNext();
    }

    @Override
    public RestResponse postJsonContent(URI uri, RestAuthentication authentication, Object content,
        String sourceIp, Iterable<KeyValuePair> cookies) throws RequestFailedException
    {
        return this.getNext();
    }

    @Override
    public RestResponse postStringContent(URI uri, RestAuthentication authentication,
        String content, ContentType contentType, String sourceIp, Iterable<KeyValuePair> cookies)
        throws RequestFailedException
    {
        return this.getNext();
    }

    @Override
    public RestResponse postContent(URI uri, RestAuthentication authentication, HttpEntity content,
        String sourceIp, Iterable<KeyValuePair> cookies) throws RequestFailedException
    {
        return this.getNext();
    }

    @Override
    public URI getFinalRedirect(URI authUrl, URI redirectUrl, RestAuthentication authentication)
        throws RequestFailedException
    {
        try
        {
            return new URI(redirectUrl.toString() + "?code=code");
        }
        catch (URISyntaxException e)
        {
            LOGGER.warn(e.getMessage());
            throw new RequestFailedException("GET", redirectUrl, e);
        }
    }
}
