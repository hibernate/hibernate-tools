/*
 * Created on 2004-11-24
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.type.IntegerType;
import org.hibernate.type.TimestampType;
;

/**
 * To be extended by VersioningForJDK50Test for the JPA generation part
 * @author max
 *
 */
public class VersioningTest extends JDBCMetaDataBinderTestCase {
	
	protected String[] getCreateSQL() {
		
		return new String[] {
				"create table withVersion (first int, second int, version int, name varchar, primary key (first))",
				"create table noVersion (first int, second int, name varchar, primary key (second))",
				"create table withRealTimestamp (first int, second int, timestamp timestamp, name varchar, primary key (first))",
				"create table withFakeTimestamp (first int, second int, timestamp int, name varchar, primary key (first))",
		};
	}

	protected String[] getDropSQL() {
		return new String[] {
				"drop table withVersion",
				"drop table noVersion",
				"drop table withRealTimestamp",
				"drop table withFakeTimestamp"
		};
	}

	public void testVersion() {
		
		PersistentClass cl = cfg.getClassMapping("Withversion");
		
		Property version = cl.getVersion();
		assertNotNull(version);
		assertEquals("version", version.getName());
		
		cl = cfg.getClassMapping("Noversion");
		assertNotNull(cl);
		version = cl.getVersion();
		assertNull(version);
		
	}
	
	public void testGenerateMappings() {
        cfg.buildMappings();
        Exporter exporter = new HibernateMappingExporter(cfg, getOutputDir());
		
		exporter.start();
		
		Configuration derived = new Configuration();
		
		derived.addFile(new File(getOutputDir(), "Withversion.hbm.xml") );
		derived.addFile(new File(getOutputDir(), "Noversion.hbm.xml") );
		derived.addFile(new File(getOutputDir(), "Withrealtimestamp.hbm.xml") );
		derived.addFile(new File(getOutputDir(), "Withfaketimestamp.hbm.xml") );
		
		testVersioningInDerivedCfg(derived);
	}
    
	protected void testVersioningInDerivedCfg(Configuration derived){
		derived.buildMappings();
		
		PersistentClass cl = derived.getClassMapping( "Withversion" );		
		
		Property version = cl.getVersion();
		assertNotNull(version);
		assertEquals("version", version.getName());
		
		cl = derived.getClassMapping( "Noversion" );
		assertNotNull(cl);
		version = cl.getVersion();
		assertNull(version);

		cl = derived.getClassMapping( "Withrealtimestamp" );
		assertNotNull(cl);
		version = cl.getVersion();
		assertNotNull(version);
		assertTrue(version.getType() instanceof TimestampType);
		
		cl = derived.getClassMapping( "Withfaketimestamp" );
		assertNotNull(cl);
		version = cl.getVersion();
		assertNotNull(version);
		assertTrue(version.getType() instanceof IntegerType);
	}
	
	
	public static Test suite() {
		return new TestSuite(VersioningTest.class);
	}
}
