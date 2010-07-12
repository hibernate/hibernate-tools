/*
 * Created on 2004-11-24
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;

/**
 * @author max
 *
 */
public class AutoQuoteTest extends JDBCMetaDataBinderTestCase {

	protected String[] getCreateSQL() {
		
		return new String[] {
				"create table \"us-ers\" ( userid INTEGER NOT NULL, department VARCHAR(3), PRIMARY KEY (userid) )", 
				"create table typ ( indexid INTEGER NOT NULL, text varchar(10) NOT NULL, korr INTEGER NOT NULL, PRIMARY KEY (indexid) )", 
				"create table workLogs ( indexid INTEGER NOT NULL, loggedid INTEGER NOT NULL, userid INTEGER NOT NULL, typ INTEGER NOT NULL, PRIMARY KEY (indexid, userid), FOREIGN KEY (userid) REFERENCES \"us-ers\"(userid), FOREIGN KEY (typ) REFERENCES typ(indexid) )"
		};
	}

	protected String[] getDropSQL() {
		
		return new String[]  {
				"drop table workLogs",
				"drop table \"us-ers\"",
				"drop table typ",
		};
	}
	
	public void testForQuotes() {

		Table table = getTable("us-ers");
		assertNotNull(table);
		assertTrue(table.isQuoted());
		
		assertEquals(2, table.getColumnSpan());
		
		PersistentClass classMapping = cfg.getClassMapping("Worklogs");
		assertNotNull(classMapping);
		Property property = classMapping.getProperty("usErs");
		assertNotNull(property);
		
	}
	
	public static Test suite() {
		return new TestSuite(AutoQuoteTest.class);
	}
}
