package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.mapping.Column;
import org.hibernate.tool.orm.jbt.api.ColumnWrapper;

public class ColumnWrapperFactory {

	public static ColumnWrapper createColumnWrapper(final String name) {
		final Column wrappedColumn = new Column(name);
		return new ColumnWrapper() {
			@Override public Column getWrappedObject() { return wrappedColumn; }
		};
	}
	
}
