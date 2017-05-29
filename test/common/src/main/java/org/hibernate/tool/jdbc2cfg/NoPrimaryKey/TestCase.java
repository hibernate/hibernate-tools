/*
 * Created on 2004-11-24
 *
 */
package org.hibernate.tool.jdbc2cfg.NoPrimaryKey;

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
 */
public class TestCase {

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
	public void testMe() throws Exception {
		Assert.assertNotNull(MetadataHelper.getMetadata(jmdcfg));
	}
	
}
