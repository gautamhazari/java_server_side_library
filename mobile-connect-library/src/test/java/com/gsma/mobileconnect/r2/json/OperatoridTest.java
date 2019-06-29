package com.gsma.mobileconnect.r2.json;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

public class OperatoridTest
{
    private Operatorid operatoridWithLinks;
    private Operatorid operatoridWithoutLinks;

    private List<Link> links;

    @BeforeMethod
    public void setUp() throws Exception
    {
        links = new ArrayList<Link>();
        links.add(new Link.Builder().withHref("href1").withRel("rel1").build());
        links.add(new Link.Builder().withHref("href2").withRel("rel2").build());

        operatoridWithLinks = new Operatorid.Builder().withLink(links).build();
        operatoridWithoutLinks = new Operatorid.Builder().build();
    }

    @AfterMethod
    public void tearDown() throws Exception
    {
        links = null;
        operatoridWithLinks = null;
        operatoridWithoutLinks = null;
    }

    @Test
    public void testGetLink() throws Exception
    {
        assertEquals(operatoridWithLinks.getLink(), links);
        assertTrue(operatoridWithoutLinks.getLink().isEmpty());
    }

}