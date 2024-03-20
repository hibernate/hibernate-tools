package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.tool.internal.reveng.strategy.OverrideRepository;
import org.hibernate.tool.orm.jbt.internal.factory.OverrideRepositoryWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OverrideRepositoryWrapperTest {

	private OverrideRepositoryWrapper overrideRepositoryWrapper = null;
	private OverrideRepository wrappedOverrideRepository = null;
	
	@BeforeEach
	public void beforeEach() {
		wrappedOverrideRepository = new OverrideRepository();
		overrideRepositoryWrapper = OverrideRepositoryWrapperFactory.createOverrideRepositoryWrapper(wrappedOverrideRepository);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(wrappedOverrideRepository);
		assertNotNull(overrideRepositoryWrapper);
	}
	
}
