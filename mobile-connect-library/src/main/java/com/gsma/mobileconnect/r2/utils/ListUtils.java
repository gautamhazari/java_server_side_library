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

import java.util.*;

/**
 * Utility methods for working with ListUtils.
 *
 * @since 2.0
 */
public final class ListUtils
{
    private ListUtils()
    {
    }

    /**
     * Copies the provided list then wraps it as an unmodifiable list.  Note that contents could
     * still be changed if they are not immutable themselves.
     *
     * @param list to copy and wrap.
     * @param <T>  type of contents of list
     * @return immutable list.
     */
    public static <T> List<T> immutableList(List<T> list)
    {
        return list == null ? null : Collections.unmodifiableList(new ArrayList<T>(list));
    }

    /**
     * Search a list for a match to a String, ignoring case.
     *
     * @param value to search for.
     * @param list  of StringUtils to search.
     * @return true if found, false if not.
     */
    public static boolean containsIgnoreCase(final String value, final Iterable<String> list)
    {
        ObjectUtils.requireNonNull(value, "value");
        ObjectUtils.requireNonNull(list, "list");

        boolean found = false;

        for (final Iterator<String> it = list.iterator(); it.hasNext() && !found; )
        {
            found = value.equalsIgnoreCase(it.next());
        }

        return found;
    }

    /**
     * Queries a collection of values and returns the first match found according to the supplied
     * predicate.
     *
     * @param iterable  to search.
     * @param predicate to apply to find match.
     * @param <T>       type of values in the collection.
     * @return the first match found.  No ordering will be applied prior to the search.
     */
    public static <T> T firstMatch(final Iterable<T> iterable, final Predicate<T> predicate)
    {
        if (iterable != null)
        {
            for (final T value : iterable)
            {
                if (predicate.apply(value))
                {
                    return value;
                }
            }
        }

        return null;
    }

    /**
     * Queries a collection of values and returns all matches found according to the supplied
     * predicate.
     *
     * @param iterable  to search.
     * @param predicate to apply to find matches.
     * @param <T>       the type of values in the collection.
     * @return all matches found.  Empty if no matches.
     */
    public static <T> Iterable<T> allMatches(final Iterable<T> iterable,
        final Predicate<T> predicate)
    {
        final List<T> matches = new ArrayList<T>();

        if (iterable != null)
        {
            for (final T value : iterable)
            {
                if (predicate.apply(value))
                {
                    matches.add(value);
                }
            }
        }

        return matches;
    }

    /**
     * Removes all matches of a String from an Iterable instance.  Note this modifies the supplied
     * iterable.
     *
     * @param strings  to search.
     * @param toRemove to remove from strings.
     * @return true if one or more entries were removed, false otherwise.
     */
    public static boolean removeIgnoreCase(final Iterable<String> strings, final String toRemove)
    {
        boolean retval = false;

        for (final Iterator<String> it = strings.iterator(); it.hasNext(); )
        {
            if (toRemove.equalsIgnoreCase(it.next()))
            {
                it.remove();
                retval = true;
            }
        }

        return retval;
    }

    /**
     * Given an iterable of strings that can be parsed to integers returns the summation
     * @param iterable collection of strings that can be parsed to integers
     * @return the summation of iterable
     */
    public static int sum(final Iterable<String> iterable)
    {
        if (iterable == null)
            return 0;

        Iterator<String> iterator = iterable.iterator();

        int sum = 0;
        while (iterator.hasNext())
            sum = sum + Integer.valueOf(iterator.next().replaceAll("[\\D]", ""));

        return sum;
    }

    /**
     * IBuilder of hash maps.
     *
     * @param <K> type of key
     * @param <V> type of value
     */
    public static class HashMapBuilder<K, V> implements IBuilder<Map<K, V>>
    {
        private final Map<K, V> map = new HashMap<K, V>();

        /**
         * Add a key value pair to the map.
         *
         * @param key   key to add.
         * @param value value to add.
         * @return this builder.
         */
        public HashMapBuilder<K, V> add(final K key, final V value)
        {
            this.map.put(key, value);
            return this;
        }

        /**
         * @return a map containing all key value pairs added.  Note repeated calls to this will
         * return new instances of a HashMap, each with the same contents.  Changes to the contents
         * or keys (if mutable) will be refelected between the maps, however changes to the maps
         * themselves will not.
         */
        @Override
        public Map<K, V> build()
        {
            return new HashMap<K, V>(this.map);
        }
    }
}
