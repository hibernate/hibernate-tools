/*
 * Created on 13-Feb-2005
 *
 */
package org.hibernate.tool.ant;

import java.io.File;
import java.util.HashMap;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.tool.ide.formatting.JavaFormatter;

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
		System.out.println(getLog());
		super.tearDown();
	}
	protected void setUp() throws Exception {
		super.setUp();
		configureProject("src/testsupport/javaformattest-build.xml");
		executeTarget( "cleanup" );
	}
	
	public void testJava() {
		
		executeTarget("prepare");
		
		File file = new File(project.getProperty( "build.dir" ), "formatting/SimpleOne.java");
		assertFileAndExists( file );
		long before = file.lastModified();	
		
		waitASec();
		
		JavaFormatter formatter = new JavaFormatter(null);
		formatter.formatFile( file );
		
		assertTrue( before!=file.lastModified() );
				
	}
	
	public void testJavaJdk5() {
		
		executeTarget("prepare");
		
		File file = new File(project.getProperty( "build.dir" ), "formatting/Simple5One.java5");
		assertFileAndExists( file );
		long before = file.lastModified();	
				
		JavaFormatter formatter = new JavaFormatter(new HashMap());
		assertFalse("formatting should fail when using zero settings", formatter.formatFile( file ));
		
		assertTrue( before==file.lastModified() );
		
		waitASec();
		
		executeTarget("prepare");
		
		
		formatter = new JavaFormatter(null);
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
		
		File file = new File(project.getProperty( "build.dir" ), "formatting/SimpleOne.java");
		assertFileAndExists( file );
		long before = file.lastModified();
		waitASec();
		executeTarget("fileset");
		System.out.println(getLog());
		assertTrue( before!=file.lastModified() );
		
		
	}
	
	public void testConfig() {
		
		executeTarget("prepare");
		
		File jdk5file = new File(project.getProperty( "build.dir" ), "formatting/Simple5One.java5");
		File jdkfile = new File(project.getProperty( "build.dir" ), "formatting/SimpleOne.java");
		assertFileAndExists( jdkfile );
		long jdk5before = jdk5file.lastModified();
		long before = jdkfile.lastModified();	
		waitASec();
		executeTarget("configtest");
		System.out.println(getLog());
		assertEquals("jdk5 should fail since config is not specifying jdk5",jdk5before, jdk5file.lastModified() );
		assertTrue(before<jdkfile.lastModified());
		
		executeTarget("noconfigtest");
		System.out.println(getLog());
		assertTrue(jdk5before<jdk5file.lastModified() );
		assertTrue(before<jdk5file.lastModified());
		
		
	}
	
	
	public static Test suite() {
		return new TestSuite(JavaFormatterTest.class);
	}

	
}
