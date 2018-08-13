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
package com.gsma.mobileconnect.r2.json;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gsma.mobileconnect.r2.utils.IBuilder;

/**
 * Object for deserialization of Discovery Response content.
 *
 * @since 2.0
 */
@JsonDeserialize(builder = Response.Builder.class)
public class Response
{
    private final String servingOperator;
    private final String country;
    private final String currency;
    private final Apis apis;
    private final String clientId;
    private final String clientSecret;
    private final String clientName;

    private Response(Builder builder)
    {
        this.servingOperator = builder.servingOperator;
        this.country = builder.country;
        this.currency = builder.currency;
        this.apis = builder.apis;
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret;
        this.clientName = builder.clientName;
    }

    public String getServingOperator()
    {
        return this.servingOperator;
    }

    public String getCountry()
    {
        return this.country;
    }

    public String getCurrency()
    {
        return this.currency;
    }

    public Apis getApis()
    {
        return this.apis;
    }

    public String getClientId()
    {
        return this.clientId;
    }

    public String getClientSecret()
    {
        return this.clientSecret;
    }

    public String getClientName()
    {
        return clientName;
    }

    public static final class Builder implements IBuilder<Response>
    {
        private String servingOperator = null;
        private String country = null;
        private String currency = null;
        private Apis apis = null;
        private String clientId = null;
        private String clientSecret = null;
        private String subscriberId = null;
        private String clientName = null;

        public Builder withServingOperator(final String val)
        {
            this.servingOperator = val;
            return this;
        }

        public Builder withCountry(final String val)
        {
            this.country = val;
            return this;
        }

        public Builder withCurrency(final String val)
        {
            this.currency = val;
            return this;
        }

        public Builder withApis(Apis val)
        {
            this.apis = val;
            return this;
        }

        public Builder withClientId(final String val)
        {
            this.clientId = val;
            return this;
        }

        public Builder withClientSecret(final String val)
        {
            this.clientSecret = val;
            return this;
        }

        public Builder withSubscriberId(final String val)
        {
            this.subscriberId = val;
            return this;
        }

        public Builder withClientName(final String val)
        {
            this.clientName = val;
            return this;
        }

        @Override
        public Response build()
        {
            return new Response(this);
        }
    }
}
