package org.hibernate.tool.test.db.mysql;

import org.hibernate.cfg.JDBCMetaDataConfigurationTest;
import org.hibernate.tool.test.jdbc2cfg.AutoQuoteTest;
import org.hibernate.tool.test.jdbc2cfg.BasicMultiSchemaTest;
import org.hibernate.tool.test.jdbc2cfg.BasicTest;
import org.hibernate.tool.test.jdbc2cfg.CompositeIdTest;
import org.hibernate.tool.test.jdbc2cfg.ForeignKeysTest;
import org.hibernate.tool.test.jdbc2cfg.IndexTest;
import org.hibernate.tool.test.jdbc2cfg.KeyPropertyCompositeIdTest;
import org.hibernate.tool.test.jdbc2cfg.ManyToManyTest;
import org.hibernate.tool.test.jdbc2cfg.MetaDataTest;
import org.hibernate.tool.test.jdbc2cfg.NoPrimaryKeyTest;
import org.hibernate.tools.test.util.DbSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(DbSuite.class)
@SuiteClasses({ 
	AutoQuoteTest.class,
	BasicMultiSchemaTest.class,
	BasicTest.class,
	CompositeIdTest.class,
	ForeignKeysTest.class,
	IndexTest.class,
	KeyPropertyCompositeIdTest.class,
	ManyToManyTest.class,
	MetaDataTest.class,
	NoPrimaryKeyTest.class,
	JDBCMetaDataConfigurationTest.class 
})
public class TestSuite {}
