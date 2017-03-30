package org.hibernate.tool.hbmlint;

import org.hibernate.boot.MetadataSources;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;
import org.hibernate.tool.hbm2x.HbmLintExporter;
import org.hibernate.tool.hbmlint.detector.BadCachingDetector;
import org.hibernate.tool.hbmlint.detector.InstrumentationDetector;
import org.hibernate.tool.hbmlint.detector.ShadowedIdentifierDetector;

public class HbmLintTest extends JDBCMetaDataBinderTestCase {

	public HbmLintTest() {
		super();
	}

	protected String[] getMappings() {
		return new String[] { "hbmlint/CachingSettings.hbm.xml", "hbmlint/IdentifierIssues.hbm.xml", "hbmlint/BrokenLazy.hbm.xml" };
	}
	
	public void testExporter() {	
		Configuration configuration = new Configuration();
		addMappings( getMappings(), configuration );
		new HbmLintExporter(configuration, getOutputDir()).start();		
	}
	public void testValidateCache() {	
		MetadataSources metadataSources = new MetadataSources();
		addMappings( getMappings(), metadataSources );
		HbmLint analyzer = new HbmLint(new Detector[] { new BadCachingDetector() });		
		analyzer.analyze(metadataSources.buildMetadata());
		assertEquals(1,analyzer.getResults().size());		
	}

	public void testValidateIdentifier() {		
		MetadataSources metadataSources = new MetadataSources();
		addMappings( getMappings(), metadataSources );
		HbmLint analyzer = new HbmLint(new Detector[] { new ShadowedIdentifierDetector() });		
		analyzer.analyze(metadataSources.buildMetadata());
		assertEquals(1,analyzer.getResults().size());
	}
	
	public void testBytecodeRestrictions() {		
		MetadataSources metadataSources = new MetadataSources();
		addMappings( getMappings(), metadataSources );
		HbmLint analyzer = new HbmLint(new Detector[] { new InstrumentationDetector() });		
		analyzer.analyze(metadataSources.buildMetadata());
		assertEquals(analyzer.getResults().toString(), 2,analyzer.getResults().size());
	}
	
	protected String[] getCreateSQL() {
		return new String[0];// { "create table Category (id numeric(5), parent_id numeric(5))" };
	}

	protected String[] getDropSQL() {
		return new String[0];// { "drop table Category" };
	}
	
	
}
