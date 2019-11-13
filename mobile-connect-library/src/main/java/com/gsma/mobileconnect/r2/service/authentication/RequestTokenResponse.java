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
package com.gsma.mobileconnect.r2.service.authentication;

import com.gsma.mobileconnect.r2.model.ErrorResponse;
import com.gsma.mobileconnect.r2.model.exceptions.InvalidResponseException;
import com.gsma.mobileconnect.r2.utils.encoding.IMobileConnectEncodeDecoder;
import com.gsma.mobileconnect.r2.model.json.IJsonService;
import com.gsma.mobileconnect.r2.model.json.JsonDeserializationException;
import com.gsma.mobileconnect.r2.web.rest.RestResponse;
import com.gsma.mobileconnect.r2.utils.*;

import java.net.URI;
import java.util.List;

/**
 * Holds the response of
 * {@link IAuthenticationService#requestTokenAsync(String, String, String, URI, URI, String)}
 *
 * @since 2.0
 */
public class RequestTokenResponse
{
    private final int responseCode;
    private final List<KeyValuePair> headers;
    private final RequestTokenResponseData responseData;
    private final String decodedIdTokenPayload;
    private final ErrorResponse errorResponse;
    private final boolean tokenValidated;

    private RequestTokenResponse(Builder builder)
    {
        this.responseCode = builder.responseCode;
        this.headers = builder.headers;
        this.responseData = builder.responseData;
        this.decodedIdTokenPayload = builder.decodedIdTokenPayload;
        this.errorResponse = builder.errorResponse;
        this.tokenValidated = builder.tokenValidated;
    }

    /**
     * Creates an instance from the specified rest response.
     *
     * @param restResponse to consume.
     * @param jsonService  to perform deserialization of rest response content.
     * @return instance of RequestTokenResponse.
     * @throws InvalidResponseException if the json content could not be translated.
     */
    public static RequestTokenResponse fromRestResponse(final RestResponse restResponse,
        final IJsonService jsonService,
        final IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder)
        throws InvalidResponseException
    {
        ObjectUtils.requireNonNull(restResponse, "restResponse");
        ObjectUtils.requireNonNull(jsonService, "jsonService");

        final Builder builder = new Builder()
            .withResponseCode(restResponse.getStatusCode())
            .withHeaders(restResponse.getHeaders());

        try
        {
            if (HttpUtils.isHttpErrorCode(restResponse.getStatusCode()))
            {
                builder.withErrorResponse(
                    jsonService.deserialize(restResponse.getContent(), ErrorResponse.class));
            }
            else
            {
                final RequestTokenResponseData data =
                    jsonService.deserialize(restResponse.getContent(),
                        RequestTokenResponseData.class);

                builder
                    .withResponseData(data)
                    .withDecodedIdTokenPayload(JsonWebTokens.Part.CLAIMS.decode(data.getIdToken(),
                        iMobileConnectEncodeDecoder));
            }
        }
        catch (final JsonDeserializationException jde)
        {
            throw new InvalidResponseException(restResponse, RequestTokenResponse.class, jde);
        }

        return builder.build();
    }

    /**
     * @return the HTTP response code returned by the next request.
     */
    public int getResponseCode()
    {
        return this.responseCode;
    }

    /**
     * @return the list of HTTP headers returned with the response.
     */

    public List<KeyValuePair> getHeaders()
    {
        return this.headers;
    }

    /**
     * @return the response if the request was not in error.
     */
    public RequestTokenResponseData getResponseData()
    {
        return this.responseData;
    }

    /**
     * @return decoded JWT payload from IdToken in standard JSON string format.
     */
    public String getDecodedIdTokenPayload()
    {
        return this.decodedIdTokenPayload;
    }

    /**
     * @return the error response if the HTTP responded with error.
     */
    public ErrorResponse getErrorResponse()
    {
        return this.errorResponse;
    }

    /**
     * @return the flag to indicate whether the token is validated or not
     */
    public boolean isTokenValidated()
    {
        return this.tokenValidated;
    }

    public static final class Builder implements IBuilder<RequestTokenResponse>
    {
        private int responseCode;
        private List<KeyValuePair> headers;
        private RequestTokenResponseData responseData;
        private String decodedIdTokenPayload;
        private ErrorResponse errorResponse;
        private boolean tokenValidated = false;

        public Builder()
        {
            /*
            Default Constructor with no parameters
             */
        }

        public Builder(final RequestTokenResponse requestTokenResponse)
        {
            this.responseCode = requestTokenResponse.responseCode;
            this.headers = requestTokenResponse.headers;
            this.responseData = requestTokenResponse.responseData;
            this.decodedIdTokenPayload = requestTokenResponse.decodedIdTokenPayload;
            this.errorResponse = requestTokenResponse.errorResponse;
            this.tokenValidated = requestTokenResponse.tokenValidated;
        }

        public Builder withResponseCode(final int val)
        {
            this.responseCode = val;
            return this;
        }

        public Builder withHeaders(final List<KeyValuePair> val)
        {
            this.headers = val;
            return this;
        }

        public Builder withResponseData(final RequestTokenResponseData val)
        {
            this.responseData = val;
            return this;
        }

        public Builder withDecodedIdTokenPayload(final String val)
        {
            this.decodedIdTokenPayload = val;
            return this;
        }

        public Builder withErrorResponse(final ErrorResponse val)
        {
            this.errorResponse = val;
            return this;
        }

        public Builder withTokenValidated(final boolean val)
        {
            this.tokenValidated = val;
            return this;
        }

        @Override
        public RequestTokenResponse build()
        {
            return new RequestTokenResponse(this);
        }
    }
}
