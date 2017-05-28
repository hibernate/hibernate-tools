package org.hibernate.tool.test.cfg.JDBCMetaDataConfiguration;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
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
