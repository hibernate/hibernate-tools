package org.hibernate.tool.hbmlint.HbmLintTest;

import java.io.File;

import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.internal.export.lint.BadCachingDetector;
import org.hibernate.tool.internal.export.lint.Detector;
import org.hibernate.tool.internal.export.lint.HbmLint;
import org.hibernate.tool.internal.export.lint.HbmLintExporter;
import org.hibernate.tool.internal.export.lint.InstrumentationDetector;
import org.hibernate.tool.internal.export.lint.ShadowedIdentifierDetector;
import org.hibernate.tools.test.util.HibernateUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestCase {

	private static final String[] HBM_XML_FILES = new String[] {
			"CachingSettings.hbm.xml",
			"IdentifierIssues.hbm.xml",
			"BrokenLazy.hbm.xml"
	};
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private File outputDir = null;
	private File resourcesDir = null;
	
	private MetadataDescriptor metadataDescriptor = null;
	
	@Before
	public void setUp() {
		outputDir = new File(temporaryFolder.getRoot(), "output");
		outputDir.mkdir();
		resourcesDir = new File(temporaryFolder.getRoot(), "resources");
		resourcesDir.mkdir();
		metadataDescriptor = HibernateUtil.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
	}
	
	@Test
	public void testExporter() {	
		HbmLintExporter exporter = new HbmLintExporter();
		exporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		exporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputDir);
		exporter.start();
	}
	
	@Test
	public void testValidateCache() {	
		HbmLint analyzer = new HbmLint(new Detector[] { new BadCachingDetector() });		
		analyzer.analyze(metadataDescriptor.createMetadata());
		Assert.assertEquals(1,analyzer.getResults().size());		
	}

	@Test
	public void testValidateIdentifier() {		
		HbmLint analyzer = new HbmLint(new Detector[] { new ShadowedIdentifierDetector() });		
		analyzer.analyze(metadataDescriptor.createMetadata());
		Assert.assertEquals(1,analyzer.getResults().size());
	}
	
	@Test
	public void testBytecodeRestrictions() {		
		HbmLint analyzer = new HbmLint(new Detector[] { new InstrumentationDetector() });		
		analyzer.analyze(metadataDescriptor.createMetadata());
		Assert.assertEquals(analyzer.getResults().toString(), 2,analyzer.getResults().size());
	}
	
}
