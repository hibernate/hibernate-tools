/*
 * Created on 13-Feb-2005
 *
 */
package org.hibernate.tool.ant;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author max
 *
 */
public class CoreTaskTest extends BuildFileTestCase {
	
	public CoreTaskTest(String name) {
		super(name);
	}
	
	
	protected void tearDown() throws Exception {
		System.out.println(getLog() );
//		super.tearDown();
	}
	

	
	protected void setUp() throws Exception {
		configureProject("src/testsupport/coretest-build.xml");
	}
	
	public void testSchemaUpdateWarning() {
		executeTarget("test-schemaupdatewarning");
		assertLogContaining( "Hibernate Core SchemaUpdate" );
		assertLogContaining( "tools.hibernate.org" );
	}

	public void testSchemaExportWarning() {
		executeTarget("test-schemaexportwarning");
		assertLogContaining( "Hibernate Core SchemaUpdate" );
		assertLogContaining( "tools.hibernate.org" );
	}
	
	public static Test suite() {
		return new TestSuite(CoreTaskTest.class);
	}

		
}
