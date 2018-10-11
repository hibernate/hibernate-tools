/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.hbm2x.HashcodeEqualsTest;

import java.io.File;

import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.export.ExporterFactory;
import org.hibernate.tool.api.export.ExporterType;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.internal.export.common.DefaultArtifactCollector;
import org.hibernate.tools.test.util.FileUtil;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JUnitUtil;
import org.hibernate.tools.test.util.JavaUtil;
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
			"HashEquals.hbm.xml"				
	};
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private File outputDir = null;
	private File resourcesDir = null;
	private DefaultArtifactCollector artifactCollector = null;
	private MetadataDescriptor metadataDescriptor = null;
	
	@Before
	public void setUp() throws Exception {
		outputDir = new File(temporaryFolder.getRoot(), "output");
		outputDir.mkdir();
		resourcesDir = new File(temporaryFolder.getRoot(), "resources");
		resourcesDir.mkdir();
		metadataDescriptor = HibernateUtil
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		Exporter exporter = ExporterFactory.createExporter(ExporterType.POJO);
		exporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		exporter.getProperties().put(ExporterConstants.OUTPUT_FOLDER, outputDir);
		artifactCollector = new DefaultArtifactCollector();
		exporter.getProperties().put(ExporterConstants.ARTIFACT_COLLECTOR, artifactCollector);
		exporter.start();
	}

	@Test
	public void testJDK5FailureExpectedOnJDK4() {
		Exporter exporter = ExporterFactory.createExporter(ExporterType.POJO);
		exporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		exporter.getProperties().put(ExporterConstants.OUTPUT_FOLDER, outputDir);
		exporter.getProperties().setProperty("jdk5", "true");
		artifactCollector = new DefaultArtifactCollector();
		exporter.getProperties().put(ExporterConstants.ARTIFACT_COLLECTOR, artifactCollector);
		exporter.start();
		testFileExistence();
		testNoVelocityLeftOvers();
		testCompilable();
	}
	
	@Test
	public void testFileExistence() {
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir, "org/hibernate/tool/hbm2x/HashEquals.java"));
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir, "org/hibernate/tool/hbm2x/Address.java"));
		Assert.assertEquals(2, artifactCollector.getFileCount("java"));
	}
	
	@Test
	public void testCompilable() {
		File compiled = new File(temporaryFolder.getRoot(), "compiled");
		compiled.mkdir();
		JavaUtil.compile(outputDir, compiled);
		JUnitUtil.assertIsNonEmptyFile(new File(
				compiled, "org/hibernate/tool/hbm2x/HashEquals.class"));
		JUnitUtil.assertIsNonEmptyFile(new File(
				compiled, "org/hibernate/tool/hbm2x/Address.class"));
	}

	@Test
	public void testNoVelocityLeftOvers() {
		Assert.assertNull(FileUtil
				.findFirstString(
						"$",
						new File(
								outputDir, 
								"org/hibernate/tool/hbm2x/HashEquals.java")));
        Assert.assertNull(FileUtil
        		.findFirstString(
        				"$",
        				new File(
        						outputDir, 
        						"org/hibernate/tool/hbm2x/Address.java")));
	}

}
