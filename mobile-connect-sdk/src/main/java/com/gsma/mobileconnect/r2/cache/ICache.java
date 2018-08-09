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

import java.util.concurrent.TimeUnit;

/**
 * Interface for the cache used during the discovery process, cache is mainly used to cache
 * DiscoveryResponse com.gsma.mobileconnect.r2.demo.objects but can also be used to cache any data used during the Discovery
 * process.
 *
 * @since 2.0
 */
public interface ICache
{
    /**
     * @return true if the cache is empty.
     * @throws CacheAccessException on failure to query the cache.
     */
    boolean isEmpty() throws CacheAccessException;

    /**
     * Add a value to the cache with the specified key.
     *
     * @param key   key (required).
     * @param value to store (required).
     * @param <T>   type of the value.
     * @throws CacheAccessException on failure to store.
     */
    <T extends AbstractCacheable> void add(final String key, final T value)
        throws CacheAccessException;

    /**
     * Return a cached value based on the key.  If it is found to be setExpired it will be removed
     * from the cache. <p>Equivalent to calling:
     * <pre>
     *     cache.get(key, true);
     * </pre>
     *
     * @param key   to match (required).
     * @param clazz the type of object to return.
     * @param <T>   the type to be returned from the cache.
     * @return the cached value if present, null otherwise.
     * @throws CacheAccessException on failure to fetch.
     */
    <T extends AbstractCacheable> T get(String key, final Class<T> clazz)
        throws CacheAccessException;

    /**
     * Return a cached value based on the key.
     *
     * @param key             to match (required).
     * @param removeIfExpired If value should be removed if it is retrieved and found to be
     *                        setExpired, should be set to false if a fallback value is required for
     *                        if the next call for the required resource fails.
     * @param clazz           the type of object to return.
     * @param <T>             the type to be returned from the cache.
     * @return the cached value if present, null otherwise
     * @throws CacheAccessException on failure to fetch.
     */
    <T extends AbstractCacheable> T get(final String key, final Class<T> clazz,
                                        final boolean removeIfExpired) throws CacheAccessException;

    /**
     * Remove an entry from the cache that matches the key.
     *
     * @param key to match (required).
     * @throws CacheAccessException on failure to remove from the cache.
     */
    void remove(final String key) throws CacheAccessException;

    /**
     * Remove all key value pairs from the cache.
     *
     * @throws CacheAccessException on failure to clear the cache.
     */
    void clear() throws CacheAccessException;

    /**
     * Set length of time before cached values of the specified type are marked as setExpired.
     *
     * @param duration the amount of time to cache for.
     * @param unit     the unit of duration.
     * @param clazz    the type of cacheable to apply the expiry time to.
     * @throws CacheExpiryLimitException when configuration is outside allowed limits.
     */
    void setCacheExpiryTime(long duration, final TimeUnit unit,
                            Class<? extends AbstractCacheable> clazz) throws CacheExpiryLimitException;
}
