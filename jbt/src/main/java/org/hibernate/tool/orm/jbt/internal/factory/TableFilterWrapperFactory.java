package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.tool.internal.reveng.strategy.TableFilter;
import org.hibernate.tool.orm.jbt.api.TableFilterWrapper;

public class TableFilterWrapperFactory {

	public static TableFilterWrapper createTableFilterWrapper(TableFilter wrappedTableFilter) {
		return new TableFilterWrapper() {
			@Override public TableFilter getWrappedObject() { return wrappedTableFilter; }
		};
	}

}
