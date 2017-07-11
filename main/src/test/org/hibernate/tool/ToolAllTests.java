package org.hibernate.tool;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ToolAllTests {

public static Test suite() {
	TestSuite suite = new TestSuite("Test for org.hibernate.tool");
	suite.addTestSuite(JDBCMetaDataBinderTestCase.class);
	suite.addTest(org.hibernate.tool.test.jdbc2cfg.Jdbc2CfgAllTests.suite() );
	return suite;
}}
