/*
 * Created on 2004-11-23
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import java.sql.SQLException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.mapping.Table;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;

/**
 * @author max
 * 
 */
public class SearchEscapeStringTest extends JDBCMetaDataBinderTestCase {

	/**
	 * @return
	 */
	protected String[] getDropSQL() {
		return new String[] { 
		        "drop table b_tab", 
		        "drop table b2tab",
				};
	}

	/**
	 * @return
	 */
	protected String[] getCreateSQL() {

		return new String[] {
				"create table b_tab ( a int not null, name varchar(20), primary key (a)  )",
				"create table b2tab ( a int not null, name varchar(20), primary key (a)  )",
				};
	}

	public void testBasic() throws SQLException {

		assertHasNext( "There should be 2 tables!", 2, cfg.getTableMappings() );

		Table table = getTable( identifier( "b_tab" ) );
		Table table2 = getTable( identifier( "b2tab" ) );

		assertNotNull(table);
		assertNotNull(table2);
		
		assertEquals(table.getColumnSpan(), 2);
		assertEquals(table2.getColumnSpan(), 2);
		
	}

	public static Test suite() {
		return new TestSuite( SearchEscapeStringTest.class );
	}

	protected void setUp() throws Exception {
		super.setUp();
	}
}
