/*
 * Created on 2004-11-24
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import java.io.File;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tools.test.util.JdbcUtil;
import org.hibernate.type.IntegerType;
import org.hibernate.type.TimestampType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * To be extended by VersioningForJDK50Test for the JPA generation part
 * @author max
 * @author koen
 *
 */
public class VersioningTest {
	
	private static final String[] CREATE_SQL = new String[] {
				"create table withVersion (first int, second int, version int, name varchar(256), primary key (first))",
				"create table noVersion (first int, second int, name varchar(256), primary key (second))",
				"create table withRealTimestamp (first int, second int, timestamp timestamp, name varchar(256), primary key (first))",
				"create table withFakeTimestamp (first int, second int, timestamp int, name varchar(256), primary key (first))",
		};

	private static final String[] DROP_SQL = new String[]  {
				"drop table withVersion",
				"drop table noVersion",
				"drop table withRealTimestamp",
				"drop table withFakeTimestamp"
		};

	private JDBCMetaDataConfiguration jmdcfg = null;
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Before
	public void setUp() {
		JdbcUtil.establishJdbcConnection(this);
		JdbcUtil.executeDDL(this, CREATE_SQL);
		jmdcfg = new JDBCMetaDataConfiguration();
		jmdcfg.readFromJDBC();
	}

	@After
	public void tearDown() {
		JdbcUtil.executeDDL(this, DROP_SQL);
		JdbcUtil.releaseJdbcConnection(this);
	}

	@Test
	public void testVersion() {		
		PersistentClass cl = jmdcfg.getMetadata().getEntityBinding("Withversion");		
		Property version = cl.getVersion();
		Assert.assertNotNull(version);
		Assert.assertEquals("version", version.getName());		
		cl = jmdcfg.getMetadata().getEntityBinding("Noversion");
		Assert.assertNotNull(cl);
		version = cl.getVersion();
		Assert.assertNull(version);		
	}
	
	@Test
	public void testGenerateMappings() {
		File testFolder = temporaryFolder.getRoot();
        Exporter exporter = new HibernateMappingExporter(jmdcfg, testFolder);		
		exporter.start();		
		MetadataSources derived = new MetadataSources();		
		derived.addFile(new File(testFolder, "Withversion.hbm.xml") );
		derived.addFile(new File(testFolder, "Noversion.hbm.xml") );
		derived.addFile(new File(testFolder, "Withrealtimestamp.hbm.xml") );
		derived.addFile(new File(testFolder, "Withfaketimestamp.hbm.xml") );		
		Metadata metadata = derived.buildMetadata();		
		PersistentClass cl = metadata.getEntityBinding( "Withversion" );				
		Property version = cl.getVersion();
		Assert.assertNotNull(version);
		Assert.assertEquals("version", version.getName());	
		cl = metadata.getEntityBinding( "Noversion" );
		Assert.assertNotNull(cl);
		version = cl.getVersion();
		Assert.assertNull(version);
		cl = metadata.getEntityBinding( "Withrealtimestamp" );
		Assert.assertNotNull(cl);
		version = cl.getVersion();
		Assert.assertNotNull(version);
		Assert.assertTrue(version.getType() instanceof TimestampType);	
		cl = metadata.getEntityBinding( "Withfaketimestamp" );
		Assert.assertNotNull(cl);
		version = cl.getVersion();
		Assert.assertNotNull(version);
		Assert.assertTrue(version.getType() instanceof IntegerType);
	}
    
}
