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

import com.gsma.mobileconnect.r2.encoding.DefaultEncodeDecoder;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.*;

/**
 * Tests {@link JsonWebTokens}
 *
 * @since 2.0
 */
public class JsonWebTokensTest
{
    @Test
    public void decodePartHeader()
    {
        final String validToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI0MTE0MjFCMC0zOEQ2LTY1NjgtQTUzQS1ERjk5NjkxQjdFQjYiLCJlbWFpbCI6InRlc3QyQGV4YW1wbGUuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWV9.AcpILNH2Uvok99MQWwxP6X7x3OwtVmTOw0t9Hq00gmQ";

        final String decodedHeader = JsonWebTokens.Part.HEADER.decode(validToken, new DefaultEncodeDecoder());

        assertNotNull(decodedHeader);
        String expectedHeader = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        assertEquals(expectedHeader, decodedHeader);
    }

    @Test
    public void decodePartPayload()
    {
        final String validToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI0MTE0MjFCMC0zOEQ2LTY1NjgtQTUzQS1ERjk5NjkxQjdFQjYiLCJlbWFpbCI6InRlc3QyQGV4YW1wbGUuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWV9.AcpILNH2Uvok99MQWwxP6X7x3OwtVmTOw0t9Hq00gmQ";

        final String decodedPayload = JsonWebTokens.Part.CLAIMS.decode(validToken, new DefaultEncodeDecoder());

        assertNotNull(decodedPayload);
        String expectedPayload = "{\"sub\":\"411421B0-38D6-6568-A53A-DF99691B7EB6\",\"email\":\"test2@example.com\",\"email_verified\":true}";
        assertEquals(expectedPayload, decodedPayload);
    }

    @Test
    public void decodePartSignatureRemainsAsToken()
    {
        final String validToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI0MTE0MjFCMC0zOEQ2LTY1NjgtQTUzQS1ERjk5NjkxQjdFQjYiLCJlbWFpbCI6InRlc3QyQGV4YW1wbGUuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWV9.AcpILNH2Uvok99MQWwxP6X7x3OwtVmTOw0t9Hq00gmQ";

        final String decodedSignature = JsonWebTokens.Part.SIGNATURE.decode(validToken, new DefaultEncodeDecoder());

        assertNotNull(decodedSignature);
        assertEquals(validToken, decodedSignature);
    }

    @Test
    public void testInvalidTokenFormat()
    {
        final boolean invalidToken = JsonWebTokens.isValidFormat("invalidToken");

        assertFalse(invalidToken);
    }

    @Test
    public void testValidTokenFormat()
    {
        final String validToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI0MTE0MjFCMC0zOEQ2LTY1NjgtQTUzQS1ERjk5NjkxQjdFQjYiLCJlbWFpbCI6InRlc3QyQGV4YW1wbGUuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWV9.AcpILNH2Uvok99MQWwxP6X7x3OwtVmTOw0t9Hq00gmQ";
        final boolean invalidToken = JsonWebTokens.isValidFormat(validToken);

        assertTrue(invalidToken);

    }
}
