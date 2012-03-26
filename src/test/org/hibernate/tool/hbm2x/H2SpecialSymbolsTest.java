/*
 * Created on 27.03.2012
 */
package org.hibernate.tool.hbm2x;

import java.sql.SQLException;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;
import org.hibernate.tool.test.TestHelper;

/**
 * @author Dmitry Geraskov (geraskov@gmail.com)
 *
 */
public class H2SpecialSymbolsTest extends JDBCMetaDataBinderTestCase {
	
	private static final String SCHEMA = "\"TEST2.DOT\"";
	private static final String TABLE = "\"TEST3.DOT\"";

	protected String[] getCreateSQL() {
		return new String[]{
				"create schema " + SCHEMA,
				"create table " + SCHEMA +'.'  + TABLE +" (ID integer not null unique, NAME varchar(255), primary key (ID), unique (ID));", 
		};
	}

	protected String[] getDropSQL() {
		return new String[]{
				"drop table " + SCHEMA + '.' + TABLE,
				"drop schema " + SCHEMA
				
		};
	}
	
	protected void configure(Configuration configuration) {
		configuration.setProperty("hibernate.connection.url", "jdbc:h2:testdb/test.dot;LOCK_MODE=0;TRACE_LEVEL_SYSTEM_OUT=2");
	}
	
	protected void configure(JDBCMetaDataConfiguration configuration) {
		configure((Configuration)configuration);
	}
	
	public void testGenerateJava() throws SQLException, ClassNotFoundException {
		ArtifactCollector collector = new ArtifactCollector();
		POJOExporter exporter = new POJOExporter(cfg,getOutputDir());
		exporter.setArtifactCollector(collector);
		exporter.getProperties().setProperty("ejb3", "true");
		exporter.start();
		
		assertTrue("Can't read tables", cfg.getTableMappings().hasNext());
		assertTrue("Nothing is generated", collector.getFileCount("java") > 0);
		
		TestHelper.deleteDir(getOutputDir());
	}
	
	public boolean appliesTo(Dialect dialect) {
		return dialect instanceof org.hibernate.dialect.H2Dialect;
	}
	
}
