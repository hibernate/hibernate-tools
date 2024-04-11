package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.query.Query;
import org.hibernate.tool.orm.jbt.api.QueryWrapper;

public class QueryWrapperFactory {

	public static QueryWrapper createQueryWrapper(Query<?> wrappedQuery) {
		return new QueryWrapper() {
			@Override public Query<?> getWrappedObject() { return wrappedQuery; }
		};
	}

}
