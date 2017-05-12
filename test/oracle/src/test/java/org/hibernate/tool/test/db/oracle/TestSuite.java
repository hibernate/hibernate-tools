package org.hibernate.tool.test.db.oracle;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	org.hibernate.cfg.JDBCMetaDataConfigurationTest.class,
	org.hibernate.tool.test.jdbc2cfg.BasicTest.class
})
public class TestSuite {}
