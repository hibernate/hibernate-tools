/*******************************************************************************
  * Copyright (c) 2007-2009 Red Hat, Inc.
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
import java.util.Iterator;
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
import org.hibernate.mapping.Backref;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.NonReflectiveTestCase;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;

/**
 * @author Dmitry Geraskov
 *
 */
public class AbstractTest extends NonReflectiveTestCase {

	private String mappingFile = "Car.hbm.xml";

	private Exporter hbmexporter;
	
	/**
	 * @param name
	 */
	public AbstractTest(String name) {
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
		assertFileAndExists(new File(getOutputDir().getAbsolutePath(),  getBaseForMappings() + "Car.hbm.xml") );
		assertFileAndExists(new File(getOutputDir().getAbsolutePath(),  getBaseForMappings() + "CarPart.hbm.xml") );
	}
	
	public void testAbstractPresent() {
		File outputXml = new File(getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/CarPart.hbm.xml");
		assertFileAndExists(outputXml);

		SAXReader xmlReader =  this.getSAXReader();

		Document document;
		try {
				document = xmlReader.read(outputXml);
				XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class");
				List list = xpath.selectNodes(document);
				assertEquals("Expected to get one class element", 1, list.size());
				Element node = (Element) list.get(0);
				assertNotNull("Abstract attrinute was not exported.", node.attribute( "abstract" ));
				assertEquals(node.attribute( "abstract" ).getText(),"true");
			} catch (DocumentException e) {
				fail("Can't parse file " + outputXml.getAbsolutePath());
			}
	}
	
	public void testReadable() {
        Configuration cfg = new Configuration();

        cfg.addFile(new File(getOutputDir(), getBaseForMappings() + "Car.hbm.xml"));
        cfg.addFile(new File(getOutputDir(), getBaseForMappings() + "CarPart.hbm.xml"));
        
        cfg.buildMappings();
    }

	protected String getBaseForMappings() {
		return "org/hibernate/tool/hbm2x/hbm2hbmxml/";
	}
	
	public static Test suite() {
		return new TestSuite(AbstractTest.class);
	}

}
