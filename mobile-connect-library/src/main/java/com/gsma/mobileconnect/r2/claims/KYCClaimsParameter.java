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
    private final Claims givenName;
    private final Claims familyName;
    private final Claims address;
    private final Claims housenoOrHousename;
    private final Claims postalCode;
    private final Claims town;
    private final Claims country;
    private final Claims birthdate;

    private final Claims nameHashed;
    private final Claims givenNameHashed;
    private final Claims familyNameHashed;
    private final Claims addressHashed;
    private final Claims housenoOrHousenameHashed;
    private final Claims postalCodeHashed;
    private final Claims townHashed;
    private final Claims countryHashed;
    private final Claims birthdateHashed;

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

    public Claims givenName()
    {
        return this.givenName;
    }

    public Claims getFamilyName()
    {
        return this.familyName;
    }

    public Claims getAddress() {
        return address;
    }

    public Claims getHousenoOrHousename() {
        return housenoOrHousename;
    }

    public Claims getPostalCode() {
        return postalCode;
    }

    public Claims getTown() {
        return town;
    }

    public Claims getCountry() {
        return country;
    }

    public Claims getBirthdate() {
        return birthdate;
    }

    public Claims getNameHashed() {
        return nameHashed;
    }

    public Claims getGivenNameHashed() {
        return givenNameHashed;
    }

    public Claims getFamilyNameHashed() {
        return familyNameHashed;
    }

    public Claims getAddressHashed() {
        return addressHashed;
    }

    public Claims getHousenoOrHousenameHashed() {
        return housenoOrHousenameHashed;
    }

    public Claims getPostalCodeHashed() {
        return postalCodeHashed;
    }

    public Claims getTownHashed() {
        return townHashed;
    }

    public Claims getCountryHashed() {
        return countryHashed;
    }

    public Claims getBirthdateHashed() {
        return birthdateHashed;
    }

    public static final class Builder implements IBuilder<KYCClaimsParameter>
    {
        private Claims name = null;
        private Claims givenName = null;
        private Claims familyName = null;
        private Claims address = null;
        private Claims housenoOrHousename = null;
        private Claims postalCode = null;
        private Claims town = null;
        private Claims country = null;
        private Claims birthdate = null;
        private Claims nameHashed = null;
        private Claims givenNameHashed = null;
        private Claims familyNameHashed = null;
        private Claims addressHashed = null;
        private Claims housenoOrHousenameHashed = null;
        private Claims postalCodeHashed = null;
        private Claims townHashed = null;
        private Claims countryHashed = null;
        private Claims birthdateHashed = null;

        public Builder withName(final Claims val)
        {
            this.name = val;
            return this;
        }

        public Builder withName(final Claims.Builder val)
        {
            this.name = val.build();
            return this;
        }

        public Builder withGivenName(final Claims val)
        {
            this.givenName = val;
            return this;
        }

        public Builder withGivenName(final Claims.Builder val)
        {
            this.givenName = val.build();
            return this;
        }

        public Builder withFamilyName(final Claims val)
        {
            this.givenName = val;
            return this;
        }

        public Builder withFamilyName(final Claims.Builder val)
        {
            this.familyName = val.build();
            return this;
        }

        public Builder withAddress(final Claims val)
        {
            this.address = val;
            return this;
        }

        public Builder withAddress(final Claims.Builder val)
        {
            this.address = val.build();
            return this;
        }

        public Builder withHousenoOrHousename(final Claims val)
        {
            this.housenoOrHousename = val;
            return this;
        }

        public Builder withHousenoOrHousename(final Claims.Builder val)
        {
            this.housenoOrHousename = val.build();
            return this;
        }

        public Builder withPostalCode(final Claims val)
        {
            this.postalCode = val;
            return this;
        }

        public Builder withPostalCode(final Claims.Builder val)
        {
            this.postalCode = val.build();
            return this;
        }

        public Builder withTown(final Claims val)
        {
            this.town = val;
            return this;
        }

        public Builder withTown(final Claims.Builder val)
        {
            this.town = val.build();
            return this;
        }

        public Builder withCountry(final Claims val)
        {
            this.country = val;
            return this;
        }

        public Builder withCountry(final Claims.Builder val)
        {
            this.country = val.build();
            return this;
        }

        public Builder withBirthdate(final Claims val)
        {
            this.birthdate = val;
            return this;
        }

        public Builder withBirthdate(final Claims.Builder val)
        {
            this.birthdate = val.build();
            return this;
        }

        public Builder withNameHashed(final Claims val)
        {
            this.nameHashed = val;
            return this;
        }

        public Builder withNameHashed(final Claims.Builder val)
        {
            this.nameHashed = val.build();
            return this;
        }

        public Builder withGivenNameHashed(final Claims val)
        {
            this.givenNameHashed = val;
            return this;
        }

        public Builder withGivenNameHashed(final Claims.Builder val)
        {
            this.givenNameHashed = val.build();
            return this;
        }

        public Builder withFamilyNameHashed(final Claims val)
        {
            this.givenNameHashed = val;
            return this;
        }

        public Builder withFamilyNameHashed(final Claims.Builder val)
        {
            this.familyNameHashed = val.build();
            return this;
        }

        public Builder withAddressHashed(final Claims val)
        {
            this.addressHashed = val;
            return this;
        }

        public Builder withAddressHashed(final Claims.Builder val)
        {
            this.addressHashed = val.build();
            return this;
        }

        public Builder withHousenoOrHousenameHashed(final Claims val)
        {
            this.housenoOrHousenameHashed = val;
            return this;
        }

        public Builder withHousenoOrHousenameHashed(final Claims.Builder val)
        {
            this.housenoOrHousenameHashed = val.build();
            return this;
        }

        public Builder withPostalCodeHashed(final Claims val)
        {
            this.postalCodeHashed = val;
            return this;
        }

        public Builder withPostalCodeHashed(final Claims.Builder val)
        {
            this.postalCodeHashed = val.build();
            return this;
        }

        public Builder withTownHashed(final Claims val)
        {
            this.townHashed = val;
            return this;
        }

        public Builder withTownHashed(final Claims.Builder val)
        {
            this.townHashed = val.build();
            return this;
        }

        public Builder withCountryHashed(final Claims val)
        {
            this.countryHashed = val;
            return this;
        }

        public Builder withCountryHashed(final Claims.Builder val)
        {
            this.countryHashed = val.build();
            return this;
        }

        public Builder withBirthdateHashed(final Claims val)
        {
            this.birthdateHashed = val;
            return this;
        }

        public Builder withBirthdateHashed(final Claims.Builder val)
        {
            this.birthdateHashed = val.build();
            return this;
        }

        @Override
        public KYCClaimsParameter build()
        {
            return new KYCClaimsParameter(this);
        }
    }
}
