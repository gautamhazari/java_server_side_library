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

import org.slf4j.Logger;
import org.slf4j.event.Level;

import java.net.URI;

/**
 * Utility methods for logging.
 *
 * @since 2.0
 */
public class LogUtils
{
    /**
     * Predefined list of URI params that will be obscured if they contain ignoring case any of the
     * values in this array.  More likely entries are listed first.
     */
    private static final String[] OBSCURE_PARAM_NAMES = {"nonce", "msisdn", "token"};

    /**
     * Predefined String of asterisks, used for cutting up for use in the {@link #mask(
     *String, Logger, Level)} method.
     */
    private static final String ASTERISK_MASK;

    static
    {
        final StringBuilder sb = new StringBuilder(100);
        for (int i = 0; i < 100; i++)
        {
            sb.append('*');
        }
        ASTERISK_MASK = sb.toString();
    }

    private LogUtils()
    {
        /*
        Private default constructor since all methods are static
         */
    }

    /**
     * Mask centre of a String with asterisks.  Will cap out at the length of ASTERISK_MASK for
     * number of asterisks included (i.e. a 300 character String).
     *
     * @param str    to maskUri.
     * @param logger to inspect for current logging level - nothing is actually logged within this
     *               method.
     * @param level  intended level of logging.
     * @return str masked, or null if it was null or the logger is not logging at level.
     */
    public static String mask(final String str, final Logger logger, final Level level)
    {
        if (isLevelEnabled(logger, level))
        {
            return mask(str);
        }
        else
        {
            return null;
        }
    }

    /**
     * Masks a String value.  This will always run irrespective of logging, and is suitable only for
     * use in masking values that will always be returned, for example messages to users and in
     * toString methods.
     *
     * @param str to maskUri.
     * @return masked value.
     * @see #mask(String, Logger, Level) for ability to run according to logging.
     */
    public static String mask(final String str)
    {
        if (StringUtils.isNullOrEmpty(str))
        {
            return str;
        }
        else
        {
            final int start = str.length() / 3;
            final int mask = (str.length() + 4) / 3;
            final int end = str.length() - ((str.length() - 1) / 3);

            return str.substring(0, start) + ASTERISK_MASK.substring(0,
                Math.min(mask, ASTERISK_MASK.length())) + str.substring(end);
        }
    }



    /**
     * Inspects a URI and masks any sensitive parameter values. The sensitive param names are
     * defined by OBSCURE_PARAM_NAMES, and are matched by searching the parameter name as to whether
     * it contains the name.
     *
     * @param uri    to mask
     * @param logger to inspect current logging level - nothing is actually logged within this
     *               method.
     * @param level  intended log level
     * @return masked uri, or null if logger is not logging at level.
     */
    public static String maskUri(final URI uri, final Logger logger, final Level level)
    {
        if (isLevelEnabled(logger, level))
        {
            return maskUri(uri.toString(), logger, level);
        }
        else
        {
            return null;
        }
    }

    /**
     * Inspects a URI and masks any sensitive parameter values. The sensitive param names are
     * defined by OBSCURE_PARAM_NAMES, and are matched by searching the parameter name as to whether
     * it contains the name.
     *
     * @param uriStr to mask
     * @param logger to inspect current logging level - nothing is actually logged within this
     *               method.
     * @param level  intended log level
     * @return masked uri, or null if logger is not logging at level.
     */
    public static String maskUri(final String uriStr, final Logger logger, final Level level)
    {
        if (isLevelEnabled(logger, level))
        {
            final int queryStart = uriStr.indexOf('?');
            if (queryStart == -1)
            {
                return uriStr;
            }
            else
            {
                return maskUriParams(uriStr, queryStart);
            }
        }
        else
        {
            return null;
        }
    }

    private static String maskUriParams(final String uri, final int queryStart)
    {
        final StringBuilder sb = new StringBuilder(uri.substring(0, queryStart + 1));

        final String[] params = uri.substring(queryStart + 1).split("&");
        for (int i = 0; i < params.length; i++)
        {
            final String param = params[i];
            final int eqIndex = param.indexOf('=');

            if (eqIndex == -1)
            {
                sb.append(param);
            }
            else
            {
                maskParam(sb, param, eqIndex);
            }

            if (i < params.length - 1)
            {
                sb.append('&');
            }
        }

        return sb.toString();
    }

    private static void maskParam(final StringBuilder sb, final String param, final int eqIndex)
    {
        final String name = param.substring(0, eqIndex);
        final String lcName = name.toLowerCase();
        for (final String obscureParam : OBSCURE_PARAM_NAMES)
        {
            if (lcName.contains(obscureParam))
            {
                sb.append(name).append('=').append(mask(param.substring(eqIndex + 1)));
                return;
            }
        }
        sb.append(param);
    }

    /**
     * Identify if the specified Logger will actually log at the level specified.
     *
     * @param logger to inspect.
     * @param level  to query.
     * @return true if level will be logged by logger.
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean isLevelEnabled(final Logger logger, final Level level)
    {
        boolean returnValue;

        switch (level)
        {
            case TRACE:
                returnValue = logger.isTraceEnabled();
                break;
            case DEBUG:
                returnValue = logger.isDebugEnabled();
                break;
            case INFO:
                returnValue = logger.isInfoEnabled();
                break;
            case WARN:
                returnValue = logger.isWarnEnabled();
                break;
            case ERROR:
                returnValue = logger.isErrorEnabled();
                break;
            default:
                returnValue = false;
        }
        return returnValue;
    }
}
