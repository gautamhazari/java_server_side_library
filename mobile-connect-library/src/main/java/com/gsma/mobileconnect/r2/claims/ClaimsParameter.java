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
@JsonDeserialize(builder = ClaimsParameter.Builder.class)
public class ClaimsParameter
{
    private final Claims userinfo;
    private final Claims idToken;

    private ClaimsParameter(Builder builder)
    {
        this.userinfo = builder.userinfo;
        this.idToken = builder.idToken;
    }

    public Claims getUserinfo()
    {
        return this.userinfo;
    }

    public Claims getIdToken()
    {
        return this.idToken;
    }

    /**
     * @return true if no claims will be requested using this claims parameter.
     */
    public boolean isEmpty()
    {
        return (this.userinfo == null || this.userinfo.isEmpty()) && (this.idToken == null
            || this.idToken.isEmpty());
    }

    public static final class Builder implements IBuilder<ClaimsParameter>
    {
        private Claims userinfo = null;
        private Claims idToken = null;

        public Builder withUserinfo(final Claims val)
        {
            this.userinfo = val;
            return this;
        }

        public Builder withUserinfo(final Claims.Builder val)
        {
            this.userinfo = val.build();
            return this;
        }

        public Builder withIdToken(final Claims val)
        {
            this.idToken = val;
            return this;
        }

        public Builder withIdToken(final Claims.Builder val)
        {
            this.idToken = val.build();
            return this;
        }

        @Override
        public ClaimsParameter build()
        {
            return new ClaimsParameter(this);
        }
    }
}
