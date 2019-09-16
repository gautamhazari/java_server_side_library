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

import com.google.gson.annotations.SerializedName;
import com.gsma.mobileconnect.r2.utils.IBuilder;
import com.gsma.mobileconnect.r2.utils.ListUtils;

import java.util.List;

/**
 * Object for deserialization of Discovery Response content.
 *
 * @since 2.0
 */
public class DiscoveryResponseData
{
    private final long ttl;
    private final String error;
    private final String description;
    private final List<Link> links;
    private final Response response;
    @SerializedName("subscriber_id")
    private String subscriberId;
    @SerializedName("subscriber_id_token")
    private String subscriberIdToken;
    @SerializedName("client_name")
    private String clientName;
    @SerializedName("correlation_id")
    private String correlationId;

    private DiscoveryResponseData(final Builder builder)
    {
        this.ttl = builder.ttl;
        this.subscriberId = builder.subscriberId;
        this.error = builder.error;
        this.description = builder.description;
        builder.setLinks();
        this.links = builder.links;
        this.response = builder.response;
        this.clientName = builder.clientName;
        this.correlationId = builder.correlationId;
        this.subscriberIdToken = builder.subscriberIdToken;
    }

    public long getTtl()
    {
        return this.ttl;
    }

    public String getSubscriberId()
    {
        return this.subscriberId;
    }

    public String getSubscriberIdToken()
    {
        return this.subscriberIdToken;
    }

    public void clearSubscriberId()
    {
        this.subscriberId = null;
    }

    public String getError()
    {
        return this.error;
    }

    public String getDescription()
    {
        return this.description;
    }

    public List<Link> getLinks()
    {
        return this.links;
    }

    public Response getResponse()
    {
        return this.response;
    }

    public String getClientName()
    {
        return clientName;
    }

    public String getCorrelationId () {
        return correlationId;
    }

    public static final class Builder implements IBuilder<DiscoveryResponseData>
    {
        private long ttl = 0L;
        private String subscriberId = null;
        private String error = null;
        private String description = null;
        private List<Link> links = null;
        private Response response = null;
        private String clientName = null;
        private String correlationId = null;
        private String subscriberIdToken = null;

        public Builder()
        {
            // default constructor
        }

        public Builder(final DiscoveryResponseData responseData)
        {
            if (responseData != null)
            {
                this.ttl = responseData.ttl;
                this.subscriberId = responseData.subscriberId;
                this.error = responseData.error;
                this.description = responseData.description;
                this.links = responseData.links;
                this.response = responseData.response;
                this.clientName = responseData.clientName;
                this.correlationId = responseData.correlationId;
                this.subscriberIdToken = responseData.subscriberIdToken;
            }
        }

        public Builder withTtl(final long val)
        {
            this.ttl = val;
            return this;
        }

        public Builder withSubscriberId(final String val)
        {
            this.subscriberId = val;
            return this;
        }

        public Builder withSubscriberIdToken(final String val)
        {
            this.subscriberIdToken = val;
            return this;
        }

        public Builder withError(final String val)
        {
            this.error = val;
            return this;
        }

        public Builder withDescription(final String val)
        {
            this.description = val;
            return this;
        }

        public Builder withLinks(final List<Link> val)
        {
            this.links = ListUtils.immutableList(val);
            return this;
        }

        public Builder withResponse(final Response val)
        {
            this.response = val;
            setLinks();
            return this;
        }

        public Builder withClientName(final String val)
        {
            this.clientName = val;
            return this;
        }

        public Builder withCorrelationId(final String val)
        {
            this.correlationId = val;
            return this;
        }

        @Override
        public DiscoveryResponseData build()
        {
            setLinks();

            if (this.clientName == null && this.response != null)
            {
                this.clientName = this.response.getClientName();
            }
            return new DiscoveryResponseData(this);
        }

        private void setLinks() {
            if (this.links == null && this.response != null)
            {
                final Apis apis = this.response.getApis();
                if (apis != null)
                {
                    final Operatorid operatorId = apis.getOperatorid();
                    if (operatorId != null)
                    {
                        this.links = operatorId.getLink();
                    }
                }
            }
        }
    }
}
