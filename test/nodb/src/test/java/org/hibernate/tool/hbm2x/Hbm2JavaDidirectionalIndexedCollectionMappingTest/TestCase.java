/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.hbm2x.Hbm2JavaDidirectionalIndexedCollectionMappingTest;

import java.io.File;

import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.hbm2x.POJOExporter;
import org.hibernate.tools.test.util.HibernateUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author max
 * @author koen
 */
public class TestCase {

	private static final String[] HBM_XML_FILES = new String[] {
			"GenericModel.hbm.xml"
	};
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private File outputDir = null;
	private File resourcesDir = null;

	@Before
	public void setUp() throws Exception {
		outputDir = new File(temporaryFolder.getRoot(), "output");
		outputDir.mkdir();
		resourcesDir = new File(temporaryFolder.getRoot(), "resources");
		resourcesDir.mkdir();
		MetadataDescriptor metadataDescriptor = HibernateUtil
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		Exporter exporter = new POJOExporter();
		exporter.setMetadataDescriptor(metadataDescriptor);
		exporter.setOutputDirectory(outputDir);
		exporter.start();
	}
	
	@Test
	public void testReflection() throws Exception {
		Assert.assertTrue(new File(outputDir, "GenericObject.java").exists());
		Assert.assertTrue(new File(outputDir, "GenericValue.java").exists());
	}
	
}
