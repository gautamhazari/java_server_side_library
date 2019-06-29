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
package com.gsma.mobileconnect.r2.claims;

import com.fasterxml.jackson.databind.JsonNode;
import com.gsma.mobileconnect.r2.utils.IBuilder;
import com.gsma.mobileconnect.r2.utils.ObjectUtils;

import java.io.IOException;
import java.util.*;

/**
 * JSON Serializable class to store configured claims values for use with mobile connect methods.
 *
 * @since 2.0
 */
public class Claims
{
    public static final ClaimsValue ESSENTIAL_CLAIM =
        new ClaimsValue.Builder().withEssential(Boolean.TRUE).build();
    private static final String VALUE = "value";
    private static final String VALUES = "values";
    private static final ClaimsValue VOLUNTARY_CLAIM =
        new ClaimsValue.Builder().withEssential(Boolean.FALSE).build();
    private final Map<String, ClaimsValue> claimsMap;

    /**
     * Constructor used by {@link JacksonDeserializer}
     */
    private Claims()
    {
        this.claimsMap = new HashMap<String, ClaimsValue>();
    }

    /**
     * Constructor used by {@link Builder}
     */
    private Claims(final Builder builder)
    {
        this.claimsMap = builder.claimsMap;
    }

    /**
     * Retrieve the claim associated with the key.
     *
     * @param key claim key.
     * @return value of claim, or null if it is not present or was voluntary.
     */
    public ClaimsValue get(final String key)
    {
        ObjectUtils.requireNonNull(key, "key");

        final ClaimsValue value = this.claimsMap.get(key);
        return value != VOLUNTARY_CLAIM ? value : null;
    }

    /**
     * Confirms whether a claim is voluntary.
     *
     * @param key to search.
     * @return true if claim is present and is voluntary, otherwise false.
     */
    public boolean isVoluntary(final String key)
    {
        ObjectUtils.requireNonNull(key, "key");

        return VOLUNTARY_CLAIM.equals(this.claimsMap.get(key));
    }

    /**
     * @return true if there are no claims stored.
     */
    public boolean isEmpty()
    {
        return this.claimsMap.isEmpty();
    }

//    protected static class JacksonDeserializer extends JsonDeserializer<Claims>
//    {
//        @Override
//        public Claims deserialize(final JsonParser jsonParser,
//            final DeserializationContext deserializationContext) throws IOException
//        {
//            final JsonNode node = jsonParser.getCodec().readTree(jsonParser);
//            final Claims claims = new Claims();
//
//            for (final Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext(); )
//            {
//                final Map.Entry<String, JsonNode> entry = it.next();
//
//                final JsonNode value = entry.getValue();
//                final ClaimsValue claimsValue;
//
//                if (value.isNull())
//                {
//                    claimsValue = VOLUNTARY_CLAIM;
//                }
//                else
//                {
//                    claimsValue = extractClaimsValue(value);
//                }
//
//                claims.claimsMap.put(entry.getKey(), claimsValue);
//            }
//
//            return claims;
//        }
//
//        private ClaimsValue extractClaimsValue(JsonNode value)
//        {
//            final ClaimsValue.Builder builder =
//                new ClaimsValue.Builder().withEssential(value.has("essential"))
//                    .withValue(value.isNumber() ? value.longValue() : value.textValue());
//
//            if (value.has(VALUES))
//            {
//                final List<Object> objects = new ArrayList<Object>();
//                for (final JsonNode v : value.get(VALUES))
//                {
//                    objects.add(v.textValue());
//                }
//                builder.withValues(objects.toArray());
//            }
//
//            if (value.isArray()) {
//                final List<Object> objects = new ArrayList<Object>();
//                for (final JsonNode v : value) {
//                    objects.add(v.textValue());
//                }
//                builder.withValues(objects.toArray());
//            }
//            return builder.build();
//        }
//    }


//    protected static class JacksonSerializer extends JsonSerializer<Claims>
//    {
//        @Override
//        public void serialize(final Claims claims, final JsonGenerator jsonGenerator,
//            final SerializerProvider serializerProvider) throws IOException
//        {
//            jsonGenerator.writeStartObject();
//
//            for (final Map.Entry<String, ClaimsValue> entry : claims.claimsMap.entrySet())
//            {
//                final ClaimsValue value =
//                    entry.getValue() == VOLUNTARY_CLAIM ? null : entry.getValue();
//                jsonGenerator.writeObjectField(entry.getKey(), value);
//            }
//
//            jsonGenerator.writeEndObject();
//        }
//    }


    public static class Builder implements IBuilder<Claims>
    {
        private final Map<String, ClaimsValue> claimsMap = new HashMap<String, ClaimsValue>();

        public Builder(String json) {

        }

        private ClaimsValue extractClaimsValue(JsonNode value)
        {
            final ClaimsValue.Builder builder =
                new ClaimsValue.Builder().withEssential(value.has("essential"))
                    .withValue(value.isNumber() ? value.longValue() : value.textValue());

            if (value.has(VALUES))
            {
                final List<Object> objects = new ArrayList<Object>();
                for (final JsonNode v : value.get(VALUES))
                {
                    objects.add(v.textValue());
                }
                builder.withValues(objects.toArray());
            }

            if (value.isArray()) {
                final List<Object> objects = new ArrayList<Object>();
                for (final JsonNode v : value) {
                    objects.add(v.textValue());
                }
                builder.withValues(objects.toArray());
            }
            return builder.build();
        }

//        public Claims deserialize(final JsonParser jsonParser,
//            final DeserializationContext deserializationContext) throws IOException
//        {
//            final JsonNode node = jsonParser.getCodec().readTree(jsonParser);
//            final Claims claims = new Claims();
//
//            for (final Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext(); )
//            {
//                final Map.Entry<String, JsonNode> entry = it.next();
//
//                final JsonNode value = entry.getValue();
//                final ClaimsValue claimsValue;
//
//                if (value.isNull())
//                {
//                    claimsValue = VOLUNTARY_CLAIM;
//                }
//                else
//                {
//                    claimsValue = extractClaimsValue(value);
//                }
//
//                claims.claimsMap.put(entry.getKey(), claimsValue);
//            }
//
//            return claims;
//        }
//


        /**
         * Add a claim value with the specified key.
         *
         * @param key   claim key.
         * @param value claims value.
         */
        public Builder add(final String key, final ClaimsValue value)
        {
            ObjectUtils.requireNonNull(key, "key");
            ObjectUtils.requireNonNull(value, VALUE);

            this.claimsMap.put(key, value);
            return this;
        }

        /**
         * Add a voluntary claim with the specified key.
         *
         * @param key claim key.
         */
        public Builder addVoluntary(final String key)
        {
            this.add(key, VOLUNTARY_CLAIM);
            return this;
        }

        /**
         * Add a required claim with the specified key.
         *
         * @param key claim key.
         */
        public Builder addEssential(final String key)
        {
            this.add(key, ESSENTIAL_CLAIM);
            return this;
        }

        /**
         * Add a claim with the specified key and value.  When claims are sent to a method that
         * accepts them the response will only contain the claim if the values match.
         *
         * @param key       claim key.
         * @param essential is claim essential?
         * @param value     claim value.
         */
        public Builder add(final String key, final boolean essential, final Object value)
        {
            this.add(key,
                new ClaimsValue.Builder().withEssential(essential).withValue(value).build());
            return this;
        }

        /**
         * Add a claim with the specified key and values. When claims are sent to a method that
         * accepts them the response will only contain the claim if the value is in the values
         * list.
         *
         * @param key       claim key.
         * @param essential is claim essential?
         * @param values    claim values.
         */
        public Builder add(final String key, final boolean essential, final Object[] values)
        {
            this.add(key,
                new ClaimsValue.Builder().withEssential(essential).withValues(values).build());
            return this;
        }

        @Override
        public Claims build()
        {
            return new Claims(this);
        }
    }
}
