/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.hbm2x;

import org.hibernate.tool.NonReflectiveTestCase;

/**
 * @author max
 *
 */
public class Hbm2JavaBidirectionalIndexedCollectionMappingTest extends NonReflectiveTestCase {

	public Hbm2JavaBidirectionalIndexedCollectionMappingTest(String name) {
		super( name, "hbm2javaoutput" );
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		Exporter exporter = new POJOExporter(getCfg(), getOutputDir() );
		exporter.start();
	}
	
	
	public void testReflection() throws Exception {
		
	}
	
	protected String getBaseForMappings() {
		return "org/hibernate/tool/hbm2x/";
	}
	
	protected String[] getMappings() {
		return new String[] { 
				"GenericModel.hbm.xml",				
		};
	}
	
        
}
