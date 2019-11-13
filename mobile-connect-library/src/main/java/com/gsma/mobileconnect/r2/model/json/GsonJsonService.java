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


import com.google.gson.Gson;
import com.gsma.mobileconnect.r2.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the {@link IJsonService} that uses to perform json serialisation and
 * deserialisation.
 *
 * @since 2.0
 */
public class GsonJsonService implements IJsonService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GsonJsonService.class);
    private Gson gson;

    public GsonJsonService()
    {
        gson = new Gson();
    }

    @Override
    public <T> T deserialize(final String json, Class<T> clazz) throws JsonDeserializationException
    {
        ObjectUtils.requireNonNull(clazz, "clazz");

        T deserializedObject = gson.fromJson(json, clazz);
        //TODO: validations
        return deserializedObject;
    }

    @Override
    //TODO: check class
    public String serialize(final Object object) throws JsonSerializationException
    {
        ObjectUtils.requireNonNull(object, "object");
        String serializedObject = null;
        try {
            serializedObject = gson.toJson(object);
        }
        catch (IllegalArgumentException e) {
            LOGGER.warn(e.getMessage());
        }
        if (serializedObject == null) {
            throw new JsonSerializationException(object);
        }
        return serializedObject;
    }
}
