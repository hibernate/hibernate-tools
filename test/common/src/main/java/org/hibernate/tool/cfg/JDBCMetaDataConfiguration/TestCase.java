package org.hibernate.tool.cfg.JDBCMetaDataConfiguration;

import org.hibernate.boot.Metadata;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tools.test.util.HibernateUtil;
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
		Metadata metadata = MetadataDescriptorFactory
				.createReverseEngineeringDescriptor(null, null)
				.createMetadata();
		Assert.assertNotNull("WithRealTimestamp", metadata.getEntityBinding("WithRealTimestamp"));
		Assert.assertNotNull("NoVersion", metadata.getEntityBinding("NoVersion"));
		Assert.assertNotNull("WithFakeTimestamp", metadata.getEntityBinding("WithFakeTimestamp"));
		Assert.assertNotNull("WithVersion", metadata.getEntityBinding("WithVersion"));
	}
	
	@Test
	public void testGetTable() throws Exception {
		Assert.assertNotNull(
				HibernateUtil.getTable(
						MetadataDescriptorFactory
							.createReverseEngineeringDescriptor(null, null)
							.createMetadata(), 
						JdbcUtil.toIdentifier(this, "WITH_REAL_TIMESTAMP")));
	}

}
