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
package com.gsma.mobileconnect.r2.cache;

/**
 * Describes a problem accessing the cache.
 *
 * @since 2.0
 */
public class CacheAccessException extends Exception
{
    private final Operation operation;
    private final String key;
    private final Class<? extends AbstractCacheable> clazz;

    public CacheAccessException(final Operation operation, final String key,
        final Class<? extends AbstractCacheable> clazz, final Throwable cause)
    {
        super(String.format("Failed to access cache to %s object with class %s with key %s",
            operation.name(), clazz.getSimpleName(), key), cause);

        this.operation = operation;
        this.key = key;
        this.clazz = clazz;
    }

    /**
     * @return the operation under way when the exception occurred.
     */
    public Operation getOperation()
    {
        return this.operation;
    }

    /**
     * @return the key being accessed.
     */
    public String getKey()
    {
        return this.key;
    }

    /**
     * @return the class type stored.
     */
    public Class<? extends AbstractCacheable> getClazz()
    {
        return this.clazz;
    }

    public enum Operation
    {
        ADD, GET, REMOVE
    }
}
