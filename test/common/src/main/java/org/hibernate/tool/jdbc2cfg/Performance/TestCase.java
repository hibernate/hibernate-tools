/*
 * Created on 2004-11-23
 *
 */
package org.hibernate.tool.jdbc2cfg.Performance;

import java.sql.SQLException;

import org.hibernate.boot.Metadata;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tools.test.util.JUnitUtil;
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

	private static final int TABLECOUNT = 200;
	private static final int COLCOUNT = 10;
	
	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);
	}
	
	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	@Test
	public void testBasic() throws SQLException {	
		Metadata metadata = MetadataDescriptorFactory
				.createReverseEngineeringDescriptor(null, null)
				.createMetadata();
		JUnitUtil.assertIteratorContainsExactly(
				"There should be " + TABLECOUNT + " tables!", 
				metadata.collectTableMappings().iterator(), 
				TABLECOUNT);
		Table tab = (Table) metadata.collectTableMappings().iterator().next();
		Assert.assertEquals(tab.getColumnSpan(), COLCOUNT+1);
	}
	
}
