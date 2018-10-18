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
package com.gsma.mobileconnect.r2.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple key value pairing.  Implements {@link NameValuePair} for compatibility with HttpClient.
 *
 * @since 2.0
 */
public class KeyValuePair extends Tuple<String, String> implements NameValuePair
{
    /**
     * Create an instance of {@link KeyValuePair}
     *
     * @param key   the key
     * @param value the value
     */
    public KeyValuePair(final String key, final String value)
    {
        super(StringUtils.requireNonEmpty(key, "key"), value);
    }

    /**
     * Searches a list of {@link KeyValuePair}s for a match.
     *
     * @param pairs to search.
     * @param key   to match.
     * @return the value of the first match for the specified key within the list, or null if not
     * found.
     */
    public static String findFirst(final Iterable<KeyValuePair> pairs, final String key)
    {
        ObjectUtils.requireNonNull(pairs, "pairs");

        final KeyValuePair pair = ListUtils.firstMatch(pairs, keyMatches(key));
        return pair == null ? null : pair.getValue();
    }

    /**
     * Return a predicate that matches {@link KeyValuePair}s based on key.
     *
     * @param key to match.
     * @return Predicate that will match on key.
     */
    public static Predicate<KeyValuePair> keyMatches(final String key)
    {
        StringUtils.requireNonEmpty(key, "key");

        return new Predicate<KeyValuePair>()
        {
            @Override
            public boolean apply(final KeyValuePair input)
            {
                return key.equalsIgnoreCase(input.getKey());
            }
        };
    }

    /**
     * @return the key.
     */
    public String getKey()
    {
        return this.getFirst();
    }

    /**
     * @return the key.
     */
    @Override
    @JsonIgnore
    public String getName()
    {
        return this.getFirst();
    }

    /**
     * @return the value.
     */
    @Override
    public String getValue()
    {
        return this.getSecond();
    }

    @Override
    public String toString()
    {
        return "KeyValuePair(key=" + this.getKey() + ",value=" + this.getValue() + ")";
    }

    @Override
    public boolean equals(final Object other)
    {
        boolean retval = false;

        if (other instanceof KeyValuePair)
        {
            retval = ((KeyValuePair) other).getKey().equals(this.getKey()) && ((KeyValuePair) other)
                .getValue()
                .equals(this.getValue());
        }

        return retval;
    }

    @Override
    public int hashCode()
    {
        return 31 * this.getKey().hashCode() + this.getValue().hashCode();
    }

    /**
     * IBuilder for creating lists of {@link KeyValuePair}.
     */
    public static class ListBuilder implements IBuilder<List<? super KeyValuePair>>
    {
        private final List<KeyValuePair> list = new ArrayList<KeyValuePair>();

        public ListBuilder add(final String key, final String value)
        {
            this.list.add(new KeyValuePair(key, value));
            return this;
        }

        public ListBuilder addIfNotEmpty(final String key, final String value)
        {
            if (!StringUtils.isNullOrEmpty(value))
                add(key, value);
            return this;
        }

        @Override
        public List<KeyValuePair> build()
        {
            return new ArrayList<KeyValuePair>(this.list);
        }

        public List<NameValuePair> buildAsNameValuePairList()
        {
            return new ArrayList<NameValuePair>(this.list);
        }
    }
}
