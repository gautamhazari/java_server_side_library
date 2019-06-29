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
package com.gsma.mobileconnect.r2.discovery;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.SerializedName;
import com.gsma.mobileconnect.r2.ErrorResponse;
import com.gsma.mobileconnect.r2.cache.AbstractCacheable;
import com.gsma.mobileconnect.r2.constants.DefaultOptions;
import com.gsma.mobileconnect.r2.constants.LinkRels;
import com.gsma.mobileconnect.r2.constants.Parameters;
import com.gsma.mobileconnect.r2.exceptions.ProviderMetadataUnavailableException;
import com.gsma.mobileconnect.r2.json.DiscoveryResponseData;
import com.gsma.mobileconnect.r2.json.IJsonService;
import com.gsma.mobileconnect.r2.json.JsonDeserializationException;
import com.gsma.mobileconnect.r2.json.Link;
import com.gsma.mobileconnect.r2.rest.RestResponse;
import com.gsma.mobileconnect.r2.utils.*;

import java.util.*;

/**
 * Class to hold a discovery response. This potentially holds cached data as indicated by the cached
 * property.
 *
 * @see IDiscoveryService
 * @since 2.0
 */
public class DiscoveryResponse extends AbstractCacheable
{
    @SerializedName(Parameters.TTL)
    private final Date ttl;
    @SerializedName(Parameters.RESPONSE_CODE)
    private final int responseCode;
    private final List<KeyValuePair> headers;
    @SerializedName(Parameters.ERROR_RESPONSE)
    private final ErrorResponse errorResponse;
    @SerializedName(Parameters.RESPONSE_DATA)
    private final DiscoveryResponseData responseData;
    @SerializedName(Parameters.OPERATOR_URLS)
    private final OperatorUrls operatorUrls;
    @SerializedName(Parameters.CLIENT_NAME)
    private final String clientName;
    @SerializedName(Parameters.PROVIDER_METADATA)
    private ProviderMetadata providerMetadata;


    private DiscoveryResponse(Builder builder)
    {
        this.ttl = builder.ttl;
        this.responseCode = builder.responseCode;
        this.headers = builder.headers;
        this.errorResponse = builder.errorResponse;
        this.responseData = builder.responseData;
        this.providerMetadata = builder.providerMetadata;
        this.operatorUrls = builder.operatorUrls;
        this.clientName = builder.clientName;

        if (this.operatorUrls != null && this.providerMetadata != null)
        {
            this.operatorUrls.override(this.providerMetadata);
        }
    }

    /**
     * Convenience method that builds a {@link DiscoveryResponse} from a {@link RestResponse}.
     *
     * @param restResponse containing json.
     * @return DiscoveryResponse instance.
     * @throws JsonDeserializationException if the json is invalid.
     */
    public static DiscoveryResponse fromRestResponse(final RestResponse restResponse,
        final IJsonService jsonService) throws JsonDeserializationException
    {
        ObjectUtils.requireNonNull(restResponse, "restResponse");
        ObjectUtils.requireNonNull(jsonService, "jsonService");

        final DiscoveryResponseData responseData = new DiscoveryResponseData.Builder(jsonService.deserialize(restResponse.getContent(), DiscoveryResponseData.class)).build();

        return new Builder()
            .withResponseCode(restResponse.getStatusCode())
            .withHeaders(restResponse.getHeaders())
            .withTtl(calculateTtl(responseData.getTtl()))
            .withResponseData(responseData)
            .build();
    }

    /**
     * Adjusts the ttl based to fit within the minimum and maximum times allowed.
     *
     * @param responseTtl to adjust.
     * @return the adjusted ttl.
     */
    protected static Date calculateTtl(final Long responseTtl)
    {
        final long retval;

        final long now = System.currentTimeMillis();
        final long min = now + DefaultOptions.MIN_TTL_MS;

        if (responseTtl == null)
        {
            retval = min;
        }
        else
        {
            final long max = now + DefaultOptions.MAX_TTL_MS;
            final long ttl = now + responseTtl;

            retval = Math.min(Math.max(min, ttl), max);
        }
        return new Date(retval);
    }

    public Date getTtl()
    {
        return this.ttl;
    }

    public int getResponseCode()
    {
        return this.responseCode;
    }

    public List<KeyValuePair> getHeaders()
    {
        return this.headers;
    }

    public ErrorResponse getErrorResponse()
    {
        return this.errorResponse;
    }

    public DiscoveryResponseData getResponseData()
    {
        return this.responseData;
    }

    public OperatorUrls getOperatorUrls()
    {
        return this.operatorUrls;
    }

    public ProviderMetadata getProviderMetadata()
    {
        return this.providerMetadata;
    }

    public void setProviderMetadata(ProviderMetadata providerMetadata)
    {
        this.providerMetadata = providerMetadata;
        this.operatorUrls.override(providerMetadata);
    }

    public String getClientName()
    {
        return this.clientName;
    }

    /**
     * Create a copy of this DiscoveryResponse with the subscriberId set to this provided value.
     *
     * @param subscriberId to overwrite.
     * @return copy of this response with overwritten subscriber id.
     */
    public DiscoveryResponse withSubscriberId(final String subscriberId)
    {
        return new Builder(this)
            .withResponseData(new DiscoveryResponseData.Builder(this.responseData)
                .withSubscriberId(subscriberId)
                .build())
            .build();
    }

    /**
     * Check to see if provided scopes are supported by the operator linked to the discovery
     * response.
     *
     * @param scope A space or comma delimited string of required scope values, if empty or null
     *              true will be returned
     * @return True if all scope values requested are supported by the operator, false otherwise
     * @throws ProviderMetadataUnavailableException if provider metadata is unavailable or does not
     *                                              specify supported scopes.
     */
    public boolean isMobileConnectServiceSupported(final String scope)
        throws ProviderMetadataUnavailableException
    {
        boolean retval = true;

        if (!StringUtils.isNullOrEmpty(scope))
        {
            if (this.providerMetadata == null)
            {
                throw new ProviderMetadataUnavailableException("No provider metadata");
            }

            final List<String> scopesSupported = this.providerMetadata.getScopesSupported();
            if (scopesSupported == null || scopesSupported.isEmpty())
            {
                throw new ProviderMetadataUnavailableException(
                    "Provider metadata does not specify supported scopes");
            }
            else
            {
                final List<String> scopes = Arrays.asList(scope.toLowerCase().split("\\s|,"));
                final Set<String> scopesSupportedLc = new HashSet<String>(scopes.size());

                for (final String s : scopesSupported)
                {
                    scopesSupportedLc.add(s.toLowerCase());
                }

                retval = scopesSupportedLc.containsAll(scopes);
            }
        }

        return retval;
    }

    @Override
    protected void cached()
    {
//        this.responseData.clearSubscriberId();
    }

    public static final class Builder implements IBuilder<DiscoveryResponse>
    {
        private Date ttl = null;
        private int responseCode;
        private List<KeyValuePair> headers = null;
        private ErrorResponse errorResponse = null;
        private DiscoveryResponseData responseData = null;
        private ProviderMetadata providerMetadata = null;
        private OperatorUrls operatorUrls = null;
        private String clientName = null;

        public Builder()
        {
            // default constructor
        }

        public Builder(final DiscoveryResponse response)
        {
            if (response != null)
            {
                this.ttl = response.ttl;
                this.responseCode = response.responseCode;
                this.headers = response.headers;
                this.errorResponse = response.errorResponse;
                this.responseData = response.responseData;
                this.providerMetadata = response.providerMetadata;
                this.operatorUrls = response.operatorUrls;
                this.clientName = response.clientName;
            }
        }

        public Builder withTtl(final Date val)
        {
            this.ttl = val;
            return this;
        }

        public Builder withResponseCode(final int val)
        {
            this.responseCode = val;
            return this;
        }

        public Builder withErrorResponse(final ErrorResponse val)
        {
            this.errorResponse = val;
            return this;
        }

        @JsonIgnore
        public Builder withHeaders(final List<KeyValuePair> val)
        {
            this.headers = val;
            return this;
        }

        public Builder withResponseData(final DiscoveryResponseData val)
        {
            this.responseData = val;
            return this;
        }

        public Builder withProviderMetadata(final ProviderMetadata val)
        {
            this.providerMetadata = val;
            return this;
        }

        @Override
        public DiscoveryResponse build()
        {
            ObjectUtils.requireNonNull(this.responseData, "responseData");

            if (this.ttl != null)
            {
                this.ttl = calculateTtl(this.ttl.getTime());
            }

            this.clientName = this.responseData.getClientName();
            this.operatorUrls = OperatorUrls.fromDiscoveryResponse(this.responseData);

            if (this.responseData.getResponse() != null)
            {
                final List<Link> links = this.responseData.getLinks();
                if (this.clientName == null && links != null)
                {
                    final Link appShortName = ListUtils.firstMatch(links, new Predicate<Link>()
                    {
                        @Override
                        public boolean apply(final Link input)
                        {
                            return LinkRels.APPLICATION_SHORT_NAME.equalsIgnoreCase(input.getRel());
                        }
                    });

                    if (appShortName != null)
                    {
                        this.clientName = appShortName.getHref();
                    }
                }
            }

            if (!StringUtils.isNullOrEmpty(this.responseData.getError()))
            {
                this.errorResponse = new ErrorResponse.Builder()
                        .withError(this.responseData.getError())
                        .withErrorDescription(this.responseData.getDescription())
                        .withCorrelationId(this.responseData.getCorrelationId())
                        .build();
            }
            return new DiscoveryResponse(this);
        }

    }


}
