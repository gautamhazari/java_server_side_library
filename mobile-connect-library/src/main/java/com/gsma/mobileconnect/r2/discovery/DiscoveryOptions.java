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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gsma.mobileconnect.r2.constants.DefaultOptions;
import com.gsma.mobileconnect.r2.utils.IBuilder;
import com.gsma.mobileconnect.r2.utils.StringUtils;

import java.net.URI;

/**
 * Parameters for the {@link IDiscoveryService}.  Object can be serialized to JSON to be a POST
 * body.
 *
 * @see IDiscoveryService
 * @since 2.0
 */
public class DiscoveryOptions
{
    private final String msisdn;
    private final URI redirectUrl;
    private final boolean manuallySelect;
    private final String identifiedMcc;
    private final String identifiedMnc;
    private final String selectedMcc;
    private final String selectedMnc;
    private final boolean usingMobileData;
    private final String localClientIp;
    private final String clientIp;
    private final String xRedirect;
    private final boolean isUsingCorrelationId;
    private final String clientSideVersion;
    private final String serverSideVersion;

    private DiscoveryOptions(final Builder builder)
    {
        this.msisdn = builder.msisdn;
        this.redirectUrl = builder.redirectUrl;
        this.manuallySelect = builder.manuallySelect;
        this.identifiedMcc = builder.identifiedMcc;
        this.identifiedMnc = builder.identifiedMnc;
        this.selectedMcc = builder.selectedMcc;
        this.selectedMnc = builder.selectedMnc;
        this.usingMobileData = builder.usingMobileData;
        this.localClientIp = builder.localClientIp;
        this.clientIp = builder.clientIp;
        this.xRedirect = builder.xRedirect;
        this.isUsingCorrelationId = builder.isUsingCorrelationId;
        this.clientSideVersion = builder.clientSideVersion;
        this.serverSideVersion = builder.serverSideVersion;
    }

    /**
     * @return The detected or user input mobile number in E.164 number formatting.
     */
    public String getMsisdn()
    {
        return this.msisdn;
    }

    /**
     * @return The URL to redirect to after succesful discovery.
     */
    @JsonProperty("Redirect_URL")
    public URI getRedirectUrl()
    {
        return this.redirectUrl;
    }

    /**
     * @return Set to true if manual select is requested.
     */
    @JsonProperty("Manually-Select")
    public boolean isManuallySelect()
    {
        return this.manuallySelect;
    }

    /**
     * @return The identified Mobile Country Code.
     */
    @JsonProperty("Identified-MCC")
    public String getIdentifiedMcc()
    {
        return this.identifiedMcc;
    }

    /**
     * @return The identified Mobile Country Code.
     */
    @JsonProperty("Identified-MNC")
    public String getIdentifiedMnc()
    {
        return this.identifiedMnc;
    }

    /**
     * @return The selected Mobile Country Code.
     */
    @JsonProperty("Selected-MCC")
    public String getSelectedMcc()
    {
        return this.selectedMcc;
    }

    /**
     * @return The selected Mobile Network Code
     */
    @JsonProperty("Selected-MNC")
    public String getSelectedMnc()
    {
        return this.selectedMnc;
    }

    /**
     * @return Set to true if your application is able to determine that the user is accessing the
     * service via mobile data. <p> This tells the Discovery Service to discover using the
     * mobile-network.</p>
     */
    @JsonProperty("Using-Mobile-Data")
    public boolean isUsingMobileData()
    {
        return this.usingMobileData;
    }

    /**
     * @return The current local IP address of the client application i.e. the actual IP address
     * currently allocated to the device running the application. <p> This can be used within header
     * injection processes from the MNO to confirm the application is directly using a mobile data
     * connection from the consumption device rather than MiFi/WiFi to mobile hotspot.</p>
     */
    @JsonProperty("Local-Client-IP")
    public String getLocalClientIp()
    {
        return this.localClientIp;
    }

    /**
     * @return Allows a server application to indicate the 'public IP address' of the connection
     * from a client application/mobile browser to the server. <p> This is used in place of the
     * public IP address normally detected by the discovery service. Note this will usually differ
     * from the Local-Client-IP address, and the public IP address detected by the application
     * server should not be used for the Local-Client-IP address.</p>
     */
    @JsonIgnore
    public String getClientIp()
    {
        return this.clientIp;
    }

    /**
     * @return X-Redicrect header value.
     */
    @JsonIgnore
    public String getXRedirect()
    {
        return this.xRedirect;
    }

    /**
     * @return Allows a server application to use correlationId.
     */
    @JsonIgnore
    public boolean getUsingCorrelationId() { return this.isUsingCorrelationId; }

    public String getClientSideVersion() {
        return clientSideVersion;
    }

    public String getServerSideVersion() {
        return serverSideVersion;
    }

    public static final class Builder implements IBuilder<DiscoveryOptions>
    {
        private String msisdn = null;
        private URI redirectUrl = null;
        private boolean manuallySelect = DefaultOptions.MANUAL_SELECT;
        private String identifiedMcc = null;
        private String identifiedMnc = null;
        private String selectedMcc = null;
        private String selectedMnc = null;
        private boolean usingMobileData = false;
        private String localClientIp = null;
        private String clientIp = null;
        private String xRedirect = null;
        private boolean isUsingCorrelationId = false;
        private String clientSideVersion;
        private String serverSideVersion;
        public Builder()
        {
            // default constructor
        }

        public Builder(final DiscoveryOptions options)
        {
            if (options != null)
            {
                this.msisdn = options.getMsisdn();
                this.redirectUrl = options.getRedirectUrl();
                this.manuallySelect = options.isManuallySelect();
                this.identifiedMcc = options.getIdentifiedMcc();
                this.identifiedMnc = options.getIdentifiedMnc();
                this.selectedMcc = options.getSelectedMcc();
                this.selectedMnc = options.getSelectedMnc();
                this.usingMobileData = options.isUsingMobileData();
                this.localClientIp = options.getLocalClientIp();
                this.clientIp = options.getClientIp();
                this.xRedirect = options.getXRedirect();
                this.isUsingCorrelationId = options.getUsingCorrelationId();
                this.clientSideVersion = options.getClientSideVersion();
                this.serverSideVersion = options.getServerSideVersion();
            }
        }

        public Builder withMsisdn(String val)
        {
            this.msisdn = val;
            return this;
        }

        public Builder withRedirectUrl(URI val)
        {
            this.redirectUrl = val;
            return this;
        }

        public Builder withManuallySelect(boolean val)
        {
            this.manuallySelect = val;
            return this;
        }

        public Builder withIdentifiedMcc(String val)
        {
            this.identifiedMcc = val;
            return this;
        }

        public Builder withIdentifiedMnc(String val)
        {
            this.identifiedMnc = val;
            return this;
        }

        public Builder withSelectedMcc(String val)
        {
            this.selectedMcc = val;
            return this;
        }

        public Builder withSelectedMnc(String val)
        {
            this.selectedMnc = val;
            return this;
        }

        public Builder withUsingMobileData(boolean val)
        {
            this.usingMobileData = val;
            return this;
        }

        public Builder withLocalClientIp(String val)
        {
            this.localClientIp = val;
            return this;
        }

        public Builder withClientIp(String val)
        {
            this.clientIp = val;
            return this;
        }

        public Builder withXRedirect(String val)
        {
            this.xRedirect = val;
            return this;
        }

        public Builder withUsingCorrelationId(boolean val) {
            this.isUsingCorrelationId = val;
            return this;
        }

        public Builder withClientSideVersion(String val) {
            if (!StringUtils.isNullOrEmpty(val)) {
                this.clientSideVersion = val;
            }
            return this;
        }

        public Builder withServerSideVersion(String val) {
            if (!StringUtils.isNullOrEmpty(val)) {
                this.serverSideVersion = val;
            }
            return this;
        }

        @Override
        public DiscoveryOptions build()
        {
            return new DiscoveryOptions(this);
        }
    }
}
