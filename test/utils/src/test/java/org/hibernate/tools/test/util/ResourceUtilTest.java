package org.hibernate.tools.test.util;

import java.io.File;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ResourceUtilTest {
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	@Test
	public void testGetResourcesLocation() {
		Assert.assertEquals(
				"/org/hibernate/tools/test/util/", 
				ResourceUtil.getResourcesLocation(this));
	}
	
	@Test
	public void testCreateResources() {
		String[] resources = new String[] { "HelloWorld.hbm.xml" };
		File outputFolder = temporaryFolder.getRoot();
		File helloWorldFile = new File(outputFolder, "HelloWorld.hbm.xml");
		Assert.assertFalse(helloWorldFile.exists());
		ResourceUtil.createResources(this, resources, outputFolder);
		Assert.assertTrue(helloWorldFile.exists());
		Assert.assertTrue(FileUtil
				.findFirstString("class", helloWorldFile)
				.contains("HelloWorld"));
	}

}
