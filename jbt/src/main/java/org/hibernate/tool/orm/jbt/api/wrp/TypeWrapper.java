package org.hibernate.tool.orm.jbt.api.wrp;

import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface TypeWrapper extends Wrapper {

	String toString(Object object);
	String getName();
	Object fromStringValue(String stringValue);
	boolean isEntityType();
	boolean isOneToOne();
	boolean isAnyType();
	boolean isComponentType();
	boolean isCollectionType();
	String getAssociatedEntityName();
	boolean isIntegerType();
	boolean isArrayType();
	boolean isInstanceOfPrimitiveType();
	Class<?> getPrimitiveClass();
	String getRole();
	String getReturnedClassName();
	
}


