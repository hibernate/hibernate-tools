/*
 * Created on 2004-11-23
 *
 */
package org.hibernate.tool.jdbc2cfg.SearchEscapeString;

import java.sql.SQLException;

import org.hibernate.boot.Metadata;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JUnitUtil;
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
	public void testBasic() throws SQLException {

		JUnitUtil.assertIteratorContainsExactly( 
				"There should be 2 tables!", 
				metadata.collectTableMappings().iterator(),
				2);

		Table table = HibernateUtil.getTable(metadata, JdbcUtil.toIdentifier(this, "B_TAB" ) );
		Table table2 = HibernateUtil.getTable(metadata, JdbcUtil.toIdentifier(this, "B2TAB" ) );

		Assert.assertNotNull(table);
		Assert.assertNotNull(table2);
		
		Assert.assertEquals(table.getColumnSpan(), 2);
		Assert.assertEquals(table2.getColumnSpan(), 2);
		
	}

}
