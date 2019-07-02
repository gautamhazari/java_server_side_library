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

import com.google.gson.Gson;
import com.gsma.mobileconnect.r2.exceptions.InvalidArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Methods for working with {@link Object}s.
 *
 * @since 2.0
 */
public final class ObjectUtils
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectUtils.class);

    private ObjectUtils()
    {
    }

    /**
     * Checks that the specified object reference is not null. This method is designed primarily for
     * doing parameter validation in methods and constructors, as demonstrated below:
     * <pre>
     *     public Foo(Bar bar) {
     *        this.bar = ObjectUtils.requireNonNull(bar, "bar");
     *     }
     * </pre>
     *
     * @param obj  the object reference to check for nullity
     * @param name to report in the message of the {@link NullPointerException}
     * @param <T>  the type of the reference
     * @return obj if not null
     * @throws InvalidArgumentException if obj is null
     */
    public static <T> T requireNonNull(final T obj, final String name)
    {
        if (obj == null)
        {
            final InvalidArgumentException iae = new InvalidArgumentException(name);
            LOGGER.warn("Required argument {} was null", name, iae);
            throw iae;
        }
        else
        {
            return obj;
        }
    }

    /**
     * Checks the specified object reference for nullity, returning objDefault in the case that obj
     * is null.
     *
     * @param obj        to check for nullity
     * @param objDefault to return in the case that obj is null
     * @param <T>        the type of the reference
     * @return obj if it is not null, otherwise objDefault
     */
    public static <T> T defaultIfNull(final T obj, final T objDefault)
    {
        return obj != null ? obj : objDefault;
    }

    /**
     * Convert object to string/
     *
     * @param obj        to convert
     */
    public static String convertToJsonString(Object obj)
    {
        return new Gson().toJson(obj);
    }

}
