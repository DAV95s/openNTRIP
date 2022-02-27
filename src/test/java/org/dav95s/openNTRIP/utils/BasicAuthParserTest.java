package org.dav95s.openNTRIP.utils;

import org.junit.Assert;
import org.junit.Test;

public class BasicAuthParserTest {
    @Test(expected = IllegalArgumentException.class)
    public void test1() {
        String tested = "Basic";
        BasicAuthParser basicAuthParser = new BasicAuthParser(tested);
    }

    @Test
    public void test2() {
        String tested = "Basic QUNDOlBBU1M=";
        BasicAuthParser basicAuthParser = new BasicAuthParser(tested);

        Assert.assertEquals("ACC", basicAuthParser.account);
        Assert.assertEquals("PASS", basicAuthParser.password);

    }

    @Test
    public void test3() {
        String tested = "Basic QUNDOg==";
        BasicAuthParser basicAuthParser = new BasicAuthParser(tested);

        Assert.assertEquals("ACC", basicAuthParser.account);
        Assert.assertEquals("", basicAuthParser.password);

    }

}