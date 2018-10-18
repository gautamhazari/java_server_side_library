package com.gsma.mobileconnect.r2.utils;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class ByteUtilsTest
{
    @Test
    public void testAddZeroPrefix() throws Exception
    {
        byte[] bytes = {1, 0, 1, 1};
        byte[] expected = {0, 1, 0, 1, 1};
        assertEquals(ByteUtils.addZeroPrefix(bytes), expected);
    }

    @Test
    public void testAddZeroPrefixWithLeadingZeros() throws Exception
    {
        byte[] bytes = {0, 0, 1, 1};
        byte[] expected = {0, 0, 0, 1, 1};
        assertEquals(ByteUtils.addZeroPrefix(bytes), expected);
    }

    @Test
    public void testAddZeroPrefixWithEmptyArray() throws Exception
    {
        byte[] bytes = {};
        byte[] expected = {0};
        assertEquals(ByteUtils.addZeroPrefix(bytes), expected);
    }

}