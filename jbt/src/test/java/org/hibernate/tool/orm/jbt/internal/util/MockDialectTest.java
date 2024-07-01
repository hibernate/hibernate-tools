package org.hibernate.tool.orm.jbt.internal.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.hibernate.dialect.DatabaseVersion;
import org.hibernate.tool.orm.jbt.internal.util.MockDialect;
import org.junit.jupiter.api.Test;

public class MockDialectTest {
	
	@Test
	public void testGetVersion() {
		DatabaseVersion version = new MockDialect().getVersion();
		assertEquals(Integer.MAX_VALUE, version.getDatabaseMajorVersion());
		assertEquals(Integer.MIN_VALUE, version.getDatabaseMinorVersion());
	}

}
