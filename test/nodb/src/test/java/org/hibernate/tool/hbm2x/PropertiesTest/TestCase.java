/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.hbm2x.PropertiesTest;

import java.io.File;
import java.nio.file.Files;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.hbm2x.ArtifactCollector;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.POJOExporter;
import org.hibernate.tools.test.util.FileUtil;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JUnitUtil;
import org.hibernate.tools.test.util.JavaUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Josh Moore josh.moore@gmx.de
 * @author koen
 */
public class TestCase {
	
	private static final String[] HBM_XML_FILES = new String[] {
			"Properties.hbm.xml"
	};
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private ArtifactCollector artifactCollector;
	private File outputDir = null;
	private File resourcesDir = null;
	
	@Before
	public void setUp() throws Exception {
		artifactCollector = new ArtifactCollector();
		outputDir = new File(temporaryFolder.getRoot(), "output");
		outputDir.mkdir();
		resourcesDir = new File(temporaryFolder.getRoot(), "resources");
		resourcesDir.mkdir();
		MetadataDescriptor metadataDescriptor = HibernateUtil
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		Exporter exporter = new POJOExporter();
		exporter.setMetadataDescriptor(metadataDescriptor);
		exporter.setOutputDirectory(outputDir);
		exporter.setArtifactCollector(artifactCollector);
		Exporter hbmexporter = new HibernateMappingExporter();
		hbmexporter.setMetadataDescriptor(metadataDescriptor);
		hbmexporter.setOutputDirectory(outputDir);
		hbmexporter.setArtifactCollector(artifactCollector);
		exporter.start();
		hbmexporter.start();
	}	
	
	@Test
	public void testNoGenerationOfEmbeddedPropertiesComponent() {
		Assert.assertEquals(2, artifactCollector.getFileCount("java"));
		Assert.assertEquals(2, artifactCollector.getFileCount("hbm.xml"));
	}
	
	@Test
	public void testGenerationOfEmbeddedProperties() throws Exception {
		File outputXml = new File(outputDir,  "properties/PPerson.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/properties")
				.evaluate(document, XPathConstants.NODESET);
		Assert.assertEquals("Expected to get one properties element", 1, nodeList.getLength());
		Element element = (Element) nodeList.item(0);
		Assert.assertEquals(element.getAttribute( "name" ),"emergencyContact");
		Assert.assertNotNull(
				FileUtil.findFirstString(
						"name", 
						new File(outputDir, "properties/PPerson.java" )));
		Assert.assertNull(
				"Embedded component/properties should not show up in .java", 
				FileUtil.findFirstString(
						"emergencyContact", 
						new File(outputDir, "properties/PPerson.java" )));		
	}
	
	@Test
	public void testCompilable() throws Exception {
		String propertiesUsageResourcePath = "/org/hibernate/tool/hbm2x/PropertiesTest/PropertiesUsage.java_";
		File propertiesUsageOrigin = new File(getClass().getResource(propertiesUsageResourcePath).toURI());
		File propertiesUsageDestination = new File(outputDir, "properties/PropertiesUsage.java");
		File targetDir = new File(temporaryFolder.getRoot(), "compilerOutput" );
		targetDir.mkdir();	
		Files.copy(propertiesUsageOrigin.toPath(), propertiesUsageDestination.toPath());
		JavaUtil.compile(outputDir, targetDir);
		Assert.assertTrue(new File(targetDir, "properties/PCompany.class").exists());
		Assert.assertTrue(new File(targetDir, "properties/PPerson.class").exists());
		Assert.assertTrue(new File(targetDir, "properties/PropertiesUsage.class").exists());
	}

}
