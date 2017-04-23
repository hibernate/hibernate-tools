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

	private static final String[] CREATE_SQL = new String[] {
				"create table \"us-ers\" ( userid INTEGER NOT NULL, department VARCHAR(3), PRIMARY KEY (userid) )", 
				"create table typ ( indexid INTEGER NOT NULL, text varchar(10) NOT NULL, korr INTEGER NOT NULL, PRIMARY KEY (indexid) )", 
				"create table workLogs ( indexid INTEGER NOT NULL, loggedid INTEGER NOT NULL, userid INTEGER NOT NULL, typ INTEGER NOT NULL, PRIMARY KEY (indexid, userid), FOREIGN KEY (userid) REFERENCES \"us-ers\"(userid), FOREIGN KEY (typ) REFERENCES typ(indexid) )"
		};

	private static final String[] DROP_SQL = new String[]  {
				"drop table workLogs",
				"drop table \"us-ers\"",
				"drop table typ",
		};
	
	private JDBCMetaDataConfiguration jmdcfg = null;

	@Before
	public void setUp() {
		JdbcUtil.establishJdbcConnection(this);
		JdbcUtil.executeDDL(this, CREATE_SQL);
		jmdcfg = new JDBCMetaDataConfiguration();
		jmdcfg.readFromJDBC();
	}

	@After
	public void tearDown() {
		JdbcUtil.executeDDL(this, DROP_SQL);
		JdbcUtil.releaseJdbcConnection(this);
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
