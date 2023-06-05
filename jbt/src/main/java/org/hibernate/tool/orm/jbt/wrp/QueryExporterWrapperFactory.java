package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import org.hibernate.tool.internal.export.query.QueryExporter;

public class QueryExporterWrapperFactory {
	
	public static QueryExporterWrapper create(QueryExporter wrappedExporter) {
		return (QueryExporterWrapper)Proxy.newProxyInstance( 
				GenericExporterWrapperFactory.class.getClassLoader(), 
				new Class[] { QueryExporterWrapper.class }, 
				new QueryExporterInvocationHandler(wrappedExporter));
	}

	private static class QueryExporterInvocationHandler implements InvocationHandler {
		
		private QueryExporterWrapper exporterWrapper = null;
		
		private QueryExporterInvocationHandler(QueryExporter wrappedExporter) {
			this.exporterWrapper = new QueryExporterWrapperImpl(wrappedExporter);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return method.invoke(exporterWrapper, args);
		}
		
	}
	
	static interface QueryExporterWrapper extends Wrapper {
		@Override QueryExporter getWrappedObject();
		default void setQueries(List<String> queries) {
			getWrappedObject().setQueries(queries);
		}
		
	}

	static class QueryExporterWrapperImpl implements QueryExporterWrapper {
		private QueryExporter delegateQueryExporter = null;
		private QueryExporterWrapperImpl(QueryExporter wrappedExporter) {
			delegateQueryExporter = wrappedExporter;
		}
		@Override 
		public QueryExporter getWrappedObject() {
			return delegateQueryExporter;
		}
	}

}
