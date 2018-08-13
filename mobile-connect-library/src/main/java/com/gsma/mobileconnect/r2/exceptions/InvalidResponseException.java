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
package com.gsma.mobileconnect.r2.exceptions;

import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.rest.RestResponse;

/**
 * Exception that is thrown where the Mobile Connect SDK has been unable to process a response
 * received from an external service.
 *
 * @since 2.0
 */
@SuppressWarnings("unused")
public class InvalidResponseException extends AbstractMobileConnectException
{
    private final Class<?> responseClass;
    private final transient RestResponse restResponse;

    /**
     * Create a new instance of InvalidResponseException that records the rest response received.
     *
     * @param restResponse  that was received.
     * @param responseClass that was being constructed.
     * @param cause         underlying exception.
     */
    public InvalidResponseException(final RestResponse restResponse, final Class<?> responseClass,
        final Throwable cause)
    {
        super(String.format(
            "Failed to process response from %s of %s, http response code %s, target class %s",
            restResponse.getMethod(), restResponse.getUri().toString(),
            restResponse.getStatusCode(), responseClass.getName()), cause);

        this.restResponse = restResponse;
        this.responseClass = responseClass;
    }

    /**
     * @return the class type that was to be constructed from the response.
     */
    public Class<?> getResponseClass()
    {
        return this.responseClass;
    }

    /**
     * @return the response that was received.
     */
    public RestResponse getRestResponse()
    {
        return this.restResponse;
    }

    @Override
    public MobileConnectStatus toMobileConnectStatus(final String task)
    {
        return MobileConnectStatus.error("invalid_response",
            String.format("Failed to process the response from %s", task), this);
    }
}
