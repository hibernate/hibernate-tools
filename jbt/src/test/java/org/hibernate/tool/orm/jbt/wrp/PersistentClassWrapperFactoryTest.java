package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;
import org.hibernate.tool.orm.jbt.wrp.PersistentClassWrapperFactory.PersistentClassWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PersistentClassWrapperFactoryTest {
	
	private PersistentClass rootClassTarget, singleTableSubclassTarget, joinedSubclassTarget = null;
	private PersistentClassWrapper rootClassWrapper, singleTableSubclassWrapper, joinedSubclassWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		rootClassTarget = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		rootClassWrapper = (PersistentClassWrapper)PersistentClassWrapperFactory
				.createPersistentClassWrapper(rootClassTarget);
		singleTableSubclassTarget = new SingleTableSubclass(rootClassTarget, DummyMetadataBuildingContext.INSTANCE);
		singleTableSubclassWrapper = (PersistentClassWrapper)PersistentClassWrapperFactory
				.createPersistentClassWrapper(singleTableSubclassTarget);
		joinedSubclassTarget = new JoinedSubclass(rootClassTarget, DummyMetadataBuildingContext.INSTANCE);
		joinedSubclassWrapper = (PersistentClassWrapper)PersistentClassWrapperFactory
				.createPersistentClassWrapper(joinedSubclassTarget);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(rootClassTarget);
		assertNotNull(rootClassWrapper);
		assertNotNull(singleTableSubclassTarget);
		assertNotNull(singleTableSubclassWrapper);
		assertNotNull(joinedSubclassTarget);
		assertNotNull(joinedSubclassWrapper);
	}
	
}
