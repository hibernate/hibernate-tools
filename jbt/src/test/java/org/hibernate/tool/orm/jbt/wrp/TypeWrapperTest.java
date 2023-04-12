package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.hibernate.tool.orm.jbt.type.ClassType;
import org.hibernate.tool.orm.jbt.wrp.TypeWrapperFactory.TypeWrapper;
import org.hibernate.type.AnyType;
import org.hibernate.type.ArrayType;
import org.junit.jupiter.api.Test;

public class TypeWrapperTest {

	@Test
	public void testToString() {
		// first try type that is string representable
		TypeWrapper classTypeWrapper = TypeWrapperFactory.createTypeWrapper(new ClassType());
		assertEquals(
				TypeWrapperTest.class.getName(), 
				classTypeWrapper.toString(TypeWrapperTest.class));
		// next try a type that cannot be represented by a string
		try {
			TypeWrapper arrayTypeWrapper = 
					TypeWrapperFactory.createTypeWrapper(new ArrayType("foo", "bar", String.class));
			arrayTypeWrapper.toString(new String[] { "foo", "bar" });
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'toString(Object)'"));
		}
	}
	
	@Test
	public void testIsAnyType() {
		// first try type that is not a any type
		TypeWrapper classTypeWrapper = TypeWrapperFactory.createTypeWrapper(new ClassType());
		assertFalse(classTypeWrapper.isAnyType());
		// next try a any type
		TypeWrapper anyTypeWrapper = 
				TypeWrapperFactory.createTypeWrapper(new AnyType(null, null, null, true));
		assertTrue(anyTypeWrapper.isAnyType());
	}
	
	@Test
	public void testIsCollectionType() {
		// first try type that is not a collection type
		TypeWrapper classTypeWrapper = 
				TypeWrapperFactory.createTypeWrapper(new ClassType());
		assertFalse(classTypeWrapper.isCollectionType());
		// next try a collection type
		TypeWrapper arrayTypeWrapper = 
				TypeWrapperFactory.createTypeWrapper(new ArrayType(null, null, String.class));
		assertTrue(arrayTypeWrapper.isCollectionType());
	}
	
}
