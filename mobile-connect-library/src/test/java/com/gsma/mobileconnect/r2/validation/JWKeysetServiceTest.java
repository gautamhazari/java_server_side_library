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

import com.google.common.collect.ImmutableMap;
import com.gsma.mobileconnect.r2.cache.DiscoveryCache;
import com.gsma.mobileconnect.r2.model.json.GsonJsonService;
import com.gsma.mobileconnect.r2.model.exceptions.RequestFailedException;
import com.gsma.mobileconnect.r2.service.validation.IJWKeysetService;
import com.gsma.mobileconnect.r2.service.validation.JWKey;
import com.gsma.mobileconnect.r2.service.validation.JWKeyset;
import com.gsma.mobileconnect.r2.service.validation.JWKeysetService;
import com.gsma.mobileconnect.r2.web.rest.RestAuthentication;
import com.gsma.mobileconnect.r2.web.rest.RestClient;
import com.gsma.mobileconnect.r2.web.rest.RestResponse;
import com.gsma.mobileconnect.r2.utils.KeyValuePair;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @since 2.0
 */
public class JWKeysetServiceTest
{
    private Map<String, RestResponse> responses = ImmutableMap.<String, RestResponse>builder()
        .put("single", new RestResponse.Builder()
            .withStatusCode(200)
            .withContent(
                "{\"keys\":[{\"kty\":\"RSA\",\"use\":\"sig\",\"n\":\"ALyIC8vj1tqEIvAvpDMQfgosw13LpBS9Z2lsMmuaLDNJjN_FKIb-HVR2qtMj7AYC0-wYJhGxJpTXJTVRRDz-zLN7uredNxuhVj76vmU1tfvEN0Xq2INYoWeJ3d9fZtkBgKl7Enfkgz858DLAfZuJzDycOzuZXR5r29zXMDstT5F5\",\"e\":\"AQAB\",\"kid\":\"PHPOP-00\"}]}")
            .build())
        .put("multi", new RestResponse.Builder()
            .withStatusCode(200)
            .withContent(
                "{keys:[{kty:\"RSA\",alg:\"RS256\",use:\"sig\",kid:\"e27d33093814b052594840219c8f4b0070ee5a3a\",n:\"vNSQ2tMH7T20JgWCUMhQb2ofkE5oG0TFqXb-eOa3ap-BdujTeKUgS-ZZj7Apw_X3Bvf-yTkY_cFuH3paqUkKHy0BNQCo_Y4qPVa8u_57n2bFntHAz0Qi4YeXGxVTwgFa7X0gLFbhWjZBPmlj44vWUsFujqfARiWJRN-dUhKPaxcc7hUBnzRIs2Ll3tYZ2nYw9DT_l1qC9-b2zikWyZ_5bqv7l5Njq2Naf5GZug2m2OgH5lrnaxNU5eQhvMyajeld36GGAzn5a76Rr1fB3F-NaurzUDuw7mgmRjZU6aCjx-OqUwHsgnS3IY5a0EEuI6Hzc6T-GCmUBqUy85kko8595Q\",e:\"AQAB\"},{kty:\"RSA\",alg:\"RS256\",use:\"sig\",kid:\"136510045208f4b17448036e7da1bce8cd8ef856\",n:\"sbi3CNplTFmPN5HslnSKGW80piY0tZW9FQf1T_l4f2-JEKLJWfqzROQ-oSR7LMK-atIZ4dbl3xRH09F4ceAGJ5n6wWBUIDUuWqzgz9GH2vpy609oYT_kyQ9rmjk4n4nCA-NQ7-pk-sN9vx5xhuSOTuU-RBwexTHYKMTMsHNAOmUxfupv5EnnEL99mNybbZlbIUORZ6J1ue7_apoqhcW-4LcF_rq-oDEANc_t3MzbmBoBXxtCSzcOKftH3YwY6F86gh3mlyar5wSQdIAfTUl7v2MaYJaDQnlbpADvqYSPULvnxv-JfsKupkMcl6_5nd7WS6rw3TYN4G6DfU1iB8e6GQ\",e:\"AQAB\"},{kty:\"RSA\",alg:\"RS256\",use:\"sig\",kid:\"6192a6b061685321732c4ba10c010969ae2f55bc\",n:\"sTlBZpLI8NxRVHsDMRNMuiUONPpthV-wQ6iPH5GgjICtZZL9qha4JVy1e7gILOWLRp4madr8qKbi5ii0rEaNSC1KGY8xQjcsqoO_WNpT_quWdSZ6Qk4HLS05uO0fD00QVNj_ZOrYdfOPciMcnWP5lZVihKq_itFe6Rz79v9ibxljaPLe74eLaker55sUwXrbSVqWkM_QM2dzdPVvnSE4-iH5j69tMUaf6NeRwCAFUmy8GyuO-1fJDpzMELVB3MonJ_3Ny6FSMYykPMEEHWKBV6Wdb86nSefTWhQfAMNtm4nvkn-F77HPKJHKNHCUjYpotR0C4by5Sjy8vbDDW5Wo6w\",e:\"AQAB\"},{kty:\"RSA\",alg:\"RS256\",use:\"sig\",kid:\"d0ec514a32b6f88c0abd12a2840699bdd3deba9d\",n:\"yecH_BNaZW3vuU2jepfqUVeXrGzRKQo6CvAI4lqOFdfYjXtj7VAg64Q7-VtCO-VDovnXsQ2f_ytts3B3UI9j8v8nNDlrNSL7vwekgu-FNfsCDV8ktmNivES9ounsL1xbg5u6Amvyp4p8fQ_QJmp0GHaUy4m2BsU9dp-kpoO7ByKqbpbjHHiSvxyST5JZk1_PV9lzsmpm5pyXw28w-l6lVrdG9in82Kao4LciOspOMserCBguag0abrSE19vE5n_36ZStqUqR-IdOsGTq3BehJP7OmX21BcqSpRep4uo5Y61qZvFBcOXLyk0YGZ4x7ksvzFHzjpl6pi_Awv3-VWfC-w\",e:\"AQAB\"}]}")
            .build())
        .build();

    private RestClient mockRestClient = Mockito.mock(RestClient.class);
    private IJWKeysetService jwKeysetServiceWithCache;
    private IJWKeysetService jwKeysetServiceWithoutCache;

    @BeforeMethod
    public void beforeMethod()
    {
        this.jwKeysetServiceWithCache = new JWKeysetService.Builder()
            .withRestClient(this.mockRestClient)
            .withICache(
                new DiscoveryCache.Builder().withJsonService(new GsonJsonService()).build())
            .build();

        this.jwKeysetServiceWithoutCache = new JWKeysetService.Builder()
        .withRestClient(this.mockRestClient)
        .build();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void retrieveJWKSAsyncReturnsJWKS()
        throws RequestFailedException, ExecutionException, InterruptedException
    {
        when(mockRestClient.get(any(URI.class), any(RestAuthentication.class), anyString(), anyString(),
            anyListOf(KeyValuePair.class), any(Iterable.class))).thenReturn(
            responses.get("single"));

        final Future<JWKeyset> jwKeysetFuture =
            jwKeysetServiceWithCache.retrieveJwksAsync("http://jwks.com/jwks");
        final JWKeyset jwKeyset = jwKeysetFuture.get();

        final List<JWKey> jwKeyList = jwKeyset.getKeys();
        assertEquals(jwKeyList.size(), 1);
        final JWKey jwKey = jwKeyList.get(0);
        assertEquals(jwKey.getKty(), "RSA");
        assertEquals(jwKey.getUse(), "sig");
        assertEquals(jwKey.getRsaN(),
            "ALyIC8vj1tqEIvAvpDMQfgosw13LpBS9Z2lsMmuaLDNJjN_FKIb-HVR2qtMj7AYC0-wYJhGxJpTXJTVRRDz-zLN7uredNxuhVj76vmU1tfvEN0Xq2INYoWeJ3d9fZtkBgKl7Enfkgz858DLAfZuJzDycOzuZXR5r29zXMDstT5F5");
        assertEquals(jwKey.getRsaE(), "AQAB");
        assertEquals(jwKey.getKid(), "PHPOP-00");
    }


    @SuppressWarnings("unchecked")
    @Test
    public void retrieveJWKSAsyncUsesCache()
        throws RequestFailedException, ExecutionException, InterruptedException
    {
        when(mockRestClient.get(any(URI.class), any(RestAuthentication.class), anyString(),anyString(),
            anyListOf(KeyValuePair.class), any(Iterable.class))).thenReturn(
            responses.get("single"));

        String jwksUrl = "http://jwks.com/jwks";
        final Future<JWKeyset> jwKeysetFuture = jwKeysetServiceWithCache.retrieveJwksAsync(jwksUrl);
        jwKeysetFuture.get();

        final Future<JWKeyset> cachedFuture = jwKeysetServiceWithCache.retrieveJwksAsync(jwksUrl);
        final JWKeyset cachedJwKeyset = cachedFuture.get();

        assertTrue(cachedJwKeyset.isCached());
        List<JWKey> jwKeyList = cachedJwKeyset.getKeys();
        assertEquals(jwKeyList.size(), 1);
        final JWKey jwKey = jwKeyList.get(0);
        assertEquals(jwKey.getKty(), "RSA");
        assertEquals(jwKey.getUse(), "sig");
        assertEquals(jwKey.getRsaN(),
            "ALyIC8vj1tqEIvAvpDMQfgosw13LpBS9Z2lsMmuaLDNJjN_FKIb-HVR2qtMj7AYC0-wYJhGxJpTXJTVRRDz-zLN7uredNxuhVj76vmU1tfvEN0Xq2INYoWeJ3d9fZtkBgKl7Enfkgz858DLAfZuJzDycOzuZXR5r29zXMDstT5F5");
        assertEquals(jwKey.getRsaE(), "AQAB");
        assertEquals(jwKey.getKid(), "PHPOP-00");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void retrieveJWKSAsyncReturnsJWKSWithoutCache()
        throws RequestFailedException, ExecutionException, InterruptedException
    {
        when(mockRestClient.get(any(URI.class), any(RestAuthentication.class), anyString(),anyString(),
            anyListOf(KeyValuePair.class), any(Iterable.class))).thenReturn(
            responses.get("single"));

        final Future<JWKeyset> jwKeysetFuture =
            jwKeysetServiceWithoutCache.retrieveJwksAsync("http://jwks.com/jwks");
        final JWKeyset jwKeyset = jwKeysetFuture.get();

        final List<JWKey> jwKeyList = jwKeyset.getKeys();
        assertEquals(jwKeyList.size(), 1);
        final JWKey jwKey = jwKeyList.get(0);
        assertEquals(jwKey.getKty(), "RSA");
        assertEquals(jwKey.getUse(), "sig");
        assertEquals(jwKey.getRsaN(),
            "ALyIC8vj1tqEIvAvpDMQfgosw13LpBS9Z2lsMmuaLDNJjN_FKIb-HVR2qtMj7AYC0-wYJhGxJpTXJTVRRDz-zLN7uredNxuhVj76vmU1tfvEN0Xq2INYoWeJ3d9fZtkBgKl7Enfkgz858DLAfZuJzDycOzuZXR5r29zXMDstT5F5");
        assertEquals(jwKey.getRsaE(), "AQAB");
        assertEquals(jwKey.getKid(), "PHPOP-00");
    }


    @SuppressWarnings("unchecked")
    @Test
    public void retrieveJWKSAsyncWithoutCachee()
        throws RequestFailedException, ExecutionException, InterruptedException
    {
        when(mockRestClient.get(any(URI.class), any(RestAuthentication.class), anyString(), anyString(),
            anyListOf(KeyValuePair.class), any(Iterable.class))).thenReturn(
            responses.get("single"));

        String jwksUrl = "http://jwks.com/jwks";
        final Future<JWKeyset> jwKeysetFuture = jwKeysetServiceWithoutCache.retrieveJwksAsync(jwksUrl);
        jwKeysetFuture.get();

        final Future<JWKeyset> cachedFuture = jwKeysetServiceWithoutCache.retrieveJwksAsync(jwksUrl);
        final JWKeyset cachedJwKeyset = cachedFuture.get();

        assertFalse(cachedJwKeyset.isCached());
    }
}
