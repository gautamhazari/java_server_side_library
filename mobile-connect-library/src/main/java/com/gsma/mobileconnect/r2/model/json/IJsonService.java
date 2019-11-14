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
 * Defines service that is capable of serialising and deserialising com.gsma.mobileconnect.r2.demo.objects to or from json.
 *
 * @since 2.0
 */
public interface IJsonService
{
    /**
     * Convert a String of json to an instance of clazz.
     *
     * @param json  to convert.
     * @param clazz to instantiate.
     * @param <T>   type of clazz.
     * @return instance of clazz.
     * @throws JsonDeserializationException on failure to deserialise.
     */
    <T> T deserialize(final String json, final Class<T> clazz) throws JsonDeserializationException;

    /**
     * Convert an object to a representation in Json.
     *
     * @param object to convert.
     * @return json representation.
     * @throws JsonSerializationException on failure to serialise.
     */
    String serialize(final Object object) throws JsonSerializationException;
}
