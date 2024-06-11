package org.hibernate.tool.orm.jbt.internal.factory;

import java.util.List;

import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.query.QueryExporter;
import org.hibernate.tool.orm.jbt.api.wrp.QueryExporterWrapper;
import org.hibernate.tool.orm.jbt.internal.wrp.AbstractWrapper;

public class QueryExporterWrapperFactory {

	public static QueryExporterWrapper createQueryExporterWrapper(QueryExporter wrappedQueryExporter) {
		return new QueryExporterWrapperImpl(wrappedQueryExporter);
	}
	
	private static class QueryExporterWrapperImpl 
			extends AbstractWrapper
			implements QueryExporterWrapper {
		
		private QueryExporter queryExporter = null;
		
		private QueryExporterWrapperImpl(QueryExporter queryExporter) {
			this.queryExporter = queryExporter;
		}
		
		@Override 
		public QueryExporter getWrappedObject() { 
			return queryExporter; 
		}
		
		@Override
		public void setQueries(List<String> queries) { 
			queryExporter.setQueries(queries); 
		}	
		
		@Override
		public void setFilename(String fileName) { 
			queryExporter.getProperties().put(ExporterConstants.OUTPUT_FILE_NAME, fileName); 
		}

	}

}
