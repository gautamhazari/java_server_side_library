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
package com.gsma.mobileconnect.r2.identity;

import com.google.gson.annotations.SerializedName;
import com.gsma.mobileconnect.r2.json.IJsonService;
import com.gsma.mobileconnect.r2.utils.IBuilder;

/**
 * Class containing properties for all available openid connect 1.0 UserInfo claims, can be used to
 * retrieve {@link IdentityResponse#getResponseJson()} as a concrete object. Use the {@link
 * IdentityResponse#getResponseAs(Class, IJsonService)} method with this type as the parameter
 * T. Alternatively a leaner type or type with additional custom properties can be provided for more
 * control over the deserialization process.
 *
 * @since 2.0
 */
public class UserInfoData
{
    private final String sub;

    // profile
    @SerializedName("name")
    private final String name;
    @SerializedName(value = "family_name", alternate = "familyName")
    private final String familyName;
    @SerializedName(value = "given_name", alternate = "givenName")
    private final String givenName;
    @SerializedName(value = "middle_name", alternate = "middleName")
    private final String middleName;
    @SerializedName("nickname")
    private final String nickname;
    @SerializedName(value = "preferred_username", alternate = "preferredUsername")
    private final String preferredUsername;
    @SerializedName("profile")
    private final String profile;
    @SerializedName("picture")
    private final String picture;
    @SerializedName("website")
    private final String website;
    @SerializedName("gender")
    private final String gender;
    @SerializedName("birthdate")
    private final String birthdate;
    @SerializedName("zoneinfo")
    private final String zoneinfo;
    @SerializedName("locale")
    private final String locale;
    @SerializedName(value = "updated_at" ,alternate = "updatedAt")
    private final Long updatedAt;

    // email
    @SerializedName("email")
    private final String email;
    @SerializedName(value = "email_verified", alternate = "emailVerified")
    private final Boolean emailVerified;

    // address
    @SerializedName("address")
    private final AddressData address;

    // phone
    @SerializedName(value = "phone_number", alternate = "phoneNumber")
    private final String phoneNumber;
    @SerializedName(value = "phone_number_verified", alternate = "phoneNumberVerified")
    private final Boolean phoneNumberVerified;

    private UserInfoData(Builder builder)
    {
        this.sub = builder.sub;
        this.name = builder.name;
        this.familyName = builder.familyName;
        this.givenName = builder.givenName;
        this.middleName = builder.middleName;
        this.nickname = builder.nickname;
        this.preferredUsername = builder.preferredUsername;
        this.profile = builder.profile;
        this.picture = builder.picture;
        this.website = builder.website;
        this.gender = builder.gender;
        this.birthdate = builder.birthdate;
        this.zoneinfo = builder.zoneinfo;
        this.locale = builder.locale;
        this.updatedAt = builder.updatedAt;
        this.email = builder.email;
        this.emailVerified = builder.emailVerified;
        this.address = builder.address;
        this.phoneNumber = builder.phoneNumber;
        this.phoneNumberVerified = builder.phoneNumberVerified;
    }

    /**
     * @return Subject - Identifier for the End-User at the Issuer
     */
    public String getSub()
    {
        return this.sub;
    }

    /**
     * @return End-User's full name in a displayable form including all name parts, possibly
     * including titles and suffixes, ordered according to the End-User's locale and preferences.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return Surname(s) or last name(s) of the End-User. Note that in some cultures, people can
     * have multiple family names or no family name; all can be present with the names being
     * separated by space characters
     */
    public String getFamilyName()
    {
        return this.familyName;
    }

    /**
     * @return Given name(s) or first name(s) of the End-User. Note that in some cultures, people
     * can have multiple given names; all can be present, with the names being separated by space
     * characters
     */
    public String getGivenName()
    {
        return this.givenName;
    }

    /**
     * @return Middle name(s) of the End-User. Note that in some cultures, people can have multiple
     * middle names; all can be present, with the names being separated by space characters
     */
    public String getMiddleName()
    {
        return this.middleName;
    }

    /**
     * @return Casual name of the End-User that may or may not be the same as the
     * {@link UserInfoData#givenName}. For instance a Nickname value of Mike may return alongside a
     * {@link #getGivenName()} of Michael
     */
    public String getNickname()
    {
        return this.nickname;
    }

    /**
     * @return Shorthand name by which the End-User wishes to be referred to at the RP, such as
     * janedoe or j.doe. This value MAY be any valid JSON string including special characters such
     * as @, /, or whitespace. The RP MUST NOT rely upon this value being unique, as discussed in
     * open-id-connect-core-1_0 Section 5.7
     */
    public String getPreferredUsername()
    {
        return this.preferredUsername;
    }

    /**
     * @return URL of the End-User's profile page. The contents of this Web page SHOULD be about the
     * End-User.
     */
    public String getProfile()
    {
        return this.profile;
    }

    /**
     * @return URL of the End-User's profile picture. This URL MUST refer to an image file (for
     * example, a PNG, JPEG, or GIF image file), rather than to a Web page containing an image. Note
     * that this URL SHOULD specifically reference a profile photo of the End-User suitable for
     * displaying when describing the End-User, rather than an arbitrary photo taken by the
     * End-User.
     */
    public String getPicture()
    {
        return this.picture;
    }

    /**
     * @return URL of the End-User's Web page or blog. This Web page SHOULD contain information
     * published by the End-User or an organization that the End-User is affiliated with.
     */
    public String getWebsite()
    {
        return this.website;
    }

    /**
     * @return End-User's gender. Values defined by this specification are female and male. Other
     * values MAY be used when neither of the defined values are applicable.
     */
    public String getGender()
    {
        return this.gender;
    }

    /**
     * @return End-User's birthday
     */
    public String getBirthdate()
    {
        return this.birthdate;
    }

    /**
     * @return String from zoneinfo time zone database representing the End-User's time zone. For
     * example, Europe/Paris or America/Los_Angeles.
     */
    public String getZoneinfo()
    {
        return this.zoneinfo;
    }

    /**
     * @return End-User's locale, represented as a BCP47 [RFC5646] language tag. This is typically
     * an ISO 639-1 Alpha-2 [ISO639‑1] language code in lowercase and an ISO 3166-1 Alpha-2
     * [ISO3166‑1] country code in uppercase, separated by a dash. For example, en-US or fr-CA. As a
     * compatibility note, some implementations have used an underscore as the separator rather than
     * a dash, for example, en_US; Relying Parties MAY choose to accept this locale syntax as well.
     */
    public String getLocale()
    {
        return this.locale;
    }

    /**
     * @return Time the End-User's information was last updated in seconds since 1st Jan 1970.
     */
    public Long getUpdatedAt()
    {
        return this.updatedAt;
    }

    /**
     * @return End-User's preferred e-mail address. Its value MUST conform to the RFC 5322 [RFC5322]
     * addr-spec syntax. The RP MUST NOT rely upon this value being unique, as discussed in
     * open-id-connect-core-1_0 Section 5.7
     */
    public String getEmail()
    {
        return this.email;
    }

    /**
     * @return True if the End-User's e-mail address has been verified; otherwise false. When this
     * Claim Value is true, this means that the OP took affirmative steps to ensure that this e-mail
     * address was controlled by the End-User at the time the verification was performed. The means
     * by which an e-mail address is verified is context-specific, and dependent upon the trust
     * framework or contractual agreements within which the parties are operating.
     */
    public Boolean getEmailVerified()
    {
        return this.emailVerified;
    }

    /**
     * @return End-User's preferred postal address.
     */
    public AddressData getAddress()
    {
        return this.address;
    }

    /**
     * @return End-User's preferred telephone number. E.164 [E.164] is RECOMMENDED as the format of
     * this Claim, for example, +1 (425) 555-1212 or +56 (2) 687 2400. If the phone number contains
     * an extension, it is RECOMMENDED that the extension be represented using the RFC 3966
     * [RFC3966] extension syntax, for example, +1 (604) 555-1234;ext=5678.
     */
    public String getPhoneNumber()
    {
        return this.phoneNumber;
    }

    /**
     * @return True if the End-User's phone number has been verified; otherwise false. When this
     * Claim Value is true, this means that the OP took affirmative steps to ensure that this phone
     * number was controlled by the End-User at the time the verification was performed. The means
     * by which a phone number is verified is context-specific, and dependent upon the trust
     * framework or contractual agreements within which the parties are operating. When true, the
     * phone_number Claim MUST be in E.164 format and any extensions MUST be represented in RFC 3966
     * format.
     */
    public Boolean getPhoneNumberVerified()
    {
        return this.phoneNumberVerified;
    }

    public static final class Builder implements IBuilder<UserInfoData>
    {
        private String sub;
        private String name;
        private String familyName;
        private String givenName;
        private String middleName;
        private String nickname;
        private String preferredUsername;
        private String profile;
        private String picture;
        private String website;
        private String gender;
        private String birthdate;
        private String zoneinfo;
        private String locale;
        private Long updatedAt;
        private String email;
        private Boolean emailVerified;
        private AddressData address;
        private String phoneNumber;
        private Boolean phoneNumberVerified;

        public Builder withSub(String val)
        {
            this.sub = val;
            return this;
        }

        public Builder withName(String val)
        {
            this.name = val;
            return this;
        }

        public Builder withFamilyName(String val)
        {
            this.familyName = val;
            return this;
        }

        public Builder withGivenName(String val)
        {
            this.givenName = val;
            return this;
        }

        public Builder withMiddleName(String val)
        {
            this.middleName = val;
            return this;
        }

        public Builder withNickname(String val)
        {
            this.nickname = val;
            return this;
        }

        public Builder withPreferredUsername(String val)
        {
            this.preferredUsername = val;
            return this;
        }

        public Builder withProfile(String val)
        {
            this.profile = val;
            return this;
        }

        public Builder withPicture(String val)
        {
            this.picture = val;
            return this;
        }

        public Builder withWebsite(String val)
        {
            this.website = val;
            return this;
        }

        public Builder withGender(String val)
        {
            this.gender = val;
            return this;
        }

        public Builder withBirthdate(String val)
        {
            this.birthdate = val;
            return this;
        }

        public Builder withZoneinfo(String val)
        {
            this.zoneinfo = val;
            return this;
        }

        public Builder withLocale(String val)
        {
            this.locale = val;
            return this;
        }

        public Builder withUpdatedAt(Long val)
        {
            this.updatedAt = val;
            return this;
        }

        public Builder withEmail(String val)
        {
            this.email = val;
            return this;
        }

        public Builder withEmailVerified(Boolean val)
        {
            this.emailVerified = val;
            return this;
        }

        public Builder withAddress(AddressData val)
        {
            this.address = val;
            return this;
        }

        public Builder withPhoneNumber(String val)
        {
            this.phoneNumber = val;
            return this;
        }

        public Builder withPhoneNumberVerified(Boolean val)
        {
            this.phoneNumberVerified = val;
            return this;
        }

        @Override
        public UserInfoData build()
        {
            return new UserInfoData(this);
        }
    }
}
