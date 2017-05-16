package org.hibernate.tool.test.db.mysql;

import org.hibernate.cfg.JDBCMetaDataConfigurationTest;
import org.hibernate.tool.test.jdbc2cfg.AutoQuoteTest;
import org.hibernate.tool.test.jdbc2cfg.BasicMultiSchemaTest;
import org.hibernate.tool.test.jdbc2cfg.BasicTest;
import org.hibernate.tools.test.util.DbSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(DbSuite.class)
@SuiteClasses({ 
	AutoQuoteTest.class,
	BasicMultiSchemaTest.class,
	BasicTest.class,
	JDBCMetaDataConfigurationTest.class 
})
public class TestSuite {}
