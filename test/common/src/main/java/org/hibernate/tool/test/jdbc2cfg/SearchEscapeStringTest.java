/*
 * Created on 2004-11-23
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

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
 * 
 */
public class SearchEscapeStringTest {

	static final String[] CREATE_SQL = new String[] { 
			"create table b_tab ( a int not null, name varchar(20), primary key (a)  )",
			"create table b2tab ( a int not null, name varchar(20), primary key (a)  )",
	};

	static final String[] DROP_SQL = new String[] { 
		        "drop table b_tab", 
		        "drop table b2tab",
	};

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

		Table table = jmdcfg.getTable( JdbcUtil.toIdentifier(this, "b_tab" ) );
		Table table2 = jmdcfg.getTable( JdbcUtil.toIdentifier(this, "b2tab" ) );

		Assert.assertNotNull(table);
		Assert.assertNotNull(table2);
		
		Assert.assertEquals(table.getColumnSpan(), 2);
		Assert.assertEquals(table2.getColumnSpan(), 2);
		
	}

}
