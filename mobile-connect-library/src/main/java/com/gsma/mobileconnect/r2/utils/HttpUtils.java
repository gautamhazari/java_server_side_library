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
package com.gsma.mobileconnect.r2.utils;

import com.gsma.mobileconnect.r2.model.ErrorResponse;
import com.gsma.mobileconnect.r2.model.constants.Headers;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @since 2.0
 */
public final class HttpUtils
{
    private static final Pattern AUTH_ERROR_REGEX = Pattern.compile("error\\s?=\\s?\"(.*?)\"");
    private static final Pattern AUTH_ERROR_DESC_REGEX =
        Pattern.compile("error_description\\s?=\\s?\"(.*?)\"");

    private HttpUtils()
    {
    }

    /**
     * Creates an {@link ErrorResponse} instance from the WWW-Authenticate header.
     *
     * @param headers to search for header.
     * @return ErrorResponse instance if header was found, otherwise null.
     */
    public static ErrorResponse generateAuthenticationError(final List<KeyValuePair> headers)
    {
        ErrorResponse errorResponse = null;

        final String value = KeyValuePair.findFirst(headers, HttpHeaders.WWW_AUTHENTICATE);

        if (!StringUtils.isNullOrEmpty(value))
        {
            final Matcher error = AUTH_ERROR_REGEX.matcher(value);
            final Matcher errorDesc = AUTH_ERROR_DESC_REGEX.matcher(value);
            if (error.find() && errorDesc.find())
            {
                errorResponse = new ErrorResponse.Builder()
                    .withError(error.group(1))
                    .withErrorDescription(errorDesc.group(1))
                    .build();
            }
        }
        return errorResponse;
    }

    /**
     * Returns true if the HTTP status code indicates an error (400s and 500s).
     *
     * @param statusCode to inspect.
     * @return true if statusCode is an error.
     */
    public static boolean isHttpErrorCode(int statusCode)
    {
        return statusCode >= 400;
    }

    /**
     * Returns the unencoded query parameter value, if it is present.
     *
     * @param uri to search
     * @param key to search for
     * @return value of key, or null if not found.
     */
    public static String extractQueryValue(final URI uri, final String key)
    {
        ObjectUtils.requireNonNull(uri, "uri");
        StringUtils.requireNonEmpty(key, "key");

        final List<NameValuePair> params = URLEncodedUtils.parse(uri, "UTF-8");

        final NameValuePair param = ListUtils.firstMatch(params, new Predicate<NameValuePair>()
        {
            @Override
            public boolean apply(NameValuePair input)
            {
                return key.equals(input.getName());
            }
        });

        return param != null ? param.getValue() : null;
    }

    /**
     * Filters an Iterable of KeyValuePairs, retaining all those with a key found in the required
     * list.
     *
     * @param required  keys
     * @param available KeyValuePairs to filter.
     * @return the filtered list of KeyValuePairs.
     */
    public static List<KeyValuePair> proxyRequired(final List<String> required,
        final Iterable<KeyValuePair> available)
    {
        ObjectUtils.requireNonNull(required, "required");

        final List<KeyValuePair> filtered = new ArrayList<KeyValuePair>();

        if (available != null)
        {
            for (final KeyValuePair pair : available)
            {
                if (required.contains(pair.getKey()))
                {
                    filtered.add(pair);
                }
            }
        }

        return filtered;
    }

    /**
     * Extract the client IP passed on the request.  An IP passed in the X_FORWARDED_FOR header,
     * otherwise falls back to the ip defined on the request.
     *
     * @param request to extract IP from.
     * @return the client IP address.
     */
    public static String extractClientIp(final HttpServletRequest request) {
        String ip = request.getHeader(Headers.X_FORWARDED_FOR);
        if (StringUtils.isNullOrEmpty(ip)) {
            ip = request.getRemoteAddr();
        } else {
            String ips[] = ip.split(",");
            for (String genIp : ips) {
                if (isValidPublicIp(genIp.trim())) {
                    ip = genIp.trim();
                    break;
                }
            }
        }
        return ip;
    }

    /**
     * Checks that ip address is valid
     * @param ip - ip address
     * @return true if is valid
     */
    public static boolean isValidPublicIp(String ip) {
        Inet4Address address;
        try {
            address = (Inet4Address) InetAddress.getByName(ip);
    } catch (UnknownHostException exception) {
        return false; // assuming no logging, exception handling required
    }
        return !(address.isSiteLocalAddress() ||
            address.isAnyLocalAddress() ||
            address.isLinkLocalAddress() ||
            address.isLoopbackAddress() ||
            address.isMulticastAddress());
    }

    /**
     * Extracts the cookies defined on an {@link HttpServletRequest} as a list of {@link
     * KeyValuePair}s.
     *
     * @param request to read cookies from.
     * @return iterable of KeyValuePairs.  Empty if there were no cookies.
     */
    public static Iterable<KeyValuePair> extractCookiesFromRequest(final HttpServletRequest request)
    {
        ObjectUtils.requireNonNull(request, "request");

        final KeyValuePair.ListBuilder builder = new KeyValuePair.ListBuilder();

        if (request.getCookies() != null)
        {
            for (final Cookie cookie : request.getCookies())
            {
                builder.add(cookie.getName(), cookie.getValue());
            }
        }

        return builder.build();
    }

    /**
     * Extracts all cookies defined in the headers.  Each cookie is expected to be included as an
     * individual "Set-Cookie" header, only the name and value are retained in the format
     * "key=value".
     *
     * @param headers to search
     * @return all cookies found as StringUtils, or empty if none found.
     */
    public static List<String> extractCookiesFromHeaders(final Iterable<KeyValuePair> headers)
    {
        final List<String> cookies = new ArrayList<String>();

        if (headers != null)
        {
            final Iterable<KeyValuePair> setCookies =
                ListUtils.allMatches(headers, KeyValuePair.keyMatches(Headers.SET_COOKIE));

            for (final KeyValuePair cookie : setCookies)
            {
                final String value = cookie.getValue();

                final int eqIndex = value.indexOf('=');
                final int scIndex = value.indexOf(';');

                if (eqIndex >= 0)
                {
                    extractCookie(cookies, value, scIndex, eqIndex);
                }
            }
        }

        return cookies;
    }

    private static void extractCookie(final List<String> cookies, final String value,
        final int scIndex, final int eqIndex)
    {
        if (scIndex > eqIndex)
        {
            cookies.add(value.substring(0, scIndex));
        }
        else
        {
            cookies.add(value);
        }
    }


    /**
     * Fetch the full URL has issued by the client browser including the query parameters.
     *
     * @param request to extract URL from
     * @return complete URL including query parameters.
     */

    public static URI extractCompleteUrl(final HttpServletRequest request)
    {
        ObjectUtils.requireNonNull(request, "request");

        return URI.create(request.getRequestURL() + "?" + request.getQueryString());
    }

    /**
     * Http Methods used by Mobile Connect.
     */
    public enum HttpMethod
    {
        GET, POST
    }
}
