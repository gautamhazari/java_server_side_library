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
package com.gsma.mobileconnect.r2.model;

import com.google.gson.annotations.SerializedName;
import com.gsma.mobileconnect.r2.utils.IBuilder;
import com.gsma.mobileconnect.r2.utils.StringUtils;

/**
 * Class to hold a Rest error response.
 *
 * @since 2.0
 */
public class ErrorResponse
{
    private final String error;
    @SerializedName("error_description")
    private final String errorDescription;
    private final String description;
    @SerializedName("error_uri")
    private final String errorUri;
    @SerializedName("correlation_id")
    private final String correlationId;

    private ErrorResponse(Builder builder)
    {
        this.error = StringUtils.requireNonEmpty(builder.error, "error");
        this.errorDescription = builder.errorDescription;
        this.description = builder.description;
        this.errorUri = builder.errorUri;
        this.correlationId = builder.correlationId;
    }

    /**
     * @return the error code.
     */
    public String getError()
    {
        return this.error;
    }

    /**
     * @return the error description.
     */
    public String getErrorDescription()
    {
        return this.errorDescription == null ? this.description : this.errorDescription;
    }

    /**
     * @return the error URI.
     */
    public String getErrorUri()
    {
        return this.errorUri;
    }

    /**
     * @return the error correlationId.
     */
    public String getCorrelationId () {
        return this.correlationId;
    }

    @Override
    public String toString()
    {
        return "ErrorResponse(code="
            + this.error
            + ", description="
            + this.errorDescription
            + ", url="
            + this.errorUri
            + ", correlation id="
            + this.correlationId
            + ")";
    }

    public static final class Builder implements IBuilder<ErrorResponse>
    {
        private String error;
        private String errorDescription;
        private String description;
        private String errorUri;
        private String correlationId;

        public Builder withError(final String val)
        {
            this.error = val;
            return this;
        }

        public Builder withErrorDescription(final String val)
        {
            this.errorDescription = val;
            return this;
        }

        public Builder withDescription(final String val)
        {
            this.description = val;
            return this;
        }

        public Builder withErrorUri(final String val)
        {
            this.errorUri = val;
            return this;
        }

        public Builder withCorrelationId(final String val)
        {
            this.correlationId = val;
            return this;
        }

        @Override
        public ErrorResponse build()
        {
            return new ErrorResponse(this);
        }
    }
}
