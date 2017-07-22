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
package org.hibernate.tool.hbm2x.hbm2hbmxml.CompositeElementTest;

import java.io.File;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.util.MetadataHelper;
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
			"Glarch.hbm.xml"
	};
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private File outputDir = null;
	private Exporter hbmexporter = null;

	@Before
	public void setUp() throws Exception {
		Configuration configuration = 
				HibernateUtil.initializeConfiguration(this, HBM_XML_FILES);
		outputDir = temporaryFolder.getRoot();
		hbmexporter = new HibernateMappingExporter();
		hbmexporter.setConfiguration(configuration);
		hbmexporter.setOutputDirectory(outputDir);
		hbmexporter.start();
	}

	@Test
	public void testAllFilesExistence() {
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir,  
				"org/hibernate/tool/hbm2x/hbm2hbmxml/CompositeElementTest/Fee.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir,  
				"org/hibernate/tool/hbm2x/hbm2hbmxml/CompositeElementTest/Glarch.hbm.xml") );
	}

	@Test
	public void testReadable() {
        Configuration cfg = new Configuration();
        cfg.setProperty(
        		AvailableSettings.DIALECT, 
        		HibernateUtil.Dialect.class.getName());
        cfg.addFile(new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/CompositeElementTest/Fee.hbm.xml"));
        cfg.addFile(new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/CompositeElementTest/Glarch.hbm.xml"));
        Assert.assertNotNull(MetadataHelper.getMetadata(cfg));
    }

	@Test
	public void testCompositeElementNode() throws DocumentException {
		File outputXml = new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/CompositeElementTest/Glarch.hbm.xml");
        JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		Document document = xmlReader.read(outputXml);
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/list");
		Element node = (Element) xpath.selectNodes(document).get(1); //second list
		List<?> list = node.elements("composite-element");
		Assert.assertEquals("Expected to get one composite-element element", 1, list.size());
		node = (Element) list.get(0);
		Assert.assertEquals("Expected to get two property element", 2, node.elements("property").size());

		node = node.element("many-to-one");
		Assert.assertEquals(node.attribute( "name" ).getText(),"fee");
		Assert.assertEquals(node.attribute( "cascade" ).getText(),"all");
		//TODO: assertEquals(node.attribute( "outer-join" ).getText(),"true");
		node = node.getParent();//composite-element
		node = node.element("nested-composite-element");
		Assert.assertEquals(node.attribute( "name" ).getText(),"subcomponent");
		Assert.assertEquals(node.attribute( "class" ).getText(),"org.hibernate.tool.hbm2x.hbm2hbmxml.CompositeElementTest.FooComponent");
	}

}
