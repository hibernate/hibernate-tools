package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;

import org.hibernate.mapping.Join;
import org.hibernate.mapping.Property;
import org.hibernate.tool.orm.jbt.internal.factory.JoinWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JoinWrapperTest {
	
	private JoinWrapper joinWrapper = null;
	private Join wrappedJoin = null;
	
	@BeforeEach
	public void beforeEach() {
		wrappedJoin = new Join();
		joinWrapper = JoinWrapperFactory.createJoinWrapper(wrappedJoin);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(wrappedJoin);
		assertNotNull(joinWrapper);
	}
	
	@Test
	public void testGetPropertyIterator() {
		Iterator<Property> propertyIterator = joinWrapper.getPropertyIterator();
		assertFalse(propertyIterator.hasNext());
		Property property = new Property();
		wrappedJoin.addProperty(property);
		propertyIterator = joinWrapper.getPropertyIterator();
		assertTrue(propertyIterator.hasNext());
		Property p = propertyIterator.next();
		assertSame(p, property);
	}

}
