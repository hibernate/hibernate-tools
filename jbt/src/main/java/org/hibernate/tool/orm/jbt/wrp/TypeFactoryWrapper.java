package org.hibernate.tool.orm.jbt.wrp;

import org.hibernate.tool.orm.jbt.type.TypeFactory;
import org.hibernate.tool.orm.jbt.wrp.TypeWrapperFactory.TypeWrapper;

public class TypeFactoryWrapper {

	public static final TypeFactoryWrapper INSTANCE = new TypeFactoryWrapper();
	
	static final TypeWrapper BOOLEAN_TYPE = TypeWrapperFactory.createTypeWrapper(TypeFactory.BOOLEAN_TYPE);
	
	private TypeFactoryWrapper() {}

	public TypeWrapper getBooleanType() {
		return BOOLEAN_TYPE;
	}

}
