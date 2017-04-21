package org.hibernate.tools.test.util;

import java.util.Iterator;

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

}
