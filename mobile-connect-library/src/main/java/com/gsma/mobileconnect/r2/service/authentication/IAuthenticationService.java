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
package com.gsma.mobileconnect.r2.service.authentication;

import com.gsma.mobileconnect.r2.service.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.service.discovery.OperatorUrls;
import com.gsma.mobileconnect.r2.service.discovery.ProviderMetadata;
import com.gsma.mobileconnect.r2.model.exceptions.HeadlessOperationFailedException;
import com.gsma.mobileconnect.r2.model.exceptions.InvalidResponseException;
import com.gsma.mobileconnect.r2.model.exceptions.RequestFailedException;
import com.gsma.mobileconnect.r2.model.json.JsonDeserializationException;

import java.net.URI;
import java.util.concurrent.Future;

/**
 * Interface for the Mobile Connect Requests
 *
 * @since 2.0
 */
public interface IAuthenticationService
{
    /**
     * Generates an authorisation url based on the supplied options and previous discovery response.
     *
     * @param clientId        The registered application ClientId (Required)
     * @param authorizeUrl    The authorization url returned by the discovery process (Required)
     * @param redirectUrl     On completion or error where the result information is sent using a
     *                        HTTP 302 redirect (Required)
     * @param state           Application specified unique scope value
     * @param nonce           Application specified nonce value. (Required)
     * @param encryptedMSISDN Encrypted MSISDN for user if returned from discovery service
     * @param versions        SupportedVersions from {@link ProviderMetadata} if null default
     *                        supported versions will be used to generate the auth url
     * @param options         Optional parameters
     */
    StartAuthenticationResponse startAuthentication(final String clientId,  //NOSONAR
                                                    final String correlationId, final URI authorizeUrl, final URI redirectUrl, final String state, final String nonce,
                                                    final String encryptedMSISDN, final AuthenticationOptions options,
                                                    final String currentVersion);

    /**
     * Synchronous wrapper for
     * {@link IAuthenticationService#requestTokenAsync(String, String, String, URI, URI, String)}
     *
     * @param clientId        The registered application ClientId (Required)
     * @param clientSecret    The registered application ClientSecret (Required)
     * @param requestTokenUrl The url for token requests recieved from the discovery process
     *                        (Required)
     * @param redirectUrl     Confirms the redirectURI that the application used when the
     *                        authorization request (Required)
     * @param code            The authorization code provided to the application via the call to the
     *                        authentication/authorization API (Required)
     * @throws RequestFailedException   on failure to access endpoint.
     * @throws InvalidResponseException on failure to process response from endpoint.
     */
    RequestTokenResponse requestToken(final String clientId, final String clientSecret, //NOSONAR
                                      final String correlationId, final URI requestTokenUrl, final URI redirectUrl, final String code,
                                      final boolean isBasicAuth)
        throws RequestFailedException, InvalidResponseException;

    /**
     * Allows an application to use the authorization code obtained from
     * authentication/authorization to obtain an access token and related information from the
     * authorization server. <p>This function requires a valid token url from the discovery process
     * and a valid code from the initial authorization call
     *
     * @param clientId        The registered application ClientId (Required)
     * @param clientSecret    The registered application ClientSecret (Required)
     * @param requestTokenUrl The url for token requests recieved from the discovery process
     *                        (Required)
     * @param redirectUrl     Confirms the redirectURI that the application used when the
     *                        authorization request (Required)
     * @param code            The authorization code provided to the application via the call to the
     *                        authentication/authorization API (Required)
     */
    Future<RequestTokenResponse> requestTokenAsync(final String clientId, final String clientSecret,
                                                   final String correlationId, final URI requestTokenUrl, final URI redirectUrl, final String code, final boolean isBasicAuth);

    /**
     * Initiates headless authentication, if authentication is successful a token will be returned.
     * This may be a long running operation as response from the user on their authentication device
     * is required.
     *
     * @param clientId         The application ClientId returned by the discovery process
     *                         (Required)
     * @param clientSecret     The ClientSecret returned by the discovery response (Required)
     * @param authorizationUrl The authorization url returned by the discovery process (Required)
     * @param requestTokenUrl  The token url returned by the discovery process (Required)
     * @param redirectUrl      On completion or error where the result information is sent using a
     *                         HTTP 302 redirect (Required)
     * @param state            Application specified unique state value (Required)
     * @param nonce            Application specified nonce value. (Required)
     * @param encryptedMsisdn  Encrypted MSISDN for user if returned from discovery service
     * @param versions         SupportedVersions from <see cref="ProviderMetadata"/> if null default
     *                         supported versions will be used to generate the auth url
     * @param options          Optional parameters
     * @return Token if headless authentication is successful
     */
    Future<RequestTokenResponse> requestHeadlessAuthentication(final String clientId, //NOSONAR
                                                               final String clientSecret, final String correlationId, final URI authorizationUrl, final URI requestTokenUrl,
                                                               final URI redirectUrl, final String state, final String nonce, final String encryptedMsisdn,
                                                               final AuthenticationOptions options, final String currentVersion, final boolean isBasicAuth)
        throws RequestFailedException, HeadlessOperationFailedException;

    /**
     * Allows an application to use the refresh token obtained from request token response and
     * request for a token refresh. <p> This function requires either a valid refresh token to be
     * provided
     *
     * @param clientId        The application ClientId returned by the discovery process (Required)
     * @param clientSecret    The ClientSecret returned by the discovery response (Required)
     * @param refreshTokenUrl The url for token refresh received from the discovery process
     *                        (Required)
     * @param refreshToken    Refresh token returned from RequestToken request
     */
    RequestTokenResponse refreshToken(final String clientId, final String clientSecret, //NOSONAR
                                      final URI refreshTokenUrl, final String refreshToken)
        throws RequestFailedException, InvalidResponseException;

    /**
     * Allows an application to use the access token or the refresh token obtained from
     * request token response and request for a token revocation.
     * <p>This function requires either a valid access token or a refresh token to be provided
     *
     * @param clientId        The application ClientId returned by the discovery process (Required)
     * @param clientSecret    The ClientSecret returned by the discovery response (Required)
     * @param refreshTokenUrl The url for token refresh received from the discovery process
     *                        (Required)
     * @param token           Access/Refresh token returned from RequestToken request
     * @param tokenTypeHint   Hint to indicate the type of token being passed in
     */
    String revokeToken(final String clientId, final String clientSecret, //NOSONAR
                       final URI refreshTokenUrl, final String token, final String tokenTypeHint)
        throws RequestFailedException, InvalidResponseException, JsonDeserializationException;

    /** Allows an application to create discovery object manually without call to discovery service
     *
     * @param clientSecret The registered application secretKey (Required)
     * @param clientKey The registered application clientKey (consumer key) (Required)
     * @param subscriberId subscriber id (Required)
     * @param name application name (Required)
     * @param operatorUrls operator specific urls returned from a successful discovery process call
     * @throws JsonDeserializationException on failure to process response from DiscoveryResponse, ProviderMetadata
     */
    DiscoveryResponse makeDiscoveryForAuthorization(String clientSecret, String clientKey, String name, OperatorUrls operatorUrls)
            throws JsonDeserializationException;
}
