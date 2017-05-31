package org.hibernate.tools.test.util;

import java.io.File;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.ComparisonFailure;
import org.junit.Test;

public class JUnitUtilTest {
	
	@Test
	public void testAssertIteratorContainsExactly() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("foo");
		list.add("bar");
		try {
			JUnitUtil.assertIteratorContainsExactly("less", list.iterator(), 1);
			Assert.fail();
		} catch (ComparisonFailure e) {
			Assert.assertTrue(e.getMessage().contains("less"));
		}
		try {
			JUnitUtil.assertIteratorContainsExactly("more", list.iterator(), 3);
			Assert.fail();		
		} catch (ComparisonFailure e) {
			Assert.assertTrue(e.getMessage().contains("more"));
		}
		try {
			JUnitUtil.assertIteratorContainsExactly("exact", list.iterator(), 2);
			Assert.assertTrue(true);		
		} catch (ComparisonFailure e) {
			Assert.fail();
		}
	}
	
	@Test
	public void testAssertIsNonEmptyFile() throws Exception {
		String classResourceName = "/org/hibernate/tools/test/util/JUnitUtilTest.class";
		File exists = new File(getClass().getResource(classResourceName).toURI());
		try {
			JUnitUtil.assertIsNonEmptyFile(exists);
		} catch (Exception e) {
			Assert.fail();
		}
		File doesNotExist = new File(exists.getAbsolutePath() + '_');
		try {
			JUnitUtil.assertIsNonEmptyFile(doesNotExist);
			Assert.fail();
		} catch (AssertionError e) {
			Assert.assertTrue(e.getMessage().contains("does not exist"));
		}		
	}

}
