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

import com.gsma.mobileconnect.r2.constants.DefaultOptions;
import com.gsma.mobileconnect.r2.discovery.ProviderMetadata;
import com.gsma.mobileconnect.r2.json.IJsonService;
import com.gsma.mobileconnect.r2.json.JsonDeserializationException;
import com.gsma.mobileconnect.r2.json.JsonSerializationException;
import com.gsma.mobileconnect.r2.utils.ListUtils;
import com.gsma.mobileconnect.r2.utils.ObjectUtils;
import com.gsma.mobileconnect.r2.utils.StringUtils;
import com.gsma.mobileconnect.r2.utils.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Base class for Discovery Caches that implements basic cache control mechanisms and type casting
 * reducing the amount of implementation needed in each derived cache class.
 *
 * @since 2.0
 */
public abstract class AbstractCache implements ICache
{
    protected static final Map<Class<? extends AbstractCacheable>, Tuple<Long, Long>>
        DEFAULT_CACHE_EXPIRY_LIMITS = Collections.unmodifiableMap(
        new ListUtils.HashMapBuilder<Class<? extends AbstractCacheable>, Tuple<Long, Long>>()
            .add(ProviderMetadata.class,
                new Tuple<Long, Long>(TimeUnit.MINUTES.toMillis(1L), TimeUnit.DAYS.toMillis(1L)))
            .build());

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCache.class);

    private final Map<Class<? extends AbstractCacheable>, Long> cacheExpiryTimes =
        new ListUtils.HashMapBuilder<Class<? extends AbstractCacheable>, Long>()
            .add(ProviderMetadata.class, DefaultOptions.PROVIDER_METADATA_TTL_MS)
            .build();
    private final Map<Class<? extends AbstractCacheable>, Tuple<Long, Long>> cacheExpiryLimits;

    protected final IJsonService jsonService;

    /**
     * Construct an instance of this discovery cache, setting the executor service to use for
     * concurrent operations.
     *
     * @param jsonService       used to serialise and deserilise com.gsma.mobileconnect.r2.demo.objects.
     * @param cacheExpiryLimits map defining limits for which types may be cached.
     */
    protected AbstractCache(final IJsonService jsonService,
        final Map<Class<? extends AbstractCacheable>, Tuple<Long, Long>> cacheExpiryLimits)
    {
        this.jsonService = jsonService;
        this.cacheExpiryLimits = cacheExpiryLimits;
    }

    @Override
    public <T extends AbstractCacheable> void add(final String key, final T value)
        throws CacheAccessException
    {
        StringUtils.requireNonEmpty(key, "key");
        ObjectUtils.requireNonNull(value, "value");

        if (key != null)
        {
            try
            {
                final String json = this.jsonService.serialize(value);
                this.internalAdd(key, new CacheEntry(json, value.getClass()));
            }
            catch (final JsonSerializationException jse)
            {
                LOGGER.warn("Failed to serialize instance of class={} to add to cache with key={}",
                    value.getClass(), key, jse);
                throw new CacheAccessException(CacheAccessException.Operation.ADD, key,
                    value.getClass(), jse);
            }
        }
    }

    @Override
    public <T extends AbstractCacheable> T get(final String key, final Class<T> clazz)
        throws CacheAccessException
    {
        return this.get(key, clazz, true);
    }

    @Override
    public <T extends AbstractCacheable> T get(final String key, final Class<T> clazz,
        final boolean removeIfExpired) throws CacheAccessException
    {
        ObjectUtils.requireNonNull(clazz, "clazz");

        T result = null;

        if (key != null)
        {
            final CacheEntry value = this.internalGet(key);
            if (value != null)
            {
                try
                {
                    result = this.jsonService.deserialize(value.getValue(), clazz);
                }
                catch (final JsonDeserializationException jde)
                {
                    this.internalRemove(key, value.getValue());
                    LOGGER.warn(
                        "Failed to deserialize cached instance of class={} with key={}; the value has been expelled from the cache",
                        clazz, key, jde);
                    throw new CacheAccessException(CacheAccessException.Operation.GET, key, clazz,
                        jde);
                }
                this.checkAndSetExpiry(value);
                result.setCacheInfo(value);

                if (removeIfExpired && value.isExpired())
                {
                    LOGGER.debug("Removing expired cached entry class={} with key={}", clazz, key);
                    result = null;
                    this.internalRemove(key, value.getValue());
                }
            }
        }

        return result;
    }

    /**
     * Checks if a object has been cached past the defined caching time or if internally the object
     * has been marked as expired.
     *
     * @param cacheEntry to check.
     * @return expiry status on completion.
     */
    protected boolean checkAndSetExpiry(final CacheEntry cacheEntry)
    {
        boolean expired = false;

        if (!cacheEntry.isExpired())
        {
            final Long timeToExpire = this.cacheExpiryTimes.get(cacheEntry.getCachedClass());
            if (timeToExpire != null)
            {
                expired = cacheEntry.getCachedTime().getTime() + timeToExpire
                    < System.currentTimeMillis();
                if (expired)
                {
                    cacheEntry.expire();
                }
            }
        }

        return expired || cacheEntry.isExpired();
    }

    @Override
    public void setCacheExpiryTime(long duration, TimeUnit unit,
        Class<? extends AbstractCacheable> clazz) throws CacheExpiryLimitException
    {
        final long cacheTime = unit.toMillis(duration);
        final Tuple<Long, Long> limits = this.cacheExpiryLimits.get(clazz);

        if (limits == null)
        {
            this.cacheExpiryTimes.put(clazz, unit.toMillis(duration));
        }
        else
        {
            if (ObjectUtils.defaultIfNull(limits.getFirst(), 0L) >= cacheTime
                || ObjectUtils.defaultIfNull(limits.getSecond(), Long.MAX_VALUE) <= cacheTime)
            {
                LOGGER.warn("Cache expiry limits are invalid; lower={}, upper={}",
                    limits.getFirst(), limits.getSecond());
                throw new CacheExpiryLimitException(clazz, limits.getFirst(), limits.getSecond());
            }
        }
    }

    /**
     * Add value to internal cache with given key.
     *
     * @param key   key
     * @param value value
     * @throws CacheAccessException if there was an issue adding the value.
     */
    protected abstract void internalAdd(final String key, final CacheEntry value)
        throws CacheAccessException;

    /**
     * Get value from internal cache with given key.
     *
     * @param key key
     * @return value
     * @throws CacheAccessException if there was an issue fetching the value.
     */
    protected abstract CacheEntry internalGet(final String key) throws CacheAccessException;

    /**
     * Remove value from the internal cache where key and value match.
     *
     * @param key   key
     * @param value value
     * @throws CacheAccessException if there was a problem removing the value from the cache.
     */
    protected abstract void internalRemove(final String key, final String value)
        throws CacheAccessException;
}
