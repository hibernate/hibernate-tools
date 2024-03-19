package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.tool.orm.jbt.internal.factory.NamingStrategyWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NamingStrategyWrapperTest {

	private NamingStrategyWrapper namingStrategyWrapper = null;
	private NamingStrategy wrappedNamingStrategy = null;
	
	@BeforeEach
	public void beforeEach() {
		wrappedNamingStrategy = new DefaultNamingStrategy();
		namingStrategyWrapper = NamingStrategyWrapperFactory.createNamingStrategyWrapper(wrappedNamingStrategy);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(wrappedNamingStrategy);
		assertNotNull(namingStrategyWrapper);
	}
	
}
