package org.hibernate.tool.test.db;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	org.hibernate.tool.test.cfg.JDBCMetaDataConfiguration.TestCase.class,
	org.hibernate.tool.test.jdbc2cfg.AutoQuote.TestCase.class,
	org.hibernate.tool.test.jdbc2cfg.Basic.TestCase.class,
	org.hibernate.tool.test.jdbc2cfg.BasicMultiSchema.TestCase.class,
	org.hibernate.tool.test.jdbc2cfg.CompositeId.TestCase.class,
	org.hibernate.tool.test.jdbc2cfg.ForeignKeys.TestCase.class,
	org.hibernate.tool.test.jdbc2cfg.Index.TestCase.class,
	org.hibernate.tool.test.jdbc2cfg.KeyPropertyCompositeId.TestCase.class,
	org.hibernate.tool.test.jdbc2cfg.ManyToMany.TestCase.class,
	org.hibernate.tool.test.jdbc2cfg.MetaData.TestCase.class,
	org.hibernate.tool.test.jdbc2cfg.NoPrimaryKey.TestCase.class,
	org.hibernate.tool.test.jdbc2cfg.OneToOne.TestCase.class,
	org.hibernate.tool.test.jdbc2cfg.OverrideBinder.TestCase.class,
	org.hibernate.tool.test.jdbc2cfg.Performance.TestCase.class,
	org.hibernate.tool.test.jdbc2cfg.SearchEscapeString.TestCase.class,
	org.hibernate.tool.test.jdbc2cfg.Versioning.TestCase.class
})
public class CommonTestSuite {}
