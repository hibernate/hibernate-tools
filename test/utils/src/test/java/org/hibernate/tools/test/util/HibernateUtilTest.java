package org.hibernate.tools.test.util;

import java.util.Collections;
import java.util.Properties;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
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
				.initializeMetadataDescriptor(
						this, 
						new String[] { "HelloWorld.hbm.xml" },
						temporaryFolder.getRoot())
				.createMetadata();
		Assert.assertSame(
				HibernateUtil.Dialect.class, 
				metadata.getDatabase().getDialect().getClass());
		Assert.assertNotNull(metadata.getEntityBinding("HelloWorld"));
	}
	
	@Test
	public void testAddAnnotatedClass() {
		Properties properties = new Properties();
		properties.setProperty(AvailableSettings.DIALECT, HibernateUtil.Dialect.class.getName());
		MetadataDescriptor metadataDescriptor = MetadataDescriptorFactory
				.createNativeDescriptor(null, null, properties);
		Assert.assertNull(metadataDescriptor
				.createMetadata()
				.getEntityBinding(
						"org.hibernate.tools.test.util.HibernateUtilTest$Dummy"));
		HibernateUtil.addAnnotatedClass(metadataDescriptor, Dummy.class);
		Assert.assertNotNull(metadataDescriptor
				.createMetadata()
				.getEntityBinding(
						"org.hibernate.tools.test.util.HibernateUtilTest$Dummy"));
	}
	
	@Entity
	private class Dummy {
		@Id public int id;
	}

}
