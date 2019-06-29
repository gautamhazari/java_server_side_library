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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.annotations.SerializedName;
import com.gsma.mobileconnect.r2.cache.AbstractCacheable;
import com.gsma.mobileconnect.r2.utils.IBuilder;
import com.gsma.mobileconnect.r2.utils.ListUtils;

import java.util.List;

/**
 * Parsed Provider Metadata returned from openid-configuration url
 *
 * @since 2.0
 */
@JsonDeserialize(builder = ProviderMetadata.Builder.class)
public class ProviderMetadata extends AbstractCacheable
{
    private final String version;
    private final String subscriberId;
    @SerializedName("issuer")
    private final String issuer;
    @SerializedName("authorization_endpoint")
    private final String authorizationEndpoint;
    @SerializedName("token_endpoint")
    private final String tokenEndpoint;
    @SerializedName("userinfo_endpoint")
    private final String userinfoEndpoint;
    @SerializedName("premiuminfo_endpoint")
    private final String premiuminfoEndpoint;
    private final String checkSessionIframe;
    private final String endSessionEndpoint;
    @SerializedName("revocation_endpoint")
    private final String revocationEndpoint;
    private final String refreshEndpoint;
    private final String registrationEndpoint;
    @SerializedName("jwks_uri")
    private final String jwksUri;
    @SerializedName("scopes_supported")
    private final List<String> scopesSupported;
    @SerializedName("response_types_supported")
    private final List<String> responseTypesSupported;
    @SerializedName("response_modes_supported")
    private final List<String> responseModesSupported;
    @SerializedName("grant_types_supported")
    private final List<String> grantTypesSupported;
    @SerializedName("acr_values_supported")
    private final List<String> acrValuesSupported;
    private final List<String> subjectTypesSupported;
    private final List<String> userinfoSigningAlgValuesSupported;
    private final List<String> userinfoEncryptionAlgValuesSupported;
    private final List<String> userinfoEncryptionEncValuesSupported;
    @SerializedName("id_token_signing_alg_values_supported")
    private final List<String> idTokenSigningAlgValuesSupported;
    private final List<String> idTokenEncryptionAlgValuesSupported;
    private final List<String> idTokenEncryptionEncValuesSupported;
    @SerializedName("request_object_signing_alg_values_supported")
    private final List<String> requestObjectSigningAlgValuesSupported;
    private final List<String> requestObjectEncryptionAlgValuesSupported;
    private final List<String> requestObjectEncryptionEncValuesSupported;
    private final List<String> tokenEndpointAuthMethodsSupported;
    private final List<String> tokenEndpointAuthSigningAlgValuesSupported;
    private final List<String> displayValuesSupported;
    private final List<String> claimTypesSupported;
    @SerializedName("claims_supported")
    private final List<String> claimsSupported;
    @SerializedName("mc_di_scopes_supported")
    private final List<String> mcDiScopesSupported;
    @SerializedName("service_documentation")
    private final String serviceDocumentation;
    private final List<String> claimsLocalesSupported;
    @SerializedName("ui_locales_supported")
    private final List<String> uiLocalesSupported;
    @SerializedName("mc_version")
    private final List<String> mcVersion;
    private final Boolean requireRequestUriRegistration;
    @SerializedName("op_policy_uri")
    private final String operatorPolicyUri;
    @SerializedName("op_tos_uri")
    private final String operatorTermsOfServiceUri;
    @SerializedName(value = "claims_parameter_supported", alternate = "mc_claims_parameter_supported")
    private final Boolean claimsParameterSupported;
    @SerializedName("request_parameter_supported")
    private final Boolean requestParameterSupported;
    private final Boolean requestStringParameterSupported;
    private final List<String> loginHintMethodsSupported;

    private ProviderMetadata(Builder builder)
    {
        this.version = builder.version;
        this.issuer = builder.issuer;
        this.subscriberId = builder.subscriberId;
        this.authorizationEndpoint = builder.authorizationEndpoint;
        this.tokenEndpoint = builder.tokenEndpoint;
        this.userinfoEndpoint = builder.userinfoEndpoint;
        this.premiuminfoEndpoint = builder.premiuminfoEndpoint;
        this.checkSessionIframe = builder.checkSessionIframe;
        this.endSessionEndpoint = builder.endSessionEndpoint;
        this.revocationEndpoint = builder.revocationEndpoint;
        this.refreshEndpoint = builder.refreshEndpoint;
        this.registrationEndpoint = builder.registrationEndpoint;
        this.jwksUri = builder.jwksUri;
        this.scopesSupported = builder.scopesSupported;
        this.responseTypesSupported = builder.responseTypesSupported;
        this.responseModesSupported = builder.responseModesSupported;
        this.grantTypesSupported = builder.grantTypesSupported;
        this.acrValuesSupported = builder.acrValuesSupported;
        this.subjectTypesSupported = builder.subjectTypesSupported;
        this.userinfoSigningAlgValuesSupported = builder.userinfoSigningAlgValuesSupported;
        this.userinfoEncryptionAlgValuesSupported = builder.userinfoEncryptionAlgValuesSupported;
        this.userinfoEncryptionEncValuesSupported = builder.userinfoEncryptionEncValuesSupported;
        this.idTokenSigningAlgValuesSupported = builder.idTokenSigningAlgValuesSupported;
        this.idTokenEncryptionAlgValuesSupported = builder.idTokenEncryptionAlgValuesSupported;
        this.idTokenEncryptionEncValuesSupported = builder.idTokenEncryptionEncValuesSupported;
        this.requestObjectSigningAlgValuesSupported =
            builder.requestObjectSigningAlgValuesSupported;
        this.requestObjectEncryptionAlgValuesSupported =
            builder.requestObjectEncryptionAlgValuesSupported;
        this.requestObjectEncryptionEncValuesSupported =
            builder.requestObjectEncryptionEncValuesSupported;
        this.tokenEndpointAuthMethodsSupported = builder.tokenEndpointAuthMethodsSupported;
        this.tokenEndpointAuthSigningAlgValuesSupported =
            builder.tokenEndpointAuthSigningAlgValuesSupported;
        this.displayValuesSupported = builder.displayValuesSupported;
        this.claimTypesSupported = builder.claimTypesSupported;
        this.claimsSupported = builder.claimsSupported;
        this.mcDiScopesSupported = builder.mcDiScopesSupported;
        this.serviceDocumentation = builder.serviceDocumentation;
        this.claimsLocalesSupported = builder.claimsLocalesSupported;
        this.uiLocalesSupported = builder.uiLocalesSupported;
        this.mcVersion = builder.mcVersion;
        this.requireRequestUriRegistration = builder.requireRequestUriRegistration;
        this.operatorPolicyUri = builder.operatorPolicyUri;
        this.operatorTermsOfServiceUri = builder.operatorTermsOfServiceUri;
        this.claimsParameterSupported = builder.claimsParameterSupported;
        this.requestParameterSupported = builder.requestParameterSupported;
        this.requestStringParameterSupported = builder.requestUriParameterSupported;
        this.loginHintMethodsSupported = builder.loginHintMethodsSupported;
    }

    /**
     * @return The version of provider metadata
     */
    public String getVersion()
    {
        return this.version;
    }
    /**
     * @return The version of provider metadata
     */
    public List<String> getMCVersion()
    {
        return this.mcVersion;
    }

    /**
     * @return the subscriber id
     */
    public String getSubscriberId() {
        return subscriberId;
    }

    /**
     * @return The name of the issuer the provider metadata is related to. This value is used when
     * validating the returned ID Token.
     */
    public String getIssuer()
    {
        return this.issuer;
    }

    /**
     * @return Authorization endpoint to use if different from url returned by discovery
     */
    public String getAuthorizationEndpoint()
    {
        return this.authorizationEndpoint;
    }

    /**
     * @return Token endpoint to use if different from url returned by discovery
     */
    public String getTokenEndpoint()
    {
        return this.tokenEndpoint;
    }

    /**
     * @return UserInfo endpoint to use if different from url returned by discovery
     */
    public String getUserinfoEndpoint()
    {
        return this.userinfoEndpoint;
    }

    /**
     * @return PremiumInfo endpoint to use if different from url returned by discovery
     */
    public String getPremiuminfoEndpoint()
    {
        return this.premiuminfoEndpoint;
    }

    public String getCheckSessionIframe()
    {
        return this.checkSessionIframe;
    }

    public String getEndSessionEndpoint()
    {
        return this.endSessionEndpoint;
    }

    /**
     * @return Revoke Token endpoint to use if different from url returned by discovery
     */
    public String getRevocationEndpoint()
    {
        return this.revocationEndpoint;
    }

    /**
     * @return Refresh Token endpoint to use if different from url returned by discovery
     */
    public String getRefreshEndpoint()
    {
        return this.refreshEndpoint;
    }

    /**
     * @return Registration endpoint to use if different from url returned by discovery
     */
    public String getRegistrationEndpoint()
    {
        return this.registrationEndpoint;
    }

    /**
     * @return JWKS endpoint to use if different from url returned by discovery
     */
    public String getJwksUri()
    {
        return this.jwksUri;
    }

    /**
     * @return A list of OAuth 2.0 scope values that the issuer supports, these can be easily
     * queried using {@link DiscoveryResponse#isMobileConnectServiceSupported(String)}
     */
    public List<String> getScopesSupported()
    {
        return this.scopesSupported;
    }

    /**
     * @return Array containing OAuth 2.0 response_type values that the issuer supports
     */
    public List<String> getResponseTypesSupported()
    {
        return this.responseTypesSupported;
    }

    /**
     * @return Array containing OAuth 2.0 response_mode values that the issuer supports, as
     * specified in OAuth 2.0 Multiple Response Type Encoding Practices
     */
    public List<String> getResponseModesSupported()
    {
        return this.responseModesSupported;
    }

    /**
     * @return Array containing OAuth 2.0 grant_type values that the issuer supports
     */
    public List<String> getGrantTypesSupported()
    {
        return this.grantTypesSupported;
    }

    /**
     * @return Array containing Authentication Context Class References that the issuer supports
     */
    public List<String> getAcrValuesSupported()
    {
        return this.acrValuesSupported;
    }

    /**
     * @return Array containing a list of the Subject Identifier Types that the issuer supports
     */
    public List<String> getSubjectTypesSupported()
    {
        return this.subjectTypesSupported;
    }

    /**
     * @return Array containing the JWS signing algorithms [JWA] supported by the issuer for the
     * Userinfo Endpoint to encode the claims in a JWT
     */
    public List<String> getUserinfoSigningAlgValuesSupported()
    {
        return this.userinfoSigningAlgValuesSupported;
    }

    /**
     * @return Array containing a list of the JWE encryption algorithms (alg values) [JWA] supported
     * by the issuer for the UserInfo Endpoint to encode the claims in a JWT
     */
    public List<String> getUserinfoEncryptionAlgValuesSupported()
    {
        return this.userinfoEncryptionAlgValuesSupported;
    }

    /**
     * @return Array containing a list of the JWE encryption algorithms (enc values) [JWE] supported
     * by the issuer for the UserInfo Endpoint to encode the claims in a JWT
     */
    public List<String> getUserinfoEncryptionEncValuesSupported()
    {
        return this.userinfoEncryptionEncValuesSupported;
    }

    /**
     * @return Array containing the JWS signing algorithms [JWA] supported by the issuer for the ID
     * Token to encode the claims in a JWT
     */
    public List<String> getIdTokenSigningAlgValuesSupported()
    {
        return this.idTokenSigningAlgValuesSupported;
    }

    /**
     * @return Array containing a list of the JWE encryption algorithms (alg values) [JWA] supported
     * by the issuer for the ID Token to encode the claims in a JWT
     */
    public List<String> getIdTokenEncryptionAlgValuesSupported()
    {
        return this.idTokenEncryptionAlgValuesSupported;
    }

    /**
     * @return Array containing a list of the JWE encryption algorithms (enc values) [JWE] supported
     * by the issuer for the ID Token to encode the claims in a JWT
     */
    public List<String> getIdTokenEncryptionEncValuesSupported()
    {
        return this.idTokenEncryptionEncValuesSupported;
    }

    /**
     * @return Array containing the JWS signing algorithms [JWA] supported by the issuer for Request
     * ObjectUtils which are described in Section 6.1 of OpenID Connect Core 1.0
     */
    public List<String> getRequestObjectSigningAlgValuesSupported()
    {
        return this.requestObjectSigningAlgValuesSupported;
    }

    /**
     * @return Array containing the JWE encryption algorithms (alg values) [JWA] supported by the
     * issuer for Request ObjectUtils which are described in Section 6.1 of OpenID Connect Core 1.0
     */
    public List<String> getRequestObjectEncryptionAlgValuesSupported()
    {
        return this.requestObjectEncryptionAlgValuesSupported;
    }

    /**
     * @return Array containing the JWE encryption algorithms (enc values) [JWE] supported by the
     * issuer for Request ObjectUtils which are described in Section 6.1 of OpenID Connect Core 1.0
     */
    public List<String> getRequestObjectEncryptionEncValuesSupported()
    {
        return this.requestObjectEncryptionEncValuesSupported;
    }

    /**
     * @return Array containing the Client Authentication methods suppoorted by the Token Endpoint
     */
    public List<String> getTokenEndpointAuthMethodsSupported()
    {
        return this.tokenEndpointAuthMethodsSupported;
    }

    /**
     * @return Array containing the JWS signing algorithms (alg values) supported by the Token
     * Endpoint for the signature on the JWT used to authenticate the client at the Token Endpoint
     * for the private_key_jwt and client_secret_jwt authentication methods
     */
    public List<String> getTokenEndpointAuthSigningAlgValuesSupported()
    {
        return this.tokenEndpointAuthSigningAlgValuesSupported;
    }

    /**
     * @return Array containing the display parameter values that the issuer supports
     */
    public List<String> getDisplayValuesSupported()
    {
        return this.displayValuesSupported;
    }

    /**
     * @return Array containing the Claim Types that the issuer supports. These Claim Types are
     * described in Section 5.6 of OpenID Connect Core 1.0
     */
    public List<String> getClaimTypesSupported()
    {
        return this.claimTypesSupported;
    }

    /**
     * @return Array containing the Claim Names of the Claims that the issuer MAY be able to supply
     * values for. Note that for privacy or other reasons this may not be an exhaustive list
     */
    public List<String> getClaimsSupported()
    {
        return this.claimsSupported;
    }

    /**
     * @return Array containing the Claim Names of the Claims that the issuer MAY be able to supply
     * values for. Note that for privacy or other reasons this may not be an exhaustive list
     */
    public List<String> getMcDiScopesSupported()
    {
        return this.mcDiScopesSupported;
    }

    /**
     * @return URL of a page containing human readable information that developers might want or
     * need to know when using the issuing service
     */
    public String getServiceDocumentation()
    {
        return this.serviceDocumentation;
    }

    /**
     * @return Array containing languages and scripts supported for values in Claims being returned
     * as an array of BCP47 [RFC5646] language tag values.  Not all languages and scripts are
     * necessarily supported for all Claim values
     */
    public List<String> getClaimsLocalesSupported()
    {
        return this.claimsLocalesSupported;
    }

    /**
     * @return Array containing the languages and scripts supported for the user interface,
     * represented as an array of BCP47 [RFC5646] language tag values
     */
    public List<String> getUiLocalesSupported()
    {
        return this.uiLocalesSupported;
    }

    /**
     * @return Boolean value specifying whether the issuer requires any request_uri values used to
     * be pre-registered using the request_uris registration parameter. Pre-registration is required
     * when the value is true
     */
    public Boolean isRequireRequestUriRegistration()
    {
        return this.requireRequestUriRegistration;
    }

    /**
     * @return URL that the OpenID Provider provides to the person registering the Client to read
     * about the issuer requirements on how the Relying Party can use the data provided by the
     * issuer. The registration process SHOULD display this URL to the person registering the Client
     * if it is given
     */
    public String getOperatorPolicyUri()
    {
        return this.operatorPolicyUri;
    }

    /**
     * @return URL that the issuer provides to the person registering the Client to read about the
     * issuers terms of service. The registration process SHOULD display this URL to the person
     * registering the client if it is given
     */
    public String getOperatorTermsOfServiceUri()
    {
        return this.operatorTermsOfServiceUri;
    }

    /**
     * @return Boolean value specifying whether the issuer supports use of the claims parameter,
     * with true indicating support
     */
    public Boolean isClaimsParameterSupported()
    {
        return this.claimsParameterSupported;
    }

    /**
     * @return Boolean value specifying whether the issuer supports use of the request parameter,
     * with true indicating support
     */
    public Boolean isRequestParameterSupported()
    {
        return this.requestParameterSupported;
    }

    /**
     * @return Boolean value specifying whether the issuer supports use of the request uri
     * parameter, with true indicating support
     */
    public Boolean isRequestStringParameterSupported()
    {
        return this.requestStringParameterSupported;
    }

    /**
     * @return Array containing a list of the login hint methods supported by the issuer ID Gateway
     */
    public List<String> getLoginHintMethodsSupported()
    {
        return this.loginHintMethodsSupported;
    }

    public static final class Builder implements IBuilder<ProviderMetadata>
    {
        private String version;
        private String subscriberId;
        private String issuer;
        private String authorizationEndpoint;
        private String tokenEndpoint;
        private String userinfoEndpoint;
        private String premiuminfoEndpoint;
        private String checkSessionIframe;
        private String endSessionEndpoint;
        private String revocationEndpoint;
        private String refreshEndpoint;
        private String registrationEndpoint;
        private String jwksUri;
        private List<String> scopesSupported;
        private List<String> responseTypesSupported;
        private List<String> responseModesSupported;
        private List<String> grantTypesSupported;
        private List<String> acrValuesSupported;
        private List<String> subjectTypesSupported;
        private List<String> userinfoSigningAlgValuesSupported;
        private List<String> userinfoEncryptionAlgValuesSupported;
        private List<String> userinfoEncryptionEncValuesSupported;
        private List<String> idTokenSigningAlgValuesSupported;
        private List<String> idTokenEncryptionAlgValuesSupported;
        private List<String> idTokenEncryptionEncValuesSupported;
        private List<String> requestObjectSigningAlgValuesSupported;
        private List<String> requestObjectEncryptionAlgValuesSupported;
        private List<String> requestObjectEncryptionEncValuesSupported;
        private List<String> tokenEndpointAuthMethodsSupported;
        private List<String> tokenEndpointAuthSigningAlgValuesSupported;
        private List<String> displayValuesSupported;
        private List<String> claimTypesSupported;
        private List<String> claimsSupported;
        private List<String> mcDiScopesSupported;
        private String serviceDocumentation;
        private List<String> claimsLocalesSupported;
        private List<String> uiLocalesSupported;
        private List<String> mcVersion;
        private Boolean requireRequestUriRegistration;
        private String operatorPolicyUri;
        private String operatorTermsOfServiceUri;
        private Boolean claimsParameterSupported;
        private Boolean requestParameterSupported;
        private Boolean requestUriParameterSupported;
        private List<String> loginHintMethodsSupported;

        public Builder(ProviderMetadata providerMetadata) {
            //default constructor
        }

        public Builder() {

        }


        public Builder withVersion(final String val)
        {
            this.version = val;
            return this;
        }

        public Builder withSubscriberId(final String val) {
            this.subscriberId = val;
            return this;
        }

        public Builder withIssuer(final String val)
        {
            this.issuer = val;
            return this;
        }

        public Builder withAuthorizationEndpoint(final String val)
        {
            this.authorizationEndpoint = val;
            return this;
        }

        public Builder withTokenEndpoint(final String val)
        {
            this.tokenEndpoint = val;
            return this;
        }

        public Builder withUserinfoEndpoint(final String val)
        {
            this.userinfoEndpoint = val;
            return this;
        }

        public Builder withPremiuminfoEndpoint(final String val)
        {
            this.premiuminfoEndpoint = val;
            return this;
        }

        public Builder withCheckSessionIframe(final String val)
        {
            this.checkSessionIframe = val;
            return this;
        }

        public Builder withEndSessionEndpoint(final String val)
        {
            this.endSessionEndpoint = val;
            return this;
        }

        public Builder withRevocationEndpoint(final String val)
        {
            this.revocationEndpoint = val;
            return this;
        }

        public Builder withRefreshEndpoint(final String val)
        {
            this.refreshEndpoint = val;
            return this;
        }

        public Builder withRegistrationEndpoint(final String val)
        {
            this.registrationEndpoint = val;
            return this;
        }

        public Builder withJwksUri(final String val)
        {
            this.jwksUri = val;
            return this;
        }

        public Builder withScopesSupported(final List<String> val)
        {
            this.scopesSupported = ListUtils.immutableList(val);
            return this;
        }

        public Builder withResponseTypesSupported(final List<String> val)
        {
            this.responseTypesSupported = ListUtils.immutableList(val);
            return this;
        }

        public Builder withResponseModesSupported(final List<String> val)
        {
            this.responseModesSupported = ListUtils.immutableList(val);
            return this;
        }

        public Builder withGrantTypesSupported(final List<String> val)
        {
            this.grantTypesSupported = ListUtils.immutableList(val);
            return this;
        }

        public Builder withAcrValuesSupported(final List<String> val)
        {
            this.acrValuesSupported = ListUtils.immutableList(val);
            return this;
        }

        public Builder withSubjectTypesSupported(final List<String> val)
        {
            this.subjectTypesSupported = ListUtils.immutableList(val);
            return this;
        }

        public Builder withUserinfoSigningAlgValuesSupported(final List<String> val)
        {
            this.userinfoSigningAlgValuesSupported = ListUtils.immutableList(val);
            return this;
        }

        public Builder withUserinfoEncryptionAlgValuesSupported(final List<String> val)
        {
            this.userinfoEncryptionAlgValuesSupported = ListUtils.immutableList(val);
            return this;
        }

        public Builder withuserinfoEncryptionEncValuesSupported(final List<String> val)
        {
            this.userinfoEncryptionEncValuesSupported = ListUtils.immutableList(val);
            return this;
        }

        public Builder withIdTokenSigningAlgValuesSupported(final List<String> val)
        {
            this.idTokenSigningAlgValuesSupported = ListUtils.immutableList(val);
            return this;
        }

        public Builder withIdTokenEncryptionAlgValuesSupported(final List<String> val)
        {
            this.idTokenEncryptionAlgValuesSupported = ListUtils.immutableList(val);
            return this;
        }

        public Builder withIdTokenEncryptionEncValuesSupported(final List<String> val)
        {
            this.idTokenEncryptionEncValuesSupported = ListUtils.immutableList(val);
            return this;
        }

        public Builder withRequestObjectSigningAlgValuesSupported(final List<String> val)
        {
            this.requestObjectSigningAlgValuesSupported = ListUtils.immutableList(val);
            return this;
        }

        public Builder withRequestObjectEncryptionAlgValuesSupported(final List<String> val)
        {
            this.requestObjectEncryptionAlgValuesSupported = ListUtils.immutableList(val);
            return this;
        }

        public Builder withRequestObjectEncryptionEncValuesSupported(final List<String> val)
        {
            this.requestObjectEncryptionEncValuesSupported = ListUtils.immutableList(val);
            return this;
        }

        public Builder withTokenEndpointAuthMethodsSupported(final List<String> val)
        {
            this.tokenEndpointAuthMethodsSupported = ListUtils.immutableList(val);
            return this;
        }

        public Builder withTokenEndpointAuthSigningAlgValuesSupported(final List<String> val)
        {
            this.tokenEndpointAuthSigningAlgValuesSupported = ListUtils.immutableList(val);
            return this;
        }

        @JsonProperty("mc_version")
        public Builder withMCVersion(final List<String> val)
        {
            this.mcVersion = ListUtils.immutableList(val);
            return this;
        }

        public Builder withDisplayValuesSupported(final List<String> val)
        {
            this.displayValuesSupported = ListUtils.immutableList(val);
            return this;
        }

        public Builder withClaimTypesSupported(final List<String> val)
        {
            this.claimTypesSupported = ListUtils.immutableList(val);
            return this;
        }

        public Builder withClaimsSupported(final List<String> val)
        {
            this.claimsSupported = ListUtils.immutableList(val);
            return this;
        }

        public Builder withMcDiScopesSupported(final List<String> val)
        {
            this.mcDiScopesSupported = ListUtils.immutableList(val);
            return this;
        }

        public Builder withServiceDocumentation(final String val)
        {
            this.serviceDocumentation = val;
            return this;
        }

        public Builder withClaimsLocalesSupported(final List<String> val)
        {
            this.claimsLocalesSupported = ListUtils.immutableList(val);
            return this;
        }

        public Builder withUiLocalesSupported(final List<String> val)
        {
            this.uiLocalesSupported = ListUtils.immutableList(val);
            return this;
        }

        public Builder withRequireRequestUriRegistation(final Boolean val)
        {
            this.requireRequestUriRegistration = val;
            return this;
        }

        @JsonProperty("op_policy_uri")
        public Builder withOperatorPolicyUri(final String val)
        {
            this.operatorPolicyUri = val;
            return this;
        }

        @JsonProperty("op_tos_uri")
        public Builder withOperatorTermsOfServiceUri(final String val)
        {
            this.operatorTermsOfServiceUri = val;
            return this;
        }

        public Builder withClaimsParameterSupported(final Boolean val)
        {
            this.claimsParameterSupported = val;
            return this;
        }

        public Builder withRequestParameterSupported(final Boolean val)
        {
            this.requestParameterSupported = val;
            return this;
        }

        public Builder withRequestUriParameterSupported(final Boolean val)
        {
            this.requestUriParameterSupported = val;
            return this;
        }

        public Builder withLoginHintMethodsSupported(final List<String> val)
        {
            this.loginHintMethodsSupported = ListUtils.immutableList(val);
            return this;
        }

        @Override
        public ProviderMetadata build()
        {
            return new ProviderMetadata(this);
        }
    }

    public ObjectNode providerToJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        ObjectNode openId = mapper.createObjectNode();

        root.put("issuer", issuer);
        ArrayNode loginHint = mapper.createArrayNode();

        for (String nextLogintHint : loginHintMethodsSupported) {
            loginHint.add(nextLogintHint);
        }

        root.putPOJO("login_hint_methods_supported", loginHint);

        ArrayNode claimsSupportedNode = mapper.createArrayNode();
        for (String claims : claimsSupported) {
            claimsSupportedNode.add(claims);
        }
        ArrayNode mcDiScopesSupportedNode = mapper.createArrayNode();
        for (String claims : mcDiScopesSupported) {
            mcDiScopesSupportedNode.add(claims);
        }

        ArrayNode idToken = mapper.createArrayNode();
        for (String token : idTokenEncryptionAlgValuesSupported) {
            idToken.add(token);
        }

        ArrayNode acrValuesSupportedNode = mapper.createArrayNode();
        for (String acrValue : acrValuesSupported) {
            acrValuesSupportedNode.add(acrValue);
        }

        ArrayNode scopes = mapper.createArrayNode();
        for(String arrScopes : scopesSupported) {
            scopes.add(arrScopes);
        }

        root.putPOJO("claims_supported", claimsSupportedNode);
        root.putPOJO("mc_di_scopes_supported", mcDiScopesSupportedNode);
        root.putPOJO("id_token_signing_alg_values_supported", idToken);
        root.putPOJO("acr_values_supported", acrValuesSupportedNode);
        root.putPOJO("scopes_supported", scopes);
        openId.put("openid", "mc_v1.1");
        root.putPOJO("mobile_connect_version_supported", openId);

        return root;
    }
}
