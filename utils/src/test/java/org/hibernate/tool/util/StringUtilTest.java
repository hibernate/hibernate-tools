package org.hibernate.tool.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class StringUtilTest {
	
	@Test
	public void testIsEmptyOrNull() {
		assertTrue(StringUtil.isEmptyOrNull(null));
		assertTrue(StringUtil.isEmptyOrNull(""));
		assertFalse(StringUtil.isEmptyOrNull("foo"));
	}

}
