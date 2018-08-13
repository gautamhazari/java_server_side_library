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
package com.gsma.mobileconnect.r2;

import com.gsma.mobileconnect.r2.authentication.DiscoveryResponseGenerateOptions;
import com.gsma.mobileconnect.r2.authentication.AuthenticationOptions;
import com.gsma.mobileconnect.r2.validation.TokenValidationOptions;
import com.gsma.mobileconnect.r2.discovery.DiscoveryOptions;
import com.gsma.mobileconnect.r2.utils.IBuilder;

/**
 * Options for a single request to {@link MobileConnectInterface}
 *
 * @since 2.0
 */
public class MobileConnectRequestOptions
{
    private final DiscoveryOptions discoveryOptions;
    private final AuthenticationOptions authenticationOptions;
    private final TokenValidationOptions validationOptions;
    private final DiscoveryResponseGenerateOptions discoveryResponseGenerateOptions;
    private final boolean autoRetrieveIdentitySet;
    private final boolean correlationId;

    private MobileConnectRequestOptions(final Builder builder)
    {
        this.discoveryOptions = builder.discoveryOptions;
        this.authenticationOptions = builder.authenticationOptions;
        this.discoveryResponseGenerateOptions = builder.discoveryResponseGenerateOptions;
        this.autoRetrieveIdentitySet = builder.autoRetrieveIdentitySet;
        this.validationOptions = builder.validationOptions;
        this.correlationId = builder.correlationId;
    }

    public DiscoveryResponseGenerateOptions getDiscoveryResponseGenerateOptions() {
        return this.discoveryResponseGenerateOptions;
    }

    DiscoveryResponseGenerateOptions.BuilderResponse getAuthOptionDiscoveryResponseBuilder() {
        return new DiscoveryResponseGenerateOptions.BuilderResponse(this.discoveryResponseGenerateOptions);
    }

    public DiscoveryOptions getDiscoveryOptions()
    {
        return this.discoveryOptions;
    }

    DiscoveryOptions.Builder getDiscoveryOptionsBuilder()
    {
        return new DiscoveryOptions.Builder(this.discoveryOptions);
    }

    public boolean isAutoRetrieveIdentitySet()
    {
        return autoRetrieveIdentitySet;
    }

    public AuthenticationOptions getAuthenticationOptions()
    {
        return this.authenticationOptions;
    }

    public boolean isCorrelationId () {
        return correlationId;
    }
    AuthenticationOptions.Builder getAuthenticationOptionsBuilder()
    {
        return new AuthenticationOptions.Builder(this.authenticationOptions);
    }

    public TokenValidationOptions getValidationOptions()
    {
        return validationOptions;
    }

    TokenValidationOptions.Builder getValidationOptionsBuilder()
    {
        return new TokenValidationOptions.Builder().withAcceptedValidationResults(this.getValidationOptions().getAcceptedValidationResults());
    }

    public static final class Builder implements IBuilder<MobileConnectRequestOptions>
    {
        private DiscoveryOptions discoveryOptions;
        private AuthenticationOptions authenticationOptions;
        private DiscoveryResponseGenerateOptions discoveryResponseGenerateOptions;
        private boolean autoRetrieveIdentitySet = false;
        private TokenValidationOptions validationOptions;
        private boolean correlationId;
        public Builder withDiscoveryOptions(final DiscoveryOptions val)
        {
            this.discoveryOptions = val;
            return this;
        }

        public Builder withAuthenticationOptions(final AuthenticationOptions val)
        {
            this.authenticationOptions = val;
            return this;
        }

        public Builder withAuthOptionDiscoveryResponse(final DiscoveryResponseGenerateOptions val)
        {
            this.discoveryResponseGenerateOptions = val;
            return this;
        }

        public Builder witAutoRetrieveIdentitySet(final boolean val)
        {
            this.autoRetrieveIdentitySet = val;
            return this;
        }

        public Builder withValidationOptions(final TokenValidationOptions val)
        {
            this.validationOptions = val;
            return this;
        }

        public Builder withUsingCorrelationId (final boolean val)
        {
            this.correlationId = val;
            return this;
        }

        @Override
        public MobileConnectRequestOptions build()
        {
            if (this.discoveryOptions == null)
            {
                this.discoveryOptions = new DiscoveryOptions.Builder().build();
            }

            if (this.authenticationOptions == null)
            {
                this.authenticationOptions = new AuthenticationOptions.Builder().build();
            }

            if (this.validationOptions == null)
            {
                this.validationOptions = new TokenValidationOptions.Builder().build();
            }


            if (this.discoveryResponseGenerateOptions == null) {
                this.discoveryResponseGenerateOptions = new DiscoveryResponseGenerateOptions.BuilderResponse().build();
            }
            return new MobileConnectRequestOptions(this);
        }
    }
}
