package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.orm.jbt.util.ReflectUtil;

public class ExporterWrapperFactory {
	
	public static ExporterWrapper create(String className) {
		return (ExporterWrapper)Proxy.newProxyInstance( 
				ExporterWrapperFactory.class.getClassLoader(), 
				new Class[] { ExporterWrapper.class }, 
				new ExporterInvocationHandler(className));
	}
	
	private static class ExporterInvocationHandler implements InvocationHandler {
		
		private ExporterWrapper exporterWrapper = null;
		
		private ExporterInvocationHandler(String className) {
			this.exporterWrapper = new ExporterWrapperImpl(className);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return method.invoke(exporterWrapper, args);
		}
		
	}
	
	static interface ExporterWrapper extends Wrapper {}
	
	static class ExporterWrapperImpl implements ExporterWrapper {
		private Exporter delegateExporter = null;
		private ExporterWrapperImpl(String className) {
			delegateExporter = (Exporter)ReflectUtil.createInstance(className);
		}
		@Override 
		public Exporter getWrappedObject() {
			return delegateExporter;
		}
	}

}
