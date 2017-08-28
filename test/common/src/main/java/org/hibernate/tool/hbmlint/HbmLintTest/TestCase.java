package org.hibernate.tool.hbmlint.HbmLintTest;

import org.hibernate.boot.Metadata;
import org.hibernate.tool.hbm2x.HbmLintExporter;
import org.hibernate.tool.hbmlint.Detector;
import org.hibernate.tool.hbmlint.HbmLint;
import org.hibernate.tool.hbmlint.detector.BadCachingDetector;
import org.hibernate.tool.hbmlint.detector.InstrumentationDetector;
import org.hibernate.tool.hbmlint.detector.ShadowedIdentifierDetector;
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
	
	private Metadata metadata = null;
	
	@Before
	public void setUp() {
		metadata = HibernateUtil.initializeMetadata(this, HBM_XML_FILES);
	}
	
	@Test
	public void testExporter() {	
		HbmLintExporter exporter = new HbmLintExporter();		
		exporter.setMetadata(metadata);
		exporter.setOutputDirectory(temporaryFolder.getRoot());
		exporter.start();
	}
	
	@Test
	public void testValidateCache() {	
		HbmLint analyzer = new HbmLint(new Detector[] { new BadCachingDetector() });		
		analyzer.analyze(metadata);
		Assert.assertEquals(1,analyzer.getResults().size());		
	}

	@Test
	public void testValidateIdentifier() {		
		HbmLint analyzer = new HbmLint(new Detector[] { new ShadowedIdentifierDetector() });		
		analyzer.analyze(metadata);
		Assert.assertEquals(1,analyzer.getResults().size());
	}
	
	@Test
	public void testBytecodeRestrictions() {		
		HbmLint analyzer = new HbmLint(new Detector[] { new InstrumentationDetector() });		
		analyzer.analyze(metadata);
		Assert.assertEquals(analyzer.getResults().toString(), 2,analyzer.getResults().size());
	}
	
}
