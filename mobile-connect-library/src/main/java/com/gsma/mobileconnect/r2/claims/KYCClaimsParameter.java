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
package com.gsma.mobileconnect.r2.claims;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gsma.mobileconnect.r2.utils.IBuilder;

/**
 * Class to construct required claims for the mobile connect process.
 *
 * @since 2.0
 */
@JsonDeserialize(builder = KYCClaimsParameter.Builder.class)
public class KYCClaimsParameter {
    private final Claims name;
    private final String givenName;
    private final String familyName;
    private final String address;
    private final String housenoOrHousename;
    private final String postalCode;
    private final String town;
    private final String country;
    private final String birthdate;

    private final String nameHashed;
    private final String givenNameHashed;
    private final String familyNameHashed;
    private final String addressHashed;
    private final String housenoOrHousenameHashed;
    private final String postalCodeHashed;
    private final String townHashed;
    private final String countryHashed;
    private final String birthdateHashed;

    private KYCClaimsParameter(Builder builder)
    {
        this.name = builder.name;
        this.givenName = builder.givenName;
        this.familyName = builder.familyName;
        this.address = builder.address;
        this.housenoOrHousename = builder.housenoOrHousename;
        this.postalCode = builder.postalCode;
        this.town = builder.town;
        this.country = builder.country;
        this.birthdate = builder.birthdate;
        this.nameHashed = builder.nameHashed;
        this.givenNameHashed = builder.givenNameHashed;
        this.familyNameHashed = builder.familyNameHashed;
        this.addressHashed = builder.addressHashed;
        this.housenoOrHousenameHashed = builder.housenoOrHousenameHashed;
        this.postalCodeHashed = builder.postalCodeHashed;
        this.townHashed = builder.townHashed;
        this.countryHashed = builder.countryHashed;
        this.birthdateHashed = builder.birthdateHashed;
    }

    public Claims getName()
    {
        return this.name;
    }

    public String getGivenName()
    {
        return this.givenName;
    }

    public String getFamilyName()
    {
        return this.familyName;
    }

    public String getAddress() {
        return address;
    }

    public String getHousenoOrHousename() {
        return housenoOrHousename;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getTown() {
        return town;
    }

    public String getCountry() {
        return country;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getNameHashed() {
        return nameHashed;
    }

    public String getGivenNameHashed() {
        return givenNameHashed;
    }

    public String getFamilyNameHashed() {
        return familyNameHashed;
    }

    public String getAddressHashed() {
        return addressHashed;
    }

    public String getHousenoOrHousenameHashed() {
        return housenoOrHousenameHashed;
    }

    public String getPostalCodeHashed() {
        return postalCodeHashed;
    }

    public String getTownHashed() {
        return townHashed;
    }

    public String getCountryHashed() {
        return countryHashed;
    }

    public String getBirthdateHashed() {
        return birthdateHashed;
    }

    public static final class Builder implements IBuilder<KYCClaimsParameter>
    {
        private Claims name = null;
        private String givenName = null;
        private String familyName = null;
        private String address = null;
        private String housenoOrHousename = null;
        private String postalCode = null;
        private String town = null;
        private String country = null;
        private String birthdate = null;
        private String nameHashed = null;
        private String givenNameHashed = null;
        private String familyNameHashed = null;
        private String  addressHashed = null;
        private String housenoOrHousenameHashed = null;
        private String postalCodeHashed = null;
        private String townHashed = null;
        private String countryHashed = null;
        private String birthdateHashed = null;

        public Builder withName(final Claims val)
        {
            this.name = val;
            return this;
        }

        public Builder withGivenName(final String val)
        {
            this.givenName = val;
            return this;
        }

        public Builder withFamilyName(final String val)
        {
            this.givenName = val;
            return this;
        }

        public Builder withAddress(final String val)
        {
            this.address = val;
            return this;
        }

        public Builder withHousenoOrHousename(final String val)
        {
            this.housenoOrHousename = val;
            return this;
        }

        public Builder withPostalCode(final String val)
        {
            this.postalCode = val;
            return this;
        }

        public Builder withTown(final String val)
        {
            this.town = val;
            return this;
        }

        public Builder withCountry(final String val)
        {
            this.country = val;
            return this;
        }

        public Builder withBirthdate(final String val)
        {
            this.birthdate = val;
            return this;
        }

        public Builder withNameHashed(final String val)
        {
            this.nameHashed = val;
            return this;
        }

        public Builder withGivenNameHashed(final String val)
        {
            this.givenNameHashed = val;
            return this;
        }

        public Builder withFamilyNameHashed(final String val)
        {
            this.givenNameHashed = val;
            return this;
        }

        public Builder withAddressHashed(final String val)
        {
            this.addressHashed = val;
            return this;
        }

        public Builder withHousenoOrHousenameHashed(final String val)
        {
            this.housenoOrHousenameHashed = val;
            return this;
        }

        public Builder withPostalCodeHashed(final String val)
        {
            this.postalCodeHashed = val;
            return this;
        }

        public Builder withTownHashed(final String val)
        {
            this.townHashed = val;
            return this;
        }

        public Builder withCountryHashed(final String val)
        {
            this.countryHashed = val;
            return this;
        }

        public Builder withBirthdateHashed(final String val)
        {
            this.birthdateHashed = val;
            return this;
        }

        @Override
        public KYCClaimsParameter build()
        {
            return new KYCClaimsParameter(this);
        }
    }
}
