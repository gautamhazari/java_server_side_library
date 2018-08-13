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

import com.gsma.mobileconnect.r2.utils.IBuilder;
import com.gsma.mobileconnect.r2.utils.StringUtils;

/**
 * Class to hold details parsed from the discovery redirect
 *
 * @since 2.0
 */
public class ParsedDiscoveryRedirect
{
    private final String selectedMcc;
    private final String selectedMnc;
    private final String encryptedMsisdn;

    private ParsedDiscoveryRedirect(Builder builder)
    {
        this.selectedMcc = builder.selectedMcc;
        this.selectedMnc = builder.selectedMnc;
        this.encryptedMsisdn = builder.encryptedMsisdn;
    }

    /**
     * @return The Mobile Country Code of the selected operator
     */
    public String getSelectedMcc()
    {
        return this.selectedMcc;
    }

    /**
     * @return The encrypted MSISDN is specified
     */
    public String getSelectedMnc()
    {
        return this.selectedMnc;
    }

    /**
     * @return Returns true if data exists for MCC and MNC
     */
    public String getEncryptedMsisdn()
    {
        return this.encryptedMsisdn;
    }

    /**
     * @return true if data exists for both the MCC and MNC.
     */
    public boolean hasMccAndMnc()
    {
        return !StringUtils.isNullOrEmpty(this.selectedMcc) && !StringUtils.isNullOrEmpty(
            this.selectedMnc);
    }

    public static final class Builder implements IBuilder<ParsedDiscoveryRedirect>
    {
        private String selectedMcc;
        private String selectedMnc;
        private String encryptedMsisdn;

        public Builder withSelectedMcc(final String val)
        {
            this.selectedMcc = val;
            return this;
        }

        public Builder withSelectedMnc(final String val)
        {
            this.selectedMnc = val;
            return this;
        }

        public Builder withEncryptedMsisdn(final String val)
        {
            this.encryptedMsisdn = val;
            return this;
        }

        @Override
        public ParsedDiscoveryRedirect build()
        {
            return new ParsedDiscoveryRedirect(this);
        }
    }
}
