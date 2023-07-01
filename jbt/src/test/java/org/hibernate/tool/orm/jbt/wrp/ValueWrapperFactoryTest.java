package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.List;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;
import org.hibernate.tool.orm.jbt.wrp.ValueWrapperFactory.ValueWrapper;
import org.junit.jupiter.api.Test;

public class ValueWrapperFactoryTest {
	
	@Test
	public void testCreateValueWrapper() {
		Value valueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		Value valueWrapper = ValueWrapperFactory.createValueWrapper(valueTarget);
		assertNotNull(valueWrapper);
		assertSame(valueTarget, ((Wrapper)valueWrapper).getWrappedObject());
	}
	
	@Test
	public void testGetElement() {
		List valueTarget = new List(DummyMetadataBuildingContext.INSTANCE, null);
		Value basicValue = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		ValueWrapper valueWrapper = ValueWrapperFactory.createValueWrapper(valueTarget);
		valueTarget.setElement(basicValue);
		assertSame(basicValue, ((Wrapper)valueWrapper.getElement()).getWrappedObject());
		valueTarget.setElement(null);
		assertNull(valueWrapper.getElement());
	}
	
}
