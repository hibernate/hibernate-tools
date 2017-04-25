package org.hibernate.tools.test.util;

import java.io.File;
import java.io.FileWriter;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class JavaUtilTest {
	
	private static final String FOO_STRING = 
			"package org.hibernate.tool.test;"+
			"public class Foo {              "+
			"  public Bar bar;               "+
			"}                               ";
	
	private static final String BAR_STRING = 
			"package org.hibernate.tool.test;"+
			"public class Bar {              "+
			"  public Foo foo;               "+
			"}                               ";
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	@Test
	public void testCompile() throws Exception {
		File testFolder = temporaryFolder.getRoot();
		File packageFolder = new File(testFolder, "org/hibernate/tool/test");
		packageFolder.mkdirs();
		File fooFile = new File(packageFolder, "Foo.java");
		FileWriter fileWriter = new FileWriter(fooFile);
		fileWriter.write(FOO_STRING);
		fileWriter.close();
		File barFile = new File(packageFolder, "Bar.java");
		fileWriter = new FileWriter(barFile);
		fileWriter.write(BAR_STRING);
		fileWriter.close();
		Assert.assertFalse(new File(packageFolder, "Foo.class").exists());
		Assert.assertFalse(new File(packageFolder, "Bar.class").exists());
		JavaUtil.compile(testFolder);
		Assert.assertTrue(new File(packageFolder, "Foo.class").exists());
		Assert.assertTrue(new File(packageFolder, "Bar.class").exists());
	}

}
