package org.hibernate.tool.test.db;

import org.hibernate.cfg.JDBCMetaDataConfigurationTest;
import org.hibernate.tool.test.jdbc2cfg.AutoQuoteTest;
import org.hibernate.tool.test.jdbc2cfg.BasicMultiSchemaTest;
import org.hibernate.tool.test.jdbc2cfg.BasicTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	AutoQuoteTest.class,
	BasicTest.class,
	BasicMultiSchemaTest.class,
	JDBCMetaDataConfigurationTest.class 
})
public class CommonTestSuite {}
