package org.hibernate.tool.test.jdbc2cfg;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Property;

public class KeyPropertyCompositeIdTest extends CompositeIdTest {
	
	protected void configure(JDBCMetaDataConfiguration configuration) {
		super.configure( configuration );
		configuration.setPreferBasicCompositeIds(false);
	}
	
	public static Test suite() {
		return new TestSuite(KeyPropertyCompositeIdTest.class);
	}
	
	protected void checkKeyProperties(Property id, Property extraId) {
		assertEquals(toPropertyName("customer"), id.getName() );
        assertEquals(toPropertyName("ordernumber"), extraId.getName() );
        
        assertTrue(id.getValue() instanceof ManyToOne);
        assertFalse(extraId.getValue() instanceof ManyToOne);
	}

	protected String getCustomerOrderQuery() {
		return "select li.id.customerorder.id from Lineitem as li";
	}

}
