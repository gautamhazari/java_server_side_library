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
package com.gsma.mobileconnect.r2.cache;

/**
 * Defines core functionality of cacheable items.
 *
 * @since 2.0
 */
public abstract class AbstractCacheable implements ICacheable
{
    private boolean cached = false;
    private boolean expired = false;

    void setCacheInfo(final CacheEntry cacheEntry)
    {
        this.cached = true;
        this.expired = cacheEntry.isExpired();

        this.cached();
    }

    @Override
    public boolean isCached()
    {
        return this.cached;
    }

    @Override
    public boolean hasExpired()
    {
        return this.expired;
    }

    /**
     * Mark this object as cached.  This is called as the item exits the cache and is marked as
     * cached, implementations may wish to modify their data when this called.
     */
    protected void cached()
    {
        // do nothing
    }
}
