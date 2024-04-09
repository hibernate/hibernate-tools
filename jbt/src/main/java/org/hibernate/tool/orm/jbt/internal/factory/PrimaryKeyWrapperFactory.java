package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.mapping.PrimaryKey;
import org.hibernate.tool.orm.jbt.api.PrimaryKeyWrapper;

public class PrimaryKeyWrapperFactory {

	public static PrimaryKeyWrapper createPrimaryKeyWrapper(PrimaryKey wrappedPrimaryKey) {
		return new PrimaryKeyWrapper() {
			@Override public PrimaryKey getWrappedObject() { return wrappedPrimaryKey; }
		};
	}
	
}
