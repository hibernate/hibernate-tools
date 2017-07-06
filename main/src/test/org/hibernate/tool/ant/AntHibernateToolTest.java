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

	public void testException() {
		try {
		executeTarget("testexceptions");
		fail("should have failed with an exception!");
		} catch(BuildException be) {
			// should happen!			
		}
	}
	

	public void testHbmLint() {
		executeTarget("testhbmlint");
		assertTrue(getLog(), checkLogWithoutExceptions());
		
		File baseDir = new File(project.getProperty("build.dir"), "linttest");
		
		assertTrue(new File(baseDir, "hbmlint-result.txt").exists());
		
	}
	
	public static Test suite() {
		return new TestSuite(AntHibernateToolTest.class);
	}

	
}
