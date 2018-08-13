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

import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.discovery.ProviderMetadata;
import com.gsma.mobileconnect.r2.exceptions.InvalidArgumentException;
import com.gsma.mobileconnect.r2.json.IJsonService;
import com.gsma.mobileconnect.r2.json.JacksonJsonService;
import com.gsma.mobileconnect.r2.json.JsonDeserializationException;
import com.gsma.mobileconnect.r2.utils.ListUtils;
import com.gsma.mobileconnect.r2.utils.TestUtils;
import com.gsma.mobileconnect.r2.utils.Tuple;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

/**
 * Tests {@link ConcurrentCache}
 *
 * @since 2.0
 */
public class ConcurrentCacheTest
{
    private final IJsonService jsonService = new JacksonJsonService();

    private ICache cache;

    private ICache cacheWithLimits(final Long lower, final Long upper)
    {
        ListUtils.HashMapBuilder<Class<? extends AbstractCacheable>, Tuple<Long, Long>>
            cacheLimitsMapBuilder =
            new ListUtils.HashMapBuilder<Class<? extends AbstractCacheable>, Tuple<Long, Long>>();

        if (lower != null && upper != null)
        {
            cacheLimitsMapBuilder.add(ProviderMetadata.class, new Tuple<Long, Long>(lower, upper));
        }

        return new ConcurrentCache.Builder()
            .withJsonService(this.jsonService)
            .withCacheExpiryLimits(cacheLimitsMapBuilder.build())
            .build();
    }

    @BeforeMethod
    public void beforeMethod()
    {
        this.cache = new ConcurrentCache.Builder().withJsonService(this.jsonService).build();
    }

    @Test
    public void createEmptyCache() throws CacheAccessException
    {
        assertTrue(this.cache.isEmpty());
    }

    @Test
    public void addShouldStoreDiscoveryResponse()
        throws CacheAccessException, JsonDeserializationException
    {
        final DiscoveryResponse discoveryResponse =
            DiscoveryResponse.fromRestResponse(TestUtils.OPERATOR_SELECTION_RESPONSE,
                this.jsonService);

        final String key = "001_01";

        this.cache.add(key, discoveryResponse);

        final DiscoveryResponse actual = this.cache.get(key, DiscoveryResponse.class);

        assertFalse(this.cache.isEmpty());
        assertNotNull(actual);
        assertEquals(actual.getResponseData().getResponse(),
            discoveryResponse.getResponseData().getResponse());
    }

    @Test
    public void cacheShouldGetResponseWhenMultipleStored()
        throws CacheAccessException, JsonDeserializationException
    {
        final DiscoveryResponse discoveryResponse =
            DiscoveryResponse.fromRestResponse(TestUtils.DISCOVERY_REQUEST_RESPONSE,
                this.jsonService);

        final String key = "001_01";

        this.cache.add(key, discoveryResponse);
        this.cache.add("002_02",
            DiscoveryResponse.fromRestResponse(TestUtils.OPERATOR_SELECTION_RESPONSE,
                this.jsonService));
        final DiscoveryResponse actual = this.cache.get(key, DiscoveryResponse.class);

        assertNotNull(actual);
        assertNotNull(actual.getResponseData().getResponse().getApis());
    }

    @Test
    public void clearShouldClearStore() throws CacheAccessException, JsonDeserializationException
    {
        this.cache.add("001_01",
            DiscoveryResponse.fromRestResponse(TestUtils.OPERATOR_SELECTION_RESPONSE,
                this.jsonService));
        this.cache.add("002_02",
            DiscoveryResponse.fromRestResponse(TestUtils.DISCOVERY_REQUEST_RESPONSE,
                this.jsonService));

        assertFalse(this.cache.isEmpty());

        this.cache.clear();

        assertTrue(this.cache.isEmpty());
    }

    @Test
    public void removeShouldRemoveFromStore()
        throws CacheAccessException, JsonDeserializationException
    {
        this.cache.add("001_01",
            DiscoveryResponse.fromRestResponse(TestUtils.OPERATOR_SELECTION_RESPONSE,
                this.jsonService));
        this.cache.add("002_02",
            DiscoveryResponse.fromRestResponse(TestUtils.DISCOVERY_REQUEST_RESPONSE,
                this.jsonService));

        assertFalse(this.cache.isEmpty());

        this.cache.remove("001_01");
        assertNull(this.cache.get("001_01", DiscoveryResponse.class));
        assertNotNull(this.cache.get("002_02", DiscoveryResponse.class));

        assertFalse(this.cache.isEmpty());
    }

    @DataProvider
    public Object[][] cacheShouldNotAddWithEmptyOrNullArgumentsData()
    {
        return new Object[][] {{null}, {""}};
    }

    @Test(dataProvider = "cacheShouldNotAddWithEmptyOrNullArgumentsData", expectedExceptions = InvalidArgumentException.class)
    public void cacheShouldNotAddWithEmptyOrNullArguments(final String key)
        throws CacheAccessException, JsonDeserializationException
    {
        this.cache.add(key,
            DiscoveryResponse.fromRestResponse(TestUtils.OPERATOR_SELECTION_RESPONSE,
                this.jsonService));
    }

    @DataProvider
    public Object[][] removeIfExpiredData()
    {
        return new Object[][] {{Boolean.TRUE}, {Boolean.FALSE}};
    }

    @Test(dataProvider = "removeIfExpiredData")
    public void cacheShouldNotReturnValueIfExpiredAndRemoveIfExpiredIsTrue(
        final Boolean removeIfExpired)
        throws CacheAccessException, CacheExpiryLimitException, InterruptedException
    {
        final ICache noLimitCache = this.cacheWithLimits(null, null);
        noLimitCache.setCacheExpiryTime(0L, TimeUnit.SECONDS, ProviderMetadata.class);
        final String key = "test";

        noLimitCache.add(key, new ProviderMetadata.Builder().build());

        Thread.sleep(50L);

        assertEquals(null == noLimitCache.get(key, ProviderMetadata.class, removeIfExpired),
            removeIfExpired.booleanValue());
    }

    @Test
    public void cacheShouldReturnDefaultValueIfKeyNull() throws CacheAccessException
    {
        final ProviderMetadata providerMetadata =
            this.cache.get(null, ProviderMetadata.class, true);
        assertNull(providerMetadata);
    }

    @DataProvider
    public Object[][] cacheExpiryLimitsData()
    {
        return new Object[][] {{10L}, {600L}};
    }

    @Test(dataProvider = "cacheExpiryLimitsData", expectedExceptions = CacheExpiryLimitException.class)
    public void setCacheExpiryTimeShouldThrowIfExpiryTimeOutsideLimits(final Long seconds)
        throws CacheExpiryLimitException
    {
        final ICache cacheWithLimits =
            this.cacheWithLimits(TimeUnit.SECONDS.toMillis(200L), TimeUnit.SECONDS.toMillis(400L));
        cacheWithLimits.setCacheExpiryTime(seconds, TimeUnit.SECONDS, ProviderMetadata.class);
    }
}
