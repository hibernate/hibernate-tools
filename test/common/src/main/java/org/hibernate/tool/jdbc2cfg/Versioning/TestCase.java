/*
 * Created on 2004-11-24
 *
 */
package org.hibernate.tool.jdbc2cfg.Versioning;

import java.io.File;

import org.hibernate.boot.Metadata;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.metadata.MetadataDescriptor;
import org.hibernate.tool.metadata.MetadataDescriptorFactory;
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
 */
public class TestCase {
	
	private Metadata metadata = null;
	private MetadataDescriptor metadataSources = null;
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);
		metadataSources = MetadataDescriptorFactory
				.createJdbcSources(null, null, true);
		metadata = metadataSources
				.buildMetadata();
	}

	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);;
	}

	@Test
	public void testVersion() {		
		PersistentClass cl = metadata.getEntityBinding("WithVersion");		
		Property version = cl.getVersion();
		Assert.assertNotNull(version);
		Assert.assertEquals("version", version.getName());		
		cl = metadata.getEntityBinding("NoVersion");
		Assert.assertNotNull(cl);
		version = cl.getVersion();
		Assert.assertNull(version);		
	}
	
	@Test
	public void testGenerateMappings() {
		File testFolder = temporaryFolder.getRoot();
        Exporter exporter = new HibernateMappingExporter();		
        exporter.setMetadataSources(metadataSources);
        exporter.setOutputDirectory(testFolder);
		exporter.start();		
		File[] files = new File[4];		
		files[0] = new File(testFolder, "Withversion.hbm.xml");
		files[1] = new File(testFolder, "Noversion.hbm.xml");
		files[2] = new File(testFolder, "Withrealtimestamp.hbm.xml");
		files[3] = new File(testFolder, "Withfaketimestamp.hbm.xml");		
		Metadata metadata = MetadataDescriptorFactory
				.createNativeSources(null, files, null)
				.buildMetadata();
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
