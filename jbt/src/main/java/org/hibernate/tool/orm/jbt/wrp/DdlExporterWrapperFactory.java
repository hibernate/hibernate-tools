package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Properties;

import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.ddl.DdlExporter;

public class DdlExporterWrapperFactory {
	
	public static DdlExporterWrapper create(DdlExporter wrappedExporter) {
		return (DdlExporterWrapper)Proxy.newProxyInstance( 
				GenericExporterWrapperFactory.class.getClassLoader(), 
				new Class[] { DdlExporterWrapper.class }, 
				new DdlExporterInvocationHandler(wrappedExporter));
	}

	private static class DdlExporterInvocationHandler implements InvocationHandler {
		
		private DdlExporterWrapper exporterWrapper = null;
		
		private DdlExporterInvocationHandler(DdlExporter wrappedExporter) {
			this.exporterWrapper = new DdlExporterWrapperImpl(wrappedExporter);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return method.invoke(exporterWrapper, args);
		}
		
	}
	
	public static interface DdlExporterWrapper extends Wrapper {
		@Override DdlExporter getWrappedObject();
		default void setExport(boolean b) {
			getWrappedObject().getProperties().put(ExporterConstants.EXPORT_TO_DATABASE, b);
		}
		default Properties getProperties() {
			return getWrappedObject().getProperties();
		}
		
	}

	static class DdlExporterWrapperImpl implements DdlExporterWrapper {
		private DdlExporter delegateDdlExporter = null;
		private DdlExporterWrapperImpl(DdlExporter wrappedExporter) {
			delegateDdlExporter = wrappedExporter;
		}
		@Override 
		public DdlExporter getWrappedObject() {
			return delegateDdlExporter;
		}
	}

}
