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
package com.gsma.mobileconnect.r2.rest;

import com.gsma.mobileconnect.r2.exceptions.RequestFailedException;
import com.gsma.mobileconnect.r2.utils.KeyValuePair;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;

import java.net.URI;
import java.util.List;

/**
 * Wrapper for Http requests, returning a simple normalised response object.
 *
 * @since 2.0
 */
public interface IRestClient
{
    /**
     * Executes a HTTP GET to the supplied uri optional basic auth and optional
     * cookies.
     *
     * @param uri            of the POST.
     * @param authentication value to be used (if auth required).
     * @param queryParams    to be added to the GET request.
     * @param sourceIp       of the request (if identified).
     * @param cookies        to add to the request (if required).
     * @return future RestResponse.
     * @throws RequestFailedException if there is a failure issuing the request.
     */
    RestResponse get(final URI uri, final RestAuthentication authentication, final String xRedirect, final String sourceIp,
                     final List<KeyValuePair> queryParams, final Iterable<KeyValuePair> cookies)
        throws RequestFailedException;

    /**
     * Executes a HTTP GET to the supplied uri optional basic auth and optional
     * cookies.
     *
     * @param uri            of the POST.
     * @param authentication value to be used (if auth required).
     * @param queryParams    to be added to the GET request.
     * @param sourceIp       of the request (if identified).
     * @param cookies        to add to the request (if required).
     * @return future RestResponse.
     * @throws RequestFailedException if there is a failure issuing the request.
     */
    RestResponse getDiscovery(final URI uri, final RestAuthentication authentication, final String xRedirect, final String sourceIp,
                              final List<KeyValuePair> queryParams, final Iterable<KeyValuePair> cookies)
            throws RequestFailedException;

    /**
     * Executes a HTTP POST to the supplied uri with x-www-form-urlencoded content and optional
     * cookies
     *
     * @param uri            of the POST.
     * @param authentication value to be used (if auth required).
     * @param formData       to be added to the POST request.
     * @param sourceIp       of the request (if identified).
     * @param cookies        to add to the request (if required).
     * @return future RestResponse.
     * @throws RequestFailedException if there is a failure issuing the request.
     */
    RestResponse postDiscoveryFormData(final URI uri, final RestAuthentication authentication, final String xRedirect,
                                       final List<KeyValuePair> formData, final String sourceIp,
                                       final Iterable<KeyValuePair> cookies) throws RequestFailedException;

    /**
     * Executes a HTTP POST to the supplied uri with x-www-form-urlencoded content and optional
     * cookies
     *
     * @param uri            of the POST.
     * @param authentication value to be used (if auth required).
     * @param formData       to be added to the POST request.
     * @param sourceIp       of the request (if identified).
     * @param cookies        to add to the request (if required).
     * @return future RestResponse.
     * @throws RequestFailedException if there is a failure issuing the request.
     */
    RestResponse postFormData(final URI uri, final RestAuthentication authentication, final String xRedirect,
                              final List<KeyValuePair> formData, final String sourceIp,
                              final Iterable<KeyValuePair> cookies) throws RequestFailedException;

    /**
     * Executes a HTTP POST to the supplied uri with the supplied content deserialize to json, with
     * optional cookies.
     *
     * @param uri            of the POST.
     * @param authentication value to be used (if auth required).
     * @param content        of the POST request to deserialise as json.
     * @param sourceIp       of the request (if identified).
     * @param cookies        to add to the request (if required).
     * @return future RestResponse.
     * @throws RequestFailedException if there is a failure issuing the request.
     */
    RestResponse postJsonContent(final URI uri, final RestAuthentication authentication,
                                 final Object content, final String sourceIp, final Iterable<KeyValuePair> cookies)
        throws RequestFailedException;

    /**
     * Executes a HTTP POST to the supplied uri with the supplied content type and content, with
     * optional cookies.
     *
     * @param uri            of the POST.
     * @param authentication value to be used (if auth required).
     * @param content        of the POST request.
     * @param contentType    of the POST request.
     * @param sourceIp       of the request (if identified).
     * @param cookies        to add to the request (if required).
     * @return future RestResponse.
     * @throws RequestFailedException if there is a failure issuing the request.
     */
    RestResponse postStringContent(final URI uri, final RestAuthentication authentication,
                                   final String content, final ContentType contentType, final String sourceIp,
                                   final Iterable<KeyValuePair> cookies) throws RequestFailedException;

    /**
     * Executes a HTTP POST to the supplied uri with the supplied HttpContent object, with optional
     * cookies. Used as the base for other PostAsync methods.
     *
     * @param uri            of the POST.
     * @param authentication value to be used (if auth required).
     * @param content        of the POST request.
     * @param sourceIp       of the request (if identified).
     * @param cookies        to add to the request (if required).
     * @return future RestResponse.
     * @throws RequestFailedException if there is a failure issuing the request.
     */
    RestResponse postContent(final URI uri, final RestAuthentication authentication,
                             final HttpEntity content, final String sourceIp, final Iterable<KeyValuePair> cookies)
        throws RequestFailedException;

    /**
     * Attempts to follow a redirect path until a concrete url is loaded or the expectedRedirectUrl
     * is reached
     *
     * @param authUrl        Target uri to attempt a HTTP GET
     * @param redirectUrl    Redirect url expected, if a redirect with this location is hit the
     *                       absolute uri of the location will be returned
     * @param authentication value to be used (if auth required).
     * @return Final redirected url
     */
    URI getFinalRedirect(final URI authUrl, final URI redirectUrl,
                         final RestAuthentication authentication) throws RequestFailedException;
}
