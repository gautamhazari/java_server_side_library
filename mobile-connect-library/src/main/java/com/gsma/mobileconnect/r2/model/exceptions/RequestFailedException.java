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
package com.gsma.mobileconnect.r2.model.exceptions;

import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.utils.HttpUtils;

import java.net.URI;

/**
 * Exception thrown when there was a failure issuing a request to an endpoint.
 *
 * @since 2.0
 */
public class RequestFailedException extends AbstractMobileConnectException
{
    private final String method;
    private final URI uri;

    /**
     * Create an instance of this exception.
     *
     * @param method HTTP method of the request.
     * @param uri    URI being accessed.
     * @param cause  the underlying exception.
     */
    public RequestFailedException(final String method, final URI uri, final Throwable cause)
    {
        super(String.format("Failed to issue HTTP %s to %s", method, uri), cause);

        this.method = method;
        this.uri = uri;
    }

    /**
     * Create an instance of this exception.
     *
     * @param method HTTP method of the request.
     * @param uri    URI being accessed.
     * @param cause  the underlying exception.
     */
    public RequestFailedException(final HttpUtils.HttpMethod method, final URI uri,
        final Throwable cause)
    {
        this(method.name(), uri, cause);
    }

    /**
     * @return the HTTP method that was being issued.
     */
    public String getMethod()
    {
        return this.method;
    }

    /**
     * @return the URI that was being called.
     */
    public URI getUri()
    {
        return this.uri;
    }

    @Override
    public MobileConnectStatus toMobileConnectStatus(final String task)
    {
        return MobileConnectStatus.error("http_failure",
            String.format("An HTTP failure occurred while performing fetch for '%s'", task), this);
    }
}
