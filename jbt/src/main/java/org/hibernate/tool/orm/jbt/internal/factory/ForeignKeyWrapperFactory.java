package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.mapping.ForeignKey;
import org.hibernate.tool.orm.jbt.api.ForeignKeyWrapper;

public class ForeignKeyWrapperFactory {

	public static ForeignKeyWrapper createForeignKeyWrapper(final ForeignKey wrappedForeignKey) {
		return new ForeignKeyWrapper() {
			@Override public ForeignKey getWrappedObject() { return wrappedForeignKey; }
		};
	}
	
}
