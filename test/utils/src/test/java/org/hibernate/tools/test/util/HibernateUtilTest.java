package org.hibernate.tools.test.util;

import java.util.Collections;

import org.hibernate.mapping.Table;
import org.junit.Assert;
import org.junit.Test;

public class HibernateUtilTest {
	
	@Test
	public void testGetForeignKey() {
		Table table = new Table();
		Assert.assertNull(HibernateUtil.getForeignKey(table, "foo"));
		Assert.assertNull(HibernateUtil.getForeignKey(table, "bar"));
		table.createForeignKey("foo", Collections.emptyList(), null, null);
		Assert.assertNotNull(HibernateUtil.getForeignKey(table, "foo"));
		Assert.assertNull(HibernateUtil.getForeignKey(table, "bar"));
	}

}
