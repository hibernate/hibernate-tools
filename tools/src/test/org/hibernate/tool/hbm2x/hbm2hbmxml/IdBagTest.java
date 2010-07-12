/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
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
public class IdBagTest extends NonReflectiveTestCase {

	private Exporter hbmexporter;

	public IdBagTest(String name) {
		super( name, "cfg2hbmoutput" );
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		hbmexporter = new HibernateMappingExporter(getCfg(), getOutputDir() );
		hbmexporter.start();		
	}
	
	public void testAllFilesExistence() {

		assertFalse(new File(getOutputDir().getAbsolutePath() + "/GeneralHbmSettings.hbm.xml").exists() );
		assertFileAndExists(new File(getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/User2.hbm.xml") );
		assertFileAndExists(new File(getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/Group2.hbm.xml") );		
	}
	
	public void testArtifactCollection() {
		
		assertEquals(2,hbmexporter.getArtifactCollector().getFileCount("hbm.xml"));
		
	}
	
	public void testReadable() {
        Configuration cfg = new Configuration();
        
        cfg.addFile(new File(getOutputDir(), getBaseForMappings() + "User2.hbm.xml"));
        cfg.addFile(new File(getOutputDir(), getBaseForMappings() + "Group2.hbm.xml"));
        
        cfg.buildMappings();
        
    }
	
	public void testIdBagAttributes() {
		File outputXml = new File(getOutputDir(),  getBaseForMappings() + "User2.hbm.xml");
		assertFileAndExists(outputXml);

		SAXReader xmlReader =  this.getSAXReader();
		
		Document document;
		try {
			document = xmlReader.read(outputXml);
			XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/idbag");
			List list = xpath.selectNodes(document);
			assertEquals("Expected to get one idbag element", 1, list.size());
			Element node = (Element) list.get(0);
			assertEquals(node.attribute( "table" ).getText(),"`UserGroups`");
			assertEquals(node.attribute( "name" ).getText(),"groups");
			assertEquals(node.attribute( "lazy" ).getText(),"false");
			assertEquals(node.attribute( "access" ).getText(),"field");
		} catch (DocumentException e) {
			fail("Can't parse file " + outputXml.getAbsolutePath());
		}		
	}
	
	public void testCollectionId() throws DocumentException {
		File outputXml = new File(getOutputDir(),  getBaseForMappings() + "User2.hbm.xml");
		SAXReader xmlReader =  this.getSAXReader();
		
		Document document = xmlReader.read(outputXml);	
		
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/idbag/collection-id");
		List list = xpath.selectNodes(document);
		assertEquals("Expected to get one collection-id element", 1, list.size());
		Element node = (Element) list.get(0);
		assertEquals(node.attribute( "column" ).getText(),"userGroupId");
		assertEquals(node.attribute( "type" ).getText(),"long");
	
		list = node.elements("generator");
		assertEquals("Expected to get one generator element", 1, list.size());
		node = (Element) list.get(0);
		assertEquals(node.attribute( "class" ).getText(),"increment");
	}
	
	protected String getBaseForMappings() {
		return "org/hibernate/tool/hbm2x/hbm2hbmxml/";
	}
	
	protected String[] getMappings() {
		return new String[] { 
				"UserGroup2.hbm.xml"				
		};
	}
	    
	public static Test suite() {
		return new TestSuite(IdBagTest.class);
	}

}
