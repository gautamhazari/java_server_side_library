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
package com.gsma.mobileconnect.r2.service.validation;

import com.gsma.mobileconnect.r2.cache.CacheAccessException;
import com.gsma.mobileconnect.r2.cache.ICache;
import com.gsma.mobileconnect.r2.model.json.GsonJsonService;
import com.gsma.mobileconnect.r2.model.json.JsonDeserializationException;
import com.gsma.mobileconnect.r2.web.rest.IRestClient;
import com.gsma.mobileconnect.r2.model.exceptions.RequestFailedException;
import com.gsma.mobileconnect.r2.web.rest.RestResponse;

import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Concrete implementation see {@link IJWKeysetService}
 *
 * @since 2.0
 */
public class JWKeysetService implements IJWKeysetService
{
    private final IRestClient restClient;
    private final ICache iCache;

    private final GsonJsonService gsonJsonService;

    /**
     * Creates an instance of the JWKeysetService with a configured cache
     *
     * @param builder Builder
     */
    private JWKeysetService(final Builder builder)
    {
        this.restClient = builder.restClient;
        this.iCache = builder.iCache;
        this.gsonJsonService = new GsonJsonService();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<JWKeyset> retrieveJwksAsync(final String url)
    {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<JWKeyset> futureJWKeyset = executorService.submit(new Callable<JWKeyset>()
        {
            @Override
            public JWKeyset call() throws Exception
            {
                return JWKeysetService.this.retrieveJwks(url);
            }
        });
        executorService.shutdownNow();
        return futureJWKeyset;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JWKeyset retrieveJwks(final String url)
        throws CacheAccessException, RequestFailedException, JsonDeserializationException
    {
        final JWKeyset cachedJWKeyset = retrieveFromCache(url);
        if (cachedJWKeyset != null && !cachedJWKeyset.hasExpired())
        {
            return cachedJWKeyset;
        }
        final RestResponse response = this.restClient.get(URI.create(url), null, null,null, null, null);
        final JWKeyset jwKeyset =
            this.gsonJsonService.deserialize(response.getContent(), JWKeyset.class);

        addToCache(url, jwKeyset);

        return jwKeyset;
    }

    private JWKeyset retrieveFromCache(final String url) throws CacheAccessException
    {
        if (this.iCache == null)
        {
            return null;
        }
        return this.iCache.get(url, JWKeyset.class);
    }

    private void addToCache(final String url, final JWKeyset jwKeyset) throws CacheAccessException
    {
        if (this.iCache != null && jwKeyset != null)
        {
            this.iCache.add(url, jwKeyset);
        }
    }

    public static final class Builder
    {
        private IRestClient restClient;
        private ICache iCache;


        public Builder()
        {
            /*
             * Default Constructor
             */
        }

        public Builder withRestClient(final IRestClient restClient)
        {
            this.restClient = restClient;
            return this;
        }

        public Builder withICache(final ICache iCache)
        {
            this.iCache = iCache;
            return this;
        }

        public JWKeysetService build()
        {
            return new JWKeysetService(this);
        }
    }
}
