package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.mapping.Join;
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
	
}
