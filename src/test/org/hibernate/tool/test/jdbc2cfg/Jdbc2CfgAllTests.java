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
		suite.addTest(DefaultReverseEngineeringStrategyTest.suite());
		suite.addTest(PersistentClassesTest.suite());
		suite.addTest(OracleViewsTest.suite());
		suite.addTest(CompositeIdTest.suite());
		suite.addTest(SearchEscapeStringTest.suite());
		suite.addTest(PerformanceTest.suite());
		suite.addTest(ForeignKeysTest.suite());
		suite.addTest(BasicMultiSchemaTest.suite());
		suite.addTest(MetaDataTest.suite());
		suite.addTest(RevEngForeignKeyTests.suite());
		suite.addTest(OracleCompositeIdOrderTest.suite());
		suite.addTest(NoPrimaryKeyTest.suite());
		suite.addTest(BasicTest.suite());
		suite.addTest(VersioningTest.suite());
		suite.addTest(AutoQuoteTest.suite());
		suite.addTest(KeyPropertyCompositeIdTest.suite());
		suite.addTest(IndexTest.suite());
		suite.addTest(new TestSuite(MetaDataDialectFactoryTest.class));
		//$JUnit-END$
		return suite;
	}

}
