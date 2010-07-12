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
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

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
public class BackrefTest extends NonReflectiveTestCase {

	private String mappingFile = "Car.hbm.xml";

	private Exporter hbmexporter;

	/**
	 * @param name
	 */
	public BackrefTest(String name) {
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
	
	public void testBackrefPresent() {
		Configuration config = getCfg();
		PersistentClass pc = config.getClassMapping("org.hibernate.tool.hbm2x.hbm2hbmxml.CarPart");
		Iterator iterator = pc.getPropertyIterator();
		boolean hasBackrefs = false;
		while (iterator.hasNext() && !hasBackrefs) {
			hasBackrefs = (iterator.next() instanceof Backref);			
		}
		assertTrue("Class mapping should create Backref for this testcase", hasBackrefs);
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
		return new TestSuite(BackrefTest.class);
	}

}
