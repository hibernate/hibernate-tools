/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.jdbc2cfg.Views;

import java.sql.SQLException;

import org.hibernate.boot.Metadata;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.hibernate.tool.metadata.MetadataDescriptorFactory;
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
	
	private Metadata metadata;
	
	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);
		metadata = MetadataDescriptorFactory
				.createJdbcSources(null, null, true)
				.buildMetadata();
	}
	
	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}
	
	@Test
	public void testViewAndSynonyms() throws SQLException {
		
		PersistentClass classMapping = metadata.getEntityBinding("Basicview");
		Assert.assertNotNull(classMapping);
	
		classMapping = metadata.getEntityBinding("Weirdname");
		Assert.assertTrue("If this is not-null synonyms apparently work!",classMapping==null);

		// get comments
		Table table = HibernateUtil.getTable(metadata, "BASIC");
		Assert.assertEquals("a basic comment", table.getComment());
		Assert.assertEquals("a solid key", table.getPrimaryKey().getColumn(0).getComment());
		
		table = HibernateUtil.getTable(metadata, "MULTIKEYED");
		Assert.assertNull(table.getComment());
		Assert.assertNull(table.getColumn(0).getComment());
		
	}


}
