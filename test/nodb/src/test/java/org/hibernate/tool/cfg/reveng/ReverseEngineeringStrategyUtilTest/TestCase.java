package org.hibernate.tool.cfg.reveng.ReverseEngineeringStrategyUtilTest;

import org.hibernate.cfg.reveng.ReverseEngineeringStrategyUtil;
import org.junit.Assert;
import org.junit.Test;


public class TestCase {

	@Test
    public void testSimplePluralizeWithSingleH() throws Exception {
        String plural = ReverseEngineeringStrategyUtil.simplePluralize("h");
        Assert.assertEquals("hs", plural);
    }
	
}