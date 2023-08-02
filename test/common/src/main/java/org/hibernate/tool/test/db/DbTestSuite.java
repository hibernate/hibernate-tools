/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2004-2021 Red Hat, Inc.
 *
 * Licensed under the GNU Lesser General Public License (LGPL), 
 * version 2.1 or later (the "License").
 * You may not use this file except in compliance with the License.
 * You may read the licence in the 'lgpl.txt' file in the root folder of 
 * project or obtain a copy at
 *
 *     http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.tool.test.db;

import org.junit.jupiter.api.Disabled;
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
	@Nested public class QueryExporterTest extends org.hibernate.tool.hbm2x.query.QueryExporterTest.TestCase {}
	@Nested public class HbmLintTest extends org.hibernate.tool.hbmlint.HbmLintTest.TestCase {}
	@Nested public class SchemaAnalyzer extends org.hibernate.tool.hbmlint.SchemaAnalyzer.TestCase {}
	@Nested public class AutoQuote extends org.hibernate.tool.jdbc2cfg.AutoQuote.TestCase {}
	@Nested public class Basic extends org.hibernate.tool.jdbc2cfg.Basic.TestCase {}
	@Nested public class BasicMultiSchema extends org.hibernate.tool.jdbc2cfg.BasicMultiSchema.TestCase {}
	// TODO HBX-2561: Reenable the test below
	@Disabled @Nested public class CompositeId extends org.hibernate.tool.jdbc2cfg.CompositeId.TestCase {}
	@Nested public class ForeignKeys extends org.hibernate.tool.jdbc2cfg.ForeignKeys.TestCase {}
	@Nested public class Identity extends org.hibernate.tool.jdbc2cfg.Identity.TestCase {}
	@Nested public class Index extends org.hibernate.tool.jdbc2cfg.Index.TestCase {}
	@Nested public class KeyPropertyCompositeId extends org.hibernate.tool.jdbc2cfg.KeyPropertyCompositeId.TestCase {}
	@Nested public class ManyToMany extends org.hibernate.tool.jdbc2cfg.ManyToMany.TestCase {}
	@Nested public class MetaData extends org.hibernate.tool.jdbc2cfg.MetaData.TestCase {}
	@Nested public class NoPrimaryKey extends org.hibernate.tool.jdbc2cfg.NoPrimaryKey.TestCase {}
	// TODO HBX-2561: Reenable the test below
	@Disabled @Nested public class OneToOne extends org.hibernate.tool.jdbc2cfg.OneToOne.TestCase {}
	// TODO HBX-2561: Reenable the test below
	@Disabled @Nested public class OverrideBinder extends org.hibernate.tool.jdbc2cfg.OverrideBinder.TestCase {}
	@Nested public class Performance extends org.hibernate.tool.jdbc2cfg.Performance.TestCase {}
	@Nested public class PersistentClasses extends org.hibernate.tool.jdbc2cfg.PersistentClasses.TestCase {}
	// TODO HBX-2561: Reenable the test below
	@Disabled @Nested public class RevEngForeignKey extends org.hibernate.tool.jdbc2cfg.RevEngForeignKey.TestCase {}
	@Nested public class SearchEscapeString extends org.hibernate.tool.jdbc2cfg.SearchEscapeString.TestCase {}
	@Nested public class TernarySchema extends org.hibernate.tool.jdbc2cfg.TernarySchema.TestCase {}
	@Nested public class Versioning extends org.hibernate.tool.jdbc2cfg.Versioning.TestCase {}
	@Nested public class Statistics extends org.hibernate.tool.stat.Statistics.TestCase {}

}
