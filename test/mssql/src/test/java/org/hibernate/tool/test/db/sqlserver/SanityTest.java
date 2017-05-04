package org.hibernate.tool.test.db.sqlserver;

import org.junit.Assert;
import org.junit.Test;

public class SanityTest {
	
	@Test
	public void testH2Present() {
		Assert.assertNotNull(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
	}
	

}
