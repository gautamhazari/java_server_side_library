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

import com.gsma.mobileconnect.r2.discovery.IPreferences;
import com.gsma.mobileconnect.r2.utils.IBuilder;
import com.gsma.mobileconnect.r2.utils.LogUtils;
import com.gsma.mobileconnect.r2.utils.ObjectUtils;

import java.net.URI;

/**
 * Configuration properties for the MobileConnectInterface, reused across all requests for a single
 * {@link MobileConnectInterface} or {@link MobileConnectWebInterface}.
 *
 * @see MobileConnectInterface
 * @see MobileConnectWebInterface
 * @since 2.0
 */
public class MobileConnectConfig implements IPreferences
{
    // required
    private final String clientId;
    private final String clientSecret;
    private final String clientName;
    private final String xRedirect;
    private final URI discoveryUrl;
    private final URI redirectUrl;
    private final boolean includeRequestIP;

    // web
    private final boolean cacheResponsesWithSessionId;

    private MobileConnectConfig(final Builder builder)
    {
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret;
        this.clientName = builder.clientName;
        this.xRedirect = builder.xRedirect;
        this.discoveryUrl = builder.discoveryUrl;
        this.redirectUrl = builder.redirectUrl;
        this.includeRequestIP = builder.includeRequestIP;
        this.cacheResponsesWithSessionId = builder.cacheResponsesWithSessionId;
    }

    @Override
    public String getClientId()
    {
        return this.clientId;
    }

    @Override
    public String getClientSecret()
    {
        return this.clientSecret;
    }

    @Override
    public String getXRedirect()
    {
        return this.xRedirect;
    }
    @Override
    public URI getDiscoveryUrl()
    {
        return this.discoveryUrl;
    }

    public boolean getIncludeRequestIp() {
        return this.includeRequestIP;
    }

    public boolean isCacheResponsesWithSessionId()
    {
        return this.cacheResponsesWithSessionId;
    }

    public URI getRedirectUrl()
    {
        return this.redirectUrl;
    }

    @Override
    public String toString()
    {
        return new StringBuilder("MobileConnectConfig(clientId=")
            .append(LogUtils.mask(this.clientId))
            .append(",clientSecret=")
            .append(LogUtils.mask(this.clientSecret))
            .append(",discoveryUrl=")
            .append(this.discoveryUrl)
            .append(",redirectUrl=")
            .append(this.redirectUrl)
            .append(",cacheResponseWithSessionId=")
            .append(this.cacheResponsesWithSessionId)
            .append(")")
            .toString();
    }

    public static final class Builder implements IBuilder<MobileConnectConfig>
    {
        private String clientId;
        private String clientSecret;
        private String clientName;
        private String xRedirect;
        private URI discoveryUrl;
        private URI redirectUrl;
        private boolean includeRequestIP;
        private boolean cacheResponsesWithSessionId = true;

        public Builder withClientId(String val)
        {
            this.clientId = val;
            return this;
        }

        public Builder withClientSecret(String val)
        {
            this.clientSecret = val;
            return this;
        }

        public Builder withClientName(String val)
        {
            this.clientName = val;
            return this;
        }

        public Builder withDiscoveryUrl(URI val)
        {
            this.discoveryUrl = val;
            return this;
        }

        public Builder withRedirectUrl(URI val)
        {
            this.redirectUrl = val;
            return this;
        }

        public Builder withIncludeRequestIP(boolean val)
        {
            this.includeRequestIP = val;
            return this;
        }

        public Builder withCacheResponsesWithSessionId(boolean val)
        {
            this.cacheResponsesWithSessionId = val;
            return this;
        }

        public Builder withXRedirect(String val)
        {
            this.xRedirect = val;
            return this;
        }

        @Override
        public MobileConnectConfig build()
        {
            ObjectUtils.requireNonNull(this.redirectUrl, "redirectUrl");

            return new MobileConnectConfig(this);
        }
    }
}
