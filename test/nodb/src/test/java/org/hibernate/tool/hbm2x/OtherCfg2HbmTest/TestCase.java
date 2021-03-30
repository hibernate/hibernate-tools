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
package org.hibernate.tool.hbm2x.OtherCfg2HbmTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.internal.export.hbm.HbmExporter;
import org.hibernate.tools.test.util.FileUtil;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JUnitUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * @author max
 * @author koen
 */
public class TestCase {

	private static final String[] HBM_XML_FILES = new String[] {
			"Customer.hbm.xml",
			"Order.hbm.xml",
			"LineItem.hbm.xml",
			"Product.hbm.xml",
			"HelloWorld.hbm.xml"
	};
	
	@TempDir
	public File outputFolder = new File("output");
	
	private File srcDir = null;
	private File resourcesDir = null;
	
	@BeforeEach
	public void setUp() throws Exception {
		srcDir = new File(outputFolder, "src");
		srcDir.mkdir();		
		resourcesDir = new File(outputFolder, "resources");
		resourcesDir.mkdir();
		MetadataDescriptor metadataDescriptor = HibernateUtil
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		Exporter hbmexporter = new HbmExporter();	
		hbmexporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		hbmexporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, srcDir);
		hbmexporter.start();		
	}
	
	@Test
	public void testFileExistence() {
		JUnitUtil.assertIsNonEmptyFile(new File(srcDir, "org/hibernate/tool/hbm2x/Customer.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile(new File(srcDir, "org/hibernate/tool/hbm2x/LineItem.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile(new File(srcDir, "org/hibernate/tool/hbm2x/Order.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile(new File(srcDir, "org/hibernate/tool/hbm2x/Product.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile(new File(srcDir, "HelloWorld.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile(new File(srcDir, "HelloUniverse.hbm.xml") );		
	}
	
	@Test
    public void testReadable() {
		StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder();
		ssrb.applySetting(AvailableSettings.DIALECT, HibernateUtil.Dialect.class.getName());
		Properties properties = new Properties();
		properties.put(AvailableSettings.DIALECT, HibernateUtil.Dialect.class.getName());
        File[] hbmFiles = new File[4];
        hbmFiles[0] = new File(srcDir, "org/hibernate/tool/hbm2x/Customer.hbm.xml");
        hbmFiles[1] = new File(srcDir, "org/hibernate/tool/hbm2x/LineItem.hbm.xml");
        hbmFiles[2] = new File(srcDir, "org/hibernate/tool/hbm2x/Order.hbm.xml");
        hbmFiles[3] = new File(srcDir, "org/hibernate/tool/hbm2x/Product.hbm.xml");       
        Metadata metadata = MetadataDescriptorFactory
        		.createNativeDescriptor(null, hbmFiles, properties)
        		.createMetadata();
        assertNotNull(metadata);      
    }
	
	@Test
	public void testNoVelocityLeftOvers() {
		assertEquals(null, FileUtil.findFirstString("$",new File(srcDir, "org/hibernate/tool/hbm2x/Customer.hbm.xml") ) );
		assertEquals(null, FileUtil.findFirstString("$",new File(srcDir, "org/hibernate/tool/hbm2x/LineItem.hbm.xml") ) );
		assertEquals(null, FileUtil.findFirstString("$",new File(srcDir, "org/hibernate/tool/hbm2x/Order.hbm.xml") ) );
		assertEquals(null, FileUtil.findFirstString("$",new File(srcDir, "org/hibernate/tool/hbm2x/Product.hbm.xml") ) );   
	}
	
	@Test
	public void testVersioning() throws DocumentException {	
    	SAXReader xmlReader = new SAXReader();
    	xmlReader.setValidation(true);
		Document document = xmlReader.read(new File(srcDir, "org/hibernate/tool/hbm2x/Product.hbm.xml"));
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/version");
		List<?> list = xpath.selectNodes(document);
		assertEquals(1, list.size(), "Expected to get one version element");			
	}
	
}
