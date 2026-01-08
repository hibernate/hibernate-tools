/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 *
 * Copyright 2004-2025 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.tool.test.db;

import org.junit.jupiter.api.Nested;

public class DbTestSuite {
	
	@Nested public class AntHibernateToolTests extends org.hibernate.tool.ant.AntHibernateTool.TestCase {}
	@Nested public class Cfg2HbmNoError extends org.hibernate.tool.ant.Cfg2HbmNoError.TestCase {}
	@Nested public class Cfg2HbmWithCustomReverseNamingStrategy extends org.hibernate.tool.ant.Cfg2HbmWithCustomReverseNamingStrategy.TestCase {}
	@Nested public class Cfg2HbmWithInvalidReverseNamingStrategy extends org.hibernate.tool.ant.Cfg2HbmWithInvalidReverseNamingStrategy.TestCase {}
	@Nested public class Cfg2HbmWithPackageName extends org.hibernate.tool.ant.Cfg2HbmWithPackageName.TestCase {}
	@Nested public class Cfg2HbmWithPackageNameAndReverseNamingStrategy extends org.hibernate.tool.ant.Cfg2HbmWithPackageNameAndReverseNamingStrategy.TestCase {}
	@Nested public class EJB3Configuration extends org.hibernate.tool.ant.EJB3Configuration.TestCase {}
	@Nested public class GenericExport extends org.hibernate.tool.ant.GenericExport.TestCase {}
	@Nested public class Hbm2JavaConfiguration extends org.hibernate.tool.ant.Hbm2JavaConfiguration.TestCase {}
	@Nested public class Hbm2JavaEJB3Configuration extends org.hibernate.tool.ant.Hbm2JavaEJB3Configuration.TestCase {}
	@Nested public class HbmLint extends org.hibernate.tool.ant.HbmLint.TestCase {}
	@Nested public class JDBCConfiguration extends org.hibernate.tool.ant.JDBCConfiguration.TestCase {}
	@Nested public class JDBCConfigWithRevEngXml extends org.hibernate.tool.ant.JDBCConfigWithRevEngXml.TestCase {}
	@Nested public class JPABogusPUnit extends org.hibernate.tool.ant.JPABogusPUnit.TestCase {}
	@Nested public class JPAPropertyOverridesPUnit extends org.hibernate.tool.ant.JPAPropertyOverridesPUnit.TestCase {}
	@Nested public class JPAPUnit extends org.hibernate.tool.ant.JPAPUnit.TestCase {}
	@Nested public class Properties extends org.hibernate.tool.ant.Properties.TestCase {}
	@Nested public class Query extends org.hibernate.tool.ant.Query.TestCase {}
	@Nested public class SchemaExportWarning extends org.hibernate.tool.ant.SchemaExportWarning.TestCase {}
	@Nested public class SchemaUpdateWarning extends org.hibernate.tool.ant.SchemaUpdateWarning.TestCase {}
	@Nested public class DriverMetaData extends org.hibernate.tool.cfg.DriverMetaData.TestCase {}
	@Nested public class JDBCMetaDataConfiguration extends org.hibernate.tool.cfg.JDBCMetaDataConfiguration.TestCase {}
	@Nested public class CachedMetaData extends org.hibernate.tool.hbm2x.CachedMetaData.TestCase {}
	@Nested public class DefaultDatabaseCollector extends org.hibernate.tool.hbm2x.DefaultDatabaseCollector.TestCase {}
	@Nested public class DefaultSchemaCatalog extends org.hibernate.tool.hbm2x.DefaultSchemaCatalog.TestCase {}
	@Nested public class GenerateFromJDBC extends org.hibernate.tool.hbm2x.GenerateFromJDBC.TestCase {}
	@Nested public class GenerateFromJDBCWithJavaKeyword extends org.hibernate.tool.hbm2x.GenerateFromJDBCWithJavaKeyword.TestCase {}
	@Nested public class IncrementalSchemaReading extends org.hibernate.tool.hbm2x.IncrementalSchemaReading.TestCase {}
	@Nested public class JdbcHbm2JavaEjb3 extends org.hibernate.tool.hbm2x.JdbcHbm2JavaEjb3.TestCase {}
	@Nested public class HBX2840 extends org.hibernate.tool.hbm2x.hbx2840.TestCase {}
	@Nested public class QueryExporterTest extends org.hibernate.tool.hbm2x.query.QueryExporterTest.TestCase {}
	@Nested public class HbmLintTest extends org.hibernate.tool.hbmlint.HbmLintTest.TestCase {}
	@Nested public class SchemaAnalyzer extends org.hibernate.tool.hbmlint.SchemaAnalyzer.TestCase {}
	@Nested public class AutoQuote extends org.hibernate.tool.jdbc2cfg.AutoQuote.TestCase {}
	@Nested public class Basic extends org.hibernate.tool.jdbc2cfg.Basic.TestCase {}
	@Nested public class Statistics extends org.hibernate.tool.stat.Statistics.TestCase {}

}
