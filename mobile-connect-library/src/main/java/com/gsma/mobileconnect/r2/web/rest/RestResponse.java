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

import com.gsma.mobileconnect.r2.utils.IBuilder;
import com.gsma.mobileconnect.r2.utils.KeyValuePair;
import com.gsma.mobileconnect.r2.utils.ListUtils;

import java.net.URI;
import java.util.List;

/**
 * Simple response object to represent a http response.
 *
 * @since 2.0
 */
public class RestResponse
{
    private final String method;
    private final URI uri;
    private final int statusCode;
    private final List<KeyValuePair> headers;
    private final String content;

    private RestResponse(final Builder builder)
    {
        this.method = builder.method;
        this.uri = builder.uri;
        this.statusCode = builder.statusCode;
        this.headers = builder.headers;
        this.content = builder.content;
    }

    /**
     * @return Status code returned by Http response.
     */
    public int getStatusCode()
    {
        return this.statusCode;
    }

    /**
     * @return Headers set on the http response.
     */
    public List<KeyValuePair> getHeaders()
    {
        return this.headers;
    }

    /**
     * @return HTTP method used to fetch this response.
     */
    public String getMethod()
    {
        return this.method;
    }

    /**
     * @return uri from which this response was fetched.
     */
    public URI getUri()
    {
        return this.uri;
    }

    /**
     * @return Content returned by the http response.
     */
    public String getContent()
    {
        return this.content;
    }


    public static final class Builder implements IBuilder<RestResponse>
    {
        private String method;
        private URI uri;
        private int statusCode;
        private List<KeyValuePair> headers;
        private String content;

        public Builder withMethod(final String method)
        {
            this.method = method;
            return this;
        }

        public Builder withUri(final URI val)
        {
            this.uri = val;
            return this;
        }

        public Builder withStatusCode(final int val)
        {
            this.statusCode = val;
            return this;
        }

        public Builder withHeaders(List<KeyValuePair> val)
        {
            this.headers = ListUtils.immutableList(val);
            return this;
        }

        public Builder withContent(String val)
        {
            this.content = val;
            return this;
        }

        @Override
        public RestResponse build()
        {
            return new RestResponse(this);
        }
    }
}
