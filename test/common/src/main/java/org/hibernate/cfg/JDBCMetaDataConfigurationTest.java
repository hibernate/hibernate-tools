package org.hibernate.cfg;

import org.hibernate.boot.Metadata;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JDBCMetaDataConfigurationTest {

	static final String[] CREATE_SQL = new String[] {
			"CREATE TABLE WITH_VERSION (FIRST INT, SECOND INT, VERSION INT, NAME VARCHAR(256), PRIMARY KEY (FIRST))",
			"CREATE TABLE NO_VERSION (FIRST INT, SECOND INT, NAME VARCHAR(256), PRIMARY KEY (SECOND))",
			"CREATE TABLE WITH_REAL_TIMESTAMP (FIRST INT, SECOND INT, TIMESTAMP TIMESTAMP, NAME VARCHAR(256), PRIMARY KEY (FIRST))",
			"CREATE TABLE WITH_FAKE_TIMESTAMP (FIRST INT, SECOND INT, TIMESTAMP INT, NAME VARCHAR(256), PRIMARY KEY (FIRST))", 
		};

	static final String[] DROP_SQL = new String[] {
			"DROP TABLE WITH_VERSION", 
			"DROP TABLE NO_VERSION", 
			"DROP TABLE WITH_REAL_TIMESTAMP",
			"DROP TABLE WITH_FAKE_TIMESTAMP" 
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
		Assert.assertNotNull("WithRealTimestamp", metadata.getEntityBinding("WithRealTimestamp"));
		Assert.assertNotNull("NoVersion", metadata.getEntityBinding("NoVersion"));
		Assert.assertNotNull("WithFakeTimestamp", metadata.getEntityBinding("WithFakeTimestamp"));
		Assert.assertNotNull("WithVersion", metadata.getEntityBinding("WithVersion"));
	}
	
	@Test
	public void testGetTable() throws Exception {
		JDBCMetaDataConfiguration cfg = new JDBCMetaDataConfiguration();
		Assert.assertNull(cfg.getTable(JdbcUtil.toIdentifier(this, "WITH_REAL_TIMESTAMP")));
		cfg = new JDBCMetaDataConfiguration();
		cfg.readFromJDBC();
		Assert.assertNotNull(cfg.getTable(JdbcUtil.toIdentifier(this, "WITH_REAL_TIMESTAMP")));
	}

}
