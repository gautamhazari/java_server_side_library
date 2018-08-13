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

/**
 * @since 2.0
 */
public final class ByteUtils
{
    /**
     * Private Constructor
     */
    ByteUtils()
    {
        /*
        Empty default constructor
        */
    }
    /**
     * Helper method to assure 0x00 byte at start of byte array
     *
     * @param bytes     byte array to prepend zero prefix
     * @return          byte array with zero prepended
     */
    public static byte[] addZeroPrefix(byte[] bytes)
    {
        byte[] updated = new byte[bytes.length + 1];
        updated[0] = (byte) 0;
        System.arraycopy(bytes, 0, updated, 1, bytes.length);
        return updated;
    }
}
