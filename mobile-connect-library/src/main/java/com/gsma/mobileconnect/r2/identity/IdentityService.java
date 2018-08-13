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
package com.gsma.mobileconnect.r2.identity;

import com.gsma.mobileconnect.r2.encoding.IMobileConnectEncodeDecoder;
import com.gsma.mobileconnect.r2.json.IJsonService;
import com.gsma.mobileconnect.r2.rest.IRestClient;
import com.gsma.mobileconnect.r2.exceptions.RequestFailedException;
import com.gsma.mobileconnect.r2.rest.RestAuthentication;
import com.gsma.mobileconnect.r2.rest.RestResponse;
import com.gsma.mobileconnect.r2.utils.ObjectUtils;
import com.gsma.mobileconnect.r2.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Concrete implementation of {@link IIdentityService}
 *
 * @since 2.0
 */
public class IdentityService implements IIdentityService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(IdentityService.class);

    private final IJsonService jsonService;
    private final IRestClient restClient;

    private IdentityService(Builder builder)
    {
        this.jsonService = builder.jsonService;
        this.restClient = builder.restClient;

        LOGGER.info("New instance of IdentityService created");
    }

    @Override
    public IdentityResponse requestInfo(final URI infoUrl, final String accessToken,
        final IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder)
        throws RequestFailedException
    {
        ObjectUtils.requireNonNull(infoUrl, "infoUrl");
        StringUtils.requireNonEmpty(accessToken, "accessToken");

        final RestAuthentication authentication = RestAuthentication.bearer(accessToken);


        final RestResponse response =
            this.restClient.get(infoUrl, authentication, null, null, null, null);

        return IdentityResponse.fromRestResponse(response, this.jsonService,
            iMobileConnectEncodeDecoder);
    }

    @Override
    public Future<IdentityResponse> requestInfoAsync(final URI infoUrl, final String accessToken,
        final IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder)
    {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<IdentityResponse> identityServiceFuture = executorService.submit(new Callable<IdentityResponse>()
        {
            @Override
            public IdentityResponse call() throws Exception
            {
                return IdentityService.this.requestInfo(infoUrl, accessToken,
                    iMobileConnectEncodeDecoder);
            }
        });
        executorService.shutdownNow();
        return identityServiceFuture;
    }

    public static final class Builder
    {
        private IJsonService jsonService;
        private IRestClient restClient;

        public Builder withJsonService(final IJsonService val)
        {
            this.jsonService = val;
            return this;
        }

        public Builder withRestClient(final IRestClient val)
        {
            this.restClient = val;
            return this;
        }

        public IdentityService build()
        {
            ObjectUtils.requireNonNull(this.jsonService, "jsonService");
            ObjectUtils.requireNonNull(this.restClient, "restClient");

            return new IdentityService(this);
        }
    }
}
