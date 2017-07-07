package org.hibernate.tool.ant.Exception;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.hibernate.tools.test.util.AntUtil;
import org.hibernate.tools.test.util.ResourceUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class TestCase {
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private File destinationDir = null;
	private File resourcesDir = null;
	
	@Before
	public void setUp() {
		destinationDir = new File(temporaryFolder.getRoot(), "destination");
		destinationDir.mkdir();
		resourcesDir = new File(temporaryFolder.getRoot(), "resources");
		resourcesDir.mkdir();
	}
	
	@Test
	public void testException() throws Exception {

		String resourcesLocation = ResourceUtil.getResourcesLocation(this);
		String[] resources = new String[] {"build.xml", "hibernate.properties", "TopDown.hbm.xml"};
		ResourceUtil.createResources(this, resourcesLocation, resources, resourcesDir);
		File buildFile = new File(resourcesDir, "build.xml");	
		
		AntUtil.Project project = AntUtil.createProject(buildFile);
		project.setProperty("destinationDir", destinationDir.getAbsolutePath());
		project.setProperty("resourcesDir", resourcesDir.getAbsolutePath());
		
		try {
		
			project.executeTarget("testException");
			Assert.fail("An exception should occur");
			
		} catch (BuildException e) {
			
			Assert.assertTrue(e.getMessage(), e.getMessage().contains("Error while processing Entity"));
			
		}
		
	}
	
}
