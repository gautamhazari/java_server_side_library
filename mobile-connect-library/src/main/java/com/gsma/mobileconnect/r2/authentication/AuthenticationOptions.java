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
package com.gsma.mobileconnect.r2.authentication;

import com.gsma.mobileconnect.r2.claims.ClaimsParameter;
import com.gsma.mobileconnect.r2.claims.KYCClaimsParameter;
import com.gsma.mobileconnect.r2.constants.DefaultOptions;
import com.gsma.mobileconnect.r2.discovery.SupportedVersions;
import com.gsma.mobileconnect.r2.utils.IBuilder;

import java.net.URI;

/**
 * Holds required and optional options for {@link IAuthenticationService#startAuthentication(
 *String, String, URI, URI, String, String, String, SupportedVersions, AuthenticationOptions, String)}.
 *
 * @since 2.0
 */
@SuppressWarnings("WeakerAccess")
public class AuthenticationOptions
{
    private final String clientId;
    private final URI redirectUrl;
    private final String acrValues;
    private final String scope;
    private final String nonce;
    private final String state;
    private final long maxAge;
    private final String display;
    private final String prompt;
    private final String uiLocales;
    private final String claimsLocales;
    private final String idTokenHint;
    private final String loginHint;
    private final String loginHintToken;
    private final String dbts;
    private final String clientName;
    private final String clientSecret;
    private final String context;
    private final String bindingMessage;
    private final String claimsJson;
    private final ClaimsParameter claims;
    private final KYCClaimsParameter kycClaims;
    private final String correlationId;
    private final String version;
    private final boolean isUsingCorrelationId;

    private AuthenticationOptions(final Builder builder)
    {
        this.clientId = builder.clientId;
        this.redirectUrl = builder.redirectUrl;
        this.acrValues = builder.acrValues;
        this.scope = builder.scope;
        this.nonce = builder.nonce;
        this.state = builder.state;
        this.maxAge = builder.maxAge;
        this.display = builder.display;
        this.prompt = builder.prompt;
        this.uiLocales = builder.uiLocales;
        this.claimsLocales = builder.claimsLocales;
        this.idTokenHint = builder.idTokenHint;
        this.loginHint = builder.loginHint;
        this.loginHintToken = builder.loginHintToken;
        this.dbts = builder.dbts;
        this.clientName = builder.clientName;
        this.clientSecret = builder.clientSecret;
        this.context = builder.context;
        this.bindingMessage = builder.bindingMessage;
        this.claimsJson = builder.claimsJson;
        this.claims = builder.claims;
        this.kycClaims = builder.kycClaims;
        this.correlationId = builder.correlationId;
        this.isUsingCorrelationId = builder.isUsingCorrelationId;
        this.version = builder.version;
    }

    public String getClientId()
    {
        return this.clientId;
    }

    public URI getRedirectUrl()
    {
        return this.redirectUrl;
    }

    public String getAcrValues()
    {
        return this.acrValues;
    }

    public String getScope()
    {
        return this.scope;
    }

    public String getNonce()
    {
        return this.nonce;
    }

    public String getState()
    {
        return this.state;
    }

    public long getMaxAge()
    {
        return this.maxAge;
    }

    public String getDisplay()
    {
        return this.display;
    }

    public String getPrompt()
    {
        return this.prompt;
    }

    public String getUiLocales()
    {
        return this.uiLocales;
    }

    public String getClaimsLocales()
    {
        return this.claimsLocales;
    }

    public String getIdTokenHint()
    {
        return this.idTokenHint;
    }

    public String getLoginHint()
    {
        return this.loginHint;
    }

    public String getLoginHintToken() {
        return this.loginHintToken;
    }

    public String getDbts()
    {
        return this.dbts;
    }

    public String getClientName()
    {
        return this.clientName;
    }

    public String getClientSecret()
    {
        return this.clientSecret;
    }

    public String getContext()
    {
        return this.context;
    }

    public String getBindingMessage()
    {
        return this.bindingMessage;
    }

    public String getClaimsJson()
    {
        return this.claimsJson;
    }

    public ClaimsParameter getClaims()
    {
        return this.claims;
    }

    public KYCClaimsParameter getKycClaims()
    {
        return this.kycClaims;
    }

    public String getCorrelationId () {
        return this.correlationId;
    }

    public boolean getUsingCorrelationId() { return this.isUsingCorrelationId; }

    public String getVersion() { return this.version; }

    @SuppressWarnings("WeakerAccess")
    public static final class Builder implements IBuilder<AuthenticationOptions>
    {
        private String clientId;
        private URI redirectUrl;
        private String acrValues = DefaultOptions.AUTHENTICATION_ACR_VALUES;
        private String scope = DefaultOptions.AUTHENTICATION_SCOPE;
        private String nonce;
        private String state;
        private long maxAge = DefaultOptions.AUTHENTICATION_MAX_AGE;
        private String display = DefaultOptions.DISPLAY;
        private String prompt;
        private String uiLocales;
        private String claimsLocales;
        private String idTokenHint;
        private String loginHint;
        private String loginHintToken;
        private String dbts;
        private String clientName;
        private String clientSecret;
        private String context;
        private String bindingMessage;
        private String claimsJson;
        private ClaimsParameter claims;
        private KYCClaimsParameter kycClaims;
        private String correlationId;
        private boolean isUsingCorrelationId;
        private String version;

        public Builder()
        {
            // default constructor
        }

        public Builder(final AuthenticationOptions options)
        {
            if (options != null)
            {
                this.clientId = options.clientId;
                this.redirectUrl = options.redirectUrl;
                this.acrValues = options.acrValues;
                this.scope = options.scope;
                this.nonce = options.nonce;
                this.state = options.state;
                this.maxAge = options.maxAge;
                this.display = options.display;
                this.prompt = options.prompt;
                this.uiLocales = options.uiLocales;
                this.claimsLocales = options.claimsLocales;
                this.idTokenHint = options.idTokenHint;
                this.loginHint = options.loginHint;
                this.loginHintToken = options.loginHintToken;
                this.dbts = options.dbts;
                this.clientName = options.clientName;
                this.clientSecret = options.clientSecret;
                this.context = options.context;
                this.bindingMessage = options.bindingMessage;
                this.claimsJson = options.claimsJson;
                this.claims = options.claims;
                this.kycClaims = options.kycClaims;
                this.correlationId = options.correlationId;
                this.isUsingCorrelationId = options.isUsingCorrelationId;

                this.version = options.version;
            }
        }

        public Builder withClientId(final String val)
        {
            this.clientId = val;
            return this;
        }

        public Builder withRedirectUrl(final URI val)
        {
            this.redirectUrl = val;
            return this;
        }

        public Builder withAcrValues(final String val)
        {
            this.acrValues = val;
            return this;
        }

        public Builder withScope(final String val)
        {
            this.scope = val;
            return this;
        }

        public Builder withNonce(final String val)
        {
            this.nonce = val;
            return this;
        }

        public Builder withState(final String val)
        {
            this.state = val;
            return this;
        }

        public Builder withMaxAge(final int val)
        {
            this.maxAge = val;
            return this;
        }

        public Builder withDisplay(final String val)
        {
            this.display = val;
            return this;
        }

        public Builder withPrompt(final String val)
        {
            this.prompt = val;
            return this;
        }

        public Builder withUiLocales(final String val)
        {
            this.uiLocales = val;
            return this;
        }

        public Builder withClaimsLocales(final String val)
        {
            this.claimsLocales = val;
            return this;
        }

        public Builder withIdTokenHint(final String val)
        {
            this.idTokenHint = val;
            return this;
        }

        public Builder withLoginHint(final String val)
        {
            this.loginHint = val;
            return this;
        }

        public Builder withLoginHintToken(final String val)
        {
            this.loginHintToken = val;
            return this;
        }

        public Builder withDbts(final String val)
        {
            this.dbts = val;
            return this;
        }

        public Builder withClientName(final String val)
        {
            this.clientName = val;
            return this;
        }

        public Builder withClientSecret(final String val)
        {
            this.clientSecret = val;
            return this;
        }

        public Builder withContext(final String val)
        {
            this.context = val;
            return this;
        }

        public Builder withBindingMessage(final String val)
        {
            this.bindingMessage = val;
            return this;
        }

        public Builder withClaimsJson(final String val)
        {
            this.claimsJson = val;
            return this;
        }

        public Builder withClaims(final ClaimsParameter val)
        {
            this.claims = val;
            return this;
        }

        public Builder withKycClaims(final KYCClaimsParameter val)
        {
            this.kycClaims = val;
            return this;
        }

        public Builder withCorrelationId (final String val) {
            this.correlationId = val;
            return this;
        }

        public Builder withUsingCorrelationId(final boolean val) {
            this.isUsingCorrelationId = val;
            return this;
        }


        public Builder withVersion(final String val) {
            this.version = val;
            return this;
        }


        @Override
        public AuthenticationOptions build()
        {
            return new AuthenticationOptions(this);
        }
    }
}
