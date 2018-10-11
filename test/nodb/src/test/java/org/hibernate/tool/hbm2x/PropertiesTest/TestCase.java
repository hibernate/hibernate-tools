/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.hbm2x.PropertiesTest;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.export.ExporterFactory;
import org.hibernate.tool.api.export.ExporterType;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.internal.export.common.DefaultArtifactCollector;
import org.hibernate.tool.internal.export.hbm.HibernateMappingExporter;
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
 * @author Josh Moore josh.moore@gmx.de
 * @author koen
 */
public class TestCase {
	
	private static final String[] HBM_XML_FILES = new String[] {
			"Properties.hbm.xml"
	};
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private DefaultArtifactCollector artifactCollector;
	private File outputDir = null;
	private File resourcesDir = null;
	
	@Before
	public void setUp() throws Exception {
		artifactCollector = new DefaultArtifactCollector();
		outputDir = new File(temporaryFolder.getRoot(), "output");
		outputDir.mkdir();
		resourcesDir = new File(temporaryFolder.getRoot(), "resources");
		resourcesDir.mkdir();
		MetadataDescriptor metadataDescriptor = HibernateUtil
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		Exporter exporter = ExporterFactory.createExporter(ExporterType.POJO);
		exporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		exporter.getProperties().put(ExporterConstants.OUTPUT_FOLDER, outputDir);
		exporter.getProperties().put(ExporterConstants.ARTIFACT_COLLECTOR, artifactCollector);
		Exporter hbmexporter = new HibernateMappingExporter();
		hbmexporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		hbmexporter.getProperties().put(ExporterConstants.OUTPUT_FOLDER, outputDir);
		hbmexporter.getProperties().put(ExporterConstants.ARTIFACT_COLLECTOR, artifactCollector);
		exporter.start();
		hbmexporter.start();
	}	
	
	@Test
	public void testNoGenerationOfEmbeddedPropertiesComponent() {
		Assert.assertEquals(2, artifactCollector.getFileCount("java"));
		Assert.assertEquals(2, artifactCollector.getFileCount("hbm.xml"));
	}
	
	@Test
	public void testGenerationOfEmbeddedProperties() {
		File outputXml = new File(outputDir,  "properties/PPerson.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
    	SAXReader xmlReader = new SAXReader();
    	xmlReader.setValidation(true);
		Document document;
		try {
			document = xmlReader.read(outputXml);
			XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/properties");
			List<?> list = xpath.selectNodes(document);
			Assert.assertEquals("Expected to get one properties element", 1, list.size());
			Element node = (Element) list.get(0);
			Assert.assertEquals(node.attribute( "name" ).getText(),"emergencyContact");
			Assert.assertNotNull(
					FileUtil.findFirstString(
							"name", 
							new File(outputDir, "properties/PPerson.java" )));
			Assert.assertNull(
					"Embedded component/properties should not show up in .java", 
					FileUtil.findFirstString(
							"emergencyContact", 
							new File(outputDir, "properties/PPerson.java" )));		
		} catch (DocumentException e) {
			Assert.fail("Can't parse file " + outputXml.getAbsolutePath());
		}		
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
