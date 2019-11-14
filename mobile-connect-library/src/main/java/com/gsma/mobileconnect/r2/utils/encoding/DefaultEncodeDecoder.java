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
package com.gsma.mobileconnect.r2.utils.encoding;

import org.apache.commons.codec.binary.Base64;

/**
 * Default implementation of the EncoderDecoder interface class
 *
 * @since 2.0
 */
public class DefaultEncodeDecoder implements IMobileConnectEncodeDecoder {

    /**
     * Method to encode byte arrays to base64
     *
     * @param value the array of byte to encode
     * @return Base64 encoded String
     */
    @Override
    public String encodeToBase64(byte[] value) {
        return Base64.encodeBase64String(value);
    }

    /**
     * Method to decode base64 strings into byte array
     *
     * @param value The string to to decode
     * @return decoded byte array
     */
    @Override
    public byte[] decodeFromBase64(String value) {
        return Base64.decodeBase64(value);
    }
}
