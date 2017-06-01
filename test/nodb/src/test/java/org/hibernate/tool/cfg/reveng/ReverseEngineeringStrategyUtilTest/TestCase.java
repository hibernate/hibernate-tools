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
	
	@Test
	public void testPluralize(){
		Assert.assertEquals("boxes", ReverseEngineeringStrategyUtil.simplePluralize("box"));
		Assert.assertEquals("buses", ReverseEngineeringStrategyUtil.simplePluralize("bus"));
		Assert.assertEquals("keys", ReverseEngineeringStrategyUtil.simplePluralize("key"));
		Assert.assertEquals("countries", ReverseEngineeringStrategyUtil.simplePluralize("country"));
		Assert.assertEquals("churches", ReverseEngineeringStrategyUtil.simplePluralize("church"));
		Assert.assertEquals("bushes", ReverseEngineeringStrategyUtil.simplePluralize("bush"));
		Assert.assertEquals("roofs", ReverseEngineeringStrategyUtil.simplePluralize("roof"));
	}

}