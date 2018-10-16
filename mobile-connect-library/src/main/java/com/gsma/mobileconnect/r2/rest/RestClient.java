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

import com.gsma.mobileconnect.r2.constants.DefaultOptions;
import com.gsma.mobileconnect.r2.constants.Headers;
import com.gsma.mobileconnect.r2.constants.Parameters;
import com.gsma.mobileconnect.r2.exceptions.HeadlessOperationFailedException;
import com.gsma.mobileconnect.r2.exceptions.RequestFailedException;
import com.gsma.mobileconnect.r2.json.IJsonService;
import com.gsma.mobileconnect.r2.json.JsonSerializationException;
import com.gsma.mobileconnect.r2.utils.*;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Concrete implementation of {@link IRestClient}
 *
 * @since 2.0
 */
public class RestClient implements IRestClient
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RestClient.class);

    private final IJsonService jsonService;
    private final HttpClient httpClient;
    private final long timeout;
    private final long waitTime;
    private final RequestConfig requestConfig;

    private RestClient(Builder builder)
    {
        this.jsonService = builder.jsonService;
        this.httpClient = builder.httpClient;
        this.timeout = builder.timeout;
        this.waitTime = builder.waitTime;

        final int timeoutAsInt = (int) this.timeout;

        this.requestConfig = RequestConfig
            .custom()
            .setConnectionRequestTimeout(timeoutAsInt)
            .setConnectTimeout(timeoutAsInt)
            .setSocketTimeout(timeoutAsInt)
            .setRedirectsEnabled(true)
            .build();

        LOGGER.info("New instance of RestClient created with timeout={} ms", timeoutAsInt);
    }

    @Override
    public RestResponse getDiscovery(final URI uri, final RestAuthentication authentication, final String xRedirect,
                            final String sourceIp, final String clientSideVersion, final String serverSideVersion, final List<KeyValuePair> queryParams,
                            final Iterable<KeyValuePair> cookies) throws RequestFailedException
    {
        LOGGER.debug("Getting from uri={} for sourceIp={}",
                LogUtils.maskUri(uri, LOGGER, Level.DEBUG), sourceIp);

        final URIBuilder uriBuilder = new URIBuilder(uri);
        if (queryParams != null)
        {
            uriBuilder.addParameters(new ArrayList<NameValuePair>(queryParams));
        }

        try
        {
            final HttpUriRequest request = this
                    .createDiscoveryRequest(HttpUtils.HttpMethod.GET, uriBuilder.build(), xRedirect, authentication,
                            sourceIp, clientSideVersion, serverSideVersion, cookies)
                    .build();

            return this.submitRequest(request, true);
        }
        catch (final URISyntaxException use)
        {
            LOGGER.warn("Failed to construct uri for GET request; baseUri={}",
                    LogUtils.maskUri(uri, LOGGER, Level.WARN), use);
            throw new RequestFailedException(HttpUtils.HttpMethod.GET, uri, use);
        }
    }

    @Override
    public RestResponse get(final URI uri, final RestAuthentication authentication, final String xRedirect,
        final String sourceIp, final List<KeyValuePair> queryParams,
        final Iterable<KeyValuePair> cookies) throws RequestFailedException
    {
        LOGGER.debug("Getting from uri={} for sourceIp={}",
            LogUtils.maskUri(uri, LOGGER, Level.DEBUG), sourceIp);

        final URIBuilder uriBuilder = new URIBuilder(uri);
        if (queryParams != null)
        {
            uriBuilder.addParameters(new ArrayList<NameValuePair>(queryParams));
        }

        try
        {
            final HttpUriRequest request = this
                .createRequest(HttpUtils.HttpMethod.GET, uriBuilder.build(), xRedirect, authentication,
                    sourceIp, cookies)
                .build();

            return this.submitRequest(request, true);
        }
        catch (final URISyntaxException use)
        {
            LOGGER.warn("Failed to construct uri for GET request; baseUri={}",
                LogUtils.maskUri(uri, LOGGER, Level.WARN), use);
            throw new RequestFailedException(HttpUtils.HttpMethod.GET, uri, use);
        }
    }

    @Override
    public RestResponse postDiscoveryFormData(final URI uri, final RestAuthentication authentication, final String xRedirect,
                                     final List<KeyValuePair> formData, final String sourceIp, final String clientSideVersion, final String serverSideVersion,
                                     final Iterable<KeyValuePair> cookies) throws RequestFailedException
    {
        LOGGER.debug("Posting form data to uri={} for sourceIp={}",
                LogUtils.maskUri(uri, LOGGER, Level.DEBUG), sourceIp);

        final HttpUriRequest request = this
                .createDiscoveryRequest(HttpUtils.HttpMethod.POST, uri, xRedirect, authentication, sourceIp, clientSideVersion, serverSideVersion, cookies)
                .addParameters(
                        ObjectUtils.requireNonNull(formData, "formData").toArray(new NameValuePair[] {}))
                .build();
        return this.submitRequest(request, true);
    }

    @Override
    public RestResponse postFormData(final URI uri, final RestAuthentication authentication, final String xRedirect,
        final List<KeyValuePair> formData, final String sourceIp,
        final Iterable<KeyValuePair> cookies) throws RequestFailedException
    {
        LOGGER.debug("Posting form data to uri={} for sourceIp={}",
            LogUtils.maskUri(uri, LOGGER, Level.DEBUG), sourceIp);

        final HttpUriRequest request = this
            .createRequest(HttpUtils.HttpMethod.POST, uri, xRedirect, authentication, sourceIp, cookies)
            .addParameters(
                ObjectUtils.requireNonNull(formData, "formData").toArray(new NameValuePair[] {}))
            .build();
        return this.submitRequest(request, true);
    }

    @Override
    public RestResponse postJsonContent(final URI uri, final RestAuthentication authentication,
        final Object content, final String sourceIp, final Iterable<KeyValuePair> cookies)
        throws RequestFailedException
    {
        try
        {
            LOGGER.debug("Posting json content to uri={} for sourceIp={}",
                LogUtils.maskUri(uri, LOGGER, Level.DEBUG), sourceIp);

            final HttpEntity entity = new StringEntity(this.jsonService.serialize(content),
                ContentType.APPLICATION_JSON.withCharset("UTF-8"));
            return this.postContent(uri, authentication, entity, sourceIp, cookies);
        }
        catch (final JsonSerializationException jse)
        {
            LOGGER.warn("Failed to serialize content for post to uri={}",
                LogUtils.maskUri(uri, LOGGER, Level.WARN), jse);

            throw new RequestFailedException(HttpUtils.HttpMethod.POST, uri, jse);
        }
    }

    @Override
    public RestResponse postStringContent(final URI uri, final RestAuthentication authentication,
        final String content, final ContentType contentType, final String sourceIp,
        final Iterable<KeyValuePair> cookies) throws RequestFailedException
    {
        LOGGER.debug("Posting String content to uri={} with contentType={} for sourceIp={}",
            LogUtils.maskUri(uri, LOGGER, Level.DEBUG), contentType, sourceIp);

        final HttpEntity entity = new StringEntity(content, contentType);
        return this.postContent(uri, authentication, entity, sourceIp, cookies);
    }

    @Override
    public RestResponse postContent(final URI uri, final RestAuthentication authentication,
        final HttpEntity content, final String sourceIp, final Iterable<KeyValuePair> cookies)
        throws RequestFailedException
    {
        LOGGER.debug("Posting content to uri={} with length={}, contentType={} for sourceIp={}",
            LogUtils.maskUri(uri, LOGGER, Level.DEBUG), content.getContentLength(),
            content.getContentType(), sourceIp);

        final HttpUriRequest request = this
            .createRequest(HttpUtils.HttpMethod.POST, uri, authentication, sourceIp, cookies)
            .setEntity(ObjectUtils.requireNonNull(content, "content"))
            .build();

        return this.submitRequest(request, true);
    }

    @Override
    public URI getFinalRedirect(final URI authUrl, final URI targetUrl,
        final RestAuthentication authentication) throws RequestFailedException
    {
        try
        {
            return followUrls(authUrl, targetUrl, authentication);
        }
        catch (URISyntaxException e)
        {
            LOGGER.error("Invalid redirect URL", e);
            throw new RequestFailedException(HttpUtils.HttpMethod.GET, authUrl, e);
        }
        catch (HeadlessOperationFailedException e)
        {
            LOGGER.error("Too many redirects", e);
            throw new RequestFailedException(HttpUtils.HttpMethod.GET, authUrl, e);
        }
    }

    private URI followUrls(final URI authUrl, final URI targetUrl,
        final RestAuthentication authentication)
        throws HeadlessOperationFailedException, RequestFailedException, URISyntaxException
    {
        int numRedirects = 0;
        RestResponse response = null;
        URI nextUrl = authUrl;
        URI locationUri = null;

        do
        {
            if (numRedirects > DefaultOptions.MAX_REDIRECTS)
            {
                throw new HeadlessOperationFailedException(
                    "Headless operation failed either due to too many redirects or it timed out");
            }
            if (response != null)
            {
                nextUrl = locationUri == null ? nextUrl : locationUri;
                numRedirects++;
            }
            RequestBuilder requestBuilder =
                this.createRequest(HttpUtils.HttpMethod.GET, nextUrl, authentication, null, null);
            response = this.submitRequest(requestBuilder.build(), false);

            locationUri = this.retrieveLocation(response);

            if (locationUri != null && locationUri.toString().startsWith(targetUrl.toString()))
            {
                break;
            }
            waitForSometime();
        } while (true);
        return locationUri;
    }

    private void waitForSometime()
    {
        try
        {
            Thread.sleep(this.waitTime);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            LOGGER.info("Waking up and trying again");
        }
    }

    private URI retrieveLocation(RestResponse response) throws URISyntaxException
    {
        URI uri = null;
        for (KeyValuePair keyValuePair : response.getHeaders())
        {
            if ("Location".equalsIgnoreCase(keyValuePair.getKey()))
            {
                uri = new URI(keyValuePair.getValue());
                break;
            }
        }
        return uri;
    }

    /**
     * Create an HTTP request builder.
     *
     * @param method         either GET or POST.
     * @param uri            for request.
     * @param authentication to apply.
     * @param sourceIp       if identified.
     * @param cookies        to proxy.
     * @return initialised request builder which can be further customised.
     */
    private RequestBuilder createRequest(final HttpUtils.HttpMethod method, final URI uri,
        final RestAuthentication authentication, final String sourceIp,
        final Iterable<KeyValuePair> cookies)
    {
        LOGGER.debug(
            "Creating request with httpMethod={}, uri={}, authentication={} for sourceIp={}",
            method, LogUtils.maskUri(uri, LOGGER, Level.DEBUG), authentication, sourceIp);

        final RequestBuilder builder = RequestBuilder
            .create(ObjectUtils.requireNonNull(method, "method").name())
            .setUri(ObjectUtils.requireNonNull(uri, "uri"))
            .setConfig(this.requestConfig);

        return prepareRequest(builder, authentication, sourceIp, cookies);
    }

    private RequestBuilder createDiscoveryRequest(final HttpUtils.HttpMethod method, final URI uri, final String xRedirect,
                                         final RestAuthentication authentication, final String sourceIp, final String clientSideVersion, final String serverSideVersion,
                                         final Iterable<KeyValuePair> cookies)
    {
        LOGGER.debug(
                "Creating discovery request with httpMethod={}, uri={}, authentication={} for sourceIp={}",
                method, LogUtils.maskUri(uri, LOGGER, Level.DEBUG), authentication, sourceIp);

        final RequestBuilder builder = RequestBuilder
                .create(ObjectUtils.requireNonNull(method, "method").name())
                .setUri(ObjectUtils.requireNonNull(uri, "uri"))
                .setConfig(this.requestConfig);

        builder.addHeader(Headers.VERSION_SDK, Parameters.SDK_VERSION);
        builder.addHeader(Headers.CLIENT_SIDE_VERSION, clientSideVersion);
        builder.addHeader(Headers.SERVER_SIDE_VERSION, serverSideVersion);

        return prepareRequest(builder, xRedirect, authentication, sourceIp, cookies);
    }

    private RequestBuilder prepareRequest(final RequestBuilder builder, final String xRedirect,
                                          final RestAuthentication authentication, final String sourceIp,
                                          final Iterable<KeyValuePair> cookies) {

        return xRedirect != null ? prepareRequest(builder, authentication, sourceIp, cookies).addHeader(Parameters.X_REDIRECT, xRedirect)
                : prepareRequest(builder, authentication, sourceIp, cookies);
    }

    private RequestBuilder prepareRequest(final RequestBuilder builder,
                                          final RestAuthentication authentication, final String sourceIp,
                                          final Iterable<KeyValuePair> cookies) {
        if (cookies != null)
        {
            final StringBuilder cookieBuilder = new StringBuilder();
            for (final KeyValuePair cookie : cookies)
            {
                cookieBuilder
                        .append(cookie.getKey())
                        .append('=')
                        .append(cookie.getValue())
                        .append(';');
            }
            builder.addHeader(Headers.COOKIE, cookieBuilder.toString());
        }

        if (!StringUtils.isNullOrEmpty(sourceIp))
        {
            builder.addHeader(Headers.X_SOURCE_IP, sourceIp);
        }

        if (authentication != null)
        {
            builder.addHeader(HttpHeaders.AUTHORIZATION,
                    authentication.getScheme() + " " + authentication.getParameter());
        }

        return builder;
    }
    private RequestBuilder createRequest(final HttpUtils.HttpMethod method, final URI uri, final String xRedirect,
                                         final RestAuthentication authentication, final String sourceIp,
                                         final Iterable<KeyValuePair> cookies)
    {
        LOGGER.debug(
                "Creating request with httpMethod={}, uri={}, authentication={} for sourceIp={}",
                method, LogUtils.maskUri(uri, LOGGER, Level.DEBUG), authentication, sourceIp);

        final RequestBuilder builder = RequestBuilder
                .create(ObjectUtils.requireNonNull(method, "method").name())
                .setUri(ObjectUtils.requireNonNull(uri, "uri"))
                .setConfig(this.requestConfig);

        return prepareRequest(builder, xRedirect, authentication, sourceIp, cookies);
    }
    /**
     * Submits a request to the executor.  When the request runs, an additional task is scheduled in
     * the future which will abort the request after the configured timeout period.
     *
     * @param request   to be run.
     * @param addHeader boolean flag to specify if headers should be added
     * @return the RestResponse.
     * @throws RequestFailedException if there is a failure issuing the request.
     */
    private RestResponse submitRequest(final HttpUriRequest request, final boolean addHeader)
        throws RequestFailedException
    {
        ObjectUtils.requireNonNull(request, "request");
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        final Future<?> abortFuture = executorService.schedule(new Runnable()
        {
            @Override
            public void run()
            {
                LOGGER.debug(
                    "Aborting httpMethod={} request to uri={} as request timed out, timeout={} ms",
                    request.getMethod(), LogUtils.maskUri(request.getURI(), LOGGER, Level.DEBUG),
                    RestClient.this.timeout);

                request.abort();
            }
        }, this.timeout, TimeUnit.MILLISECONDS);

        try
        {
            if (addHeader)
            {
                request.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            }
            LOGGER.debug("Issuing httpMethod={} request to uri={}", request.getMethod(),
                LogUtils.maskUri(request.getURI(), LOGGER, Level.DEBUG));
            executorService.shutdownNow();

            return this.httpClient.execute(request,
                new RestResponseHandler(request.getMethod(), request.getURI(), abortFuture));
        }
        catch (final InterruptedIOException ioe)
        {
            if (request.isAborted())
            {
                LOGGER.warn("Failed to perform httpMethod={} to uri={}; timed out, timeout={} ms",
                    request.getMethod(), LogUtils.maskUri(request.getURI(), LOGGER, Level.WARN),
                    this.timeout, ioe);

                throw new RequestFailedException(request.getMethod(), request.getURI(),
                    new TimeoutException(String.format("HTTP %s request was aborted after %s ms",
                        request.getMethod(), this.timeout)));
            }
            else
            {
                LOGGER.warn("Failed to perform httpMethod={} to uri={}; interrupted IO",
                    request.getMethod(), LogUtils.maskUri(request.getURI(), LOGGER, Level.WARN),
                    ioe);

                throw new RequestFailedException(request.getMethod(), request.getURI(), ioe);
            }
        }
        catch (final Exception e)
        {
            LOGGER.warn("Failed to perform httpMethod={} to uri={}", request.getMethod(),
                LogUtils.maskUri(request.getURI(), LOGGER, Level.WARN), e);
            throw new RequestFailedException(request.getMethod(), request.getURI(), e);
        }
    }

    static class RestResponseHandler implements ResponseHandler<RestResponse>
    {
        private final String method;
        private final URI uri;
        private final Future<?> abortFuture;

        RestResponseHandler(final String method, final URI uri, final Future<?> abortFuture)
        {
            this.method = method;
            this.uri = uri;
            this.abortFuture = abortFuture;
        }

        @Override
        public RestResponse handleResponse(final HttpResponse httpResponse) throws IOException
        {
            LOGGER.debug("Received response statusCode={} for httpMethod={} request to uri={}",
                httpResponse.getStatusLine().getStatusCode(), this.method,
                LogUtils.maskUri(this.uri, LOGGER, Level.DEBUG));

            this.abortFuture.cancel(false);

            final KeyValuePair.ListBuilder headersBuilder = new KeyValuePair.ListBuilder();

            for (final Header header : httpResponse.getAllHeaders())
            {
                headersBuilder.add(header.getName(), header.getValue());
            }

            return new RestResponse.Builder()
                .withMethod(this.method)
                .withUri(this.uri)
                .withStatusCode(httpResponse.getStatusLine().getStatusCode())
                .withHeaders(headersBuilder.build())
                .withContent(EntityUtils.toString(httpResponse.getEntity()))
                .build();
        }
    }


    public static final class Builder implements IBuilder<RestClient>
    {
        private IJsonService jsonService;
        private HttpClient httpClient;
        private long timeout = DefaultOptions.TIMEOUT_MS;
        private long waitTime = DefaultOptions.WAIT_TIME;

        public Builder withJsonService(final IJsonService val)
        {
            this.jsonService = val;
            return this;
        }

        public Builder withHttpClient(final HttpClient val)
        {
            this.httpClient = val;
            return this;
        }

        public Builder withTimeout(final long duration, final TimeUnit unit)
        {
            this.timeout = unit.toMillis(duration);
            return this;
        }

        public Builder withWaitTime(final long waitTime)
        {
            this.waitTime = waitTime;
            return this;
        }

        @Override
        public RestClient build()
        {
            ObjectUtils.requireNonNull(this.jsonService, "jsonService");
            ObjectUtils.requireNonNull(this.httpClient, "httpClient");

            return new RestClient(this);
        }
    }
}
