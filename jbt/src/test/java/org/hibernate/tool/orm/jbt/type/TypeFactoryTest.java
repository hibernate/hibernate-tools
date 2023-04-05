package org.hibernate.tool.orm.jbt.type;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

public class TypeFactoryTest {
	
	@Test
	public void testConstruction() {
		assertNotNull(TypeFactory.INSTANCE);
	}

	@Test
	public void testGetBooleanType() {
		assertSame(BooleanType.INSTANCE, TypeFactory.INSTANCE.getBooleanType());
	}
	
	@Test
	public void testGetByteType() {
		assertSame(ByteType.INSTANCE, TypeFactory.INSTANCE.getByteType());
	}
	
	@Test
	public void testGetBigIntegerType() {
		assertSame(BigIntegerType.INSTANCE, TypeFactory.INSTANCE.getBigIntegerType());
	}
	
}
