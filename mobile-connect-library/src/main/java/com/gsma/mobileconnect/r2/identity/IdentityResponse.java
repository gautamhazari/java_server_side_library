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

import com.gsma.mobileconnect.r2.ErrorResponse;
import com.gsma.mobileconnect.r2.encoding.IMobileConnectEncodeDecoder;
import com.gsma.mobileconnect.r2.json.IJsonService;
import com.gsma.mobileconnect.r2.json.JsonDeserializationException;
import com.gsma.mobileconnect.r2.rest.RestResponse;
import com.gsma.mobileconnect.r2.utils.HttpUtils;
import com.gsma.mobileconnect.r2.utils.IBuilder;
import com.gsma.mobileconnect.r2.utils.JsonWebTokens;
import com.gsma.mobileconnect.r2.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * Class to hold identity response.
 *
 * @since 2.0
 */
@SuppressWarnings("WeakerAccess")
public class IdentityResponse
{
    private static final Logger LOGGER = LoggerFactory.getLogger(IdentityResponse.class);

    private static final Pattern ERROR_REGEX = Pattern.compile("\\\"error\\\":");
    private final int responseCode;
    private final ErrorResponse errorResponse;
    private final String responseJson;
    private Object convertedResponseData;

    private IdentityResponse(Builder builder)
    {
        this.responseCode = builder.responseCode;
        this.errorResponse = builder.errorResponse;
        this.responseJson = builder.responseJson;
    }

    /**
     * Create a new instance of {@link IdentityResponse} from json content of a {@link RestResponse}
     *
     * @param restResponse to convert to {@link IdentityResponse}
     * @param jsonService  to perform deserialization.
     * @return IdentityResponse instance.
     */
    public static IdentityResponse fromRestResponse(final RestResponse restResponse,
        final IJsonService jsonService,
        final IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder)
    {
        final Builder builder = new Builder().withResponseCode(restResponse.getStatusCode());

        if (!HttpUtils.isHttpErrorCode(restResponse.getStatusCode()))
        {
            final String json = extractJson(restResponse.getContent(), iMobileConnectEncodeDecoder);
            builder.withResponseJson(json);

            if (!StringUtils.isNullOrEmpty(json) && ERROR_REGEX.matcher(json).find())
            {
                try
                {
                    builder.withErrorResponse(jsonService.deserialize(json, ErrorResponse.class));
                }
                catch (final JsonDeserializationException jde)
                {
                    LOGGER.warn("Error parsing error identity response", jde);
                }
            }
        }
        else
        {
            builder.withErrorResponse(
                HttpUtils.generateAuthenticationError(restResponse.getHeaders()));
        }

        return builder.build();
    }

    private static String extractJson(String responseJson,
        final IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder)
    {
        if (StringUtils.isNullOrEmpty(responseJson) || responseJson.indexOf('{') > -1)
        {
            return responseJson;
        }
        else if (JsonWebTokens.isValidFormat(responseJson))
        {
            return JsonWebTokens.Part.CLAIMS.decode(responseJson, iMobileConnectEncodeDecoder);
        }
        else
        {
            return "{\"error\":\"invalid_format\",\"error_description\":\"Received UserInfo response that is not JSON or JWT format\"}";
        }
    }

    /**
     * @return the HTTP response code or 0 if the data is cached.
     */
    public int getResponseCode()
    {
        return this.responseCode;
    }

    /**
     * @return the response if the network request returned an error.
     */
    public ErrorResponse getErrorResponse()
    {
        return this.errorResponse;
    }

    /**
     * @return the parsed json response data.
     */
    public String getResponseJson()
    {
        return this.responseJson;
    }

    /**
     * Attempts to convert the responseJson held by this instance to the identified class.
     *
     * @param clazz       to convert to.
     * @param jsonService to perform the conversion.
     * @param <T>         the type of the class.
     * @return the converted instance.  This may be cached if this is a subsequent call to this
     * method.
     * @throws JsonDeserializationException if the jsonService was unable to error the content to an
     *                                      instance of the requested class.
     */
    public <T> T getResponseAs(final Class<T> clazz, final IJsonService jsonService)
        throws JsonDeserializationException
    {
        if (this.convertedResponseData == null || this.convertedResponseData.getClass() != clazz)
        {
            this.convertedResponseData = jsonService.deserialize(this.responseJson, clazz);
        }
        return clazz.cast(this.convertedResponseData);
    }

    public static final class Builder implements IBuilder<IdentityResponse>
    {
        private int responseCode = 0;
        private ErrorResponse errorResponse = null;
        private String responseJson = null;

        public Builder withResponseCode(final int val)
        {
            this.responseCode = val;
            return this;
        }

        public Builder withErrorResponse(final ErrorResponse val)
        {
            this.errorResponse = val;
            return this;
        }

        public Builder withResponseJson(final String val)
        {
            this.responseJson = val;
            return this;
        }

        @Override
        public IdentityResponse build()
        {
            return new IdentityResponse(this);
        }
    }
}
