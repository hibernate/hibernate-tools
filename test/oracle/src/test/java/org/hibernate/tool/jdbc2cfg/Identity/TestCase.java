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

	// TODO HBX-1412: Enable the test below and make sure it passes.
	@Ignore
	@Test
	public void testIdentity() throws SQLException {

		PersistentClass classMapping = jmdcfg
				.getMetadata()
				.getEntityBinding("Autoinc");
		Assert.assertNotNull(classMapping);
		Assert.assertEquals(
				"identity", 
				((SimpleValue)classMapping
						.getIdentifierProperty()
						.getValue())
					.getIdentifierGeneratorStrategy());
		
		classMapping = jmdcfg
				.getMetadata()
				.getEntityBinding("Noautoinc");
		Assert.assertEquals(
				"assigned", 
				((SimpleValue)classMapping
						.getIdentifierProperty()
						.getValue())
					.getIdentifierGeneratorStrategy());
	}

}
