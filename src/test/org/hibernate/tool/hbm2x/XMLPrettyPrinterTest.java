/*
 * Created on 17-Dec-2004
 *
 */
package org.hibernate.tool.hbm2x;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

/**
 * @author max
 *
 */
public class XMLPrettyPrinterTest extends TestCase {

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();        
    }
    
    public void testBasics() throws IOException, DocumentException, SAXException {
        
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        XMLPrettyPrinter.prettyPrint(new ByteArrayInputStream("<basic attrib='1'></basic>".getBytes() ), byteArrayOutputStream);
        
        String string = byteArrayOutputStream.toString();
        
        assertEquals("<basic attrib='1'></basic>" + lineSeparator(),string);
    }

	private String lineSeparator() {
		return System.getProperty("line.separator");
	}
   
  /*  public void testCloseTag() throws IOException, DocumentException, SAXException {
        
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        XMLPrettyPrinter.prettyPrint(new ByteArrayInputStream("<basic></basic>".getBytes() ), byteArrayOutputStream);
        
        String string = byteArrayOutputStream.toString();
        
        assertEquals("<basic/>\r\n",string);
    }*/
 
    public void testDeclarationWithoutValidation() throws IOException, DocumentException, SAXException {
        
        String input = "<hibernate-mapping defaultx-lazy=\"false\"/>";
        
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        XMLPrettyPrinter.prettyPrint(new ByteArrayInputStream(input.getBytes() ), byteArrayOutputStream);
        
        String string = byteArrayOutputStream.toString();
        
        assertEquals( 
                "<hibernate-mapping defaultx-lazy=\"false\" />" + lineSeparator() + lineSeparator()   
                ,string);
    }
}
