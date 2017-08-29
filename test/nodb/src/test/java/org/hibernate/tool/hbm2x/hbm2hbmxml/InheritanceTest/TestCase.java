//$Id$

/*
 * Tests for generating the HBM documents from the Configuration data structure.
 * The generated XML document will be validated and queried to make sure the
 * basic structure is correct in each test.
 */
package org.hibernate.tool.hbm2x.hbm2hbmxml.InheritanceTest;

import java.io.File;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JUnitUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * this test should be fixed to have a proper model. currently a mix of subclass/joinedsubclass is in play.
 * @author max
 *
 */
public class TestCase {

	private static final String[] HBM_XML_FILES = new String[] {
			"Aliens.hbm.xml"
	};
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private File outputDir = null;
	private Exporter hbmexporter = null;

	@Before
	public void setUp() throws Exception {
		Metadata metadata = 
				HibernateUtil.initializeMetadata(this, HBM_XML_FILES);
		outputDir = temporaryFolder.getRoot();
		hbmexporter = new HibernateMappingExporter();
		hbmexporter.setMetadata(metadata);
		hbmexporter.setOutputDirectory(outputDir);
		hbmexporter.start();
	}

	@Test
	public void testAllFilesExistence() {
		Assert.assertFalse(new File(
				outputDir,
				"GeneralHbmSettings.hbm.xml").exists() );
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/InheritanceTest/Human.hbm.xml"));
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/InheritanceTest/Alien.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir,
				"/org/hibernate/tool/hbm2x/hbm2hbmxml/InheritanceTest/Animal.hbm.xml") );
	}

	@Test
	public void testArtifactCollection() {
		Assert.assertEquals(
				3,
				hbmexporter.getArtifactCollector().getFileCount("hbm.xml"));
	}

	public void testReadable() {
		StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder();
		ssrb.applySetting(AvailableSettings.DIALECT, HibernateUtil.Dialect.class.getName());
        MetadataSources metadataSources = new MetadataSources(ssrb.build());
        metadataSources.addFile(new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/InheritanceTest/Alien.hbm.xml"));
        metadataSources.addFile(new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/InheritanceTest/Human.hbm.xml"));
        metadataSources.addFile(new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/InheritanceTest/Animal.hbm.xml"));
        Assert.assertNotNull(metadataSources.buildMetadata());
    }

	// TODO Re-enable this test: HBX-1247
	@Ignore
	@Test
	public void testComment() {
		File outputXml = new File(
				outputDir,
				"/org/hibernate/tool/hbm2x/hbm2hbmxml/InheritanceTest/Alien.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document;
		try {
			document = xmlReader.read(outputXml);
			XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/joined-subclass/comment");
			List<?> list = xpath.selectNodes(document);
			Assert.assertEquals("Expected to get one comment element", 1, list.size());
		} catch (DocumentException e) {
			Assert.fail("Can't parse file " + outputXml.getAbsolutePath());
		}
    }
	
	@Test
	public void testDiscriminator() throws DocumentException {
		File outputXml = new File(
				outputDir, 
				"/org/hibernate/tool/hbm2x/hbm2hbmxml/InheritanceTest/Animal.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/discriminator");
		List<?> list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get one discriminator element", 1, list.size());	
		Element node = (Element) list.get(0);
		Assert.assertEquals(node.attribute( "type" ).getText(), "string");
	}

}
