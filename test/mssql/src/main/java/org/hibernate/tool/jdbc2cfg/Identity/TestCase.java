/*
 * Created on 2004-11-23
 *
 */
package org.hibernate.tool.jdbc2cfg.Identity;

import java.sql.SQLException;

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
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

	// TODO HBX-1413: Enable the test below
	@Ignore 
	@Test
	public void testIdentity() throws SQLException {
		PersistentClass classMapping = jmdcfg
				.getMetadata()
				.getEntityBinding("WithIdentity");
		Assert.assertNotNull(classMapping);
		Assert.assertEquals(
				"identity", 
				((SimpleValue)classMapping
						.getIdentifierProperty()
						.getValue())
					.getIdentifierGeneratorStrategy());		
	}
	
	// TODO HBX-1413: Enable the test below
	@Ignore 
	@Test
	public void testGuid() throws SQLException {
		PersistentClass classMapping = jmdcfg
				.getMetadata()
				.getEntityBinding("WithGuid");
		Assert.assertNotNull(classMapping);
		Assert.assertEquals(
				"guid", 
				((SimpleValue)classMapping
						.getIdentifierProperty()
						.getValue())
					.getIdentifierGeneratorStrategy());		
	}
}
