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
package com.gsma.mobileconnect.r2.model.claims;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.gsma.mobileconnect.r2.model.constants.LinkRels;
import com.gsma.mobileconnect.r2.utils.IBuilder;
import com.gsma.mobileconnect.r2.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to construct required claims for the mobile connect process.
 *
 * @since 2.0
 */
public class KYCClaimsParameter {
    private final String name;
    @SerializedName(KYCClaimsConstants.GIVEN_NAME)
    private final String givenName;
    @SerializedName(KYCClaimsConstants.FAMILY_NAME)
    private final String familyName;
    @SerializedName(KYCClaimsConstants.ADDRESS)
    private final String address;
    @SerializedName(KYCClaimsConstants.HOUSENO_OR_HOUSENAME)
    private final String housenoOrHousename;
    @SerializedName(KYCClaimsConstants.POSTAL_CODE)
    private final String postalCode;
    @SerializedName(KYCClaimsConstants.TOWN)
    private final String town;
    @SerializedName(KYCClaimsConstants.COUNTRY)
    private final String country;
    @SerializedName(KYCClaimsConstants.BIRTHDATE)
    private final String birthdate;

    @SerializedName(KYCClaimsConstants.NAME_HASHED)
    private final String nameHashed;
    @SerializedName(KYCClaimsConstants.GIVEN_NAME_HASHED)
    private final String givenNameHashed;
    @SerializedName(KYCClaimsConstants.FAMILY_NAME_HASHED)
    private final String familyNameHashed;
    @SerializedName(KYCClaimsConstants.ADDRESS_HASHED)
    private final String addressHashed;
    @SerializedName(KYCClaimsConstants.HOUSENO_OR_HOUSENAME_HASHED)
    private final String housenoOrHousenameHashed;
    @SerializedName(KYCClaimsConstants.POSTAL_CODE_HASHED)
    private final String postalCodeHashed;
    @SerializedName(KYCClaimsConstants.TOWN_HASHED)
    private final String townHashed;
    @SerializedName(KYCClaimsConstants.COUNTRY_HASHED)
    private final String countryHashed;
    @SerializedName(KYCClaimsConstants.BIRTHDATE_HASHED)
    private final String birthdateHashed;

    private static final Logger LOGGER = LoggerFactory.getLogger(KYCClaimsParameter.class);

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

    public String toJson() {

        Gson gson = new Gson();
        List<JsonObject> claimsList = new ArrayList<>();

        claimsList.add(paramToJson(name, KYCClaimsConstants.NAME));
        claimsList.add(paramToJson(givenName, KYCClaimsConstants.GIVEN_NAME));
        claimsList.add(paramToJson(familyName, KYCClaimsConstants.FAMILY_NAME));
        claimsList.add(paramToJson(address, KYCClaimsConstants.ADDRESS));
        claimsList.add(paramToJson(housenoOrHousename, KYCClaimsConstants.HOUSENO_OR_HOUSENAME));
        claimsList.add(paramToJson(postalCode, KYCClaimsConstants.POSTAL_CODE));
        claimsList.add(paramToJson(town, KYCClaimsConstants.TOWN));
        claimsList.add(paramToJson(country, KYCClaimsConstants.COUNTRY));
        claimsList.add(paramToJson(birthdate, KYCClaimsConstants.BIRTHDATE));

        claimsList.add(paramToJson(nameHashed, KYCClaimsConstants.NAME_HASHED));
        claimsList.add(paramToJson(givenNameHashed, KYCClaimsConstants.GIVEN_NAME_HASHED));
        claimsList.add(paramToJson(familyNameHashed, KYCClaimsConstants.FAMILY_NAME_HASHED));
        claimsList.add(paramToJson(addressHashed, KYCClaimsConstants.ADDRESS_HASHED));
        claimsList.add(paramToJson(housenoOrHousenameHashed, KYCClaimsConstants.HOUSENO_OR_HOUSENAME_HASHED));
        claimsList.add(paramToJson(postalCodeHashed, KYCClaimsConstants.POSTAL_CODE_HASHED));
        claimsList.add(paramToJson(townHashed, KYCClaimsConstants.TOWN_HASHED));
        claimsList.add(paramToJson(countryHashed, KYCClaimsConstants.COUNTRY_HASHED));
        claimsList.add(paramToJson(birthdateHashed, KYCClaimsConstants.BIRTHDATE_HASHED));

        JsonObject premiumInfo = new JsonObject();
        premiumInfo.add(LinkRels.PREMIUMINFO, gson.toJsonTree(claimsList, new TypeToken<List<JsonObject>>(){}.getType()));

        return gson.toJson(premiumInfo);
    }

    private JsonObject paramToJson(String param, String name) {
        JsonObject paramJson = new JsonObject();
        if (!StringUtils.isNullOrEmpty(param)) {
            JsonObject value = new JsonObject();
            value.addProperty(LinkRels.VALUE, param);
            paramJson.add(param, value);
        }
        return paramJson;
    }

    public String getName()
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
        private String name = null;
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

        public Builder withName(final String val)
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
