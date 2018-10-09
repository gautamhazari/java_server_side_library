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

import com.gsma.mobileconnect.r2.exceptions.InvalidArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Iterator;

/**
 * Utility methods for working with {@link String}s.
 *
 * @since 2.0
 */
public final class StringUtils
{
    private static final Logger LOGGER = LoggerFactory.getLogger(StringUtils.class);

    private StringUtils()
    {
    }

    /**
     * Inspect a String for content.  Note that this method considers white space as content and
     * therefore a non-empty String.
     *
     * @param str to inspect.
     * @return true if the String is not null and has zero length.
     */
    public static boolean isNullOrEmpty(final String str)
    {
        return str == null || "".equals(str);
    }

    /**
     * Inspect a String for content.  Note that this method considers white space as content and
     * therefore a non-empty String.
     *
     * @param values to inspect.
     * @return true if the String is not null and has zero length.
     */
    public static boolean isNullOrEmpty(final String... values)
    {
        boolean isEmpty = true;
        for (String val : values) {
            if (!isNullOrEmpty(val)) {
                isEmpty = false;
            }
        }
        return isEmpty;
    }
    /**
     * Inspect a String for content.  Note that this method considers white space as content and
     * therefore a non-empty String.
     *
     * @param values to inspect.
     * @return true if the String is not null and has zero length.
     */
    public static boolean isNotEmpty(final String... values)
    {
        boolean isNotEmpty = true;
        for (String val : values) {
            if (isNullOrEmpty(val)) {
                isNotEmpty = false;
            }
        }
        return isNotEmpty;
    }

    /**
     * Inspect a String for content.  Note that this method considers white space as content and
     * therefore a null String.
     *
     * @param str to inspect.
     * @return true if the String is null.
     */
    public static boolean isNull(final String str)
    {
        return str == null;
    }

    /**
     * Inspect a String for content, throws NullPointerException if is null or empty.
     *
     * @param value to inspect.
     * @param name  of String to report in exception.
     * @return the inspected value.
     * @throws InvalidArgumentException detailing name of String that is either null or empty.
     */
    public static String requireNonEmpty(final String value, final String name)
    {
        ObjectUtils.requireNonNull(name, "name");

        if (isNullOrEmpty(value))
        {
            final InvalidArgumentException iae = new InvalidArgumentException(name,
                InvalidArgumentException.Disallowed.NULL_OR_EMPTY);
            LOGGER.warn("Required String {} was null or empty", name, iae);
            throw iae;
        }
        else
        {
            return value;
        }
    }
    /**
     * Inspect a String for content, throws NullPointerException if is null or empty.
     *
     * @param value to inspect.
     * @param name  of String to report in exception.
     * @throws InvalidArgumentException detailing name of String that is either null or empty.
     */
    public static void requireNonEmpty(final String name, final String value, final String... values)
    {
        ObjectUtils.requireNonNull(name, "name");
        if ((isNullOrEmpty(value) & !isNullOrEmpty(values))) {
            for (String val : values) {
                requireNonEmpty(val, name);
            }
        }
    }

    /**
     * Joins strings using the specified join String.
     *
     * @param strings to join.
     * @param join    the string to join strings with.
     * @return single String representing joined strings.
     */
    public static String join(final Iterable<String> strings, final String join)
    {
        final StringBuilder builder = new StringBuilder();
        for (final Iterator<String> it = strings.iterator(); it.hasNext(); )
        {
            builder.append(it.next());
            if (it.hasNext())
            {
                builder.append(join);
            }
        }
        return builder.toString();
    }

    /**
     * Trims leading characters from a String.
     *
     * @param str to trim.
     * @param t   character to trim.
     * @return str with all leading characters t removed.
     */
    public static String trimLeading(final String str, final char t)
    {
        String retval = str;

        if (str != null)
        {
            for (int i = 0; i < str.length(); i++)
            {
                if (str.charAt(i) != t)
                {
                    retval = str.substring(i);
                    break;
                }
            }
        }

        return retval;
    }

    /**
     * Convert key array into string
     *
     * @param keys to convert.
     * @return string that contains all keys.
     */
    public static String formatKey(String... keys) {
        StringBuilder stringBuilder = new StringBuilder();
        String appender = "_";
        for (String key : keys) {
            if (key != null) {
                stringBuilder.append(key);
                stringBuilder.append(appender);
            }
        }
        if (stringBuilder.length() == 0) {
            return null;
        } else {
            return stringBuilder.toString() ;
        }
    }

    /**
     * Set value to null if it is empty
     *
     * @param value to check.
     * @return value or null if it is empty.
     */
    public static String setValueToNullIfIsEmpty (String value) {
        if (isNullOrEmpty(value)) {
            return null;
        }
        return value;
    }

    /**
     * Set value to null if it is empty
     *
     * @param value to check.
     * @return value or null if it is empty.
     */
    public static URI setValueToNullIfIsEmpty (URI value) {
        if (value == null || value.toString().equals("")) {
            return null;
        }
        return value;
    }

}
