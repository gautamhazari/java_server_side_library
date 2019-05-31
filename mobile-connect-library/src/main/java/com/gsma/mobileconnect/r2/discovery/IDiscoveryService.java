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

import com.gsma.mobileconnect.r2.exceptions.InvalidResponseException;
import com.gsma.mobileconnect.r2.cache.CacheAccessException;
import com.gsma.mobileconnect.r2.cache.ICache;
import com.gsma.mobileconnect.r2.exceptions.RequestFailedException;
import com.gsma.mobileconnect.r2.utils.KeyValuePair;

import java.net.URI;
import java.util.concurrent.Future;

/**
 * Interface for Mobile Connect Discovery requests.  All methods may throw {@link
 * NullPointerException} where a required parameter is missing or an {@link
 * IllegalArgumentException} where a required String parameter is provided non-empty.
 *
 * @see DiscoveryOptions
 * @see DiscoveryResponse
 * @since 2.0
 */
public interface IDiscoveryService
{

    ProviderMetadata retrieveProviderMetadata(final URI url, final boolean useCache);

    /**
     * @return the discovery response cache.
     */
    ICache getCache();

    /**
     * Synchronous wrapper for {@link IDiscoveryService#startAutomatedOperatorDiscoveryAsync(
     *String, String, URI, URI, DiscoveryOptions, Iterable)}
     *
     * @param clientId       The registered application clientId (Required)
     * @param clientSecret   the registered application client secret (Required)
     * @param discoveryUrl   The URL of the discovery endpoint (Required)
     * @param redirectUrl    The URL of the operator selection functionality redirects to.
     *                       (Required)
     * @param options        Optional parameters
     * @param currentCookies List of the current cookies sent by the browser if applicable
     * @return the discovery response.
     * @throws RequestFailedException   if there is a failure to issue the HTTP request.
     * @throws InvalidResponseException if there is a failure processing the HTTP response.
     */
    DiscoveryResponse startAutomatedOperatorDiscovery(final String clientId,
                                                      final String clientSecret, final URI discoveryUrl, final URI redirectUrl,
                                                      final DiscoveryOptions options, final Iterable<KeyValuePair> currentCookies)
            throws RequestFailedException, InvalidResponseException;

    /**
     * Synchronous wrapper for {@link IDiscoveryService#startAutomatedOperatorDiscoveryAsync(
     *IPreferences, URI, DiscoveryOptions, Iterable)}
     *
     * @param preferences    Instance of IPreferences that provides clientId, clientSecret and
     *                       discoveryUrl (Required)
     * @param redirectUrl    The URL of the operator selection functionality redirects to.
     *                       (Required)
     * @param options        Optional parameters
     * @param currentCookies List of the current cookies sent by the browser if applicable
     * @return the discovery response.
     * @throws RequestFailedException   if there is a failure to issue the HTTP request.
     * @throws InvalidResponseException if there is a failure processing the HTTP response.
     */
    DiscoveryResponse startAutomatedOperatorDiscovery(final IPreferences preferences, final URI discoveryUrl,
                                                      final URI redirectUrl, final DiscoveryOptions options,
                                                      final Iterable<KeyValuePair> currentCookies)
            throws RequestFailedException, InvalidResponseException;

    /**
     * Allows an application to conduct discovery based on the predetermined operator/network
     * identified operator semantics. If the operator cannot be identified the function will return
     * the 'operator selection' form of the response. The application can then determine how to
     * proceed i.e. open the operator selection page separately or otherwise handle this. <p> The
     * operator selection functionality will display a series of pages that enables the user to
     * identify an operator, the results are passed back to the current application as parameters on
     * the redirect URL.</p> <p> Valid discovery responses can be cached and this method can return
     * cached data.</p>
     *
     * @param clientId       The registered application clientId (Required)
     * @param clientSecret   the registered application client secret (Required)
     * @param discoveryUrl   The URL of the discovery endpoint (Required)
     * @param redirectUrl    The URL of the operator selection functionality redirects to.
     *                       (Required)
     * @param options        Optional parameters
     * @param currentCookies List of the current cookies sent by the browser if applicable
     * @return the discovery response.
     */
    Future<DiscoveryResponse> startAutomatedOperatorDiscoveryAsync(final String clientId,
                                                                   final String clientSecret, final URI discoveryUrl, final URI redirectUrl,
                                                                   final DiscoveryOptions options, final Iterable<KeyValuePair> currentCookies);

    /**
     * Convenience version of {@link IDiscoveryService#startAutomatedOperatorDiscoveryAsync(
     *String, String, URI, URI, DiscoveryOptions, Iterable)} where the clientId,
     * clientSecret and discoveryUrl are provided by the IPreferences implementation
     *
     * @param preferences    Instance of IPreferences that provides clientId, clientSecret and
     *                       discoveryUrl (Required)
     * @param redirectUrl    The URL of the operator selection functionality redirects to.
     *                       (Required)
     * @param options        Optional parameters
     * @param currentCookies List of the current cookies sent by the browser if applicable
     * @return the discovery response.
     */
    Future<DiscoveryResponse> startAutomatedOperatorDiscoveryAsync(final IPreferences preferences, final URI discoveryUrl,
                                                                   final URI redirectUrl, final DiscoveryOptions options,
                                                                   final Iterable<KeyValuePair> currentCookies);



    /**
     * Synchronous wrapper for {@link IDiscoveryService#getOperatorSelectionURLAsync(
     *String, String, URI, URI)}
     *
     * @param clientId     The registered application client id. (Required)
     * @param clientSecret The registered application client secret. (Required)
     * @param discoveryUrl The URL of the discovery end point. (Required)
     * @param redirectUrl  The URL the operator selection functionality redirects to. (Required)
     * @return the discovery response.
     * @throws RequestFailedException   if a failure occurred making the HTTP request.
     * @throws InvalidResponseException if a failure occurred processing the HTTP response.
     */
    DiscoveryResponse getOperatorSelectionURL(final String clientId, final String clientSecret,
                                              final URI discoveryUrl, final URI redirectUrl)
            throws RequestFailedException, InvalidResponseException;

    /**
     * Synchronous wrapper for
     * {@link IDiscoveryService#getOperatorSelectionURLAsync(IPreferences, URI)}
     *
     * @param preferences Instance of IPreferences that provides clientId, clientSecret and
     *                    discoveryUrl (Required)
     * @param redirectUrl The URL the operator selection functionality redirects to. (Required)
     * @throws RequestFailedException   if a failure occurred making the HTTP request.
     * @throws InvalidResponseException if a failure occurred processing the HTTP response.
     */
    DiscoveryResponse getOperatorSelectionURL(final IPreferences preferences, final URI discoveryUrl, final URI redirectUrl)
            throws RequestFailedException, InvalidResponseException;

    /**
     * Allows an application to get the URL for the operator selection UI of the discovery service.
     * This will not reference the discovery result cache. The returned URL will contain a session
     * id created by the discovery server. The URL must be used as-is.
     *
     * @param clientId     The     registered application client id. (Required)
     * @param clientSecret The registered application client secret. (Required)
     * @param discoveryUrl The URL of the discovery end point. (Required)
     * @param redirectUrl  The  URL the operator selection functionality redirects to. (Required)
     * @return the discovery response.
     */
    Future<DiscoveryResponse> getOperatorSelectionURLAsync(final String clientId,
                                                           final String clientSecret, final URI discoveryUrl, final URI redirectUrl);

    /**
     * Convenience wrapper for {@link IDiscoveryService#getOperatorSelectionURLAsync(
     *String, String, URI, URI)} where the clientId, clientSecret and discoveryUrl are provided by
     * the IPreferences implementation
     *
     * @param preferences Instance of IPreferences that provides clientId, clientSecret and
     *                    discoveryUrl (Required)
     * @param redirectUrl The URL the operator selection functionality redirects to. (Required)
     * @return the discovery response.
     */
    Future<DiscoveryResponse> getOperatorSelectionURLAsync(final IPreferences preferences, final URI discoveryUrl,
                                                           final URI redirectUrl);

    /**
     * Allows an application to obtain parameters which have been passed within a discovery redirect
     * URL <p>The function will parse the redirect URL and parse out the components expected for
     * discovery i.e.</p> <ul> <li>selectedMCC</li> <li>selectedMNC</li> <li>encryptedMSISDN</li>
     * </ul>
     *
     * @param redirectedUrl The URL the operator selection functionality redirected to (Required)
     * @return the parsed discovery redirect.
     */
    ParsedDiscoveryRedirect parseDiscoveryRedirect(URI redirectedUrl);

    /**
     * Allows an application to use the selected operator MCC and MNC to obtain the discovery
     * response. In the case there is already a discovery result in the cache and the
     * Selected-MCC/Selected-MNC in the new request are the same as relates to the discovery result
     * for the cached result, the cached result will be returned. <p> If the operator cannot be
     * identified by the discovery service the function will return the 'operator selection' form of
     * the response.</p>
     *
     * @param clientId     The registered application clientId (Required)
     * @param clientSecret the registered application client secret (Required)
     * @param discoveryUrl The URL of the discovery endpoint (Required)
     * @param redirectUrl  The registered application redirect url (Required)
     * @param selectedMCC  The Mobile Country Code of the selected operator. (Required)
     * @param selectedMNC  The Mobile Network Code of the selected operator. (Required)
     * @return the discovery response.
     * @throws RequestFailedException   if a failure occurred making the HTTP request.
     * @throws InvalidResponseException if a failure occurred processing the HTTP response.
     */
    DiscoveryResponse completeSelectedOperatorDiscovery(final String clientId,
                                                        final String clientSecret, final URI discoveryUrl, final URI redirectUrl,
                                                        final String selectedMCC, final String selectedMNC)
            throws RequestFailedException, InvalidResponseException;

    /**
     * Allows an application to use the selected operator MCC and MNC to obtain the discovery
     * response. In the case there is already a discovery result in the cache and the
     * Selected-MCC/Selected-MNC in the new request are the same as relates to the discovery result
     * for the cached result, the cached result will be returned. <p> If the operator cannot be
     * identified by the discovery service the function will return the 'operator selection' form of
     * the response.</p>
     *
     * @param preferences Instance of IPreferences that provides clientId, clientSecret and
     *                    discoveryUrl (Required)
     * @param redirectUrl The registered application redirect url (Required)
     * @param selectedMCC The Mobile Country Code of the selected operator. (Required)
     * @param selectedMNC The Mobile Network Code of the selected operator. (Required)
     * @return the discovery response
     * @throws RequestFailedException   if a failure occurred making the HTTP request.
     * @throws InvalidResponseException if a failure occurred processing the HTTP response.
     */
    DiscoveryResponse completeSelectedOperatorDiscovery(final IPreferences preferences, final URI discoveryUrl,
                                                        final URI redirectUrl, final String selectedMCC, final String selectedMNC)
            throws RequestFailedException, InvalidResponseException;

    /**
     * Asynchronous wrapper for {@link IDiscoveryService#completeSelectedOperatorDiscovery(
     *String, String, URI, URI, String, String)}
     *
     * @param clientId     The registered application clientId (Required)
     * @param clientSecret the registered application client secret (Required)
     * @param discoveryUrl The URL of the discovery endpoint (Required)
     * @param redirectUrl  The registered application redirect url (Required)
     * @param selectedMCC  The Mobile Country Code of the selected operator. (Required)
     * @param selectedMNC  The Mobile Network Code of the selected operator. (Required)
     * @return the discovery response.
     */
    Future<DiscoveryResponse> completeSelectedOperatorDiscoveryAsync(final String clientId,
                                                                     final String clientSecret, final URI discoveryUrl, final URI redirectUrl,
                                                                     final String selectedMCC, final String selectedMNC);

    /**
     * Convenience version of {@link IDiscoveryService#completeSelectedOperatorDiscovery(
     *String, String, URI, URI, String, String)} where the clientId, clientSecret and
     * discoveryUrl are provided by the IPreferences implementation
     *
     * @param preferences Instance of IPreferences that provides clientId, clientSecret and
     *                    discoveryUrl (Required)
     * @param redirectUrl The registered application redirect url (Required)
     * @param selectedMCC The Mobile Country Code of the selected operator. (Required)
     * @param selectedMNC The Mobile Network Code of the selected operator. (Required)
     * @return the discovery response.
     */
    Future<DiscoveryResponse> completeSelectedOperatorDiscoveryAsync(final IPreferences preferences, final URI discoveryUrl,
                                                                     final URI redirectUrl, final String selectedMCC, final String selectedMNC);

    /**
     * Helper function to extract operator selection URL from the discovery reponse
     *
     * @param result The discovery response to parse (Required)
     * @return The operator selection URL or null if not found
     */
    String extractOperatorSelectionURL(final DiscoveryResponse result);

    /**
     * Helper function which retrieves a discovery response (if available) from the discovery cache
     * which corresponds with the operator details
     *
     * @param mcc The Mobile Country Code (Required)
     * @param mnc The Mobile Network Code (Required)
     * @return A cached entry if found, otherwise null
     * @throws CacheAccessException if an error was encountered fetching the cache entry.
     */
    DiscoveryResponse getCachedDiscoveryResponse(final String mcc, final String mnc)
            throws CacheAccessException;

    /**
     * Helper function which clears the cache.  Note that this will clear everything from the cache,
     * not just DiscoveryResponses.
     *
     * @throws CacheAccessException if there was an error clearing the cache.
     */
    void clearCache() throws CacheAccessException;

    /**
     * Helper function which clears any result from the discovery cache which corresponds with the
     * provided parameters.
     * <p>If either mcc or mnc are null or empty the cache will be cleared.</p>
     *
     * @param mcc The mobile country code of the cached object (Required)
     * @param mnc The mobile network code of the cached object (Required)
     * @throws CacheAccessException if there was an error clearing the cache.
     */
    void clearCache(final String mcc, final String mnc) throws CacheAccessException;

    /**
     * Retrieves an updated version of the ProviderMetadata if available, the discovery response
     * property ProviderMetadata will also be updated with this version for future access.
     * <p>This method can trigger an HTTP GET request</p>
     *
     * @param response         Discovery response to retrieve provider metadata for
     * @param forceCacheBypass True if cache should be bypassed and the latest version of the
     *                         ProviderMetadata should be fetched from the provider metadata
     *                         endpoint. False if the cache should be tested first for a
     *                         non-setExpired ProviderMetadata before trying the provider metadata
     *                         endpoint.
     * @return An updated ProviderMetadata object
     */
    Future<ProviderMetadata> getProviderMetadata(final DiscoveryResponse response,
                                                 final boolean forceCacheBypass);
}
