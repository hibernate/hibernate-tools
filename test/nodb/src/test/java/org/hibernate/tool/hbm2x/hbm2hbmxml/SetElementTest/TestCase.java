/*
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.tool.hbm2x.hbm2hbmxml.SetElementTest;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.internal.export.hbm.HibernateMappingExporter;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JUnitUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Dmitry Geraskov
 * @author koen
 */
public class TestCase {

	private static final String[] HBM_XML_FILES = new String[] {
			"Search.hbm.xml",
	};
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private Exporter hbmexporter = null;
	private File outputDir = null;
	private File resourcesDir = null;

	@Before
	public void setUp() throws Exception {
		outputDir = new File(temporaryFolder.getRoot(), "output");
		outputDir.mkdir();
		resourcesDir = new File(temporaryFolder.getRoot(), "resources");
		resourcesDir.mkdir();
		MetadataDescriptor metadataDescriptor = HibernateUtil
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		hbmexporter = new HibernateMappingExporter();
		hbmexporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		hbmexporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputDir);
		hbmexporter.start();
	}

	@Test
	public void testAllFilesExistence() {
		JUnitUtil.assertIsNonEmptyFile(
				new File(
						outputDir,  
						"org/hibernate/tool/hbm2x/hbm2hbmxml/SetElementTest/Search.hbm.xml"));
	}

	@Test
	public void testReadable() {
        File searchHbmXml =	new File(
						outputDir,  
						"org/hibernate/tool/hbm2x/hbm2hbmxml/SetElementTest/Search.hbm.xml");
		Properties properties = new Properties();
		properties.setProperty(AvailableSettings.DIALECT, HibernateUtil.Dialect.class.getName());
		File[] files = new File[] { searchHbmXml };
		MetadataDescriptor metadataDescriptor = MetadataDescriptorFactory
				.createNativeDescriptor(null, files, properties);
        Assert.assertNotNull(metadataDescriptor.createMetadata());
    }

	@Test
	public void testKey() throws DocumentException {
		File outputXml = 
				new File(
						outputDir,  
						"org/hibernate/tool/hbm2x/hbm2hbmxml/SetElementTest/Search.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/set/key");
		List<?> list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get one key element", 1, list.size());
		Element node = (Element) list.get(0);
		if (node.attribute( "column" ) != null){//implied attribute
			Assert.assertEquals(node.attribute( "column" ).getText(),"searchString");
		} else {
			node = node.element("column");
			Assert.assertEquals(node.attribute( "name" ).getText(),"searchString");
		}
	}

	@Test
	public void testSetElement() throws DocumentException {
		File outputXml = 
				new File(
						outputDir,  
						"org/hibernate/tool/hbm2x/hbm2hbmxml/SetElementTest/Search.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/set");
		List<?> list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get one set element", 1, list.size());
		Element node = (Element) list.get(0);
		Assert.assertEquals(node.attribute( "name" ).getText(),"searchResults");
		Assert.assertEquals(node.attribute( "access" ).getText(),"field");
		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/set/element");
		list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get one element 'element'", 1, list.size());
		node = (Element) list.get(0);
		Assert.assertEquals(node.attribute( "type" ).getText(), "string");
		list = node.selectNodes("column");
		Assert.assertEquals("Expected to get one element 'column'", 1, list.size());
		node = (Element) list.get(0);
		Assert.assertEquals(node.attribute( "name" ).getText(), "text");
	}

}
