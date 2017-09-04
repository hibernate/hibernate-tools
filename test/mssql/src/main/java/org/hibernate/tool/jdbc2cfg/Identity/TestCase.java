/*
 * Created on 2004-11-23
 *
 */
package org.hibernate.tool.jdbc2cfg.Identity;

import java.sql.SQLException;

import org.hibernate.boot.Metadata;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.tool.metadata.MetadataSourcesFactory;
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

	private Metadata metadata = null;

	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);
		MetadataSourcesFactory.createJdbcSources(null, null).buildMetadata();
	}

	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	// TODO HBX-1413: Enable the test below
	@Ignore 
	@Test
	public void testIdentity() throws SQLException {
		PersistentClass classMapping = metadata.getEntityBinding("WithIdentity");
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
		PersistentClass classMapping = metadata.getEntityBinding("WithGuid");
		Assert.assertNotNull(classMapping);
		Assert.assertEquals(
				"guid", 
				((SimpleValue)classMapping
						.getIdentifierProperty()
						.getValue())
					.getIdentifierGeneratorStrategy());		
	}
}
