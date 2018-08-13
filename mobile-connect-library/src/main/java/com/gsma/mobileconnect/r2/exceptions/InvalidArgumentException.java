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

import com.gsma.mobileconnect.r2.IHasMobileConnectStatus;
import com.gsma.mobileconnect.r2.MobileConnectStatus;

/**
 * Thrown to warn of an invalid argument on entry to a method.
 *
 * @since 2.0
 */
@SuppressWarnings("unused")
public class InvalidArgumentException extends IllegalArgumentException
    implements IHasMobileConnectStatus
{
    private final String name;
    private final Disallowed disallowed;

    /**
     * Create an instance of this exception, specifying the name of the missing argument.
     *
     * @param name of the null argument.
     */
    public InvalidArgumentException(final String name)
    {
        super(String.format("Required parameter '%s' was null", name));
        this.name = name;
        this.disallowed = Disallowed.NULL;
    }

    /**
     * Create an instance of this exception, specifying what is invalid about the argument.
     *
     * @param name       of the arguement.
     * @param disallowed description of the problem.
     */
    public InvalidArgumentException(final String name, final Disallowed disallowed)
    {
        super(String.format("Required parameter '%s' was %s", name, disallowed.getDescription()));
        this.name = name;
        this.disallowed = disallowed;
    }

    /**
     * @return the name of the argument in error.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return a description of the problem.
     */
    public Disallowed getDisallowed()
    {
        return this.disallowed;
    }

    @Override
    public MobileConnectStatus toMobileConnectStatus(final String task)
    {
        return MobileConnectStatus.error("invalid_argument",
            String.format("The argument %s was found to be invalid for %s.", this.name, task),
            this);
    }

    /**
     * Flag to describe what is not allowed for the field in error.
     */
    public enum Disallowed
    {
        NULL("null"), NULL_OR_EMPTY("null or empty");

        private final String description;

        Disallowed(final String description)
        {
            this.description = description;
        }

        /**
         * Return the description of this error.
         */
        public String getDescription()
        {
            return this.description;
        }
    }
}
