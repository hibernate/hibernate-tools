package org.hibernate.tools.test.util;

import java.util.Collections;

import org.hibernate.boot.Metadata;
import org.hibernate.mapping.Table;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class HibernateUtilTest {
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

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
		Metadata metadata = HibernateUtil
				.initializeMetadataSources(
						this, 
						new String[] { "HelloWorld.hbm.xml" },
						temporaryFolder.getRoot())
				.buildMetadata();
		Assert.assertSame(
				HibernateUtil.Dialect.class, 
				metadata.getDatabase().getDialect().getClass());
		Assert.assertNotNull(metadata.getEntityBinding("HelloWorld"));
	}

}
