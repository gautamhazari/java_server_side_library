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
package com.gsma.mobileconnect.r2.service.identity;

import com.google.gson.annotations.SerializedName;
import com.gsma.mobileconnect.r2.utils.IBuilder;

/**
 * Stores data related to an Address Claim received from UserInfo/PremiumInfo.
 *
 * @since 2.0
 */
public class AddressData
{
    @SerializedName("formatted")
    private final String formatted;
    @SerializedName(value = "street_address", alternate = "streetAddress")
    private final String streetAddress;
    @SerializedName("locality")
    private final String locality;
    @SerializedName("region")
    private final String region;
    @SerializedName(value = "postal_code", alternate = "postalCode")
    private final String postalCode;
    @SerializedName("country")
    private final String country;

    private AddressData(Builder builder)
    {
        this.formatted = builder.formatted;
        this.streetAddress = builder.streetAddress;
        this.locality = builder.locality;
        this.region = builder.region;
        this.postalCode = builder.postalCode;
        this.country = builder.country;
    }

    /**
     * @return Full mailing address, formatted for display or use on a mailing label. May contain
     * multiple lines, separated by newlines (either "\r\n" or "\n")
     */
    public String getFormatted()
    {
        return this.formatted;
    }

    /**
     * @return Full street address component, which MAY include house number, street name, Post
     * Office Box and multi line extended street address information. May contain multiple lines,
     * separated by newlines (either "\r\n" or "\n")
     */
    public String getStreetAddress()
    {
        return this.streetAddress;
    }

    /**
     * @return City or locality/
     */
    public String getLocality()
    {
        return this.locality;
    }

    /**
     * @return State, province, perfecture or region.
     */
    public String getRegion()
    {
        return this.region;
    }

    /**
     * @return zip or postal code.
     */
    public String getPostalCode()
    {
        return this.postalCode;
    }

    /**
     * @return country name
     */
    public String getCountry()
    {
        return this.country;
    }

    public static final class Builder implements IBuilder<AddressData>
    {
        private String formatted = null;
        private String streetAddress = null;
        private String locality = null;
        private String region = null;
        private String postalCode = null;
        private String country = null;

        public Builder withFormatted(String val)
        {
            this.formatted = val;
            return this;
        }

        public Builder withStreetAddress(String val)
        {
            this.streetAddress = val;
            return this;
        }

        public Builder withLocality(String val)
        {
            this.locality = val;
            return this;
        }

        public Builder withRegion(String val)
        {
            this.region = val;
            return this;
        }

        public Builder withPostalCode(String val)
        {
            this.postalCode = val;
            return this;
        }

        public Builder withCountry(String val)
        {
            this.country = val;
            return this;
        }

        @Override
        public AddressData build()
        {
            return new AddressData(this);
        }
    }
}
