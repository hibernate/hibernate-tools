package org.hibernate.tool;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.tool.hbm2x.query.QueryAllTests;
import org.hibernate.tool.hbmlint.HbmLintAllTests;
import org.hibernate.tool.ide.completion.CompletionAllTests;

public class ToolAllTests {

public static Test suite() {
	TestSuite suite = new TestSuite("Test for org.hibernate.tool");
	
	suite.addTest(org.hibernate.tool.ant.AntAllTests.suite() );
	suite.addTest(org.hibernate.tool.hbm2x.hbm2hbmxml.Cfg2HbmAllTests.suite() );
	suite.addTest(org.hibernate.tool.test.jdbc2cfg.Jdbc2CfgAllTests.suite() );
	suite.addTest(org.hibernate.tool.hbm2x.Hbm2XAllTests.suite() );
	suite.addTest(CompletionAllTests.suite() );
	suite.addTest(QueryAllTests.suite() );
	suite.addTest(HbmLintAllTests.suite() );
	return suite;
}}
