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
public class SetElementTest extends NonReflectiveTestCase {

	private String mappingFile = "Search.hbm.xml";

	private Exporter hbmexporter;

	/**
	 * @param name
	 */
	public SetElementTest(String name) {
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
		assertFileAndExists(new File(getOutputDir().getAbsolutePath(),  getBaseForMappings() + mappingFile) );
	}

	public void testReadable() {
        Configuration cfg = new Configuration();

        cfg.addFile(new File(getOutputDir(), getBaseForMappings() + mappingFile));

        cfg.buildMappings();
    }

	public void testKey() throws DocumentException {
		File outputXml = new File(getOutputDir(),  getBaseForMappings() + mappingFile);
		assertFileAndExists(outputXml);

		SAXReader xmlReader =  getSAXReader();

		Document document = xmlReader.read(outputXml);

		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/set/key");
		List list = xpath.selectNodes(document);
		assertEquals("Expected to get one key element", 1, list.size());
		Element node = (Element) list.get(0);
		if (node.attribute( "column" ) != null){//implied attribute
			assertEquals(node.attribute( "column" ).getText(),"searchString");
		} else {
			node = node.element("column");
			assertEquals(node.attribute( "name" ).getText(),"searchString");
		}
	}

	public void testSetElement() throws DocumentException {
		File outputXml = new File(getOutputDir(),  getBaseForMappings() + mappingFile);
		assertFileAndExists(outputXml);

		SAXReader xmlReader =  getSAXReader();

		Document document = xmlReader.read(outputXml);

		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/set");
		List list = xpath.selectNodes(document);
		assertEquals("Expected to get one set element", 1, list.size());
		Element node = (Element) list.get(0);
		assertEquals(node.attribute( "name" ).getText(),"searchResults");
		assertEquals(node.attribute( "access" ).getText(),"field");

		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/set/element");
		list = xpath.selectNodes(document);
		assertEquals("Expected to get one element 'element'", 1, list.size());
		node = (Element) list.get(0);
		assertEquals(node.attribute( "type" ).getText(), "string");
		list = node.selectNodes("column");
		assertEquals("Expected to get one element 'column'", 1, list.size());
		node = (Element) list.get(0);
		assertEquals(node.attribute( "name" ).getText(), "text");
	}

	protected String getBaseForMappings() {
		return "org/hibernate/tool/hbm2x/hbm2hbmxml/";
	}

	public static Test suite() {
		return new TestSuite(SetElementTest.class);
	}

}
