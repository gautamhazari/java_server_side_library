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
package com.gsma.mobileconnect.r2.discovery;

import com.gsma.mobileconnect.r2.ErrorResponse;
import com.gsma.mobileconnect.r2.cache.CacheAccessException;
import com.gsma.mobileconnect.r2.cache.ICache;
import com.gsma.mobileconnect.r2.constants.LinkRels;
import com.gsma.mobileconnect.r2.constants.Parameters;
import com.gsma.mobileconnect.r2.encoding.DefaultEncodeDecoder;
import com.gsma.mobileconnect.r2.encoding.IMobileConnectEncodeDecoder;
import com.gsma.mobileconnect.r2.exceptions.InvalidResponseException;
import com.gsma.mobileconnect.r2.exceptions.RequestFailedException;
import com.gsma.mobileconnect.r2.json.IJsonService;
import com.gsma.mobileconnect.r2.json.JsonDeserializationException;
import com.gsma.mobileconnect.r2.json.Link;
import com.gsma.mobileconnect.r2.rest.IRestClient;
import com.gsma.mobileconnect.r2.rest.RestAuthentication;
import com.gsma.mobileconnect.r2.rest.RestResponse;
import com.gsma.mobileconnect.r2.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Concrete implementation of {@link IDiscoveryService}
 *
 * @since 2.0
 */
public class DiscoveryService implements IDiscoveryService
{
    private static final List<String> REQUIRED_COOKIES =
            Arrays.asList("ENUM_NONCE", "MOST_RECENT_SELECTED_OPERATOR",
                    "MOST_RECENT_SELECTED_OPERATOR_EXPIRY");

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoveryService.class);
    private static final String ARG_PREFERENCES = "preferences";

    private final ICache cache;
    private final IJsonService jsonService;
    private final IRestClient restClient;
    private final IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder;

    private DiscoveryService(final Builder builder)
    {
        this.cache = builder.cache;
        this.jsonService = builder.jsonService;
        this.restClient = builder.restClient;
        this.iMobileConnectEncodeDecoder = builder.iMobileConnectEncodeDecoder;

        LOGGER.info("New instance of DiscoveryService created");
    }

    /**
     * Concatenates MCC and MNC into a single key for use in the cache.
     *
     * @param mcc Mobile Country Code
     * @param mnc Mobile Network Code
     * @return concatenated key.
     */
    private static String concatKey(final String mcc, final String mnc)
    {
        String key = null;

        if (!StringUtils.isNullOrEmpty(mcc) && !StringUtils.isNullOrEmpty(mnc))
        {
            key = String.format("%s_%s", mcc, mnc);
        }

        return key;
    }

    @Override
    public ICache getCache()
    {
        return this.cache;
    }

    @Override
    public DiscoveryResponse startAutomatedOperatorDiscovery(final String clientId,
                                                             final String clientSecret, final URI discoveryUrl, final URI redirectUrl,
                                                             final DiscoveryOptions options, final Iterable<KeyValuePair> currentCookies)
            throws RequestFailedException, InvalidResponseException
    {
        final DiscoveryOptions.Builder builder =
                new DiscoveryOptions.Builder(options).withRedirectUrl(redirectUrl);

        return this.callDiscoveryEndpoint(clientId, clientSecret, discoveryUrl, builder.build(),
                currentCookies, true);
    }

    @Override
    public DiscoveryResponse startAutomatedOperatorDiscovery(final IPreferences preferences,
                                                             final URI redirectUrl, final DiscoveryOptions options,
                                                             final Iterable<KeyValuePair> currentCookies)
            throws RequestFailedException, InvalidResponseException
    {
        ObjectUtils.requireNonNull(preferences, ARG_PREFERENCES);

        return this.startAutomatedOperatorDiscovery(preferences.getClientId(),
                preferences.getClientSecret(), preferences.getDiscoveryUrl(), redirectUrl, options,
                currentCookies);
    }

    @Override
    public Future<DiscoveryResponse> startAutomatedOperatorDiscoveryAsync(final String clientId,
                                                                          final String clientSecret, final URI discoveryUrl, final URI redirectUrl,
                                                                          final DiscoveryOptions options, final Iterable<KeyValuePair> currentCookies)
    {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<DiscoveryResponse> discoveryResponseFuture = executorService.submit(new Callable<DiscoveryResponse>()
        {
            @Override
            public DiscoveryResponse call() throws Exception
            {
                return DiscoveryService.this.startAutomatedOperatorDiscovery(clientId, clientSecret,
                        discoveryUrl, redirectUrl, options, currentCookies);
            }
        });
        executorService.shutdownNow();
        return discoveryResponseFuture;
    }

    @Override
    public Future<DiscoveryResponse> startAutomatedOperatorDiscoveryAsync(
            final IPreferences preferences, final URI redirectUrl, final DiscoveryOptions options,
            final Iterable<KeyValuePair> currentCookies)
    {
        ObjectUtils.requireNonNull(preferences, ARG_PREFERENCES);

        return this.startAutomatedOperatorDiscoveryAsync(preferences.getClientId(),
                preferences.getClientSecret(), preferences.getDiscoveryUrl(), redirectUrl, options,
                currentCookies);
    }

    private DiscoveryResponse callDiscoveryEndpoint(final String clientId,
                                                    final String clientSecret, final URI discoveryUrl, final DiscoveryOptions options,
                                                    final Iterable<KeyValuePair> currentCookies, final boolean useCache)
            throws RequestFailedException, InvalidResponseException
    {
        StringUtils.requireNonEmpty(clientId, "clientId");
        StringUtils.requireNonEmpty(clientSecret, "clientSecret");
        ObjectUtils.requireNonNull(discoveryUrl, "discoveryUrl");
        ObjectUtils.requireNonNull(options, "options");
        ObjectUtils.requireNonNull(options.getRedirectUrl(), "options.redirectUrl");

        DiscoveryResponse cachedDiscoveryResponse = fetchCachedDiscoveryResponse(options, useCache);

        DiscoveryResponse discoveryResponse;
        final String correlationId = UUID.randomUUID().toString();

        if (cachedDiscoveryResponse != null && !cachedDiscoveryResponse.hasExpired())
        {
            discoveryResponse = cachedDiscoveryResponse;
        }
        else
        {
            final Iterable<KeyValuePair> cookies =
                    HttpUtils.proxyRequired(REQUIRED_COOKIES, currentCookies);
            final RestAuthentication authentication =
                    RestAuthentication.basic(clientId, clientSecret, iMobileConnectEncodeDecoder);
            final List<KeyValuePair> queryParams = this.extractQueryParams(options);
            if (options.getUsingCorrelationId()) {
                queryParams.add(new KeyValuePair(Parameters.CORRELATION_ID, correlationId));
            }

            RestResponse restResponse = null;

            try
            {
                restResponse = StringUtils.isNullOrEmpty(options.getMsisdn())
                        ? this.restClient.getDiscovery(discoveryUrl, authentication, options.getXRedirect(),
                        options.getClientIp(), options.getClientSideVersion(), options.getServerSideVersion(), queryParams, cookies)
                        : this.restClient.postDiscoveryFormData(discoveryUrl, authentication, options.getXRedirect(),
                        queryParams, options.getClientIp(), options.getClientSideVersion(), options.getServerSideVersion(), cookies);
            }
            catch (final RequestFailedException e)
            {
                LOGGER.warn("Failed to perform fetch of discovery response", e);
                if (cachedDiscoveryResponse == null)
                {
                    throw e;
                }
            }
            discoveryResponse = convertFromRestResponse(restResponse, cachedDiscoveryResponse);

            if (discoveryResponse.getErrorResponse() != null) {
                discoveryResponse = new DiscoveryResponse.Builder(discoveryResponse).withErrorResponse(new ErrorResponse
                        .Builder(discoveryResponse.getErrorResponse()).withErrorUri(restResponse.getUri().toString()).build()).build();
                ErrorResponse errorResponse = new ErrorResponse
                        .Builder(discoveryResponse.getErrorResponse()).withErrorUri(restResponse.getUri().toString()).build();
                System.out.println(errorResponse.getError());
                discoveryResponse = new DiscoveryResponse.Builder(discoveryResponse).withErrorResponse(errorResponse).build();
                System.out.println(discoveryResponse.getErrorResponse().getError());
            }

            this.addCachedDiscoveryResponse(options, discoveryResponse);
        }

        if (discoveryResponse == null && cachedDiscoveryResponse != null)
        {
            LOGGER.warn(
                    "Falling back to expired cached instance of discovery response due to previous error");
            discoveryResponse = cachedDiscoveryResponse;
        }

        updateWithProviderMetadata(discoveryResponse, useCache);

        if (discoveryResponse != null) {
            if (discoveryResponse.getErrorResponse() != null)
            {
                if (discoveryResponse.getErrorResponse().getCorrelationId() == null)
                {
                    LOGGER.warn("Error discovery response not contains correlation id");
                }
                else if (discoveryResponse.getErrorResponse().getCorrelationId().equals(correlationId)) {
                    LOGGER.info("Error discovery response match correlation id");
                } else {
                    throw new IllegalStateException("Invalid correlation id in the error discovery response");
                }
            }
            else
            {
                if (discoveryResponse.getResponseData().getCorrelationId() == null) {
                    LOGGER.warn("Discovery response not contains correlation id");
                }
                else if (discoveryResponse.getResponseData().getCorrelationId().equals(correlationId)) {
                    LOGGER.info("Discovery response match correlation id");
                } else {
                    throw new IllegalStateException("Invalid correlation id in the discovery response");
                }
            }
        }
        return discoveryResponse;
    }

    private DiscoveryResponse convertFromRestResponse(RestResponse restResponse,
                                                      DiscoveryResponse cachedDiscoveryResponse) throws InvalidResponseException
    {
        DiscoveryResponse discoveryResponse = null;
        try
        {
            discoveryResponse =
                    DiscoveryResponse.fromRestResponse(restResponse, this.jsonService);
        }
        catch (final JsonDeserializationException jde)
        {
            LOGGER.warn("Failed to fetch response from discovery service", jde);
            if (cachedDiscoveryResponse == null)
            {
                throw new InvalidResponseException(restResponse, DiscoveryResponse.class, jde);
            }
        }
        return discoveryResponse;
    }

    private void updateWithProviderMetadata(final DiscoveryResponse discoveryResponse,
                                            final boolean useCache)
    {
        if (discoveryResponse != null)
        {
            final URI url = this.extractProviderMetadataUrl(discoveryResponse);
            discoveryResponse.setProviderMetadata(this.retrieveProviderMetadata(url, useCache));
        }
    }

    private DiscoveryResponse fetchCachedDiscoveryResponse(final DiscoveryOptions options,
                                                           final boolean useCache)
    {
        DiscoveryResponse cachedDiscoveryResponse = null;
        if (useCache)
        {
            try
            {
                cachedDiscoveryResponse = this.getCachedDiscoveryResponse(options);
            }
            catch (final CacheAccessException cae)
            {
                LOGGER.warn("Failed to fetch cached discovery response", cae);
            }
        }
        return cachedDiscoveryResponse;
    }

    private DiscoveryResponse getCachedDiscoveryResponse(final DiscoveryOptions options)
            throws CacheAccessException
    {
        final String mcc =
                ObjectUtils.defaultIfNull(options.getIdentifiedMcc(), options.getSelectedMcc());
        final String mnc =
                ObjectUtils.defaultIfNull(options.getIdentifiedMnc(), options.getSelectedMnc());
        return this.cache != null
                ? this.cache.get(concatKey(mcc, mnc), DiscoveryResponse.class)
                : null;
    }

    public void addCachedDiscoveryResponse(final DiscoveryOptions options,
                                           final DiscoveryResponse response)
    {
        final String mcc =
                ObjectUtils.defaultIfNull(options.getIdentifiedMcc(), options.getSelectedMcc());
        final String mnc =
                ObjectUtils.defaultIfNull(options.getIdentifiedMnc(), options.getSelectedMnc());

        final String key = concatKey(mcc, mnc);

        if (response.getErrorResponse() == null && key != null)
        {
            try
            {
                this.cache.add(key, response);
            }
            catch (final CacheAccessException cae)
            {
                LOGGER.warn("Failed to store discovery response in cache", cae);
            }
        }
    }

    private List<KeyValuePair> extractQueryParams(final DiscoveryOptions options)
    {
        KeyValuePair.ListBuilder listBuilder = new KeyValuePair.ListBuilder()
                .addIfNotEmpty(Parameters.REDIRECT_URL, options.getRedirectUrl().toString())
                .addIfNotEmpty(Parameters.IDENTIFIED_MCC, options.getIdentifiedMcc())
                .addIfNotEmpty(Parameters.IDENTIFIED_MNC, options.getIdentifiedMnc())
                .addIfNotEmpty(Parameters.SELECTED_MCC, options.getSelectedMcc())
                .addIfNotEmpty(Parameters.SELECTED_MNC, options.getSelectedMnc())
                .addIfNotEmpty(Parameters.LOCAL_CLIENT_IP, options.getLocalClientIp())
                .addIfNotEmpty(Parameters.USING_MOBILE_DATA, options.isUsingMobileData() ? "1" : "0");

        if (options.getMsisdn() != null)
        {
            listBuilder.add(Parameters.MSISDN, StringUtils.trimLeading(options.getMsisdn(), '+'));
        }
        return listBuilder.build();
    }

    @Override
    public DiscoveryResponse getOperatorSelectionURL(final String clientId,
                                                     final String clientSecret, final URI discoveryUrl, final URI redirectUrl)
            throws RequestFailedException, InvalidResponseException
    {
        ObjectUtils.requireNonNull(redirectUrl, "redirectUrl");

        final DiscoveryOptions options =
                new DiscoveryOptions.Builder().withRedirectUrl(redirectUrl).build();
        return this.callDiscoveryEndpoint(clientId, clientSecret, discoveryUrl, options, null,
                false);
    }

    @Override
    public DiscoveryResponse getOperatorSelectionURL(final IPreferences preferences,
                                                     final URI redirectUrl) throws RequestFailedException, InvalidResponseException
    {
        ObjectUtils.requireNonNull(preferences, ARG_PREFERENCES);

        return this.getOperatorSelectionURL(preferences.getClientId(),
                preferences.getClientSecret(), preferences.getDiscoveryUrl(), redirectUrl);
    }

    @Override
    public Future<DiscoveryResponse> getOperatorSelectionURLAsync(final String clientId,
                                                                  final String clientSecret, final URI discoveryUrl, final URI redirectUrl)
    {
        StringUtils.requireNonEmpty(clientId, "clientId");
        StringUtils.requireNonEmpty(clientSecret, "clientSecret");
        ObjectUtils.requireNonNull(discoveryUrl, "discoveryUrl");
        ObjectUtils.requireNonNull(redirectUrl, "redirectUrl");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<DiscoveryResponse> discoveryResponseFuture = executorService.submit(new Callable<DiscoveryResponse>()
        {
            @Override
            public DiscoveryResponse call() throws Exception
            {
                return DiscoveryService.this.getOperatorSelectionURL(clientId, clientSecret,
                        discoveryUrl, redirectUrl);
            }
        });
        executorService.shutdownNow();
        return discoveryResponseFuture;
    }

    @Override
    public Future<DiscoveryResponse> getOperatorSelectionURLAsync(final IPreferences preferences,
                                                                  final URI redirectUrl)
    {
        ObjectUtils.requireNonNull(preferences, ARG_PREFERENCES);

        return this.getOperatorSelectionURLAsync(preferences.getClientId(),
                preferences.getClientSecret(), preferences.getDiscoveryUrl(), redirectUrl);
    }

    @Override
    public ParsedDiscoveryRedirect parseDiscoveryRedirect(final URI redirectedUrl)
    {
        ObjectUtils.requireNonNull(redirectedUrl, "redirectedUrl");

        final ParsedDiscoveryRedirect.Builder builder = new ParsedDiscoveryRedirect.Builder();

        if (!StringUtils.isNullOrEmpty(redirectedUrl.getQuery()))
        {
            final String mccMnc = HttpUtils.extractQueryValue(redirectedUrl, Parameters.MCC_MNC);

            if (mccMnc != null)
            {
                final String[] parts = mccMnc.split("_");
                if (parts.length == 2)
                {
                    builder.withSelectedMcc(parts[0]).withSelectedMnc(parts[1]);
                }
            }

            builder.withEncryptedMsisdn(
                    HttpUtils.extractQueryValue(redirectedUrl, Parameters.SUBSCRIBER_ID));
        }

        return builder.build();
    }

    @Override
    public DiscoveryResponse completeSelectedOperatorDiscovery(final String clientId,
                                                               final String clientSecret, final URI discoveryUrl, final URI redirectUrl,
                                                               final String selectedMCC, final String selectedMNC)
            throws RequestFailedException, InvalidResponseException
    {
        StringUtils.requireNonEmpty(selectedMCC, "selectedMCC");
        StringUtils.requireNonEmpty(selectedMNC, "selectedMNC");

        final DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder()
                .withRedirectUrl(redirectUrl)
                .withSelectedMcc(selectedMCC)
                .withSelectedMnc(selectedMNC)
                .build();

        return this.callDiscoveryEndpoint(clientId, clientSecret, discoveryUrl, discoveryOptions,
                null, true);
    }

    @Override
    public DiscoveryResponse completeSelectedOperatorDiscovery(final IPreferences preferences,
                                                               final URI redirectUrl, final String selectedMCC, final String selectedMNC)
            throws RequestFailedException, InvalidResponseException
    {
        ObjectUtils.requireNonNull(preferences, ARG_PREFERENCES);

        return this.completeSelectedOperatorDiscovery(preferences.getClientId(),
                preferences.getClientSecret(), preferences.getDiscoveryUrl(), redirectUrl, selectedMCC,
                selectedMNC);
    }

    @Override
    public Future<DiscoveryResponse> completeSelectedOperatorDiscoveryAsync(final String clientId,
                                                                            final String clientSecret, final URI discoveryUrl, final URI redirectUrl,
                                                                            final String selectedMCC, final String selectedMNC)
    {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<DiscoveryResponse> discoveryResponseFuture = executorService.submit(new Callable<DiscoveryResponse>()
        {
            @Override
            public DiscoveryResponse call() throws Exception
            {
                return DiscoveryService.this.completeSelectedOperatorDiscovery(clientId,
                        clientSecret, discoveryUrl, redirectUrl, selectedMCC, selectedMNC);
            }
        });
        executorService.shutdownNow();
        return discoveryResponseFuture;
    }

    @Override
    public Future<DiscoveryResponse> completeSelectedOperatorDiscoveryAsync(
            final IPreferences preferences, final URI redirectUrl, final String selectedMCC,
            final String selectedMNC)
    {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<DiscoveryResponse> discoveryResponseFuture = executorService.submit(new Callable<DiscoveryResponse>()
        {
            @Override
            public DiscoveryResponse call() throws Exception
            {
                return DiscoveryService.this.completeSelectedOperatorDiscovery(preferences,
                        redirectUrl, selectedMCC, selectedMNC);
            }
        });
        executorService.shutdownNow();
        return discoveryResponseFuture;
    }

    @Override
    public String extractOperatorSelectionURL(final DiscoveryResponse result)
    {
        ObjectUtils.requireNonNull(result, "result");

        String url = null;

        if (result.getResponseData() != null)
        {
            final Link link =
                    ListUtils.firstMatch(result.getResponseData().getLinks(), input -> LinkRels.OPERATOR_SELECTION.equalsIgnoreCase(input.getRel()));

            if (link != null)
            {
                url = link.getHref();
            }
        }

        return url;
    }

    @Override
    public DiscoveryResponse getCachedDiscoveryResponse(final String mcc, final String mnc)
            throws CacheAccessException
    {
        final DiscoveryResponse discoveryResponse = this.cache != null
                ? this.cache.get(concatKey(mcc, mnc),
                DiscoveryResponse.class)
                : null;
        if (discoveryResponse != null)
        {
            final URI providerMetadataUrl = this.extractProviderMetadataUrl(discoveryResponse);
            if (providerMetadataUrl != null)
            {
                discoveryResponse.setProviderMetadata(
                        this.cache.get(providerMetadataUrl.toString(), ProviderMetadata.class));
            }
        }
        return discoveryResponse;
    }

    @Override
    public void clearCache() throws CacheAccessException
    {
        this.cache.clear();
    }

    @Override
    public void clearCache(final String mcc, final String mnc) throws CacheAccessException
    {
        final String key = concatKey(mcc, mnc);
        if (key == null)
        {
            this.cache.clear();
        }
        else
        {
            this.cache.remove(concatKey(mcc, mnc));
        }
    }

    @Override
    public Future<ProviderMetadata> getProviderMetadata(final DiscoveryResponse response,
                                                        final boolean forceCacheBypass)
    {
        final URI providerMetadataUrl = this.extractProviderMetadataUrl(response);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<ProviderMetadata> providerMetadataFuture = executorService.submit(new Callable<ProviderMetadata>()
        {
            @Override
            public ProviderMetadata call() throws Exception
            {
                final ProviderMetadata providerMetadata =
                        DiscoveryService.this.retrieveProviderMetadata(providerMetadataUrl,
                                !forceCacheBypass);
                response.setProviderMetadata(providerMetadata);
                return providerMetadata;
            }
        });
        executorService.shutdownNow();
        return providerMetadataFuture;
    }

    private URI extractProviderMetadataUrl(final DiscoveryResponse response)
    {
        ObjectUtils.requireNonNull(response, "response");

        final OperatorUrls operatorUrls = response.getOperatorUrls();
        return operatorUrls == null || operatorUrls.getProviderMetadataUri() == null
                ? null
                : URI.create(operatorUrls.getProviderMetadataUri());
    }

    public ProviderMetadata retrieveProviderMetadata(final URI url, final boolean useCache)
    {
        ProviderMetadata providerMetadata = null;

        if (url != null)
        {
            ProviderMetadata cached = null;
            if (useCache)
            {
                try
                {
                    cached = this.cache.get(url.toString(), ProviderMetadata.class);
                }
                catch (final CacheAccessException cae)
                {
                    LOGGER.warn("Failed to fetch cached provider metadata", cae);
                }
            }

            if (cached == null || cached.hasExpired())
            {
                try
                {
                    final RestResponse restResponse =
                            this.restClient.get(url, null, null, null,null, null);

                    providerMetadata = processRestResponse(restResponse, url);
                }

                catch (final RequestFailedException ehe)
                {
                    LOGGER.warn("Failed to perform fetch of provider metadata from provider", ehe);
                }
            }

            if (providerMetadata == null && cached != null)
            {
                LOGGER.warn(
                        "Falling back to expired cached provider metadata due to previous error");
                providerMetadata = cached;
            }
        }
        else
        {
            LOGGER.debug("Provider metadata url was null, provider does not support metadata");
        }

        if (providerMetadata == null)
        {
            LOGGER.debug("Returning default (empty) instance of provider metadata");
            providerMetadata = new ProviderMetadata.Builder().build();
        }

        return providerMetadata;
    }

    private ProviderMetadata processRestResponse(final RestResponse restResponse, final URI url)
    {
        ProviderMetadata providerMetadata = null;
        try
        {
            if (!HttpUtils.isHttpErrorCode(restResponse.getStatusCode()))
            {
                providerMetadata =
                        this.jsonService.deserialize(restResponse.getContent(), ProviderMetadata.class);

                this.cache.add(url.toString(), providerMetadata);
            }
            else
            {
                LOGGER.warn(
                        "Received an error response with HTTP status {} for provider metadata from {}",
                        restResponse.getStatusCode(), url);
            }
        }
        catch (final JsonDeserializationException jde)
        {
            LOGGER.warn("Failed to deserialize provider metadata from provider", jde);
        }
        catch (final CacheAccessException cae)
        {
            LOGGER.warn("Failed to store provider metadata in cache", cae);
        }
        return providerMetadata;
    }


    public static final class Builder implements IBuilder<DiscoveryService>
    {
        private ICache cache;
        private IJsonService jsonService;
        private IRestClient restClient;
        private IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder;

        public Builder withCache(ICache val)
        {
            this.cache = val;
            return this;
        }

        public Builder withJsonService(IJsonService val)
        {
            this.jsonService = val;
            return this;
        }

        public Builder withRestClient(IRestClient val)
        {
            this.restClient = val;
            return this;
        }

        public Builder withIMobileConnectEncodeDecoder(IMobileConnectEncodeDecoder val)
        {
            this.iMobileConnectEncodeDecoder = val;
            return this;
        }

        @Override
        public DiscoveryService build()
        {
            ObjectUtils.requireNonNull(this.cache, "cache");
            ObjectUtils.requireNonNull(this.jsonService, "jsonService");
            ObjectUtils.requireNonNull(this.restClient, "restClient");
            if (iMobileConnectEncodeDecoder == null)
            {
                iMobileConnectEncodeDecoder = new DefaultEncodeDecoder();
            }

            return new DiscoveryService(this);
        }
    }
}
