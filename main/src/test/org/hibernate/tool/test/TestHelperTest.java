package org.hibernate.tool.test;

import java.io.File;

import org.hibernate.Version;
import org.junit.Assert;
import org.junit.Test;


public class TestHelperTest {
	
	@Test
	public void testFindJarFileFor() {
		File file = TestHelper.findJarFileFor(Version.class);
		String expectedFileName = "hibernate-core-" + Version.getVersionString() + ".jar";
		Assert.assertTrue(file.getPath().endsWith(expectedFileName));
	}

}
