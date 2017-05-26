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
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.RowVersionType;
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
	
	static final String[] CREATE_SQL = new String[] {
				"CREATE TABLE WITH_VERSION (FIRST INT, SECOND INT, VERSION INT, NAME VARCHAR(256), PRIMARY KEY (FIRST))",
				"CREATE TABLE NO_VERSION (FIRST INT, SECOND INT, NAME VARCHAR(256), PRIMARY KEY (SECOND))",
				"CREATE TABLE WITH_REAL_TIMESTAMP (FIRST INT, SECOND INT, TIMESTAMP TIMESTAMP, NAME VARCHAR(256), PRIMARY KEY (FIRST))",
				"CREATE TABLE WITH_FAKE_TIMESTAMP (FIRST INT, SECOND INT, TIMESTAMP INT, NAME VARCHAR(256), PRIMARY KEY (FIRST))",
		};

	static final String[] DROP_SQL = new String[]  {
				"DROP TABLE WITH_VERSION",
				"DROP TABLE NO_VERSION",
				"DROP TABLE WITH_REAL_TIMESTAMP",
				"DROP TABLE WITH_FAKE_TIMESTAMP"
		};

	private JDBCMetaDataConfiguration jmdcfg = null;
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);;
		jmdcfg = new JDBCMetaDataConfiguration();
		jmdcfg.readFromJDBC();
	}

	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);;
	}

	@Test
	public void testVersion() {		
		PersistentClass cl = jmdcfg.getMetadata().getEntityBinding("WithVersion");		
		Property version = cl.getVersion();
		Assert.assertNotNull(version);
		Assert.assertEquals("version", version.getName());		
		cl = jmdcfg.getMetadata().getEntityBinding("NoVersion");
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
		PersistentClass cl = metadata.getEntityBinding( "WithVersion" );				
		Property version = cl.getVersion();
		Assert.assertNotNull(version);
		Assert.assertEquals("version", version.getName());	
		cl = metadata.getEntityBinding( "NoVersion" );
		Assert.assertNotNull(cl);
		version = cl.getVersion();
		Assert.assertNull(version);
		cl = metadata.getEntityBinding( "WithRealTimestamp" );
		Assert.assertNotNull(cl);
		version = cl.getVersion();
		Assert.assertNotNull(version);
		Assert.assertTrue(
				version.getType() instanceof TimestampType || 
				version.getType() instanceof RowVersionType);	// on MS SQL Server
		cl = metadata.getEntityBinding( "WithFakeTimestamp" );
		Assert.assertNotNull(cl);
		version = cl.getVersion();
		Assert.assertNotNull(version);
		Assert.assertTrue(
				version.getType() instanceof IntegerType ||
				version.getType() instanceof BigDecimalType); // on Oracle
	}
    
}
