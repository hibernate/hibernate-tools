package org.hibernate.tool.test;

import org.hsqldb.jdbc.JDBCDriver;
import org.junit.Assert;
import org.junit.Test;

public class HsqlTest {
	
	@Test
	public void testHsqlPresent() {
		Assert.assertNotNull(org.hsqldb.jdbcDriver.driverInstance);
	}
	

}
