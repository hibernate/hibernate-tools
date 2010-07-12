package org.hibernate.tool.hbm2x.hbm2hbmxml;

import junit.framework.Test;
import junit.framework.TestSuite;

public class Cfg2HbmAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.hibernate.tool.cfg2hbm");
		//$JUnit-BEGIN$
		suite.addTest(ListArrayTest.suite());
		suite.addTest(CompositeElementTest.suite());
		suite.addTest(MapAndAnyTest.suite());
		suite.addTest(DynamicComponentTest.suite());
		suite.addTest(IdBagTest.suite());
		suite.addTest(Hbm2HbmXmlTest.suite());
		suite.addTest(ManyToManyTest.suite());
		suite.addTest(OneToOneTest.suite());
		suite.addTest(InheritanceTest.suite());
		suite.addTest(SetElementTest.suite());
		suite.addTest(BackrefTest.suite());
		suite.addTest(Cfg2HbmToolTest.suite());
		suite.addTest(AbstractTest.suite());
		suite.addTest(TypeParamsTest.suite());
		//$JUnit-END$
		return suite;
	}

}
