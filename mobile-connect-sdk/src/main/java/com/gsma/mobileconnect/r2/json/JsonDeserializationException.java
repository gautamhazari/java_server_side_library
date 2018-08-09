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
package com.gsma.mobileconnect.r2.json;

/**
 * Exception thrown where an unexpected error was encountered deserialising a known String
 * of json.  This will likely indicate either poorly formatted json or a deviation from the
 * Mobile Connect specification.
 *
 * @since 2.0
 */
public class JsonDeserializationException extends Exception
{
    private final String json;
    private final Class<?> clazz;

    /**
     * Create a new json deserialization exception.
     *
     * @param clazz the type the json was being deserialised to.
     * @param json  that was being deserialised.
     * @param cause the underlying exception.
     */
    public JsonDeserializationException(final Class<?> clazz, final String json,
        final Throwable cause)
    {
        super(String.format("Failed to deserialize json to instance of %s", clazz.getName()),
            cause);
        this.clazz = clazz;
        this.json = json;
    }

    /**
     * @return the json that was being deserialised.
     */
    public String getJson()
    {
        return this.json;
    }

    /**
     * @return the class type that was being deserialized to.
     */
    public Class<?> getDeserializationClass()
    {
        return this.clazz;
    }
}
