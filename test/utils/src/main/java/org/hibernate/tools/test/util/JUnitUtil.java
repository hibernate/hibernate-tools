package org.hibernate.tools.test.util;

import java.io.File;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.ComparisonFailure;

public class JUnitUtil {
	
	public static void assertIteratorContainsExactly(
			String reason, 
			Iterator<?> iterator, 
			int expectedAmountOfElements) {
		int actualAmountOfElements = 0;
		while (iterator.hasNext() && 
				actualAmountOfElements <= expectedAmountOfElements) {
			actualAmountOfElements++;
			iterator.next();
		}
		if (actualAmountOfElements != expectedAmountOfElements) {
			throw new ComparisonFailure(
					reason, 
					Integer.toString(expectedAmountOfElements),
					Integer.toString(actualAmountOfElements));
		}
	}
	
	public static void assertIsNonEmptyFile(File file) {
		Assert.assertTrue(file + " does not exist", file.exists() );
		Assert.assertTrue(file + " not a file", file.isFile() );		
		Assert.assertTrue(file + " does not have any contents", file.length()>0);
	}

}
