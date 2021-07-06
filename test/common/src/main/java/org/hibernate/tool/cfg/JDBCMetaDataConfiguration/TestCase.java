package org.hibernate.tool.cfg.JDBCMetaDataConfiguration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.boot.Metadata;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author max
 * @author koen
 */
public class TestCase {

	@BeforeEach
	public void setUp() {
		JdbcUtil.createDatabase(this);
	}

	@AfterEach
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	@Test
	public void testReadFromJDBC() throws Exception {
		Metadata metadata = MetadataDescriptorFactory
				.createJdbcDescriptor(null, null, true)
				.createMetadata();
		assertNotNull(metadata.getEntityBinding("WithRealTimestamp"), "WithRealTimestamp");
		assertNotNull(metadata.getEntityBinding("NoVersion"), "NoVersion");
		assertNotNull(metadata.getEntityBinding("WithFakeTimestamp"), "WithFakeTimestamp");
		assertNotNull(metadata.getEntityBinding("WithVersion"), "WithVersion");
	}
	
	@Test
	public void testGetTable() throws Exception {
		assertNotNull(
				HibernateUtil.getTable(
						MetadataDescriptorFactory
							.createJdbcDescriptor(null, null, true)
							.createMetadata(), 
						JdbcUtil.toIdentifier(this, "WITH_REAL_TIMESTAMP")));
	}

}
