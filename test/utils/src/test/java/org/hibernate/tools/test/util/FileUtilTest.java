package org.hibernate.tools.test.util;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

public class FileUtilTest {
	
	@Test
	public void testFindFirstString() throws Exception {
		String resource = "/org/hibernate/tools/test/util/FileUtilTest.resource";
		File file = new File(getClass().getResource(resource).toURI());
		Assert.assertEquals("or would it be a bar test?", FileUtil.findFirstString("bar", file));
		Assert.assertNull(FileUtil.findFirstString("foobar", file));
		Assert.assertEquals("This is a foo test", FileUtil.findFirstString("test", file));
		Assert.assertEquals("And a third line...", FileUtil.findFirstString("third", file));
	}

}
