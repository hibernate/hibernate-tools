/*
 * Created on 13-Feb-2005
 *
 */
package org.hibernate.tool.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author max
 *
 */
public class AntHibernateToolTest extends BuildFileTestCase {


	private static final Logger log = LoggerFactory.getLogger(AntHibernateToolTest.class);

	private String property;
	
	public AntHibernateToolTest(String name) {
		super(name);
	}
	
	protected void tearDown() throws Exception {
		cleanup();
		super.tearDown();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		configureProject("src/testsupport/anttest-build.xml");
	}
	
	/**
	 * Maximum number of attempts to make a clean.
	 */
	private final short ATTEMPT_COUNT = 20;
	
	private void cleanup(){
		executeTarget("afterCfg2hbm");
		boolean removed = false;
		
		short attempt = 1;
		do {
			try {
				log.debug("Attempt " + attempt + " for calling 'remmoveDirs'");
				executeTarget("removeDirs");
				removed = true;
			} catch (BuildException be){
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
				//Unable to delete file ...\testdb\hsqldb.log
				//if isn't removed calls
				//User SA not found for all the next tests.
			}
			attempt++;
			
			if (attempt > ATTEMPT_COUNT && !removed){
				fail("Could not complete cleanup. Next tests behaviour is unpredictable.");
			}
		} while (!removed);
	}

	public void testHbm2JavaConfiguration() {
		executeTarget("testanthbm2java");
		assertTrue(getLog(), checkLogWithoutExceptions());
	}
	
	public void testHbm2JavaEJB3Configuration() {
		executeTarget("testantejb3hbm2java");
		assertTrue(getLog(), checkLogWithoutExceptions());
	}
	
    public void testCfg2HbmNoError() {
        executeTarget("testantcfg2hbm1");
        assertTrue(getLog(), checkLogWithoutExceptions());
    }
    
    public void testCfg2HbmWithCustomReverseNamingStrategy() {
        executeTarget("testantcfg2hbm2");
        assertTrue(getLog(), checkLogWithoutExceptions());
    }
    
    public void testCfg2HbmWithInvalidReverseNamingStrategy() {
        expectSpecificBuildException("testantcfg2hbm3", 
                "namingStrategy attribute should not be loaded", 
                "Could not create or find invalid.classname with one argument delegate constructor");
    }
    
    public void testCfg2HbmWithPackageName() {
        executeTarget("testantcfg2hbm4");
        assertTrue(getLog(), checkLogWithoutExceptions());
    }
    
    public void testCfg2HbmWithPackageNameAndReverseNamingStrategy() {
        executeTarget("testantcfg2hbm5");
        assertTrue(getLog(), checkLogWithoutExceptions());
    }
    
  
	public void testJDBCConfigWithRevEngXml() {
		executeTarget("testantjdbccfgoverrides");
		assertTrue(getLog(), checkLogWithoutExceptions());
	}
	
	public void testProperties() {
		executeTarget("testproperties");
		assertTrue(getLog(), checkLogWithoutExceptions());
	}
	
	public void testGenericExport() {
		executeTarget("testgeneric");
		assertTrue(getLog(), checkLogWithoutExceptions());
		
		property = project.getProperty("build.dir");
		assertTrue(new File(property, "generic").exists());
		assertTrue(new File(property, "generic/org/hibernate/tool/hbm2x/ant/TopDown.quote").exists());
	}
	
	// TODO try to get this sorted out in Hibernate Core
/*	public void testNoConnInfoExport() {
		executeTarget("noconinfoexport");
		File baseDir = new File(project.getProperty("build.dir"), "noconinfo");
		File onlyCreate = new File(baseDir, "noconinfo.sql");
		
		assertTrue(onlyCreate.toString() + " should exist", onlyCreate.exists());
		
		assertTrue(onlyCreate.length()>0);
		
		assertNotNull(TestHelper.findFirstString("create", onlyCreate));
		
		
	}
*/
	public void testNoExporters() {
		try {
		executeTarget("testnoexporters");
		fail("should have failed with no exporters!");
		} catch(BuildException be) {
			// should happen!
			assertTrue(be.getMessage().indexOf("No exporters specified")>=0);
		}
		
	}
	
	public void testException() {
		try {
		executeTarget("testexceptions");
		fail("should have failed with an exception!");
		} catch(BuildException be) {
			// should happen!			
		}
	}
	

//  TODO Investigate why this method fails on HSQLDB	
//	public void testQuery() {
//		executeTarget("testquery");
//		assertTrue(getLog(), checkLogWithoutExceptions());
//		
//		File baseDir = new File(project.getProperty("build.dir"), "querytest");
//		
//		assertTrue(new File(baseDir, "queryresult.txt").exists());
//		
//	}
	
	public void testHbmLint() {
		executeTarget("testhbmlint");
		assertTrue(getLog(), checkLogWithoutExceptions());
		
		File baseDir = new File(project.getProperty("build.dir"), "linttest");
		
		assertTrue(new File(baseDir, "hbmlint-result.txt").exists());
		
	}
	
	public void testNoConfig() {
		try {
			executeTarget("noconfig-shoulderror");
		} catch(BuildException e) {
			assertTrue(e.getMessage().indexOf("No configuration specified")>=0);
		}
		
	}
		
	public static Test suite() {
		return new TestSuite(AntHibernateToolTest.class);
	}

	
}
