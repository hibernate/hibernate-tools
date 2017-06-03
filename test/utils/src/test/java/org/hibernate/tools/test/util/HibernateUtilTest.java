package org.hibernate.tools.test.util;

import java.util.Collections;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Table;
import org.hibernate.tool.util.MetadataHelper;
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
	
	@Test
	public void testDialectInstantiation() {
		Assert.assertNotNull(new HibernateUtil.Dialect());
	}
	
	@Test
	public void testInitializeConfiguration() {
		Configuration configuration = 
				HibernateUtil.initializeConfiguration(
						this, 
						new String[] { "HelloWorld.hbm.xml" });
		Metadata metadata = MetadataHelper.getMetadata(configuration);
		Assert.assertSame(
				HibernateUtil.Dialect.class, 
				metadata.getDatabase().getDialect().getClass());
		Assert.assertNotNull(metadata.getEntityBinding("HelloWorld"));
		Assert.assertEquals(
				"org.hibernate.tools.test.util.HibernateUtil$Dialect",
				configuration.getProperty("hibernate.dialect"));
	}

}
