package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.mapping.Table;
import org.hibernate.tool.orm.jbt.api.TableWrapper;

public class TableWrapperFactory {

	public static TableWrapper createTableWrapper(Table wrappedTable) {
		return new TableWrapper() {
			@Override public Table getWrappedObject() { return wrappedTable; }
		};
	}

}
