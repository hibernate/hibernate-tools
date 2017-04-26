package org.hibernate.tool.test.db;

import org.hibernate.cfg.JDBCMetaDataConfigurationTest;
import org.hibernate.tool.test.jdbc2cfg.AutoQuoteTest;
import org.hibernate.tool.test.jdbc2cfg.BasicMultiSchemaTest;
import org.hibernate.tool.test.jdbc2cfg.BasicTest;
import org.hibernate.tool.test.jdbc2cfg.CompositeIdTest;
import org.hibernate.tool.test.jdbc2cfg.ForeignKeysTest;
import org.hibernate.tool.test.jdbc2cfg.KeyPropertyCompositeIdTest;
import org.hibernate.tool.test.jdbc2cfg.VersioningTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	AutoQuoteTest.class,
	BasicTest.class,
	BasicMultiSchemaTest.class,
	CompositeIdTest.class,
	ForeignKeysTest.class,
	JDBCMetaDataConfigurationTest.class,
	KeyPropertyCompositeIdTest.class,
	VersioningTest.class
})
public class CommonTestSuite {}
