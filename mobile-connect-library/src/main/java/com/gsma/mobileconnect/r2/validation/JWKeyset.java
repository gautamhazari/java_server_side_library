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
package com.gsma.mobileconnect.r2.validation;

import com.gsma.mobileconnect.r2.cache.AbstractCacheable;
import com.gsma.mobileconnect.r2.utils.ListUtils;
import com.gsma.mobileconnect.r2.utils.Predicate;

import java.util.List;

/**
 * JWKS retrieved from the JWKS endpoint
 *
 * @since 2.0
 */
public class JWKeyset extends AbstractCacheable
{
    private List<JWKey> keys;

    private JWKeyset()
    {
    }

    /**
     * @return All available keys
     */
    public List<JWKey> getKeys()
    {
        return keys;
    }

    /**
     * Return all keys matching the predicate
     *
     * @param predicate A function to test each key for eligibility
     * @return Ienumerable containing matching elements
     */
    public Iterable<JWKey> getMatching(Predicate<JWKey> predicate)
    {
        if (predicate == null || keys == null || keys.isEmpty())
        {
            return keys;
        }
        return ListUtils.allMatches(keys, predicate);
    }
}
