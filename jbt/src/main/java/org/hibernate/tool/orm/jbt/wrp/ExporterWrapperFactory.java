package org.hibernate.tool.orm.jbt.wrp;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.api.export.ArtifactCollector;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.cfg.CfgExporter;
import org.hibernate.tool.internal.export.common.GenericExporter;
import org.hibernate.tool.internal.export.ddl.DdlExporter;
import org.hibernate.tool.orm.jbt.util.ConfigurationMetadataDescriptor;
import org.hibernate.tool.orm.jbt.util.ReflectUtil;
import org.hibernate.tool.orm.jbt.wrp.DdlExporterWrapperFactory.DdlExporterWrapper;
import org.hibernate.tool.orm.jbt.wrp.GenericExporterWrapperFactory.GenericExporterWrapper;

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
	
	static interface ExporterWrapper extends Wrapper {
		@Override Exporter getWrappedObject();
		default void setConfiguration(Configuration configuration) {
			if (CfgExporter.class.isAssignableFrom(getWrappedObject().getClass())) {
				((CfgExporter)getWrappedObject()).setCustomProperties(configuration.getProperties());
			}
			getProperties().put(
					ExporterConstants.METADATA_DESCRIPTOR, 
					new ConfigurationMetadataDescriptor(configuration));
		}
		default void setArtifactCollector(ArtifactCollector artifactCollector) {
			getProperties().put(ExporterConstants.ARTIFACT_COLLECTOR, artifactCollector);
		}
		default void setOutputDirectory(File dir) {
			getProperties().put(ExporterConstants.DESTINATION_FOLDER, dir);
		}
		default void setTemplatePath(String[] templatePath) {
			getProperties().put(ExporterConstants.TEMPLATE_PATH, templatePath);
		}
		default void start() {
			getWrappedObject().start();
		}
		default Properties getProperties() {
			return getWrappedObject().getProperties();
		}
		default GenericExporterWrapper getGenericExporter() {
			if (getWrappedObject() instanceof GenericExporter) {
				return GenericExporterWrapperFactory.create((GenericExporter)getWrappedObject());
			} else {
				return null;
			}
		}
		default DdlExporterWrapper getHbm2DDLExporter() {
			if (getWrappedObject() instanceof DdlExporter) {
				return DdlExporterWrapperFactory.create((DdlExporter)getWrappedObject());
			} else {
				return null;
			}
		}
	}
	
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
