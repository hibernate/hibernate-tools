package org.hibernate.tools.test.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ResourceUtilTest {
	
	@TempDir
	public File outputFolder = new File("output");
	
	@Test
	public void testGetResourcesLocation() {
		assertEquals(
				"/org/hibernate/tools/test/util/", 
				ResourceUtil.getResourcesLocation(this));
	}
	
	@Test
	public void testCreateResources() {
		String[] resources = new String[] { "HelloWorld.hbm.xml" };
		File helloWorldFile = new File(outputFolder, "HelloWorld.hbm.xml");
		assertFalse(helloWorldFile.exists());
		ResourceUtil.createResources(this, resources, outputFolder);
		assertTrue(helloWorldFile.exists());
		assertTrue(FileUtil
				.findFirstString("class", helloWorldFile)
				.contains("HelloWorld"));
	}

}
