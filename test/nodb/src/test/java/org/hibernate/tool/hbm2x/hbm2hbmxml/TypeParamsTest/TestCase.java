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
package org.hibernate.tool.hbm2x.hbm2hbmxml.TypeParamsTest;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.metadata.MetadataDescriptor;
import org.hibernate.tool.metadata.MetadataSourcesFactory;
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
			"Order.hbm.xml",
	};
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private File outputDir = null;
	private File resourcesDir = null;

	@Before
	public void setUp() throws Exception {
		outputDir = new File(temporaryFolder.getRoot(), "output");
		outputDir.mkdir();
		resourcesDir = new File(temporaryFolder.getRoot(), "resources");
		resourcesDir.mkdir();
		MetadataDescriptor metadataDescriptor = HibernateUtil
				.initializeMetadataSources(this, HBM_XML_FILES, resourcesDir);
		Exporter hbmexporter = new HibernateMappingExporter();
		hbmexporter.setMetadataSources(metadataDescriptor);
		hbmexporter.setOutputDirectory(outputDir);
		hbmexporter.start();
	}

	@Test
	public void testAllFilesExistence() {
		JUnitUtil.assertIsNonEmptyFile(
				new File(
						outputDir,  
						"org/hibernate/tool/hbm2x/hbm2hbmxml/TypeParamsTest/Order.hbm.xml"));
	}

	@Test
	public void testReadable() {
		File orderHbmXml =
        		new File(
        				outputDir, 
        				"org/hibernate/tool/hbm2x/hbm2hbmxml/TypeParamsTest/Order.hbm.xml");
		Properties properties = new Properties();
		properties.setProperty(AvailableSettings.DIALECT, HibernateUtil.Dialect.class.getName());
		File[] files = new File[] { orderHbmXml };
		MetadataDescriptor metadataDescriptor = MetadataSourcesFactory
				.createNativeSources(null, files, properties);
        Assert.assertNotNull(metadataDescriptor.buildMetadata());
    }

	@Test
	public void testTypeParamsElements() throws DocumentException {
		File outputXml = new File(
				outputDir,  
				"org/hibernate/tool/hbm2x/hbm2hbmxml/TypeParamsTest/Order.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/property");
		List<?> list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get one property element", 2, list.size());
		Element statusElement = (Element) list.get(0);
		Element nameElement = (Element) list.get(1);
		if(!statusElement.attribute( "name" ).getText().equals("status")) {
			Element temp = nameElement;
			nameElement = statusElement;
			statusElement = temp;
		}
		Assert.assertEquals(statusElement.attribute( "name" ).getText(),"status");
		list = statusElement.elements("type");
		Assert.assertEquals("Expected to get one type element", 1, list.size());
		list =  ((Element) list.get(0)).elements("param");		
		Assert.assertEquals("Expected to get 5 params elements", list.size(), 5);
		Map<String, String> params = new HashMap<String, String>();
		for (int i = 0; i < list.size(); i++) {
			Element param = (Element) list.get(i);
			params.put(param.attribute( "name" ).getText(), param.getText());
		}
		Set<String> set = params.keySet();
		Assert.assertEquals("Expected to get 5 different params elements", params.size(), 5);
		Assert.assertTrue(
				"Can't find 'catalog' param", 
				set.contains("catalog"));
		Assert.assertEquals("", params.get("catalog"));
		Assert.assertTrue(
				"Can't find 'column' param", 
				set.contains("column"));
		Assert.assertEquals("STATUS", params.get("column"));
		Assert.assertTrue(
				"Can't find 'table' param", 
				set.contains("table"));
		Assert.assertEquals("ORDERS", params.get("table"));
		Assert.assertTrue(
				"Can't find 'schema' param", 
				set.contains("schema"));
		Assert.assertEquals("", params.get("schema"));
		Assert.assertTrue(
				"Can't find 'enumClass' param", 
				set.contains("enumClass"));
		Assert.assertEquals(
				"org.hibernate.tool.hbm2x.hbm2hbmxml.Order$Status", 
				params.get("enumClass"));
		Assert.assertTrue("property name should not have any type element",nameElement.elements("type").isEmpty());
		Assert.assertEquals(nameElement.attribute("type").getText(), "string");
	}

}
