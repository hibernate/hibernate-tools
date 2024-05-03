package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.api.ValueWrapper;

public class ValueWrapperFactory {

	public static ValueWrapper createValueWrapper(Value wrappedArrayValue) {
		return new ValueWrapper() {
			@Override public Value getWrappedObject() { return wrappedArrayValue; }
		};
	}

}
