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
package com.gsma.mobileconnect.r2.service.discovery;

import com.google.gson.annotations.SerializedName;
import com.gsma.mobileconnect.r2.model.constants.LinkRels;
import com.gsma.mobileconnect.r2.model.json.DiscoveryResponseData;
import com.gsma.mobileconnect.r2.model.json.JsonRequired;
import com.gsma.mobileconnect.r2.model.json.Link;
import com.gsma.mobileconnect.r2.utils.*;

import java.util.ArrayList;
import java.util.List;

import static com.gsma.mobileconnect.r2.utils.ObjectUtils.defaultIfNull;

/**
 * Object to hold the operator specific urls returned from a successful discovery process call.
 *
 * @since 2.0
 */
public class OperatorUrls
{
    @SerializedName(value = "authorization_url", alternate = {"authorizationUrl", "authorization_endpoint"})
    @JsonRequired private String authorizationUrl;
    @SerializedName(value = "request_token_url", alternate = {"requestTokenUrl", "token_endpoint"})
    @JsonRequired private String requestTokenUrl;
    @SerializedName(value = "userinfo_url", alternate = {"userinfo_endpoint", "userInfoUrl"})
    private String userInfoUrl;
    @SerializedName(value = "premiuminfo_uri", alternate = {"premiumInfoUri", "premiuminfo_endpoint"})
    private String premiumInfoUri;
    @SerializedName(value = "jwks_uri")
    private String jwksUri;
    @SerializedName(value = "revoke_token_url", alternate = {"revokeTokenUrl", "revocation_endpoint"})
    private String revokeTokenUrl;
    @SerializedName(value = "refresh_token_url", alternate = {"refreshTokenUrl"})
    private String refreshTokenUrl;
    @SerializedName(value = "scope_url", alternate = {"scopeUrl"})
    private String scopeUrl;
    @SerializedName(value = "provider_metadata_uri", alternate = {"providerMetadataUri"})
    private String providerMetadataUri;

    private OperatorUrls(Builder builder)
    {
        this.authorizationUrl = builder.authorizationUrl;
        this.requestTokenUrl = builder.requestTokenUrl;
        this.userInfoUrl = builder.userInfoUrl;
        this.premiumInfoUri = builder.premiumInfoUri;
        this.jwksUri = builder.jwksUri;
        this.revokeTokenUrl = builder.revokeTokenUrl;
        this.refreshTokenUrl = builder.refreshTokenUrl;
        this.scopeUrl = builder.scopeUrl;
        this.providerMetadataUri = builder.providerMetadataUri;
    }

    public List<String> getOperatorsUrls() {
        List<String> urls = new ArrayList<String>();
        urls.add(getAuthorizationUrl());
        urls.add(getRequestTokenUrl());
        urls.add(getUserInfoUrl());
        urls.add(getPremiumInfoUri());
        urls.add(getJwksUri());
        urls.add(getRevokeTokenUrl());
        urls.add(getRefreshTokenUrl());
        urls.add(getScopeUrl());
        urls.add(getProviderMetadataUri());

        return urls;
    }

    public List<String> getOperatorsRel() {
        List<String> rel = new ArrayList<String>();
        rel.add(LinkRels.AUTHORIZATION);
        rel.add(LinkRels.TOKEN);
        rel.add(LinkRels.USERINFO);
        rel.add(LinkRels.PREMIUMINFO);
        rel.add(LinkRels.JWKS);
        rel.add(LinkRels.TOKENREVOKE);
        rel.add(LinkRels.TOKENREFRESH);
        rel.add(LinkRels.SCOPE);
        rel.add(LinkRels.OPENID_CONFIGURATION);
        rel.add(LinkRels.ISSUER);

        return rel;
    }
    /**
     * Create instance of operator urls extracting each from the list of links supplied within the
     * response data.
     *
     * @param responseData the response data containing the links.
     */
    public static OperatorUrls fromDiscoveryResponse(final DiscoveryResponseData responseData)
    {
        ObjectUtils.requireNonNull(responseData, "responseData");

        final Builder builder = new Builder();

        final List<Link> links = responseData.getLinks();

        if (links != null)
        {
            String providerMetadataUrl = getUrl(LinkRels.OPENID_CONFIGURATION, links);
            if (StringUtils.isNullOrEmpty(providerMetadataUrl)) {
                providerMetadataUrl = StringUtils.concatenateURL(getUrl(LinkRels.ISSUER, links), LinkRels.PROVIDER_METADATA_POSTFIX);
            }
            builder
                    .withAuthorizationUrl(getUrl(LinkRels.AUTHORIZATION, links))
                    .withRequestTokenUrl(getUrl(LinkRels.TOKEN, links))
                    .withUserInfoUrl(getUrl(LinkRels.USERINFO, links))
                    .withPremiumInfoUri(getUrl(LinkRels.PREMIUMINFO, links))
                    .withJwksUri(getUrl(LinkRels.JWKS, links))
                    .withRefershTokenUrl(getUrl(LinkRels.TOKENREFRESH, links))
                    .withRevokeTokenUrl(getUrl(LinkRels.TOKENREVOKE, links))
                    .withScopeUri(getUrl(LinkRels.SCOPE, links))
                    .withProviderMetadataUri(providerMetadataUrl);

        }

        return builder.build();
    }

    private static String getUrl(final String rel, final List<Link> links)
    {
        ObjectUtils.requireNonNull(rel, "rel");
        ObjectUtils.requireNonNull(links, "links");

        String retval = null;

        final Link link = ListUtils.firstMatch(links, new Predicate<Link>()
        {
            @Override
            public boolean apply(final Link input)
            {
                return rel.equalsIgnoreCase(input.getRel());
            }
        });

        if (link != null)
        {
            retval = link.getHref();
        }

        return retval;
    }

    /**
     * @return Url for authorization call.
     */
    public String getAuthorizationUrl()
    {
        return this.authorizationUrl;
    }

    /**
     * @return Url for token request call.
     */
    public String getRequestTokenUrl()
    {
        return this.requestTokenUrl;
    }

    /**
     * @return Url for user info call.
     */
    public String getUserInfoUrl()
    {
        return this.userInfoUrl;
    }

    /**
     * @return Url for identity services call.
     */
    public String getPremiumInfoUri()
    {
        return this.premiumInfoUri;
    }

    /**
     * @return Url for JWKS info.
     */
    public String getJwksUri()
    {
        return this.jwksUri;
    }

    /**
     * @return Url for token revoke call.
     */
    public String getRevokeTokenUrl()
    {
        return revokeTokenUrl;
    }

    /**
     * @return Url for token refresh call.
     */
    public String getRefreshTokenUrl()
    {
        return refreshTokenUrl;
    }

    /**
     * @return Url for Provider Metadata.
     */
    public String getProviderMetadataUri()
    {
        return this.providerMetadataUri;
    }

    /**
     * @return Url for scopes call
     */
    public String getScopeUrl() {return  this.scopeUrl;}
    /**
     * Replaces URLs from the discovery response with URLs from the provider metadata.
     * This allows providers to use temporary urls while the main url is down for maintenance.
     *
     * @param metadata metatdata to get overriding urls from.
     */
    protected void override(final ProviderMetadata metadata)
    {
        if (metadata != null)
        {
            this.authorizationUrl =
                    defaultIfNull(metadata.getAuthorizationEndpoint(), this.authorizationUrl);
            this.requestTokenUrl = defaultIfNull(metadata.getTokenEndpoint(), this.requestTokenUrl);
            this.userInfoUrl = defaultIfNull(metadata.getUserinfoEndpoint(), this.userInfoUrl);
            this.premiumInfoUri =
                    defaultIfNull(metadata.getPremiuminfoEndpoint(), this.premiumInfoUri);
            this.jwksUri = defaultIfNull(metadata.getJwksUri(), this.jwksUri);
            this.revokeTokenUrl =
                    defaultIfNull(metadata.getRevocationEndpoint(), this.revokeTokenUrl);
            this.refreshTokenUrl =
                    defaultIfNull(metadata.getRefreshEndpoint(), this.refreshTokenUrl);
        }
    }

    public static final class Builder implements IBuilder<OperatorUrls>
    {
        private String authorizationUrl = null;
        private String requestTokenUrl = null;
        private String userInfoUrl = null;
        private String premiumInfoUri = null;
        private String jwksUri = null;
        private String revokeTokenUrl = null;
        private String refreshTokenUrl = null;
        private String scopeUrl = null;
        private String providerMetadataUri = null;

        public Builder withAuthorizationUrl(final String val)
        {
            this.authorizationUrl = val;
            return this;
        }

        public Builder withRequestTokenUrl(final String val)
        {
            this.requestTokenUrl = val;
            return this;
        }

        public Builder withUserInfoUrl(final String val)
        {
            this.userInfoUrl = val;
            return this;
        }

        public Builder withPremiumInfoUri(final String val)
        {
            this.premiumInfoUri = val;
            return this;
        }

        public Builder withJwksUri(final String val)
        {
            this.jwksUri = val;
            return this;
        }

        public Builder withRevokeTokenUrl(final String val)
        {
            this.revokeTokenUrl = val;
            return this;
        }

        public Builder withRefershTokenUrl(final String val)
        {
            this.refreshTokenUrl = val;
            return this;
        }

        public Builder withProviderMetadataUri(final String val)
        {
            this.providerMetadataUri = val;
            return this;
        }

        public Builder withScopeUri(final String val)
        {
            this.scopeUrl = val;
            return this;
        }
        @Override
        public OperatorUrls build()
        {
            return new OperatorUrls(this);
        }
    }
}
