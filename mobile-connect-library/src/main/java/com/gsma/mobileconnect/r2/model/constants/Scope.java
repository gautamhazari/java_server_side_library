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
package com.gsma.mobileconnect.r2.model.constants;

/**
 * @since 2.0
 */
public class Scope
{
    public static final String OPENID = "openid";
    public static final String AUTHN = "mc_authn";
    public static final String AUTHZ = "mc_authz";

    public static final String MC_PREFIX = "mc_";

    public static final String PROFILE = "profile";
    public static final String EMAIL = "email";
    public static final String ADDRESS = "address";
    public static final String PHONE = "phone";
    public static final String OFFLINE_ACCESS = "offline_access";

    public static final String IDENTITY = "mc_identity";
    public static final String IDENTITY_PHONE = "mc_identity_phonenumber";
    public static final String IDENTITY_SIGNUP = "mc_identity_signup";
    public static final String IDENTITY_SIGNUPPLUS = "mc_identity_signupplus";
    public static final String IDENTITY_NATIONALID = "mc_identity_nationalid";

    public static final String MC_PHONE = "mc_phonenumber";
    public static final String MC_SIGNUP = "mc_signup";
    public static final String MC_NATIONALID = "mc_nationalid";

    public static final String KYC_PLAIN = "mc_kyc_plain";
    public static final String KYC_HASHED = "mc_kyc_hashed";
    public static final String ATTR_VM_MATCH = "mc_attr_vm_match";
    public static final String ATTR_VM_MATCH_HASH = "mc_attr_vm_match_hash";

    public static final String MC_INDIA_TC = "mc_india_tc";
    public static final String MC_MNV_VALIDATE = "mc_mnv_validate";
    public static final String MC_MNV_VALIDATE_PLUS = "mc_mnv_validate_plus";
    public static final String MC_ATTR_VM_SHARE = "mc_attr_vm_share";
    public static final String MC_ATTR_VM_SHARE_HASH = "mc_attr_vm_share_hash";



    private Scope()
    {
        /*
        Private default constructor
         */
    }
}
