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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exception that is thrown where the Mobile Connect SDK has been unable to process a scope
 * received from an external service.
 *
 * @since 2.0
 */
@SuppressWarnings("unused")
public class InvalidScopeException extends AbstractMobileConnectException
{
    private static final String MESSAGE = "Failed to process the scope: '%s'. The scope doesn't support (scope isn't correct or doesn't match with version)";
    private static final Logger LOGGER = LoggerFactory.getLogger(InvalidScopeException.class);

    /**
     * Create a new instance of InvalidScopeException that records the scope.
     *
     * @param scope       that was used.
     */
    public InvalidScopeException(final String scope)
    {
        super(getMessage(scope));
        LOGGER.warn(getMessage(scope));
    }

    private static String getMessage(String scope) {
        return String.format(MESSAGE, scope);
    }

    @Override
    public MobileConnectStatus toMobileConnectStatus(final String scope)
    {
        return MobileConnectStatus.error("invalid_scope", getMessage(scope), this);
    }
}
