package org.hibernate.tool.cfg.reveng.NameConverterTest;

import org.hibernate.tool.internal.util.NameConverter;
import org.junit.Assert;
import org.junit.Test;


public class TestCase {

	@Test
    public void testSimplePluralizeWithSingleH() throws Exception {
        String plural = NameConverter.simplePluralize("h");
        Assert.assertEquals("hs", plural);
    }
	
	@Test
	public void testPluralize(){
		Assert.assertEquals("boxes", NameConverter.simplePluralize("box"));
		Assert.assertEquals("buses", NameConverter.simplePluralize("bus"));
		Assert.assertEquals("keys", NameConverter.simplePluralize("key"));
		Assert.assertEquals("countries", NameConverter.simplePluralize("country"));
		Assert.assertEquals("churches", NameConverter.simplePluralize("church"));
		Assert.assertEquals("bushes", NameConverter.simplePluralize("bush"));
		Assert.assertEquals("roofs", NameConverter.simplePluralize("roof"));
	}

}