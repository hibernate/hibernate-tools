package org.hibernate.tool.hbmlint.HbmLintTest;

import org.hibernate.boot.MetadataSources;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2x.HbmLintExporter;
import org.hibernate.tool.hbmlint.Detector;
import org.hibernate.tool.hbmlint.HbmLint;
import org.hibernate.tool.hbmlint.detector.BadCachingDetector;
import org.hibernate.tool.hbmlint.detector.InstrumentationDetector;
import org.hibernate.tool.hbmlint.detector.ShadowedIdentifierDetector;
import org.hibernate.tools.test.util.HibernateUtil;
import org.junit.After;
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
	
	@Before
	public void setUp() {
		System.setProperty("hibernate.dialect", HibernateUtil.Dialect.class.getName());
	}
	
	@After
	public void tearDown() {
		System.getProperties().remove("hibernate.dialect");
	}

	@Test
	public void testExporter() {	
		Configuration configuration = HibernateUtil
				.initializeConfiguration(this, HBM_XML_FILES);
		HbmLintExporter exporter = new HbmLintExporter();		
		exporter.setConfiguration(configuration);
		exporter.setOutputDirectory(temporaryFolder.getRoot());
		exporter.start();
	}
	
	@Test
	public void testValidateCache() {	
		MetadataSources metadataSources = initializeMetadataSources();
		HbmLint analyzer = new HbmLint(new Detector[] { new BadCachingDetector() });		
		analyzer.analyze(metadataSources.buildMetadata());
		Assert.assertEquals(1,analyzer.getResults().size());		
	}

	@Test
	public void testValidateIdentifier() {		
		MetadataSources metadataSources = initializeMetadataSources();
		HbmLint analyzer = new HbmLint(new Detector[] { new ShadowedIdentifierDetector() });		
		analyzer.analyze(metadataSources.buildMetadata());
		Assert.assertEquals(1,analyzer.getResults().size());
	}
	
	@Test
	public void testBytecodeRestrictions() {		
		MetadataSources metadataSources = initializeMetadataSources();
		HbmLint analyzer = new HbmLint(new Detector[] { new InstrumentationDetector() });		
		analyzer.analyze(metadataSources.buildMetadata());
		Assert.assertEquals(analyzer.getResults().toString(), 2,analyzer.getResults().size());
	}
	
	private MetadataSources initializeMetadataSources() {
		MetadataSources result = new MetadataSources();
		String resourcesLocation = '/' + getClass().getPackage().getName().replace(".", "/") + '/';
		for (int i = 0; i < HBM_XML_FILES.length; i++) {
			result.addResource(resourcesLocation + HBM_XML_FILES[i]);
		}
		return result;
	}
	 

	
}
