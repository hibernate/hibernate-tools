/*
 * Created on 13-Feb-2005
 *
 */
package org.hibernate.tool.ant;

import java.io.File;

import org.hibernate.tool.ide.formatting.JavaFormatter;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author max
 *
 */
public class JavaFormatterTest extends BuildFileTestCase {


	
	public JavaFormatterTest(String name) {
		super(name);
	}

	
	protected void tearDown() throws Exception {
		executeTarget( "cleanup" );
		super.tearDown();
	}
	protected void setUp() throws Exception {
		super.setUp();
		configureProject("src/testsupport/javaformattest-build.xml");
	}
	
	public void testJava() {
		
		executeTarget("prepare");
		assertTrue(getLog(), checkLogWithoutExceptions());
		
		File file = new File(project.getProperty( "build.dir" ), "formatting/SimpleOne.jav");
		assertFileAndExists( file );
		long before = file.lastModified();	
		
		waitASec();
		
		JavaFormatter formatter = new JavaFormatter(null);
		formatter.formatFile( file );
		
		assertTrue( before!=file.lastModified() );
				
	}
	
	public void testJavaJdk5() {
		
		executeTarget("prepare");
		assertTrue(getLog(), checkLogWithoutExceptions());
		
		File file = new File(project.getProperty( "build.dir" ), "formatting/Simple5One.java5");
		assertFileAndExists( file );
		long before = file.lastModified();	
						
		executeTarget("prepare");
		assertTrue(getLog(), checkLogWithoutExceptions());
		
		waitASec();
		
		JavaFormatter formatter = new JavaFormatter(null);
		assertTrue("formatting should pass when using default settings", formatter.formatFile( file ));
		
		
		assertTrue( before<file.lastModified() );
	}

	private void waitASec() {
		try {
			Thread.sleep(1000);
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void testAntxDestDir() {
		
		executeTarget("prepare");
		assertTrue(getLog(), checkLogWithoutExceptions());
		
		File file = new File(project.getProperty( "build.dir" ), "formatting/SimpleOne.jav");
		assertFileAndExists( file );
		long before = file.lastModified();
		waitASec();
		executeTarget("fileset");
		assertTrue(getLog(), checkLogWithoutExceptions());
		assertTrue( before!=file.lastModified() );
	}
	
	public void testConfig() {
		
		executeTarget("prepare");
		assertTrue(getLog(), checkLogWithoutExceptions());
		
		File jdk5file = new File(project.getProperty( "build.dir" ), "formatting/Simple5One.java5");
		File jdkfile = new File(project.getProperty( "build.dir" ), "formatting/SimpleOne.jav");
		assertFileAndExists( jdkfile );
		long jdk5before = jdk5file.lastModified();
		long before = jdkfile.lastModified();	
		waitASec();
		executeTarget("configtest");
		assertTrue(getLog(), checkLogWithoutExceptions());

		assertTrue(jdk5before<jdk5file.lastModified() );
		assertTrue(before<jdkfile.lastModified());
		
		executeTarget("noconfigtest");
		assertTrue(getLog(), checkLogWithoutExceptions());
		assertTrue(jdk5before<jdk5file.lastModified() );
		assertTrue(before<jdk5file.lastModified());
		
	}
	
	public static Test suite() {
		return new TestSuite(JavaFormatterTest.class);
	}

	
}
