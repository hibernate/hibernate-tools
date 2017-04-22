package org.hibernate.tool.test.db.h2;

import org.junit.Assert;
import org.junit.Test;

public class SanityTest {
	
	@Test
	public void testH2Present() {
		Assert.assertNotNull(org.h2.Driver.load());
	}
	

}
