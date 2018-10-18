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
package com.gsma.mobileconnect.r2;

import com.gsma.mobileconnect.r2.authentication.AuthenticationService;
import com.gsma.mobileconnect.r2.authentication.IAuthenticationService;
import com.gsma.mobileconnect.r2.cache.DiscoveryCache;
import com.gsma.mobileconnect.r2.validation.IJWKeysetService;
import com.gsma.mobileconnect.r2.validation.JWKeysetService;
import com.gsma.mobileconnect.r2.cache.ConcurrentCache;
import com.gsma.mobileconnect.r2.cache.ICache;
import com.gsma.mobileconnect.r2.constants.DefaultOptions;
import com.gsma.mobileconnect.r2.discovery.DiscoveryService;
import com.gsma.mobileconnect.r2.discovery.IDiscoveryService;
import com.gsma.mobileconnect.r2.encoding.DefaultEncodeDecoder;
import com.gsma.mobileconnect.r2.encoding.IMobileConnectEncodeDecoder;
import com.gsma.mobileconnect.r2.identity.IIdentityService;
import com.gsma.mobileconnect.r2.identity.IdentityService;
import com.gsma.mobileconnect.r2.json.IJsonService;
import com.gsma.mobileconnect.r2.json.JacksonJsonService;
import com.gsma.mobileconnect.r2.rest.IRestClient;
import com.gsma.mobileconnect.r2.rest.RestClient;
import com.gsma.mobileconnect.r2.utils.IBuilder;
import com.gsma.mobileconnect.r2.utils.ObjectUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Convenience methods to construct and access the core public interfaces of MobileConnect. <p> A
 * default instance can be created via . Limited
 * customisation is possible via the  method.  Otherwise
 * full customisation can be achieved via the individual builders provided by each of the
 * interfaces. </p> <p> For those who wish only to work with the {@link MobileConnectInterface},
 * this can be reached via . </p>
 *
 * @see MobileConnectConfig
 * @see MobileConnectInterface
 * @see IDiscoveryService
 * @see IAuthenticationService
 * @see IIdentityService
 * @since 2.0
 */
public final class MobileConnect
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MobileConnect.class);

    private final IDiscoveryService discoveryService;
    private final IAuthenticationService authnService;
    private final IIdentityService identityService;
    private final IJWKeysetService jwKeysetService;
    private final MobileConnectInterface mobileConnectInterface;
    private final MobileConnectWebInterface mobileConnectWebInterface;
    private final IMobileConnectEncodeDecoder iMobileConnectEncoderDecoder;

    private MobileConnect(final Builder builder)
    {
        this.iMobileConnectEncoderDecoder = builder.iMobileConnectEncodeDecoder;

        this.discoveryService = new DiscoveryService.Builder()
            .withCache(builder.discoveryCache)
            .withJsonService(builder.jsonService)
            .withRestClient(builder.restClient)
            .withIMobileConnectEncodeDecoder(this.iMobileConnectEncoderDecoder)
            .build();

        this.authnService = new AuthenticationService.Builder()
            .withJsonService(builder.jsonService)
            .withRestClient(builder.restClient)
            .withIMobileConnectEncodeDecoder(this.iMobileConnectEncoderDecoder)
            .build();

        this.identityService = new IdentityService.Builder()
            .withJsonService(builder.jsonService)
            .withRestClient(builder.restClient)
            .build();

        this.jwKeysetService = new JWKeysetService.Builder()
            .withRestClient(builder.restClient)
            .withICache(builder.cache)
            .build();

        this.mobileConnectInterface = new MobileConnectInterface.Builder()
            .withAuthnService(this.authnService)
            .withDiscoveryService(this.discoveryService)
            .withIdentityService(this.identityService)
            .withJwKeysetService(this.jwKeysetService)
            .withJsonService(builder.jsonService)
            .withiMobileConnectEncodeDecoder(this.iMobileConnectEncoderDecoder)
            .withConfig(builder.config)
            .build();

        this.mobileConnectWebInterface = new MobileConnectWebInterface.Builder()
            .withAuthnService(this.authnService)
            .withDiscoveryService(this.discoveryService)
            .withIdentityService(this.identityService)
            .withJwKeysetService(this.jwKeysetService)
            .withJsonService(builder.jsonService)
            .withConfig(builder.config)
            .build();

        LOGGER.info("Construction of new MobileConnect instance complete");
    }

    /**
     * Builds a MobileConnect with all defaults.
     *
     * @param config must be specified.
     * @param iMobileConnectEncodeDecoder An object that extends {@link IMobileConnectEncodeDecoder}. Defaults to {@link DefaultEncodeDecoder}
     * @return constructed MobileConnect instance.
     */
    public static MobileConnect build(final MobileConnectConfig config, final IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder, ConcurrentCache cache, ConcurrentCache discoveryCache)
    {
        return builder(config, iMobileConnectEncodeDecoder, cache, discoveryCache).build();
    }

    /**
     * Builds a MobileConnect with all defaults.
     *
     * @param config must be specified.
     * @return constructed MobileConnect instance.
     */
    public static MobileConnect build(final MobileConnectConfig config,  ConcurrentCache cache, ConcurrentCache discoveryCache)
    {
        return builder(config, new DefaultEncodeDecoder(), cache, discoveryCache).build();
    }

    /**
     * Builds a MobileConnect with all defaults and returns only the interface.
     *
     * @param config must be specified.
     * @return constructed MobileConnectInterface instance.
     */
    public static MobileConnectInterface buildInterface(final MobileConnectConfig config,
        final IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder,  ConcurrentCache cache, ConcurrentCache discoveryCache)
    {
        return build(config, iMobileConnectEncodeDecoder, cache, discoveryCache).getMobileConnectInterface();
    }

    /**
     * Builds a MobileConnectWebInterface with all defaults.
     *
     * @param config must be specified.
     * @return constructed MobileConnectWebInterface instance.
     */
    public static MobileConnectWebInterface buildWebInterface(final MobileConnectConfig config,
        final IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder, final ConcurrentCache cache, final ConcurrentCache discoveryCache)
    {
        return build(config, iMobileConnectEncodeDecoder, cache, discoveryCache).getMobileConnectWebInterface();
    }

    /**
     * Convenience method for accessing {@link Builder} to override defaults.
     *
     * @param config must be specified.
     * @return MobileConnect IBuilder instance.
     */
    public static Builder builder(final MobileConnectConfig config,
        IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder,
                                  ConcurrentCache cache, ConcurrentCache discoveryCache)
    {
        return new Builder(config, iMobileConnectEncodeDecoder, cache, discoveryCache);
    }

    /**
     * The configured discovery service.
     *
     * @return discovery service instance.
     */
    public IDiscoveryService getDiscoveryService()
    {
        return this.discoveryService;
    }

    /**
     * The configured authentication service.
     *
     * @return authentication service instance.
     */
    public IAuthenticationService getAuthnService()
    {
        return this.authnService;
    }

    /**
     * The configured identity service.
     *
     * @return identity service instance.
     */
    public IIdentityService getIdentityService()
    {
        return this.identityService;
    }

    /**
     * The configured MobileConnectInterface instance.
     *
     * @return mobile connect interface instance.
     */
    public MobileConnectInterface getMobileConnectInterface()
    {
        return this.mobileConnectInterface;
    }

    /**
     * The configured MobileConnectWebInterface instance.
     *
     * @return mobile connect web interface instance.
     */
    public MobileConnectWebInterface getMobileConnectWebInterface()
    {
        return this.mobileConnectWebInterface;
    }

    /**
     * Builds a configured instance of MobileConnect.
     */
    public static final class Builder implements IBuilder<MobileConnect>
    {
        private final IJsonService jsonService;
        private final MobileConnectConfig config;
        private IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder;

        private ICache cache = null;
        private ICache discoveryCache = null;
        private ScheduledExecutorService scheduledExecutorService = null;
        private HttpClient httpClient = null;
        private TimeUnit timeoutTimeUnit = TimeUnit.MILLISECONDS;
        private Long timeoutDuration = DefaultOptions.TIMEOUT_MS;
        private IRestClient restClient = null;

        /**
         * Start the builder, specifying the required configuration.  The defaults applied by this
         * builder are as follows: <ul> <li>scheduledExecutorService will use {@link
         * Executors#newScheduledThreadPool(int)} with core size of {@link
         * DefaultOptions#THREAD_POOL_SIZE}</li> <li>httpClient will use default result of {@link
         * HttpClientBuilder}</li> <li>http timeout will be set to {@link
         * DefaultOptions#TIMEOUT_MS}</li><li>restClient will use {@link RestClient}, with timeout
         * and http client above</li><li>cache will use {@link ConcurrentCache}</li></ul><p>Note
         * that specifying a rest client instance will overrule any setting of http client, or
         * timeout duration.</p>
         *
         * @param config for Mobile Connect.
         */
        public Builder(final MobileConnectConfig config,
            IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder,
                       ConcurrentCache cache, ConcurrentCache discoveryCache)
        {
            this.jsonService = new JacksonJsonService();
            this.iMobileConnectEncodeDecoder = iMobileConnectEncodeDecoder;
            this.config = ObjectUtils.requireNonNull(config, "config");
            this.cache = cache;
            this.discoveryCache = discoveryCache;
        }

        /**
         * Specify a configured executor service to use.
         *
         * @param val executor service to be used.
         * @return builder to continue further configuration.
         */
        public Builder withScheduledExecutorService(final ScheduledExecutorService val)
        {
            this.scheduledExecutorService = val;
            return this;
        }

        /**
         * Specify a {@link IMobileConnectEncodeDecoder}
         * to use.
         *
         * @param val encode/decoder to be used.
         * @return builder to continue further configuration.
         */
        public Builder withIMobileConnectEncodeDecoder(
            final IMobileConnectEncodeDecoder val)
        {
            this.iMobileConnectEncodeDecoder = val;
            return this;
        }

        /**
         * Specify a configured HttpClient to use.
         *
         * @param val http client to be used.
         * @return builder to continue further configuration.
         */
        public Builder withHttpClient(final HttpClient val)
        {
            this.httpClient = val;
            return this;
        }

        /**
         * Specify the timeout for HTTP connections.
         *
         * @param duration the number of units.
         * @param unit     the unit of the duration.
         * @return builder to continue further configuration.
         */
        public Builder withHttpTimeout(final long duration, final TimeUnit unit)
        {
            this.timeoutDuration = duration;
            this.timeoutTimeUnit = unit;
            return this;
        }

        /**
         * Specify a configured cache to use.
         *
         * @param val cache to be used.
         * @return builder to continue further configuration.
         */
        public Builder withCache(final ICache val)
        {
            this.cache = val;
            return this;
        }

        /**
         * Specify a configured rest client to use.  Note that setting this will result in any
         * configuration of http client or timeout to be ignored.
         *
         * @param val rest client to be used.
         * @return builder to continue further configuration.
         */
        public Builder withRestClient(final IRestClient val)
        {
            this.restClient = val;
            return this;
        }

        /**
         * Create an instance of MobileConnect that will provide access to the full suite of
         * MobileConnect interfaces.
         *
         * @return configured MobileConnect instance.
         */
        @Override
        public MobileConnect build()
        {
            if (this.scheduledExecutorService == null)
            {
                LOGGER.info("Using Executors#newScheduledThreadPool with corePoolSize={}",
                    DefaultOptions.THREAD_POOL_SIZE);
                this.scheduledExecutorService =
                    Executors.newScheduledThreadPool(DefaultOptions.THREAD_POOL_SIZE);
            }

            if (this.restClient == null)
            {
                if (this.httpClient == null)
                {
                    LOGGER.info("Building default instance of HttpClient");
                    this.httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
                }

                LOGGER.info("Building RestClient with timeout of duration={}, unit={}",
                    this.timeoutDuration, this.timeoutTimeUnit.name());
                this.restClient = new RestClient.Builder()
                    .withHttpClient(this.httpClient)
                    .withJsonService(this.jsonService)
                    .withTimeout(this.timeoutDuration, this.timeoutTimeUnit)
                    .build();
            }

            if (this.cache == null)
            {
                LOGGER.info("Building default instance of ConcurrentCache");
                this.cache =
                    new DiscoveryCache.Builder().withJsonService(this.jsonService).build();
            }

            return new MobileConnect(this);
        }
    }
}
