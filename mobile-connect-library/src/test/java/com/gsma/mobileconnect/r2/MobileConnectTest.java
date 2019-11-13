package com.gsma.mobileconnect.r2;

import com.gsma.mobileconnect.r2.service.authentication.IAuthenticationService;
import com.gsma.mobileconnect.r2.cache.DiscoveryCache;
import com.gsma.mobileconnect.r2.cache.ICache;
import com.gsma.mobileconnect.r2.model.constants.DefaultOptions;
import com.gsma.mobileconnect.r2.service.discovery.IDiscoveryService;
import com.gsma.mobileconnect.r2.utils.encoding.DefaultEncodeDecoder;
import com.gsma.mobileconnect.r2.utils.encoding.IMobileConnectEncodeDecoder;
import com.gsma.mobileconnect.r2.service.identity.IIdentityService;
import com.gsma.mobileconnect.r2.model.json.IJsonService;
import com.gsma.mobileconnect.r2.model.json.GsonJsonService;
import com.gsma.mobileconnect.r2.web.rest.IRestClient;
import com.gsma.mobileconnect.r2.service.validation.IJWKeysetService;
import org.apache.http.client.HttpClient;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertNotNull;

public class MobileConnectTest
{
    private MobileConnect mobileConnect;
    private MobileConnectConfig mobileConnectConfig;
    private IMobileConnectEncodeDecoder encodeDecoder;

    @Mock
    private IDiscoveryService discoveryServiceMock;

    @Mock
    private IAuthenticationService authenticationServiceMock;

    @Mock
    private IIdentityService identityServiceMock;

    @Mock
    private IJWKeysetService jwKeysetServiceMock;

    @Mock
    private ICache cacheMock;

    @Mock
    private ScheduledExecutorService scheduledExecutorServiceMock;

    @Mock
    private HttpClient httpClientMock;

    @Mock
    private IRestClient restClient;

    @Mock
    private IJsonService jsonService = new GsonJsonService();

    private TimeUnit timeoutTimeUnit = TimeUnit.MILLISECONDS;
    private Long timeoutDuration = DefaultOptions.TIMEOUT_MS;


    @BeforeMethod
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks(this);

        mobileConnectConfig = new MobileConnectConfig.Builder()
            .withClientId("clientId")
            .withClientSecret("clientSecret")
            .withDiscoveryUrl(new URI("http://discovery"))
            .withRedirectUrl(new URI("http://redirect"))
            .build();
        encodeDecoder = new DefaultEncodeDecoder();

        mobileConnect = new MobileConnect.Builder(mobileConnectConfig, encodeDecoder, new DiscoveryCache.Builder().withJsonService(jsonService).withMaxCacheSize(999999999).build(),
                new DiscoveryCache.Builder().withJsonService(jsonService).withMaxCacheSize(999999999).build())
            .withCache(cacheMock)
            .withHttpClient(httpClientMock)
            .withHttpTimeout(timeoutDuration, timeoutTimeUnit)
            .withRestClient(restClient)
            .withScheduledExecutorService(scheduledExecutorServiceMock)
            .withIMobileConnectEncodeDecoder(encodeDecoder)
            .build();
    }

    @AfterMethod
    public void tearDown() throws Exception
    {
        mobileConnect = null;
        encodeDecoder = null;
        mobileConnectConfig = null;
    }

    @Test
    public void testBuildWithconfigAndEncoderDecoder() throws Exception
    {
        assertNotNull(MobileConnect.build(mobileConnectConfig, encodeDecoder, new DiscoveryCache.Builder().withJsonService(jsonService).withMaxCacheSize(999999999).build(),
                new DiscoveryCache.Builder().withJsonService(jsonService).withMaxCacheSize(999999999).build()));
    }

    @Test
    public void testBuildWithconfig() throws Exception
    {
        assertNotNull(MobileConnect.build(mobileConnectConfig,new DiscoveryCache.Builder().withJsonService(jsonService).withMaxCacheSize(999999999).build(),
                new DiscoveryCache.Builder().withJsonService(jsonService).withMaxCacheSize(999999999).build()));
    }

    @Test
    public void testBuildInterface() throws Exception
    {
        assertNotNull(MobileConnect.buildInterface(mobileConnectConfig, encodeDecoder, new DiscoveryCache.Builder().withJsonService(jsonService).withMaxCacheSize(999999999).build(),
                new DiscoveryCache.Builder().withJsonService(jsonService).withMaxCacheSize(999999999).build()));
    }

    @Test
    public void testBuildWebInterface() throws Exception
    {
        assertNotNull(MobileConnect.buildWebInterface(mobileConnectConfig, encodeDecoder,
                new DiscoveryCache.Builder().withJsonService(jsonService).withMaxCacheSize(999999999).build(), new DiscoveryCache.Builder().withJsonService(jsonService).withMaxCacheSize(999999999).build()));
    }

    @Test
    public void testBuilder() throws Exception
    {
        assertNotNull(MobileConnect.builder(mobileConnectConfig, encodeDecoder,
                new DiscoveryCache.Builder().withJsonService(jsonService).withMaxCacheSize(999999999).build(), new DiscoveryCache.Builder().withJsonService(jsonService).withMaxCacheSize(999999999).build()));
    }

    @Test
    public void testGetDiscoveryService() throws Exception
    {
        assertNotNull(mobileConnect.getDiscoveryService());
    }

    @Test
    public void testGetAuthnService() throws Exception
    {
        assertNotNull(mobileConnect.getAuthnService());
    }

    @Test
    public void testGetIdentityService() throws Exception
    {
        assertNotNull(mobileConnect.getIdentityService());
    }

    @Test
    public void testGetMobileConnectInterface() throws Exception
    {
        assertNotNull(mobileConnect.getMobileConnectInterface());
    }

    @Test
    public void testGetMobileConnectWebInterface() throws Exception
    {
        assertNotNull(mobileConnect.getMobileConnectWebInterface());
    }

}