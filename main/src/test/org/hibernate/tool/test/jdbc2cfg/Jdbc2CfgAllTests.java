package org.hibernate.tool.test.jdbc2cfg;


import junit.framework.Test;
import junit.framework.TestSuite;

public class Jdbc2CfgAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.hibernate.tool.test.jdbc2cfg");
		//$JUnit-BEGIN$
		suite.addTest(ManyToManyTest.suite());
		suite.addTest(OverrideBinderTest.suite());
		suite.addTest(PersistentClassesTest.suite());
		suite.addTest(OracleViewsTest.suite());
		suite.addTest(SearchEscapeStringTest.suite());
		suite.addTest(PerformanceTest.suite());
		suite.addTest(RevEngForeignKeyTests.suite());
		suite.addTest(OracleCompositeIdOrderTest.suite());
		//$JUnit-END$
		return suite;
	}

}
