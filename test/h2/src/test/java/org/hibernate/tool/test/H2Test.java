package org.hibernate.tool.test;

import org.junit.Assert;
import org.junit.Test;

public class H2Test {
	
	@Test
	public void testH2Present() {
		Assert.assertNotNull(org.h2.Driver.load());
	}
	

}
