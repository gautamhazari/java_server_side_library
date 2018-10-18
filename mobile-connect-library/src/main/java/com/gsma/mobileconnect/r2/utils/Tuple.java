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

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Simple tuple linking two related values.  Context will be applied by its use.
 *
 * @param <T1> the first item in the tuple.
 * @param <T2> the second item in the tuple.
 * @since 2.0
 */
public class Tuple<T1, T2>
{
    private final T1 first;
    private final T2 second;

    /**
     * Create the instance of the tuple.
     *
     * @param first  first item
     * @param second second item
     */
    public Tuple(final T1 first, final T2 second)
    {
        this.first = first;
        this.second = second;
    }

    /**
     * @return the first item.
     */
    @JsonIgnore
    public T1 getFirst()
    {
        return this.first;
    }

    /**
     * @return the second item.
     */
    @JsonIgnore
    public T2 getSecond()
    {
        return this.second;
    }
}
