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
package com.gsma.mobileconnect.r2.model.json;

/**
 * Exception thrown where an unexpected error was encountered serialising an object to a
 * String of json.
 *
 * @since 2.0
 */
public class JsonSerializationException extends Exception
{
    private final transient Object object;

    /**
     * Create a new json serialization exception.
     *
     * @param object the object that was being serialised to json.
     * @param cause  the underlying exception.
     */
    public JsonSerializationException(final Object object, final Throwable cause)
    {
        super(String.format("Failed to serialize instance of %s to json",
            object.getClass().getName()), cause);
        this.object = object;
    }

    /**
     * Create a new json serialization exception.
     *
     * @param object the object that was being serialised to json.
     */
    public JsonSerializationException(final Object object)
    {
        super(String.format("Failed to serialize instance of %s to json",
            object.getClass().getName()));
        this.object = object;
    }

    /**
     * @return the object that was being serialized to json.
     */
    public Object getSerializationObject()
    {
        return this.object;
    }
}
