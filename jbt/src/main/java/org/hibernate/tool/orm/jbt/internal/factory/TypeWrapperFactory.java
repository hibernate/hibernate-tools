package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.tool.orm.jbt.api.TypeWrapper;
import org.hibernate.type.Type;

public class TypeWrapperFactory {

	public static TypeWrapper createTypeWrapper(Type wrappedType) {
		return new TypeWrapper() {
			@Override public Type getWrappedObject() { return wrappedType; }
		};
	}

}
