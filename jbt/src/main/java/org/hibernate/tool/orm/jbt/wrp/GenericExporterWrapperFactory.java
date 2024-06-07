package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.common.GenericExporter;

public class GenericExporterWrapperFactory {

	public static GenericExporterWrapper create(GenericExporter wrappedExporter) {
		return (GenericExporterWrapper)Proxy.newProxyInstance( 
				GenericExporterWrapperFactory.class.getClassLoader(), 
				new Class[] { GenericExporterWrapper.class }, 
				new GenericExporterInvocationHandler(wrappedExporter));
	}
	
	private static class GenericExporterInvocationHandler implements InvocationHandler {
		
		private GenericExporterWrapper exporterWrapper = null;
		
		private GenericExporterInvocationHandler(GenericExporter wrappedExporter) {
			this.exporterWrapper = new GenericExporterWrapperImpl(wrappedExporter);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return method.invoke(exporterWrapper, args);
		}
		
	}
	
	public static interface GenericExporterWrapper extends Wrapper {
		@Override GenericExporter getWrappedObject();
		default void setFilePattern(String filePattern) {
			getWrappedObject().getProperties().setProperty(ExporterConstants.FILE_PATTERN, filePattern);
		}
		default void setTemplateName(String templateName) {
			getWrappedObject().getProperties().setProperty(ExporterConstants.TEMPLATE_NAME, templateName);
		}
		default void setForEach(String forEach) {
			getWrappedObject().getProperties().setProperty(ExporterConstants.FOR_EACH, forEach);
		}
		default String getFilePattern() {
			return getWrappedObject().getProperties().getProperty(ExporterConstants.FILE_PATTERN);
		}
		default String getTemplateName() {
			return getWrappedObject().getProperties().getProperty(ExporterConstants.TEMPLATE_NAME);
		}
	}
	
	static class GenericExporterWrapperImpl implements GenericExporterWrapper {
		private GenericExporter delegateGenericExporter = null;
		private GenericExporterWrapperImpl(GenericExporter wrappedExporter) {
			delegateGenericExporter = wrappedExporter;
		}
		@Override 
		public GenericExporter getWrappedObject() {
			return delegateGenericExporter;
		}
	}

}
