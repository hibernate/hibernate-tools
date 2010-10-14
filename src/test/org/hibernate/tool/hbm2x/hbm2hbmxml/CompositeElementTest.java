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
package org.hibernate.tool.hbm2x.hbm2hbmxml;

import java.io.File;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.NonReflectiveTestCase;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;

/**
 * @author Dmitry Geraskov
 *
 */
public class CompositeElementTest extends NonReflectiveTestCase {

	private String mappingFile = "Glarch.hbm.xml";

	private Exporter hbmexporter;

	/**
	 * @param name
	 */
	public CompositeElementTest(String name) {
		super(name, "cfg2hbmoutput");
	}


	protected String[] getMappings() {
		return new String[] {
				mappingFile
		};
	}

	protected void setUp() throws Exception {
		super.setUp();

		hbmexporter = new HibernateMappingExporter(getCfg(), getOutputDir() );
		hbmexporter.start();
	}

	public void testAllFilesExistence() {
		assertFileAndExists(new File(getOutputDir().getAbsolutePath(),  getBaseForMappings() + "Fee.hbm.xml") );
		assertFileAndExists(new File(getOutputDir().getAbsolutePath(),  getBaseForMappings() + "Glarch.hbm.xml") );
	}

	public void testReadable() {
        Configuration cfg = new Configuration();

        cfg.addFile(new File(getOutputDir(), getBaseForMappings() + "Fee.hbm.xml"));
        cfg.addFile(new File(getOutputDir(), getBaseForMappings() + "Glarch.hbm.xml"));

        cfg.buildMappings();
    }

	public void testCompositeElementNode() throws DocumentException {
		File outputXml = new File(getOutputDir(),  getBaseForMappings() + "Glarch.hbm.xml");
		assertFileAndExists(outputXml);

		SAXReader xmlReader =  getSAXReader();

		Document document = xmlReader.read(outputXml);

		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/list");
		Element node = (Element) xpath.selectNodes(document).get(1); //second list
		List list = node.elements("composite-element");
		assertEquals("Expected to get one composite-element element", 1, list.size());
		node = (Element) list.get(0);
		assertEquals("Expected to get two property element", 2, node.elements("property").size());

		node = node.element("many-to-one");
		assertEquals(node.attribute( "name" ).getText(),"fee");
		assertEquals(node.attribute( "cascade" ).getText(),"all");
		//TODO: assertEquals(node.attribute( "outer-join" ).getText(),"true");

		node = node.getParent();//composite-element
		node = node.element("nested-composite-element");
		assertEquals(node.attribute( "name" ).getText(),"subcomponent");
		assertEquals(node.attribute( "class" ).getText(),"org.hibernate.tool.hbm2x.hbm2hbmxml.FooComponent");
	}

	protected String getBaseForMappings() {
		return "org/hibernate/tool/hbm2x/hbm2hbmxml/";
	}

	public static Test suite() {
		return new TestSuite(CompositeElementTest.class);
	}

}
