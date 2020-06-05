/*
 * Created on 2004-11-24
 *
 */
package org.hibernate.tool.jdbc2cfg.AutoQuote;

import org.hibernate.boot.Metadata;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author max
 * @author koen
 */
public class TestCase {

	private Metadata metadata = null;

	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);
		metadata = MetadataDescriptorFactory
				.createReverseEngineeringDescriptor(null, null)
				.createMetadata();
	}

	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	@Test
	public void testForQuotes() {
		Table table = HibernateUtil.getTable(metadata, "us-ers");
		Assert.assertNotNull(table);
		Assert.assertTrue(table.isQuoted());		
		Assert.assertEquals(2, table.getColumnSpan());		
		PersistentClass classMapping = metadata.getEntityBinding("Worklogs");
		Assert.assertNotNull(classMapping);
		Property property = classMapping.getProperty("usErs");
		Assert.assertNotNull(property);	
	}
	
}
