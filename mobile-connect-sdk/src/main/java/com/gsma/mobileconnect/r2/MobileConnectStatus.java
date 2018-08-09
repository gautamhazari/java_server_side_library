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
package com.gsma.mobileconnect.r2;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gsma.mobileconnect.r2.authentication.IAuthenticationService;
import com.gsma.mobileconnect.r2.authentication.RequestTokenResponse;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.discovery.IDiscoveryService;
import com.gsma.mobileconnect.r2.identity.IdentityResponse;
import com.gsma.mobileconnect.r2.utils.*;

import java.util.List;

/**
 * Object to hold the details of a response returned from @see {@link MobileConnectInterface} and
 * {@link MobileConnectWebInterface} all information required to continue the process is included
 *
 * @since 2.0
 */
@SuppressWarnings("WeakerAccess")
@JsonDeserialize(builder = MobileConnectStatus.Builder.class)
public class MobileConnectStatus
{
    public static final String INTERNAL_ERROR_CODE = "internal error";
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_RESPONSE = "response";

    private final ResponseType responseType;
    private final String errorCode;
    private final String errorMessage;
    private final String outcome;
    private final String url;
    private final String state;
    private final String nonce;
    private final List<String> setCookie;
    private final String sdkSession;
    private final DiscoveryResponse discoveryResponse;
    private final RequestTokenResponse requestTokenResponse;
    private final IdentityResponse identityResponse;
    private final Exception exception;

    private MobileConnectStatus(final Builder builder)
    {
        this.responseType = builder.responseType;
        this.errorCode = builder.errorCode;
        this.errorMessage = builder.errorMessage;
        this.outcome = builder.outcome;
        this.url = builder.url;
        this.state = builder.state;
        this.nonce = builder.nonce;
        this.setCookie = builder.setCookie;
        this.sdkSession = builder.sdkSession;
        this.discoveryResponse = builder.discoveryResponse;
        this.requestTokenResponse = builder.requestTokenResponse;
        this.identityResponse = builder.identityResponse;
        this.exception = builder.exception;
    }

    /**
     * Creates a Status with ResponseType error and error related properties filled.
     * Indicates that the MobileConnect process has been aborted due to an issue encountered.
     *
     * @param error   error code
     * @param message user friendly error message
     * @param e       exception encountered
     * @return MobileConnectStatus with {@link ResponseType#ERROR}
     */
    public static MobileConnectStatus error(final String error, final String message,
        final Exception e)
    {
        return new Builder()
            .withErrorCode(ObjectUtils.defaultIfNull(error, INTERNAL_ERROR_CODE))
            .withErrorMessage(StringUtils.requireNonEmpty(message, ARG_MESSAGE))
            .withException(e)
            .withResponseType(ResponseType.ERROR)
            .build();

    }

    /**
     * Creates a Status with ResponseType error and error related properties filled.
     * Indicates that the MobileConnect process has been aborted due to an issue encountered.
     *
     * @param error    error code
     * @param message  user friendly error message
     * @param e        exception encountered
     * @param response discovery response if returned from {@link IDiscoveryService}
     * @return MobileConnectStatus with {@link ResponseType#ERROR}
     */
    public static MobileConnectStatus error(final String error, final String message,
        final Exception e, final DiscoveryResponse response)
    {
        return new Builder()
            .withErrorCode(ObjectUtils.defaultIfNull(error, INTERNAL_ERROR_CODE))
            .withErrorMessage(StringUtils.requireNonEmpty(message, ARG_MESSAGE))
            .withException(e)
            .withDiscoveryResponse(ObjectUtils.requireNonNull(response, "discoveryResponse"))
            .withResponseType(ResponseType.ERROR)
            .build();
    }

    /**
     * Creates a Status with ResponseType error and error related properties filled.
     * Indicates that the MobileConnect process has been aborted due to an issue encountered.
     *
     * @param error    error code
     * @param message  user friendly error message
     * @param e        exception encountered
     * @param response request token response if returned from {@link IAuthenticationService}
     * @return MobileConnectStatus with {@link ResponseType#ERROR}
     */
    public static MobileConnectStatus error(final String error, final String message,
        final Exception e, final RequestTokenResponse response)
    {
        return new Builder()
            .withErrorCode(ObjectUtils.defaultIfNull(error, INTERNAL_ERROR_CODE))
            .withErrorMessage(StringUtils.requireNonEmpty(message, ARG_MESSAGE))
            .withException(e)
            .withRequestTokenResponse(ObjectUtils.requireNonNull(response, ARG_RESPONSE))
            .withResponseType(ResponseType.ERROR)
            .build();
    }

    /**
     * Creates a Status with ResponseType OperatorSelection and url for next process step.
     * Indicates that the next step should be navigating to the operator selection URL.
     *
     * @param url operator selection url returned from {@link IDiscoveryService}
     * @return MobileConnectStatus with {@link ResponseType#OPERATOR_SELECTION}
     */
    public static MobileConnectStatus operatorSelection(final String url)
    {
        return new Builder()
            .withUrl(StringUtils.requireNonEmpty(url, "url"))
            .withResponseType(ResponseType.OPERATOR_SELECTION)
            .build();
    }

    /**
     * Creates a Status with ResponseType#START_AUTHENTICATION and the complete {@link
     * DiscoveryResponse}. Indicates that the next step should be starting authorization.
     *
     * @param response DiscoveryResponse returned from {@link IDiscoveryService}
     * @return MobileConnectStatus with {@link ResponseType#START_AUTHENTICATION}
     */
    public static MobileConnectStatus startAuthentication(final DiscoveryResponse response)
    {
        ObjectUtils.requireNonNull(response, ARG_RESPONSE);

        final List<String> cookies = HttpUtils.extractCookiesFromHeaders(response.getHeaders());

        return new Builder()
            .withDiscoveryResponse(response)
            .withSetCookie(cookies)
            .withResponseType(ResponseType.START_AUTHENTICATION)
            .build();
    }

    /**
     * Creates a Status with ResponseType#START_DISCOVERY. Indicates that some required data was
     * missing and the discovery process needs to be restarted.
     *
     * @return MobileConnectStatus with {@link ResponseType#START_DISCOVERY}
     */
    public static MobileConnectStatus startDiscovery()
    {
        return new Builder().withResponseType(ResponseType.START_DISCOVERY).build();
    }

    /**
     * Creates a Status with ResponseType#AUTHENTICATION and url for next process step. Indicates
     * that the next step should be navigating to the Authorization URL.
     *
     * @param url   Url returned from {@link IAuthenticationService}
     * @param state The unique state string generated or passed in for the authorization url
     * @param nonce The unique nonce string generated or passed in for the authorization url
     * @return MobileConnectStatus with {@link ResponseType#AUTHENTICATION}
     */
    public static MobileConnectStatus authentication(final String url, final String state,
        final String nonce)
    {
        return new Builder()
            .withUrl(StringUtils.requireNonEmpty(url, "url"))
            .withState(StringUtils.requireNonEmpty(state, "state"))
            .withNonce(StringUtils.requireNonEmpty(nonce, "nonce"))
            .withResponseType(ResponseType.AUTHENTICATION)
            .build();
    }

    /**
     * Creates a status with ResponseType#COMPLETE and the complete {@link RequestTokenResponse}.
     * Indicates that a user info request has been successful.
     *
     * @param response RequestTokenResponse returned from {@link IAuthenticationService}
     * @return MobileConnectStatus with {@link ResponseType#COMPLETE}
     */
    public static MobileConnectStatus complete(final RequestTokenResponse response)
    {
        return new Builder()
            .withRequestTokenResponse(ObjectUtils.requireNonNull(response, ARG_RESPONSE))
            .withResponseType(ResponseType.COMPLETE)
            .build();
    }

    /**
     * Creates a status with ResponseType#COMPLETE and Indicates that a revoke token request has
     * been successful.
     *
     * @param operationOutcome The outcome of the operation if no RequestTokenResponse is to be
     *                         returned
     * @return MobileConnectStatus with {@link ResponseType#COMPLETE}
     */
    public static MobileConnectStatus complete(final String operationOutcome)
    {
        return new Builder()
            .withResponseType(ResponseType.COMPLETE)
            .withOutcome(operationOutcome)
            .build();
    }

    /**
     * Create a MobileConnectStatus instance describing an exception.
     *
     * @param task the task that was being run when the exception was being thrown.  This should be
     *             high level, for example "calling the discovery endpoint".
     * @param e    the exception that was thrown.
     * @return the instance of MobileConnectStatus
     */
    public static MobileConnectStatus error(final String task, final Exception e)
    {
        return error("unknown_error",
            String.format("An unknown error occurred while performing task '%s'", task), e);
    }

    /**
     * @return Type of response, indicates the step in the process that should be triggered next
     */
    public ResponseType getResponseType()
    {
        return this.responseType;
    }

    /**
     * @return Error code if included
     */
    public String getErrorCode()
    {
        return this.errorCode;
    }

    /**
     * @return User friendly error description if included.
     */
    public String getErrorMessage()
    {
        return this.errorMessage;
    }

    /**
     * @return URL to navigate to in the next step if required.
     */
    public String getUrl()
    {
        return this.url;
    }

    /**
     * @return state value used for authorization.
     */
    public String getState()
    {
        return this.state;
    }

    /**
     * @return nonce value used for authorization.
     */
    public String getNonce()
    {
        return this.nonce;
    }

    /**
     * @return Content of the Set-Cookie header returned in the response, should be used to proxy
     * cookies back to the user if required
     */
    public List<String> getSetCookie()
    {
        return this.setCookie;
    }

    /**
     * @return SDK session id used to link sessions to discovery responses when {@link
     * MobileConnectConfig#cacheResponsesWithSessionId} is set to true.
     */
    public String getSdkSession()
    {
        return this.sdkSession;
    }

    /**
     * @return complete discovery response if included.
     */
    public DiscoveryResponse getDiscoveryResponse()
    {
        return this.discoveryResponse;
    }

    /**
     * @return complete token response if included.
     */
    public RequestTokenResponse getRequestTokenResponse()
    {
        return this.requestTokenResponse;
    }

    /**
     * @return complete identity response if included.
     */
    public IdentityResponse getIdentityResponse()
    {
        return this.identityResponse;
    }

    /**
     * @return exception encountered during request if included.
     */
    public Exception getException()
    {
        return this.exception;
    }

    /**
     * @return Returns the outcome of the operation if no token is to be sent back
     */
    public String getOutcome()
    {
        return outcome;
    }

    /**
     * Create a copy of this status with the session set to the provided value.
     *
     * @param sdkSession to set.
     * @return copy of this status with the sdk session key set.
     */
    MobileConnectStatus withSdkSession(final String sdkSession)
    {
        return new Builder(this).withSdkSession(sdkSession).build();
    }

    /**
     * Enum of possible response types for {@link MobileConnectStatus}
     */
    public enum ResponseType
    {
        /**
         * ResponseType indicating Error was encountered
         */
        ERROR,

        /**
         * ResponseType indicating the next step should be OperatorSelection
         */
        OPERATOR_SELECTION,

        /**
         * ResponseType indicating the next step should be to restart Discovery
         */
        START_DISCOVERY,

        /**
         * ResponseType indicating the next step should be startAuthentication
         */
        START_AUTHENTICATION,

        /**
         * ResponseType indicating the next step should be Authentication
         */
        AUTHENTICATION,

        /**
         * ResponseType indicating completion of the MobileConnectProcess
         */
        COMPLETE,

        /**
         * ResponseType indicating userInfo has been received
         */
        USER_INFO,

        /**
         * ResponseType indicating identity has been received
         */
        IDENTITY
    }


    protected static final class Builder implements IBuilder<MobileConnectStatus>
    {
        private ResponseType responseType;
        private String errorCode;
        private String errorMessage;
        private String outcome;
        private String url;
        private String state;
        private String nonce;
        private List<String> setCookie;
        private String sdkSession;
        private DiscoveryResponse discoveryResponse;
        private RequestTokenResponse requestTokenResponse;
        private IdentityResponse identityResponse;
        private Exception exception;

        public Builder()
        {
            // default constructor
        }

        public Builder(final MobileConnectStatus status)
        {
            if (status != null)
            {
                this.responseType = status.responseType;
                this.errorCode = status.errorCode;
                this.errorMessage = status.errorMessage;
                this.url = status.url;
                this.state = status.state;
                this.nonce = status.nonce;
                this.setCookie = status.setCookie;
                this.sdkSession = status.sdkSession;
                this.discoveryResponse = status.discoveryResponse;
                this.requestTokenResponse = status.requestTokenResponse;
                this.identityResponse = status.identityResponse;
                this.exception = status.exception;
                this.outcome = status.outcome;
            }
        }

        public Builder withResponseType(ResponseType val)
        {
            this.responseType = val;
            return this;
        }

        public Builder withErrorCode(String val)
        {
            this.errorCode = val;
            return this;
        }

        public Builder withErrorMessage(String val)
        {
            this.errorMessage = val;
            return this;
        }

        public Builder withOutcome(String val)
        {
            this.outcome = val;
            return this;
        }

        public Builder withUrl(String val)
        {
            this.url = val;
            return this;
        }

        public Builder withState(String val)
        {
            this.state = val;
            return this;
        }

        public Builder withNonce(String val)
        {
            this.nonce = val;
            return this;
        }

        public Builder withSetCookie(List<String> val)
        {
            this.setCookie = ListUtils.immutableList(val);
            return this;
        }

        public Builder withSdkSession(String val)
        {
            this.sdkSession = val;
            return this;
        }

        public Builder withDiscoveryResponse(DiscoveryResponse val)
        {
            this.discoveryResponse = val;
            return this;
        }

        public Builder withRequestTokenResponse(RequestTokenResponse val)
        {
            this.requestTokenResponse = val;
            return this;
        }

        public Builder withIdentityResponse(IdentityResponse val)
        {
            this.identityResponse = val;
            return this;
        }

        public Builder withException(Exception val)
        {
            this.exception = val;
            return this;
        }

        @Override
        public MobileConnectStatus build()
        {
            ObjectUtils.requireNonNull(this.responseType, "responseType");

            return new MobileConnectStatus(this);
        }
    }
}
