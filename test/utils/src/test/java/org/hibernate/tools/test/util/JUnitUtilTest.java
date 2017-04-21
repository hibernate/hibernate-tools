package org.hibernate.tools.test.util;

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

}
