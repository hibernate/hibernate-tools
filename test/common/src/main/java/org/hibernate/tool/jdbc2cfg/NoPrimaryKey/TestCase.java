/*
 * Created on 2004-11-24
 *
 */
package org.hibernate.tool.jdbc2cfg.NoPrimaryKey;

import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
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
	public void testMe() {
		Assert.assertNotNull(
			MetadataDescriptorFactory
				.createReverseEngineeringDescriptor(null, null)
				.createMetadata());
	}
	
}
