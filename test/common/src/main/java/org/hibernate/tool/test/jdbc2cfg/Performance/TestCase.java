/*
 * Created on 2004-11-23
 *
 */
package org.hibernate.tool.test.jdbc2cfg.Performance;

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

	private static final int TABLECOUNT = 200;
	private static final int COLCOUNT = 10;
	
	private JDBCMetaDataConfiguration jmdcfg = null;

	@Before
	public void setUp() {
		jmdcfg = new JDBCMetaDataConfiguration();
		JdbcUtil.createDatabase(this);
	}
	
	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	@Test
	public void testBasic() throws SQLException {	
		jmdcfg.readFromJDBC();
		JUnitUtil.assertIteratorContainsExactly(
				"There should be " + TABLECOUNT + " tables!", 
				jmdcfg.getMetadata().collectTableMappings().iterator(), 
				TABLECOUNT);
		Table tab = (Table) jmdcfg.getMetadata().collectTableMappings().iterator().next();
		Assert.assertEquals(tab.getColumnSpan(), COLCOUNT+1);
	}
	
}
