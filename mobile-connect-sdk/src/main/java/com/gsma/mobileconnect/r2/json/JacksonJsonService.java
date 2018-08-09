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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.gsma.mobileconnect.r2.utils.ObjectUtils;
import com.gsma.mobileconnect.r2.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Implementation of the {@link IJsonService} that uses Jackson to perform json serialisation and
 * deserialisation.
 *
 * @since 2.0
 */
public class JacksonJsonService implements IJsonService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonJsonService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JacksonJsonService()
    {
        this.objectMapper.setPropertyNamingStrategy(
            PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public <T> T deserialize(final String json, Class<T> clazz) throws JsonDeserializationException
    {
        ObjectUtils.requireNonNull(clazz, "clazz");

        try
        {
            LOGGER.debug("Deserializing json to instance of class={}", clazz);
            return StringUtils.isNullOrEmpty(json)
                   ? null
                   : this.objectMapper.readValue(json, clazz);
        }
        catch (final IOException ioe)
        {
            LOGGER.info("Failed to deserialize json to instance of class={}", clazz, ioe);
            throw new JsonDeserializationException(clazz, json, ioe);
        }
    }

    @Override
    public String serialize(final Object object) throws JsonSerializationException
    {
        ObjectUtils.requireNonNull(object, "object");

        try
        {
            LOGGER.debug("Serializing instance of class={} to json", object.getClass());
            return this.objectMapper.writeValueAsString(object);
        }
        catch (final IOException ioe)
        {
            LOGGER.info("Failed to serialize instance of class={} to json", object.getClass(), ioe);
            throw new JsonSerializationException(object, ioe);
        }
    }

    /**
     * @return a copy of the configured {@link ObjectMapper} instance used by the SDK.
     */
    public ObjectMapper getObjectMapper()
    {
        return this.objectMapper.copy();
    }
}
