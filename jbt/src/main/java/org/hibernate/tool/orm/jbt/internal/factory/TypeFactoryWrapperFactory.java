package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.tool.orm.jbt.api.TypeFactoryWrapper;

public class TypeFactoryWrapperFactory {
	
	private static TypeFactoryWrapper INSTANCE = new TypeFactoryWrapper() {};

	public static TypeFactoryWrapper createTypeFactoryWrapper() {
		return INSTANCE;
	}

}
