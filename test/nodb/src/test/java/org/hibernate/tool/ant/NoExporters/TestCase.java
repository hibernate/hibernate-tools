package org.hibernate.tool.ant.NoExporters;

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
	
	private File resourcesDir = null;
	
	@Before
	public void setUp() {
		resourcesDir = new File(temporaryFolder.getRoot(), "resources");
		resourcesDir.mkdir();
	}
	
	@Test
	public void testNoConnInfoExport() {

		String[] resources = new String[] {"build.xml", "hibernate.properties"};
		ResourceUtil.createResources(this, resources, resourcesDir);
		File buildFile = new File(resourcesDir, "build.xml");	
		
		AntUtil.Project project = AntUtil.createProject(buildFile);
		project.setProperty("resourcesDir", resourcesDir.getAbsolutePath());
		
		try {
		
			project.executeTarget("testNoExporters");
			Assert.fail("should have failed with no exporters!");
		
		} catch (BuildException e) {
			
			// should happen!
			Assert.assertTrue(e.getMessage().indexOf("No exporters specified")>=0);
			
		}
				
	}
	
}
