/*
 * Created on 2004-11-23
 *
 */
package org.hibernate.tool.jdbc2cfg.Identity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.SQLException;

import org.hibernate.boot.Metadata;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * @author max
 * @author koen
 */
public class TestCase {

	private Metadata metadata = null;

	@BeforeEach
	public void setUp() {
		JdbcUtil.createDatabase(this);
		MetadataDescriptorFactory
			.createJdbcDescriptor(null, null, true)
			.createMetadata();
	}

	@AfterEach
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	// TODO HBX-1413: Enable the test below
	@Disabled
	@Test
	public void testIdentity() throws SQLException {
		PersistentClass classMapping = metadata.getEntityBinding("WithIdentity");
		assertNotNull(classMapping);
		assertEquals(
				"identity", 
				((SimpleValue)classMapping
						.getIdentifierProperty()
						.getValue())
					.getIdentifierGeneratorStrategy());		
	}
	
	// TODO HBX-1413: Enable the test below
	@Disabled
	@Test
	public void testGuid() throws SQLException {
		PersistentClass classMapping = metadata.getEntityBinding("WithGuid");
		assertNotNull(classMapping);
		assertEquals(
				"guid", 
				((SimpleValue)classMapping
						.getIdentifierProperty()
						.getValue())
					.getIdentifierGeneratorStrategy());		
	}
}
