package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Value;
import org.junit.jupiter.api.Test;

public class ValueWrapperFactoryTest {
	
	@Test
	public void testCreateArrayWrapper() {
		PersistentClassWrapper persistentClassWrapper = PersistentClassWrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = persistentClassWrapper.getWrappedObject();
		Value arrayWrapper = ValueWrapperFactory.createArrayWrapper(persistentClassWrapper);
		assertTrue(arrayWrapper instanceof Array);
		assertSame(((Array)arrayWrapper).getOwner(), persistentClassTarget);
	}

	@Test
	public void testCreateBagWrapper() {
		PersistentClassWrapper persistentClassWrapper = PersistentClassWrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = persistentClassWrapper.getWrappedObject();
		Value bagWrapper = ValueWrapperFactory.createBagWrapper(persistentClassWrapper);
		assertTrue(bagWrapper instanceof Bag);
		assertSame(((Bag)bagWrapper).getOwner(), persistentClassTarget);
	}

}
