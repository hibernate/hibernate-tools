/*
 * Created on 07-Dec-2004
 *
 */
package org.hibernate.tool.hbm2x.GenerateFromJDBC;

import java.io.File;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.hibernate.boot.Metadata;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.api.reveng.ReverseEngineeringSettings;
import org.hibernate.tool.hbm2x.DocExporter;
import org.hibernate.tool.hbm2x.HibernateConfigurationExporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.POJOExporter;
import org.hibernate.tools.test.util.JUnitUtil;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.After;
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
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private MetadataDescriptor metadataDescriptor = null;
	private File outputDir = null;
	
	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);
		outputDir = temporaryFolder.getRoot();
		DefaultReverseEngineeringStrategy configurableNamingStrategy = new DefaultReverseEngineeringStrategy();
		configurableNamingStrategy.setSettings(new ReverseEngineeringSettings(configurableNamingStrategy).setDefaultPackageName("org.reveng").setCreateCollectionForForeignKey(false));
		metadataDescriptor = MetadataDescriptorFactory
				.createJdbcDescriptor(configurableNamingStrategy, null, true);
	}
	
	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	@Test
	public void testGenerateJava() throws SQLException, ClassNotFoundException {
		POJOExporter exporter = new POJOExporter();		
		exporter.setMetadataDescriptor(metadataDescriptor);
		exporter.setOutputDirectory(outputDir);
		exporter.start();
		exporter = new POJOExporter();
		exporter.setMetadataDescriptor(metadataDescriptor);
		exporter.setOutputDirectory(outputDir);
		exporter.getProperties().setProperty("ejb3", "true");
		exporter.start();
	}
	
	@Test
	public void testGenerateMappings() {
		Exporter exporter = new HibernateMappingExporter();	
		exporter.setMetadataDescriptor(metadataDescriptor);
		exporter.setOutputDirectory(outputDir);
		exporter.start();	
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "org/reveng/Child.hbm.xml"));
		File file = new File(outputDir, "GeneralHbmSettings.hbm.xml");
		Assert.assertTrue(file + " should not exist", !file.exists() );
		File[] files = new File[2];
		files[0] = new File(outputDir, "org/reveng/Child.hbm.xml");
		files[1] = new File(outputDir, "org/reveng/Master.hbm.xml");
		Metadata metadata = MetadataDescriptorFactory
				.createNativeDescriptor(null, files, null)
				.createMetadata();
		Assert.assertNotNull(metadata.getEntityBinding("org.reveng.Child") );
		Assert.assertNotNull(metadata.getEntityBinding("org.reveng.Master") );
	}
	
	@Test
	public void testGenerateCfgXml() throws DocumentException {	
		Exporter exporter = new HibernateConfigurationExporter();
		exporter.setMetadataDescriptor(metadataDescriptor);
		exporter.setOutputDirectory(outputDir);
		exporter.start();				
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "hibernate.cfg.xml"));
		SAXReader xmlReader =  new SAXReader();
      	xmlReader.setValidation(true);
		Document document = xmlReader.read(new File(outputDir, "hibernate.cfg.xml"));
		// Validate the Generator and it has no arguments 
		XPath xpath = DocumentHelper.createXPath("//hibernate-configuration/session-factory/mapping");
		List<?> list = xpath.selectNodes(document);
		Element[] elements = new Element[list.size()];
		for (int i = 0; i < list.size(); i++) {
			elements[i] = (Element)list.get(i);
		}
		Assert.assertEquals(2,elements.length);	
		for (int i = 0; i < elements.length; i++) {
			Element element = elements[i];
			Assert.assertNotNull(element.attributeValue("resource"));
			Assert.assertNull(element.attributeValue("class"));
		}		
	}
	
	@Test
	public void testGenerateAnnotationCfgXml() throws DocumentException {
		HibernateConfigurationExporter exporter = 
				new HibernateConfigurationExporter();
		exporter.setMetadataDescriptor(metadataDescriptor);
		exporter.setOutputDirectory(outputDir);
		exporter.getProperties().setProperty("ejb3", "true");
		exporter.start();	
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "hibernate.cfg.xml"));
		SAXReader xmlReader =  new SAXReader();
    	xmlReader.setValidation(true);
		Document document = xmlReader.read(new File(outputDir, "hibernate.cfg.xml"));
		// Validate the Generator and it has no arguments 
		XPath xpath = DocumentHelper.createXPath("//hibernate-configuration/session-factory/mapping");
		List<?> list = xpath.selectNodes(document);
		Element[] elements = new Element[list.size()];
		for (int i = 0; i < list.size(); i++) {
			elements[i] = (Element)list.get(i);
		}
		Assert.assertEquals(2, elements.length);
		for (int i = 0; i < elements.length; i++) {
			Element element = elements[i];
			Assert.assertNull(element.attributeValue("resource"));
			Assert.assertNotNull(element.attributeValue("class"));
		}		
	}
	
	@Test
	public void testGenerateDoc() {	
		DocExporter exporter = new DocExporter();
		exporter.setMetadataDescriptor(metadataDescriptor);
		exporter.setOutputDirectory(outputDir);
		exporter.start();
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "index.html"));
	}
	
	@Test
	public void testPackageNames() {
		Iterator<PersistentClass> iter = metadataDescriptor
				.createMetadata()
				.getEntityBindings()
				.iterator();
		while (iter.hasNext() ) {
			PersistentClass element = iter.next();
			Assert.assertEquals("org.reveng", StringHelper.qualifier(element.getClassName() ) );
		}
	}
}
