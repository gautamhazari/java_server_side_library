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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gsma.mobileconnect.r2.utils.IBuilder;

/**
 * Class representing a single claim to be requested.
 *
 * @since 2.0
 */
@JsonDeserialize(builder = ClaimsValue.Builder.class)
public class ClaimsValue
{
    @JsonProperty private final Boolean essential;
    private final Object value;
    private final Object[] values;

    private ClaimsValue(Builder builder)
    {
        this.essential = Boolean.TRUE.equals(builder.essential) ? Boolean.TRUE : null;
        this.value = builder.value;
        this.values = builder.values;
    }

    /**
     * @return true if the claim is essential.
     */
    @JsonIgnore
    public boolean isEssential()
    {
        return this.essential != null;
    }

    /**
     * @return the value held by this claim.
     */
    public Object getValue()
    {
        return this.value;
    }

    /**
     * @return the values held by this claim.
     */
    public Object[] getValues()
    {
        return this.values;
    }

    public static final class Builder implements IBuilder<ClaimsValue>
    {
        private Boolean essential = null;
        private Object value = null;
        private Object[] values = null;

        public Builder withEssential(Boolean val)
        {
            this.essential = val;
            return this;
        }

        public Builder withValue(Object val)
        {
            this.value = val;
            return this;
        }

        public Builder withValues(Object... val)
        {
            this.values = val;
            return this;
        }

        @Override
        public ClaimsValue build()
        {
            if (this.value != null && this.values != null)
            {
                throw new IllegalStateException("Both value and values cannot be set");
            }

            return new ClaimsValue(this);
        }
    }
}
