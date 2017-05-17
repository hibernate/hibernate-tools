/*
 * Created on 2004-11-24
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

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
 *
 */
public class AutoQuoteTest {

	static final String[] CREATE_SQL = new String[] {
				"CREATE TABLE \"us-ers\" ( USERID INTEGER NOT NULL, DEPARTMENT VARCHAR(3), PRIMARY KEY (USERID) )", 
				"CREATE TABLE TYP ( INDEXID INTEGER NOT NULL, TEXT VARCHAR(10) NOT NULL, KORR INTEGER NOT NULL, PRIMARY KEY (INDEXID) )", 
				"CREATE TABLE WORKLOGS ( INDEXID INTEGER NOT NULL, LOGGEDID INTEGER NOT NULL, USERID INTEGER NOT NULL, TYP INTEGER NOT NULL, PRIMARY KEY (INDEXID, USERID), FOREIGN KEY (USERID) REFERENCES \"us-ers\"(USERID), FOREIGN KEY (TYP) REFERENCES TYP(INDEXID) )"
		};

	static final String[] DROP_SQL = new String[]  {
				"DROP TABLE WORKLOGS",
				"DROP TABLE \"us-ers\"",
				"DROP TABLE TYP",
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
