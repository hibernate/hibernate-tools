/*
 * Created on 2004-11-24
 *
 */
package org.hibernate.tool.jdbc2cfg.AutoQuote;

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
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

	private JDBCMetaDataConfiguration jmdcfg = null;

	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);
		jmdcfg = new JDBCMetaDataConfiguration();
		jmdcfg.readFromJDBC();
	}

	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	@Test
	public void testForQuotes() {
		Table table = jmdcfg.getTable("us-ers");
		Assert.assertNotNull(table);
		Assert.assertTrue(table.isQuoted());		
		Assert.assertEquals(2, table.getColumnSpan());		
		PersistentClass classMapping = jmdcfg.getMetadata().getEntityBinding("Worklogs");
		Assert.assertNotNull(classMapping);
		Property property = classMapping.getProperty("usErs");
		Assert.assertNotNull(property);	
	}
	
}
