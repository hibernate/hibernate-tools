/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2004-2021 Red Hat, Inc.
 *
 * Licensed under the GNU Lesser General Public License (LGPL), 
 * version 2.1 or later (the "License").
 * You may not use this file except in compliance with the License.
 * You may read the licence in the 'lgpl.txt' file in the root folder of 
 * project or obtain a copy at
 *
 *     http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.tool.hbm2x.GenerateFromJDBC;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.export.ExporterFactory;
import org.hibernate.tool.api.export.ExporterType;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.internal.export.doc.DocExporter;
import org.hibernate.tool.internal.export.hbm.HbmExporter;
import org.hibernate.tool.internal.reveng.strategy.AbstractStrategy;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.hibernate.tools.test.util.JUnitUtil;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * @author max
 * @author koen
 */
public class TestCase {
	
	@TempDir
	public File outputDir = new File("output");
	
	private MetadataDescriptor metadataDescriptor = null;
	
	@BeforeEach
	public void setUp() {
		JdbcUtil.createDatabase(this);
		AbstractStrategy configurableNamingStrategy = new DefaultStrategy();
		configurableNamingStrategy.setSettings(new RevengSettings(configurableNamingStrategy).setDefaultPackageName("org.reveng").setCreateCollectionForForeignKey(false));
		metadataDescriptor = MetadataDescriptorFactory
				.createReverseEngineeringDescriptor(configurableNamingStrategy, null);
	}
	
	@AfterEach
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	@Test
	public void testGenerateJava() throws SQLException, ClassNotFoundException {
		Exporter exporter = ExporterFactory.createExporter(ExporterType.JAVA);		
		exporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		exporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputDir);
		exporter.start();
		exporter = ExporterFactory.createExporter(ExporterType.JAVA);
		exporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		exporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputDir);
		exporter.getProperties().setProperty("ejb3", "true");
		exporter.start();
	}
	
	@Test
	public void testGenerateMappings() {
		Exporter exporter = new HbmExporter();	
		exporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		exporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputDir);
		exporter.start();	
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "org/reveng/Child.hbm.xml"));
		File file = new File(outputDir, "GeneralHbmSettings.hbm.xml");
		assertTrue(!file.exists(), file + " should not exist" );
		File[] files = new File[2];
		files[0] = new File(outputDir, "org/reveng/Child.hbm.xml");
		files[1] = new File(outputDir, "org/reveng/Master.hbm.xml");
		Metadata metadata = MetadataDescriptorFactory
				.createNativeDescriptor(null, files, null)
				.createMetadata();
		assertNotNull(metadata.getEntityBinding("org.reveng.Child") );
		assertNotNull(metadata.getEntityBinding("org.reveng.Master") );
	}
	
	@Test
	public void testGenerateCfgXml() throws DocumentException {	
		Exporter exporter = ExporterFactory.createExporter(ExporterType.CFG);
		exporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		exporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputDir);
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
		assertEquals(2,elements.length);	
		for (int i = 0; i < elements.length; i++) {
			Element element = elements[i];
			assertNotNull(element.attributeValue("resource"));
			assertNull(element.attributeValue("class"));
		}		
	}
	
	@Test
	public void testGenerateAnnotationCfgXml() throws DocumentException {
		Exporter exporter = ExporterFactory.createExporter(ExporterType.CFG);
		exporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		exporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputDir);
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
		assertEquals(2, elements.length);
		for (int i = 0; i < elements.length; i++) {
			Element element = elements[i];
			assertNull(element.attributeValue("resource"));
			assertNotNull(element.attributeValue("class"));
		}		
	}
	
	@Test
	public void testGenerateDoc() {	
		DocExporter exporter = new DocExporter();
		exporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		exporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputDir);
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
			assertEquals("org.reveng", StringHelper.qualifier(element.getClassName() ) );
		}
	}
}
