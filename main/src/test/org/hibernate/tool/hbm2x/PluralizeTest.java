/*
 * Created on 29.12.2011
 */
package org.hibernate.tool.hbm2x;

import org.hibernate.cfg.reveng.ReverseEngineeringStrategyUtil;

import junit.framework.TestCase;

/**
 * @author Dmitry Geraskov (geraskov@gmail.com)
 *
 */
public class PluralizeTest extends TestCase {
	
	public void testPluralizeTest(){
		assertEquals("boxes", ReverseEngineeringStrategyUtil.simplePluralize("box"));
		assertEquals("buses", ReverseEngineeringStrategyUtil.simplePluralize("bus"));
		assertEquals("keys", ReverseEngineeringStrategyUtil.simplePluralize("key"));
		assertEquals("countries", ReverseEngineeringStrategyUtil.simplePluralize("country"));
		assertEquals("churches", ReverseEngineeringStrategyUtil.simplePluralize("church"));
		assertEquals("bushes", ReverseEngineeringStrategyUtil.simplePluralize("bush"));
		assertEquals("roofs", ReverseEngineeringStrategyUtil.simplePluralize("roof"));
	}

}
