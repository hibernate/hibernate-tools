/*
 * Created on 2004-11-24
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.tool.util.MetadataHelper;
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
public class NoPrimaryKeyTest {

	private static final String[] CREATE_SQL = new String[] {
				"create table G0 ( AN_ID VARCHAR(20), CONSTRAINT \"C0\" PRIMARY KEY (\"AN_ID\") )", 
				"create table G1 ( AN_ID VARCHAR(20), CONSTRAINT \"C1\" FOREIGN KEY (\"AN_ID\") REFERENCES \"G0\")"
		};


	private static final String[] DROP_SQL = new String[] {
				"drop table G1",
				"drop table G0"								
		};

	
	private JDBCMetaDataConfiguration jmdcfg = null;

	@Before
	public void setUp() {
		JdbcUtil.establishJdbcConnection(this);
		JdbcUtil.executeSql(this, CREATE_SQL);
		jmdcfg = new JDBCMetaDataConfiguration();
		jmdcfg.readFromJDBC();
	}

	@After
	public void tearDown() {
		JdbcUtil.executeSql(this, DROP_SQL);
		JdbcUtil.releaseJdbcConnection(this);
	}

	@Test
	public void testMe() throws Exception {
		Assert.assertNotNull(MetadataHelper.getMetadata(jmdcfg));
	}
	
}
