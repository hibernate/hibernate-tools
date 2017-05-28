/*
 * Created on 2004-11-23
 *
 */
package org.hibernate.tool.test.jdbc2cfg.SearchEscapeString;

import java.sql.SQLException;

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.mapping.Table;
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
	public void testBasic() throws SQLException {

		JUnitUtil.assertIteratorContainsExactly( 
				"There should be 2 tables!", 
				jmdcfg.getMetadata().collectTableMappings().iterator(),
				2);

		Table table = jmdcfg.getTable( JdbcUtil.toIdentifier(this, "B_TAB" ) );
		Table table2 = jmdcfg.getTable( JdbcUtil.toIdentifier(this, "B2TAB" ) );

		Assert.assertNotNull(table);
		Assert.assertNotNull(table2);
		
		Assert.assertEquals(table.getColumnSpan(), 2);
		Assert.assertEquals(table2.getColumnSpan(), 2);
		
	}

}
