package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.tool.orm.jbt.type.ClassType;
import org.hibernate.tool.orm.jbt.wrp.TypeWrapperFactory.TypeWrapper;
import org.hibernate.type.ArrayType;
import org.junit.jupiter.api.Test;

public class TypeWrapperTest {

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
