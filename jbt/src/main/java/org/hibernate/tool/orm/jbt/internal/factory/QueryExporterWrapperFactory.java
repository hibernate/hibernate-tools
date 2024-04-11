package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.tool.internal.export.query.QueryExporter;
import org.hibernate.tool.orm.jbt.api.QueryExporterWrapper;

public class QueryExporterWrapperFactory {

	public static QueryExporterWrapper createQueryExporterWrapper(QueryExporter wrappedQueryExporter) {
		return new QueryExporterWrapper() {
			@Override public QueryExporter getWrappedObject() { return wrappedQueryExporter; }
		};
	}

}
