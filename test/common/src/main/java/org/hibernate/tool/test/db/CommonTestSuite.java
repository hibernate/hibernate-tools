package org.hibernate.tool.test.db;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	org.hibernate.tool.ant.HbmLint.TestCase.class,
	org.hibernate.tool.ant.JDBCConfiguration.TestCase.class,
	org.hibernate.tool.ant.JDBCConfigWithRevEngXml.TestCase.class,
	org.hibernate.tool.ant.JPABogusPUnit.TestCase.class,
	org.hibernate.tool.ant.JPAPropertyOverridesPUnit.TestCase.class,
	org.hibernate.tool.ant.JPAPUnit.TestCase.class,
	org.hibernate.tool.ant.Properties.TestCase.class,
	org.hibernate.tool.ant.Query.TestCase.class,
	org.hibernate.tool.ant.SchemaExportWarning.TestCase.class,
	org.hibernate.tool.cfg.DriverMetaData.TestCase.class,
	org.hibernate.tool.cfg.JDBCMetaDataConfiguration.TestCase.class,
	org.hibernate.tool.hbm2x.CachedMetaData.TestCase.class,
	org.hibernate.tool.hbm2x.DefaultDatabaseCollector.TestCase.class,
	org.hibernate.tool.hbm2x.DefaultSchemaCatalog.TestCase.class,
	org.hibernate.tool.hbm2x.GenerateFromJDBC.TestCase.class,
	org.hibernate.tool.hbm2x.GenerateFromJDBCWithJavaKeyword.TestCase.class,
	org.hibernate.tool.hbm2x.JdbcHbm2JavaEjb3.TestCase.class,
	org.hibernate.tool.hbm2x.IncrementalSchemaReading.TestCase.class,
	org.hibernate.tool.hbm2x.query.QueryExporterTest.TestCase.class,
	org.hibernate.tool.hbmlint.HbmLintTest.TestCase.class,
	org.hibernate.tool.hbmlint.SchemaAnalyzer.TestCase.class,
	org.hibernate.tool.jdbc2cfg.AutoQuote.TestCase.class,
	org.hibernate.tool.jdbc2cfg.Basic.TestCase.class,
	org.hibernate.tool.jdbc2cfg.BasicMultiSchema.TestCase.class,
	org.hibernate.tool.jdbc2cfg.CompositeId.TestCase.class,
	org.hibernate.tool.jdbc2cfg.ForeignKeys.TestCase.class,
	org.hibernate.tool.jdbc2cfg.Identity.TestCase.class,
	org.hibernate.tool.jdbc2cfg.Index.TestCase.class,
	org.hibernate.tool.jdbc2cfg.KeyPropertyCompositeId.TestCase.class,
	org.hibernate.tool.jdbc2cfg.ManyToMany.TestCase.class,
	org.hibernate.tool.jdbc2cfg.MetaData.TestCase.class,
	org.hibernate.tool.jdbc2cfg.NoPrimaryKey.TestCase.class,
	org.hibernate.tool.jdbc2cfg.OneToOne.TestCase.class,
	org.hibernate.tool.jdbc2cfg.OverrideBinder.TestCase.class,
	org.hibernate.tool.jdbc2cfg.Performance.TestCase.class,
	org.hibernate.tool.jdbc2cfg.PersistentClasses.TestCase.class,
	org.hibernate.tool.jdbc2cfg.RevEngForeignKey.TestCase.class,
	org.hibernate.tool.jdbc2cfg.SearchEscapeString.TestCase.class,
	org.hibernate.tool.jdbc2cfg.TernarySchema.TestCase.class,
	org.hibernate.tool.jdbc2cfg.Versioning.TestCase.class,
	org.hibernate.tool.stat.Statistics.TestCase.class
})
public class CommonTestSuite {}
