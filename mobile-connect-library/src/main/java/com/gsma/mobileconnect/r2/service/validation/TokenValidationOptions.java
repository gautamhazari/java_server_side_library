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
package com.gsma.mobileconnect.r2.service.validation;

/**
 * @since 2.0
 */
@SuppressWarnings("WeakerAccess")
public class TokenValidationOptions
{

    private TokenValidationResult acceptedValidationResults;

    public TokenValidationOptions(final Builder builder)
    {
        this.acceptedValidationResults = builder.acceptedValidationResults;
    }

    /**
     * Bit flag specifying which validation results should be accepted as "OK", if any results
     + not specified are returned from validation an error status to be returned when requesting a token.
     + By default only tokens that pass all validation steps will be accepted, allowing others to be accepted
     + is at the SDK users own risk and is not advised
     */
    public TokenValidationResult getAcceptedValidationResults()
    {
        return acceptedValidationResults;
    }

    public static final class Builder
    {
        private TokenValidationResult acceptedValidationResults;

        public Builder()
        {
            // empty constructor for builder class
        }
        
        public Builder withAcceptedValidationResults(
            TokenValidationResult acceptedValidationResults)
        {
            this.acceptedValidationResults = acceptedValidationResults;
            return this;
        }

        public TokenValidationOptions build()
        {
            return new TokenValidationOptions(this);
        }
    }
}
