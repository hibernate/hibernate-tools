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
import org.hibernate.util.DTDEntityResolver;

public class ManyToManyTest extends NonReflectiveTestCase {

	private Exporter hbmexporter;

	public ManyToManyTest(String name) {
		super( name, "cfg2hbmoutput" );
	}

	protected void setUp() throws Exception {
		super.setUp();

		hbmexporter = new HibernateMappingExporter(getCfg(), getOutputDir() );
		hbmexporter.start();
	}

	public void testAllFilesExistence() {

		assertFalse(new File(getOutputDir().getAbsolutePath() + "/GeneralHbmSettings.hbm.xml").exists() );
		assertFileAndExists(new File(getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/User.hbm.xml") );
		assertFileAndExists(new File(getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/Group.hbm.xml") );
	}

	public void testArtifactCollection() {

		assertEquals(2,hbmexporter.getArtifactCollector().getFileCount("hbm.xml"));

	}

	public void testReadable() {
        Configuration cfg = new Configuration();

        cfg.addFile(new File(getOutputDir(), getBaseForMappings() + "User.hbm.xml"));
        cfg.addFile(new File(getOutputDir(), getBaseForMappings() + "Group.hbm.xml"));

        cfg.buildMappings();

    }

	public void testManyToMany() throws DocumentException {
		File outputXml = new File(getOutputDir(),  getBaseForMappings() + "User.hbm.xml");
		assertFileAndExists(outputXml);

		SAXReader xmlReader =  this.getSAXReader();

		Document document = xmlReader.read(outputXml);

		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/set/many-to-many");
		List list = xpath.selectNodes(document);
		assertEquals("Expected to get one many-to-many element", 1, list.size());
		Element node = (Element) list.get(0);
		assertEquals(node.attribute( "entity-name" ).getText(),"org.hibernate.tool.hbm2x.hbm2hbmxml.Group");


		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/set");
		list = xpath.selectNodes(document);
		assertEquals("Expected to get one set element", 1, list.size());
		node = (Element) list.get(0);
		assertEquals(node.attribute( "table" ).getText(),"UserGroup");


	}

		public void testCompositeId() throws DocumentException {
				File outputXml = new File(getOutputDir(),  getBaseForMappings() + "Group.hbm.xml");
		 		SAXReader xmlReader =  this.getSAXReader();

				Document document = xmlReader.read(outputXml);

				XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class");
		 		List list = xpath.selectNodes(document);
				assertEquals("Expected to get one class element", 1, list.size());
		 		Element node = (Element) list.get(0);

				assertEquals(node.attribute( "table" ).getText(),"`Group`");

				xpath = DocumentHelper.createXPath("//hibernate-mapping/class/composite-id");
				list = xpath.selectNodes(document);
				assertEquals("Expected to get one composite-id element", 1, list.size());


				xpath = DocumentHelper.createXPath("//hibernate-mapping/class/composite-id/key-property");
		 		list = xpath.selectNodes(document);
				assertEquals("Expected to get two key-property elements", 2, list.size());
		 		node = (Element) list.get(0);
				assertEquals(node.attribute( "name" ).getText(),"name");
				node = (Element) list.get(1);
				assertEquals(node.attribute( "name" ).getText(),"org");
			}

			public void testSetAttributes() {
					File outputXml = new File(getOutputDir(),  getBaseForMappings() + "Group.hbm.xml");
					assertFileAndExists(outputXml);

					SAXReader xmlReader =  this.getSAXReader();


					Document document;
					try {
						document = xmlReader.read(outputXml);
						XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/set");
						List list = xpath.selectNodes(document);
						assertEquals("Expected to get one set element", 1, list.size());
						Element node = (Element) list.get(0);
						assertEquals(node.attribute( "table" ).getText(),"UserGroup");
						assertEquals(node.attribute( "name" ).getText(),"users");
						assertEquals(node.attribute( "inverse" ).getText(),"true");
						assertEquals(node.attribute( "lazy" ).getText(),"extra");
					} catch (DocumentException e) {
						fail("Can't parse file " + outputXml.getAbsolutePath());
					}
			 	}

	protected String getBaseForMappings() {
		return "org/hibernate/tool/hbm2x/hbm2hbmxml/";
	}

	protected String[] getMappings() {
		return new String[] {
				"UserGroup.hbm.xml"
		};
	}

	public static Test suite() {
		return new TestSuite(ManyToManyTest.class);
	}

}
