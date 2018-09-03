package com.gsma.mobileconnect.r2.json;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class DiscoveryResponseDataTest
{
    private DiscoveryResponseData discoveryResponseData;
    private DiscoveryResponseData discoveryResponseDataCopy;
    private Response response;
    private List<Link> links;

    @BeforeMethod
    public void setUp() throws Exception
    {
        links = new ArrayList<Link>();
        links.add(new Link.Builder().withHref("href1").withRel("rel1").build());
        links.add(new Link.Builder().withHref("href2").withRel("rel2").build());

        response = new Response.Builder()
            .withClientId("clientId")
            .withClientName("clientName")
            .withClientSecret("clientSecret")
            .withCountry("country")
            .withCurrency("currency")
            .withServingOperator("servingOperator")
            .withSubscriberId("subscriberId")
            .build();

        discoveryResponseData = new DiscoveryResponseData.Builder()
            .withResponse(response)
            .withClientName("clientName")
            .withDescription("description")
            .withError("error")
            .withLinks(links)
            .withSubscriberId("subscriberId")
            .withTtl(100L)
            .build();

        discoveryResponseDataCopy =
            new DiscoveryResponseData.Builder(discoveryResponseData).build();
    }

    @AfterMethod
    public void tearDown() throws Exception
    {
        links = null;
        response = null;
        discoveryResponseData = null;
        discoveryResponseDataCopy = null;
    }

    @Test
    public void testGetTtl() throws Exception
    {
        assertEquals(discoveryResponseData.getTtl(), 100L);
        assertEquals(discoveryResponseDataCopy.getTtl(), 100L);
    }

    @Test
    public void testGetSubscriberId() throws Exception
    {
        assertEquals(discoveryResponseData.getSubscriberId(), "subscriberId");
        assertEquals(discoveryResponseDataCopy.getSubscriberId(), "subscriberId");
    }

    @Test
    public void testClearSubscriberId() throws Exception
    {
        discoveryResponseData.clearSubscriberId();
        assertNull(discoveryResponseData.getSubscriberId());
    }

    @Test
    public void testGetError() throws Exception
    {
        assertEquals(discoveryResponseData.getError(), "error");
        assertEquals(discoveryResponseDataCopy.getError(), "error");
    }

    @Test
    public void testGetDescription() throws Exception
    {
        assertEquals(discoveryResponseData.getDescription(), "description");
        assertEquals(discoveryResponseDataCopy.getDescription(), "description");
    }

    @Test
    public void testGetLinks() throws Exception
    {
        assertEquals(discoveryResponseData.getLinks(), links);
        assertEquals(discoveryResponseDataCopy.getLinks(), links);
    }

    @Test
    public void testGetResponse() throws Exception
    {
        assertEquals(discoveryResponseData.getResponse(), response);
        assertEquals(discoveryResponseDataCopy.getResponse(), response);
    }

    @Test
    public void testGetClientName() throws Exception
    {
        assertEquals(discoveryResponseData.getClientName(), "clientName");
        assertEquals(discoveryResponseDataCopy.getClientName(), "clientName");
    }

}