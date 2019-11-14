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
package com.gsma.mobileconnect.r2.web;

import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.service.authentication.RequestTokenResponseData;
import com.gsma.mobileconnect.r2.service.discovery.DiscoveryResponse;

/**
 * Helper class to convert from a heavyweight MobileConnectStatus instance to a Lightweight
 * serializable MobileConnectWebResponse instance.
 *
 * @since 2.0
 */
public final class MobileConnectWebResponse
{
    private final String status;
    private final String action;
    private final String clientName;
    private final String url;
    private final String sdkSession;
    private final String state;
    private final String nonce;
    private final String subscriberId;
    private final RequestTokenResponseData token;
    private final boolean tokenValidated;
    private final String identity;
    private final String error;
    private final String description;
    private final String outcome;

    /**
     * Create instance of {@link MobileConnectWebResponse} copying all attributes from provided
     * {@link MobileConnectWebResponse}.
     *
     * @param status to copy data from.
     */
    public MobileConnectWebResponse(final MobileConnectStatus status)
    {
        this.action = status.getResponseType().name().toLowerCase();

        if (status.getResponseType() == MobileConnectStatus.ResponseType.ERROR)
        {
            this.status = "failure";
            this.error = status.getErrorCode();
            this.description = status.getErrorMessage();
        }
        else
        {
            this.status = "success";
            this.error = null;
            this.description = null;
        }

        this.nonce = status.getNonce();
        this.state = status.getState();
        this.url = status.getUrl();
        this.sdkSession = status.getSdkSession();

        final DiscoveryResponse discoveryResponse = status.getDiscoveryResponse();
        if (discoveryResponse != null)
        {
            this.clientName = discoveryResponse.getClientName();
            this.subscriberId = discoveryResponse.getResponseData() == null
                                ? null
                                : discoveryResponse.getResponseData().getSubscriberId();
        }
        else
        {
            this.clientName = null;
            this.subscriberId = null;
        }

        this.token = status.getRequestTokenResponse() == null
                     ? null
                     : status.getRequestTokenResponse().getResponseData();
        this.tokenValidated = status.getRequestTokenResponse() != null && status
            .getRequestTokenResponse()
            .isTokenValidated();

        this.identity = status.getIdentityResponse() == null
                        ? null
                        : status.getIdentityResponse().getResponseJson();

        this.outcome = status.getOutcome();
    }

    public String getStatus()
    {
        return this.status;
    }

    public String getAction()
    {
        return this.action;
    }

    public String getClientName()
    {
        return this.clientName;
    }

    public String getUrl()
    {
        return this.url;
    }

    public String getSdkSession()
    {
        return this.sdkSession;
    }

    public String getState()
    {
        return this.state;
    }

    public String getNonce()
    {
        return this.nonce;
    }

    public String getSubscriberId()
    {
        return this.subscriberId;
    }

    public RequestTokenResponseData getToken()
    {
        return this.token;
    }

    public boolean isTokenValidated()
    {
        return this.tokenValidated;
    }

    public String getOutcome()
    {
        return outcome;
    }

    public String getIdentity()
    {
        return this.identity;
    }

    public String getError()
    {
        return this.error;
    }

    public String getDescription()
    {
        return this.description;
    }
}
