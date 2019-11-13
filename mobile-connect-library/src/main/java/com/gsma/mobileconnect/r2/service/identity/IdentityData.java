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
import com.gsma.mobileconnect.r2.model.json.IJsonService;
import com.gsma.mobileconnect.r2.utils.IBuilder;

/**
 * Class containing properties for all available Mobile Connect Identity Claims, can be used to
 * retrieve {@link IdentityResponse#getResponseJson()} as a concrete object. Use the {@link
 * IdentityResponse#getResponseAs(Class, IJsonService)}  method with this type as the parameter T.
 * Alternatively a leaner type or type with additional custom properties can be provided for more
 * control over the deserialization process.
 *
 * @since 2.0
 */
public class IdentityData
{
    private final String sub;

    // signup
    @SerializedName(value = "phone_number_alternate", alternate = "phoneNumberAlternate")
    private final String phoneNumberAlternate;
    @SerializedName("title")
    private final String title;
    @SerializedName(value = "given_name", alternate = "givenName")
    private final String givenName;
    @SerializedName(value = "family_name", alternate = "familyName")
    private final String familyName;
    @SerializedName(value = "middle_name", alternate = "middleName")
    private final String middleName;
    @SerializedName(value = "street_address", alternate = "streetAddress")
    private final String streetAddress;
    @SerializedName("city")
    private final String city;
    @SerializedName("state")
    private final String state;
    @SerializedName(value = "postal_code", alternate = "postalCode")
    private final String postalCode;
    @SerializedName("country")
    private final String country;
    @SerializedName("email")
    private final String email;

    // phone number
    @SerializedName(value = "phone_number", alternate = "phoneNumber")
    private final String phoneNumber;

    // national id:
    @SerializedName("birthdate")
    private final String birthdate;
    @SerializedName(value = "national_identifier", alternate = "nationalIdentifier")
    private final String nationalIdentifier;

    private IdentityData(Builder builder)
    {
        this.sub = builder.sub;
        this.phoneNumberAlternate = builder.phoneNumberAlternate;
        this.title = builder.title;
        this.givenName = builder.givenName;
        this.familyName = builder.familyName;
        this.middleName = builder.middleName;
        this.streetAddress = builder.streetAddress;
        this.city = builder.city;
        this.state = builder.state;
        this.postalCode = builder.postalCode;
        this.country = builder.country;
        this.email = builder.email;
        this.phoneNumber = builder.phoneNumber;
        this.birthdate = builder.birthdate;
        this.nationalIdentifier = builder.nationalIdentifier;
    }

    /**
     * @return subject - identifier for the end-user at the issuer.
     */
    public String getSub()
    {
        return this.sub;
    }

    /**
     * @return user's alternate/secondary telephone number.
     */
    public String getPhoneNumberAlternate()
    {
        return this.phoneNumberAlternate;
    }

    /**
     * @return user's salutation/honourific
     */
    public String getTitle()
    {
        return this.title;
    }

    /**
     * @return given name(s)
     */
    public String getGivenName()
    {
        return this.givenName;
    }

    /**
     * @return family name(s)
     */
    public String getFamilyName()
    {
        return this.familyName;
    }

    /**
     * @return middle name(s)
     */
    public String getMiddleName()
    {
        return this.middleName;
    }

    /**
     * @return user's street (including house name/number}
     */
    public String getStreetAddress()
    {
        return this.streetAddress;
    }

    /**
     * @return user's city
     */
    public String getCity()
    {
        return this.city;
    }

    /**
     * @return user's state/county
     */
    public String getState()
    {
        return this.state;
    }

    /**
     * @return user's zip/postal code
     */
    public String getPostalCode()
    {
        return this.postalCode;
    }

    /**
     * @return user's country
     */
    public String getCountry()
    {
        return this.country;
    }

    /**
     * @return user's email address
     */
    public String getEmail()
    {
        return this.email;
    }

    /**
     * @return user's Mobile Connect designated mobile number
     */
    public String getPhoneNumber()
    {
        return this.phoneNumber;
    }

    /**
     * @return user's birthdate
     */
    public String getBirthdate()
    {
        return this.birthdate;
    }

    /**
     * @return user's identifier (eIDAS), any national identifier such as Social Security
     * Identifier, passport etc. (depends on the local regulations)
     */
    public String getNationalIdentifier()
    {
        return this.nationalIdentifier;
    }


    public static final class Builder implements IBuilder<IdentityData>
    {
        private String sub = null;
        private String phoneNumberAlternate = null;
        private String title = null;
        private String givenName = null;
        private String familyName = null;
        private String middleName = null;
        private String streetAddress = null;
        private String city = null;
        private String state = null;
        private String postalCode = null;
        private String country = null;
        private String email = null;
        private String phoneNumber = null;
        private String birthdate = null;
        private String nationalIdentifier = null;

        public Builder withSub(String val)
        {
            this.sub = val;
            return this;
        }

        public Builder withPhoneNumberAlternate(String val)
        {
            this.phoneNumberAlternate = val;
            return this;
        }

        public Builder withTitle(String val)
        {
            this.title = val;
            return this;
        }

        public Builder withGivenName(String val)
        {
            this.givenName = val;
            return this;
        }

        public Builder withFamilyName(String val)
        {
            this.familyName = val;
            return this;
        }

        public Builder withMiddleName(String val)
        {
            this.middleName = val;
            return this;
        }

        public Builder withStreetAddress(String val)
        {
            this.streetAddress = val;
            return this;
        }

        public Builder withCity(String val)
        {
            this.city = val;
            return this;
        }

        public Builder withState(String val)
        {
            this.state = val;
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

        public Builder withEmail(String val)
        {
            this.email = val;
            return this;
        }

        public Builder withPhoneNumber(String val)
        {
            this.phoneNumber = val;
            return this;
        }

        public Builder withBirthdate(String val)
        {
            this.birthdate = val;
            return this;
        }

        public Builder withNationalIdentifier(String val)
        {
            this.nationalIdentifier = val;
            return this;
        }

        @Override
        public IdentityData build()
        {
            return new IdentityData(this);
        }
    }
}
