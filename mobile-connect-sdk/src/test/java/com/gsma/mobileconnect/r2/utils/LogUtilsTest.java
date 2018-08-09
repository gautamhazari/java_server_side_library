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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URI;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * Tests {@link LogUtils}
 *
 * @since 2.0
 */
public class LogUtilsTest
{
    @DataProvider
    public final Object[][] maskData()
    {
        return new Object[][] {{null, null}, {"", ""}, {"a", "*"}, {"ab", "**"}, {"abc", "a**"},
                               {"abcd", "a**d"}, {"abcde", "a***e"}, {"abcdef", "ab***f"},
                               {"abcdefg", "ab***fg"}, {"abcdefgh", "ab****gh"},
                               {"abcdefghi", "abc****hi"}, {"abcdefghij", "abc****hij"}};
    }

    @Test(dataProvider = "maskData")
    public void mask(final String str, final String expected)
    {
        final String actual = LogUtils.mask(str);

        assertEquals(actual, expected);
        if (str != null)
        {
            assertEquals(actual.length(), expected.length());
        }
    }

    @DataProvider
    public Object[][] maskUriData()
    {
        return new Object[][] {{"http://test/", "http://test/"},
                               {"http://test/?paramA=1&paramB=2", "http://test/?paramA=1&paramB=2"},
                               {"http://test/?paramA=1&paramB=2&msisdn=447712345678&expectedNonce=alphanumeric&paramC=3",
                                "http://test/?paramA=1&paramB=2&msisdn=4477*****678&expectedNonce=alph*****ric&paramC=3"},
                               {"https://discovery.integration.sandbox.mobileconnect.io/v2/discovery?MSISDN=&Redirect_URL=http%3A%2F%2Flocalhost%3A8001%2Fmobileconnect.html&Identified-MCC&Identified-MNC&Selected-MCC&Selected-MNC&Local-Client-IP&Using-Mobile-Data=0",
                                "https://discovery.integration.sandbox.mobileconnect.io/v2/discovery?MSISDN=&Redirect_URL=http%3A%2F%2Flocalhost%3A8001%2Fmobileconnect.html&Identified-MCC&Identified-MNC&Selected-MCC&Selected-MNC&Local-Client-IP&Using-Mobile-Data=0"}};
    }

    @Test(dataProvider = "maskUriData")
    public void maskUri(final String original, final String masked)
    {
        final Logger logger = this.getLogger(Level.INFO);
        assertEquals(LogUtils.maskUri(URI.create(original), logger, Level.INFO), masked);
    }

    @Test
    public void maskUri_notLogged()
    {
        final Logger logger = this.getLogger(Level.INFO);
        assertNull(LogUtils.maskUri(URI.create("http://test"), logger, Level.DEBUG));
    }

    private Logger getLogger(final Level level)
    {
        final Logger logger = mock(Logger.class);

        when(logger.isTraceEnabled()).thenReturn(Level.TRACE.compareTo(level) <= 0);
        when(logger.isDebugEnabled()).thenReturn(Level.DEBUG.compareTo(level) <= 0);
        when(logger.isInfoEnabled()).thenReturn(Level.INFO.compareTo(level) <= 0);
        when(logger.isWarnEnabled()).thenReturn(Level.WARN.compareTo(level) <= 0);
        when(logger.isErrorEnabled()).thenReturn(Level.ERROR.compareTo(level) <= 0);

        return logger;
    }
}
