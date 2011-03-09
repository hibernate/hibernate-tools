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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

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
public class TypeParamsTest extends NonReflectiveTestCase {
	
	private String mappingFile = "Order.hbm.xml";

	private Exporter hbmexporter;

	/**
	 * @param name
	 */
	public TypeParamsTest(String name) {
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
        cfg.addFile(new File(getOutputDir(), getBaseForMappings() + "Order.hbm.xml"));
        cfg.buildMappings();
    }

	public void testTypeParamsElements() throws DocumentException {
		File outputXml = new File(getOutputDir(),  getBaseForMappings() + mappingFile);
		assertFileAndExists(outputXml);

		SAXReader xmlReader =  getSAXReader();

		Document document = xmlReader.read(outputXml);

		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/property");
		List list = xpath.selectNodes(document);
		assertEquals("Expected to get one property element", 2, list.size());
		Element statusElement = (Element) list.get(0);
		Element nameElement = (Element) list.get(1);
		if(!statusElement.attribute( "name" ).getText().equals("status")) {
			Element temp = nameElement;
			nameElement = statusElement;
			statusElement = temp;
		}
		assertEquals(statusElement.attribute( "name" ).getText(),"status");
		
		list = statusElement.elements("type");
		assertEquals("Expected to get one type element", 1, list.size());
		
		list =  ((Element) list.get(0)).elements("param");		
		assertEquals("Expected to get 5 params elements", list.size(), 5);
		
		Map params = new HashMap();
		for (int i = 0; i < list.size(); i++) {
			Element param = (Element) list.get(i);
			params.put(param.attribute( "name" ).getText(), param.getText());
		}
		
		Set set = params.entrySet();
		assertEquals("Expected to get 5 different params elements", set.size(), 5);
		
		assertTrue("Can't find 'catalog' param", 
				set.contains(new TestEntry("catalog", "")));
		
		assertTrue("Can't find 'column' param", 
				set.contains(new TestEntry("column", "STATUS")));
		
		assertTrue("Can't find 'table' param", 
				set.contains(new TestEntry("table", "ORDERS")));
		
		assertTrue("Can't find 'schema' param", 
				set.contains(new TestEntry("schema", "")));
		
		assertTrue("Can't find 'enumClass' param", 
				set.contains(new TestEntry("enumClass", "org.hibernate.tool.hbm2x.hbm2hbmxml.Order$Status")));

		assertTrue("property name should not have any type element",nameElement.elements("type").isEmpty());
		assertEquals(nameElement.attribute("type").getText(), "string");
	}

	protected String getBaseForMappings() {
		return "org/hibernate/tool/hbm2x/hbm2hbmxml/";
	}

	public static Test suite() {
		return new TestSuite(TypeParamsTest.class);
	}

}

class TestEntry implements Entry{
	
	private Object key;
	
	private Object value;
	
	public TestEntry(Object key, Object value){
		this.key = key;
		this.value = value;
	}

	public Object getKey() {
		return key;
	}

	public Object getValue() {
		return value;
	}

	public Object setValue(Object value) {
		return this.value = value;
	}

	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Entry)){
			return false;
		}
		
		Entry other = (Entry) obj;		
		if (key == null) {
			if (other.getKey() != null)
				return false;
		} else if (!key.equals(other.getKey()))
			return false;
		if (value == null) {
			if (other.getValue() != null)
				return false;
		} else if (!value.equals(other.getValue()))
			return false;
		return true;
	}
	
	
	
}
