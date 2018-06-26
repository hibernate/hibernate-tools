/*
 * Created on 07-Dec-2004
 *
 */
package org.hibernate.tool.hbx1093;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.hbm2x.POJOExporter;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author koen
 */
public class TestCase {
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private MetadataDescriptor metadataDescriptor = null;
	private File outputDir = null;
	
	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);
		outputDir = temporaryFolder.getRoot();
        DefaultReverseEngineeringStrategy c = new DefaultReverseEngineeringStrategy();
        c.setSettings(new ReverseEngineeringSettings(c).setDetectManyToMany(true)); 
		metadataDescriptor = MetadataDescriptorFactory
				.createJdbcDescriptor(c, null, true);
	}
	
	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	@Test
	public void testGenerateJava() throws IOException {
		POJOExporter exporter = new POJOExporter();		
		exporter.setMetadataDescriptor(metadataDescriptor);
		exporter.setOutputDirectory(outputDir);
		exporter.getProperties().setProperty("ejb3", "true");
		exporter.start();
		File etManyToManyComp1 = new File(outputDir, "EtManyToManyComp1.java");
		String str = new String(Files.readAllBytes(etManyToManyComp1.toPath()));
		Assert.assertTrue(str.contains("@JoinColumn(name=\"FK_ET_MANY_TO_MANY_COMP22_ID\""));
		File etManyToManyComp2 = new File(outputDir, "EtManyToManyComp2.java");
		str = new String(Files.readAllBytes(etManyToManyComp2.toPath()));
		Assert.assertTrue(str.contains("@JoinColumn(name=\"FK_ET_MANY_TO_MANY_COMP11_ID\""));
	}
	
}
