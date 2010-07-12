//$Id$

/* 
 * Tests for generating the HBM documents from the Configuration data structure.
 * The generated XML document will be validated and queried to make sure the 
 * basic structure is correct in each test.
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



public class OneToOneTest extends NonReflectiveTestCase {

	private Exporter hbmexporter;

	public OneToOneTest(String name) {
		super( name );
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		hbmexporter = new HibernateMappingExporter(getCfg(), getOutputDir() );
		hbmexporter.start();		
	}
	
	public void testAllFilesExistence() {

		assertFalse(new File(getOutputDir().getAbsolutePath() + "/GeneralHbmSettings.hbm.xml").exists() );
		assertFileAndExists(new File(getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/Person.hbm.xml") );
		assertFileAndExists(new File(getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/Address.hbm.xml") );		
	}
	
	public void testArtifactCollection() {
		
		assertEquals(2,hbmexporter.getArtifactCollector().getFileCount("hbm.xml"));
		
	}
	
	public void testReadable() {
        Configuration cfg = new Configuration();
        
        cfg.addFile(new File(getOutputDir(), getBaseForMappings() + "Person.hbm.xml"));
        cfg.addFile(new File(getOutputDir(), getBaseForMappings() + "Address.hbm.xml"));
        
        cfg.buildMappings();
                
        
    }
	
	public void testOneToOne() throws DocumentException {
		Document document = getXMLDocument(getBaseForMappings() + "Person.hbm.xml");		
	
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/one-to-one");
		List list = xpath.selectNodes(document);
		assertEquals("Expected to get one-to-one element", 1, list.size());
		Element node = (Element) list.get(0);
		assertEquals(node.attribute( "name" ).getText(),"address");
		assertEquals(node.attribute( "constrained" ).getText(),"false");
		
		document = getXMLDocument(getBaseForMappings() + "Address.hbm.xml");
		
		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/one-to-one");
		list = xpath.selectNodes(document);
		assertEquals("Expected to get one set element", 1, list.size());
		node = (Element) list.get(0);
		assertEquals(node.attribute( "name" ).getText(),"person");
		assertEquals(node.attribute( "constrained" ).getText(),"true");
		assertEquals(node.attribute( "access" ).getText(), "field");
		
	}

	private Document getXMLDocument(String location) throws DocumentException {
		File outputXml = new File(getOutputDir(),  location);
		assertFileAndExists(outputXml);

		SAXReader xmlReader =  this.getSAXReader();
		
		Document document = xmlReader.read(outputXml);
		return document;
	}
	
	protected String getBaseForMappings() {
		return "org/hibernate/tool/hbm2x/hbm2hbmxml/";
	}
	
	protected String[] getMappings() {
		return new String[] { 
				"PersonAddressOneToOnePrimaryKey.hbm.xml"				
		};
	}
	    
	public static Test suite() {
		return new TestSuite(OneToOneTest.class);
	}

}
