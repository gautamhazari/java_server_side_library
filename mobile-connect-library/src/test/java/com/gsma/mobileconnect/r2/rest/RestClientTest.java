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
package com.gsma.mobileconnect.r2.rest;

import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.encoding.DefaultEncodeDecoder;
import com.gsma.mobileconnect.r2.exceptions.RequestFailedException;
import com.gsma.mobileconnect.r2.json.IJsonService;
import com.gsma.mobileconnect.r2.json.JacksonJsonService;
import com.gsma.mobileconnect.r2.utils.KeyValuePair;
import com.gsma.mobileconnect.r2.utils.TestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.internal.thread.ThreadTimeoutException;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Tests {@link RestClient}
 *
 * @since 2.0
 */
public class RestClientTest
{
    private static final URI TEST_URI = URI.create("http://test");
    private static final URI REDIRECT_URI = URI.create("http://redirect");
    private static final RestAuthentication AUTHENTICATION =
        RestAuthentication.basic("test-key", "test-secret", new DefaultEncodeDecoder());
    private static final String SOURCE_IP = "192.168.0.1";
    private static final List<KeyValuePair> COOKIES =
        new KeyValuePair.ListBuilder().add("test-cookie", "test-cookie-value").build();

    private static final String[] EXPECTED_HEADERS =
        new String[] {"Cookie=test-cookie=test-cookie-value;", "X-Source-IP=192.168.0.1",
                      "Authorization=Basic dGVzdC1rZXk6dGVzdC1zZWNyZXQ=",
                      "Accept=application/json"};

    @Mock private HttpClient httpClient;
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    @Captor private ArgumentCaptor<HttpUriRequest> requestCaptor;

    private IJsonService jsonService = new JacksonJsonService();
    private RestClient restClient;

    private static String getStringContent(final StringEntity entity) throws IOException
    {
        return IOUtils.toString(entity.getContent(), "UTF-8");
    }

    @BeforeMethod
    public void beforeMethod()
    {
        MockitoAnnotations.initMocks(this);

        this.restClient = new RestClient.Builder()
            .withHttpClient(httpClient)
            .withJsonService(jsonService)
            .withTimeout(10L, TimeUnit.MILLISECONDS)
            .withWaitTime(1L)
            .build();
    }

    @AfterClass
    public void afterClass() throws InterruptedException
    {
        scheduledExecutorService.shutdown();
        assertTrue(scheduledExecutorService.awaitTermination(5L, TimeUnit.SECONDS));
    }

    @Test
    public void postJsonContent() throws RequestFailedException, IOException
    {
        final Object objContent = new KeyValuePair("test", "testvalue");
        final String jsonContent = "{\"key\":\"test\",\"value\":\"testvalue\"}";

        restClient.postJsonContent(TEST_URI, AUTHENTICATION, objContent, SOURCE_IP, COOKIES);

        verify(httpClient).execute(requestCaptor.capture(),
            isA(RestClient.RestResponseHandler.class));

        final HttpEntityEnclosingRequest request =
            verifyRequest("POST", TEST_URI, HttpEntityEnclosingRequest.class);

        final StringEntity entity = (StringEntity) request.getEntity();
        assertEqualsNoOrder(TestUtils.splitArray(getStringContent(entity)),
            TestUtils.splitArray(jsonContent));
        assertEquals(entity.getContentType().getValue(),
            ContentType.APPLICATION_JSON.withCharset("UTF-8").toString());
    }

    @Test(expectedExceptions = RequestFailedException.class)
    public void postJsonContent_invalid() throws RequestFailedException
    {
        restClient.postJsonContent(TEST_URI, AUTHENTICATION, new Object(), SOURCE_IP, COOKIES);
    }

    @Test
    public void postStringContent() throws RequestFailedException, IOException
    {
        final String content = "test content to go inside request";

        restClient.postStringContent(TEST_URI, AUTHENTICATION, content,
            ContentType.TEXT_PLAIN.withCharset("UTF-8"), SOURCE_IP, COOKIES);

        verify(httpClient).execute(requestCaptor.capture(),
            isA(RestClient.RestResponseHandler.class));

        final HttpEntityEnclosingRequest request =
            verifyRequest("POST", TEST_URI, HttpEntityEnclosingRequest.class);

        final StringEntity entity = (StringEntity) request.getEntity();
        assertEquals(getStringContent(entity), content);
        assertEquals(entity.getContentType().getValue(),
            ContentType.TEXT_PLAIN.withCharset("UTF-8").toString());
    }

    @Test
    public void postContent() throws RequestFailedException, IOException
    {
        final HttpEntity entity = new BasicHttpEntity();

        restClient.postContent(TEST_URI, AUTHENTICATION, entity, SOURCE_IP, COOKIES);

        final HttpEntityEnclosingRequest request =
            verifyRequest("POST", TEST_URI, HttpEntityEnclosingRequest.class);

        assertEquals(request.getEntity(), entity);
    }

    @Test(expectedExceptions = {RequestFailedException.class, ThreadTimeoutException.class}, invocationTimeOut = 1500L)
    public void submitRequest_timeout() throws RequestFailedException, IOException
    {
        when(httpClient.execute(isA(HttpUriRequest.class),
            isA(RestClient.RestResponseHandler.class))).thenAnswer(new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable
            {
                final HttpUriRequest request =
                    invocationOnMock.getArgumentAt(0, HttpUriRequest.class);

                while (!request.isAborted())
                {
                    Thread.sleep(50L);
                }

                throw new InterruptedIOException("request has been aborted");
            }
        });

        restClient.postJsonContent(TEST_URI, AUTHENTICATION, "test", SOURCE_IP, COOKIES);
    }

    @Test(expectedExceptions = RequestFailedException.class)
    public void submitRequest_interupted() throws RequestFailedException, IOException
    {
        when(httpClient.execute(isA(HttpUriRequest.class),
            isA(RestClient.RestResponseHandler.class))).thenAnswer(new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable
            {
                throw new InterruptedIOException("test exception");
            }
        });

        restClient.postJsonContent(TEST_URI, AUTHENTICATION, "test", SOURCE_IP, COOKIES);
    }

    @Test
    public void submitRequest_unexpectedError() throws RequestFailedException, IOException
    {
        final Exception expectedException = new RuntimeException("test exception");

        try
        {
            when(httpClient.execute(isA(HttpUriRequest.class),
                isA(RestClient.RestResponseHandler.class))).thenThrow(expectedException);

            restClient.postJsonContent(TEST_URI, AUTHENTICATION, "value", SOURCE_IP, COOKIES);

            fail("expected exception");
        }
        catch (final RequestFailedException rfe)
        {
            assertEquals(rfe.getMethod(), "POST");
            assertEquals(rfe.getUri(), TEST_URI);
            assertEquals(rfe.getCause(), expectedException);

            final MobileConnectStatus status = rfe.toMobileConnectStatus("test");

            assertEquals(status.getResponseType(), MobileConnectStatus.ResponseType.ERROR);
            assertEquals(status.getErrorCode(), "http_failure");
            assertEquals(status.getErrorMessage(),
                "An HTTP failure occurred while performing fetch for 'test'");
            assertEquals(status.getException(), rfe);
        }
    }

    @Test
    public void responseHandler() throws IOException
    {
        // given:
        final Future<?> future = mock(Future.class);
        final HttpResponse httpResponse = mock(HttpResponse.class, RETURNS_DEEP_STUBS);

        final Header[] headers = new Header[] {new BasicHeader("test", "testvalue")};

        final RestClient.RestResponseHandler handler =
            new RestClient.RestResponseHandler("GET", TEST_URI, future);

        when(httpResponse.getAllHeaders()).thenReturn(headers);
        when(httpResponse.getStatusLine().getStatusCode()).thenReturn(
            org.apache.commons.httpclient.HttpStatus.SC_OK);
        when(httpResponse.getEntity()).thenReturn(
            new StringEntity("{}", ContentType.APPLICATION_JSON.withCharset("UTF-8")));

        // when:
        final RestResponse restResponse = handler.handleResponse(httpResponse);

        // that:
        assertNotNull(restResponse);
        assertEquals(restResponse.getMethod(), "GET");
        assertEquals(restResponse.getUri(), TEST_URI);
        assertEquals(restResponse.getStatusCode(), HttpStatus.SC_OK);
        assertEquals(restResponse.getContent(), "{}");
        assertEquals(restResponse.getHeaders().get(0), new KeyValuePair("test", "testvalue"));

        verify(future).cancel(false);
    }

    private <T extends HttpRequest> T verifyRequest(final String method, final URI uri,
        final Class<T> clazz) throws IOException
    {
        verify(httpClient).execute(requestCaptor.capture(),
            isA(RestClient.RestResponseHandler.class));

        final HttpUriRequest request = requestCaptor.getValue();

        assertNotNull(request);
        assertEquals(request.getMethod(), method);
        assertEquals(request.getURI(), uri);
        assertEqualsNoOrder(headersAsStrings(request.getAllHeaders()), EXPECTED_HEADERS);
        assertTrue(clazz.isInstance(request));

        return clazz.cast(request);
    }

    private String[] headersAsStrings(final Header[] headers)
    {
        final List<String> strings = new ArrayList<String>();
        for (final Header header : headers)
        {
            strings.add(header.getName() + "=" + header.getValue());
        }
        return strings.toArray(new String[] {});
    }


    @Test
    public void testGetFinalRedirectSuccess() throws RequestFailedException, IOException
    {
        List<KeyValuePair> headers = new ArrayList<KeyValuePair>();
        headers.add(new KeyValuePair("Location", "http://redirect"));
        RestResponse restResponse = new RestResponse.Builder()
            .withHeaders(headers)
            .withStatusCode(302)
            .build();
        when(httpClient.execute(any(HttpUriRequest.class),
            any(RestClient.RestResponseHandler.class))).thenReturn(restResponse);

        URI uriResponse = restClient.getFinalRedirect(TEST_URI, REDIRECT_URI, AUTHENTICATION);

        assertEquals(uriResponse, REDIRECT_URI);
    }

    @Test(expectedExceptions = RequestFailedException.class)
    public void testGetFinalRedirectTimedOut() throws RequestFailedException, IOException
    {
        List<KeyValuePair> headers = new ArrayList<KeyValuePair>();
        headers.add(new KeyValuePair("Location", "http://1redirect"));
        RestResponse restResponse = new RestResponse.Builder()
            .withHeaders(headers)
            .withStatusCode(302)
            .build();
        when(httpClient.execute(any(HttpUriRequest.class),
            any(RestClient.RestResponseHandler.class))).thenReturn(restResponse);

        restClient.getFinalRedirect(TEST_URI, REDIRECT_URI, AUTHENTICATION);
    }

    @SuppressWarnings("unchecked")
    @Test(expectedExceptions = RequestFailedException.class)
    public void testGetFinalRedirectRequestFailed() throws RequestFailedException, IOException
    {
        when(httpClient.execute(any(HttpUriRequest.class),
            any(RestClient.RestResponseHandler.class))).thenThrow(RequestFailedException.class);

        restClient.getFinalRedirect(TEST_URI, REDIRECT_URI, AUTHENTICATION);
    }

    @SuppressWarnings("unchecked")
    @Test(expectedExceptions = RequestFailedException.class)
    public void testGetFinalRedirectURISyntaxException() throws RequestFailedException, IOException
    {
        List<KeyValuePair> headers = new ArrayList<KeyValuePair>();
        headers.add(new KeyValuePair("Location", "%$!$"));
        RestResponse restResponse = new RestResponse.Builder()
            .withHeaders(headers)
            .withStatusCode(302)
            .build();
        when(httpClient.execute(any(HttpUriRequest.class),
            any(RestClient.RestResponseHandler.class))).thenReturn(restResponse);

        restClient.getFinalRedirect(TEST_URI, REDIRECT_URI, AUTHENTICATION);
    }

}
