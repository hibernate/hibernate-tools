package org.hibernate.cfg;

import org.hibernate.boot.Metadata;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JDBCMetaDataConfigurationTest {

	static final String[] CREATE_SQL = new String[] {
			"create table withVersion (first int, second int, version int, name varchar(256), primary key (first))",
			"create table noVersion (first int, second int, name varchar(256), primary key (second))",
			"create table withRealTimestamp (first int, second int, timestamp timestamp, name varchar(256), primary key (first))",
			"create table withFakeTimestamp (first int, second int, timestamp int, name varchar(256), primary key (first))", 
		};

	static final String[] DROP_SQL = new String[] {
			"drop table withVersion", 
			"drop table noVersion", 
			"drop table withRealTimestamp",
			"drop table withFakeTimestamp" 
		};

	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);
	}

	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	@Test
	public void testReadFromJDBC() throws Exception {
		JDBCMetaDataConfiguration cfg = new JDBCMetaDataConfiguration();
		cfg.readFromJDBC();
		Metadata metadata = cfg.getMetadata();
		Assert.assertNotNull("Withrealtimestamp", metadata.getEntityBinding("Withrealtimestamp"));
		Assert.assertNotNull("Noversion", metadata.getEntityBinding("Noversion"));
		Assert.assertNotNull("Withfaketimestamp", metadata.getEntityBinding("Withfaketimestamp"));
		Assert.assertNotNull("Withversion", metadata.getEntityBinding("Withversion"));
	}
	
	@Test
	public void testGetTable() throws Exception {
		JDBCMetaDataConfiguration cfg = new JDBCMetaDataConfiguration();
		Assert.assertNull(cfg.getTable(JdbcUtil.toIdentifier(this, "withrealtimestamp")));
		cfg = new JDBCMetaDataConfiguration();
		cfg.readFromJDBC();
		Assert.assertNotNull(cfg.getTable(JdbcUtil.toIdentifier(this, "withrealtimestamp")));
	}

}
