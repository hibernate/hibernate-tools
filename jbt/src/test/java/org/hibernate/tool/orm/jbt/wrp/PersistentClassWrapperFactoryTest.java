package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PersistentClassWrapperFactoryTest {
	
	private PersistentClass rootClassTarget = null;
	private PersistentClassWrapper rootClassWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		rootClassTarget = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		rootClassWrapper = (PersistentClassWrapper)PersistentClassWrapperFactory
				.createRootClassWrapper();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(rootClassTarget);
		assertNotNull(rootClassWrapper);
	}
	
}
