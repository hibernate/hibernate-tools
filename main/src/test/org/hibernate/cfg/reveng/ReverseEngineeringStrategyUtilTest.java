package org.hibernate.cfg.reveng;

import junit.framework.TestCase;

public class ReverseEngineeringStrategyUtilTest extends TestCase {

  public void testSimplePluralizeWithSingleH() throws Exception {
    String plural = ReverseEngineeringStrategyUtil.simplePluralize("h");
    assertEquals("hs", plural);
  }
}