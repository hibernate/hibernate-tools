package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.hibernate.tool.orm.jbt.internal.factory.RevengStrategyWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RevengStrategyWrapperTest {

	private RevengStrategy wrappedRevengStrategy = null;
	private RevengStrategyWrapper revengStrategyWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		wrappedRevengStrategy = new DefaultStrategy();
		revengStrategyWrapper = RevengStrategyWrapperFactory.createRevengStrategyWrapper(wrappedRevengStrategy);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(wrappedRevengStrategy);
		assertNotNull(revengStrategyWrapper);
	}
	
	
}
