package org.hibernate.tool.test.db.hsql;

import org.junit.Assert;
import org.junit.Test;

public class SanityTest {
	
	@Test
	public void testHsqlPresent() {
		Assert.assertNotNull(org.hsqldb.jdbcDriver.driverInstance);
	}
	

}
