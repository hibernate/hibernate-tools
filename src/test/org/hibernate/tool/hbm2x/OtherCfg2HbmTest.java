/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.hbm2x;

import java.io.File;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.NonReflectiveTestCase;
import org.hibernate.util.DTDEntityResolver;

/**
 * @author max
 *
 */
public class OtherCfg2HbmTest extends NonReflectiveTestCase {

	public OtherCfg2HbmTest(String name) {
		super( name, "hbm2xoutput" );
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		Exporter hbmexporter = new HibernateMappingExporter(getCfg(), getOutputDir() );
		
		hbmexporter.start();		
	}
	
	public void testFileExistence() {
		
		assertFileAndExists(new File(getOutputDir(), "org/hibernate/tool/hbm2x/Customer.hbm.xml") );
		assertFileAndExists(new File(getOutputDir(), "org/hibernate/tool/hbm2x/LineItem.hbm.xml") );
		assertFileAndExists(new File(getOutputDir(), "org/hibernate/tool/hbm2x/Order.hbm.xml") );
		assertFileAndExists(new File(getOutputDir(), "org/hibernate/tool/hbm2x/Product.hbm.xml") );
		assertFileAndExists(new File(getOutputDir(), "HelloWorld.hbm.xml") );
		assertFileAndExists(new File(getOutputDir(), "HelloUniverse.hbm.xml") );		
	}
	
    public void testReadable() {
        Configuration cfg = new Configuration();
        
        cfg.addFile(new File(getOutputDir(), "org/hibernate/tool/hbm2x/Customer.hbm.xml") );
        cfg.addFile(new File(getOutputDir(), "org/hibernate/tool/hbm2x/LineItem.hbm.xml") );
        cfg.addFile(new File(getOutputDir(), "org/hibernate/tool/hbm2x/Order.hbm.xml") );
        cfg.addFile(new File(getOutputDir(), "org/hibernate/tool/hbm2x/Product.hbm.xml") );        
        
        cfg.buildMappings();
        
    }
	
	public void testNoVelocityLeftOvers() {
		
		assertEquals(null,findFirstString("$",new File(getOutputDir(), "org/hibernate/tool/hbm2x/Customer.hbm.xml") ) );
        assertEquals(null,findFirstString("$",new File(getOutputDir(), "org/hibernate/tool/hbm2x/LineItem.hbm.xml") ) );
        assertEquals(null,findFirstString("$",new File(getOutputDir(), "org/hibernate/tool/hbm2x/Order.hbm.xml") ) );
        assertEquals(null,findFirstString("$",new File(getOutputDir(), "org/hibernate/tool/hbm2x/Product.hbm.xml") ) );
        
	}
	
	public void testVersioning() throws DocumentException {
		
		SAXReader xmlReader =  this.getSAXReader();
		
		Document document = xmlReader.read(new File(getOutputDir(), "org/hibernate/tool/hbm2x/Product.hbm.xml"));
		
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/version");
		List list = xpath.selectNodes(document);
		assertEquals("Expected to get one version element", 1, list.size());			
	}
	
	protected String getBaseForMappings() {
		return "org/hibernate/tool/hbm2x/";
	}
	
	protected String[] getMappings() {
		return new String[] { 
				"Customer.hbm.xml",
				"Order.hbm.xml",
				"LineItem.hbm.xml",
				"Product.hbm.xml",
				"HelloWorld.hbm.xml"
		};
	}
	
}
