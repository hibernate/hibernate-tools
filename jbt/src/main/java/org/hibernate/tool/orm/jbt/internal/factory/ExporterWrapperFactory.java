package org.hibernate.tool.orm.jbt.internal.factory;

import java.io.File;
import java.io.StringWriter;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.cfg.CfgExporter;
import org.hibernate.tool.internal.export.common.GenericExporter;
import org.hibernate.tool.internal.export.ddl.DdlExporter;
import org.hibernate.tool.internal.export.query.QueryExporter;
import org.hibernate.tool.orm.jbt.api.wrp.ArtifactCollectorWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ConfigurationWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.DdlExporterWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ExporterWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.GenericExporterWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.QueryExporterWrapper;
import org.hibernate.tool.orm.jbt.internal.wrp.AbstractWrapper;
import org.hibernate.tool.orm.jbt.util.ConfigurationMetadataDescriptor;
import org.hibernate.tool.orm.jbt.util.DummyMetadataDescriptor;
import org.hibernate.tool.orm.jbt.util.ReflectUtil;

public class ExporterWrapperFactory {
	
	public static ExporterWrapper createExporterWrapper(String className) {
		Exporter wrappedExporter = (Exporter)ReflectUtil.createInstance(className);
		return createExporterWrapper(wrappedExporter);
	}

	private static ExporterWrapper createExporterWrapper(final Exporter wrappedExporter) {
		return new ExporterWrapperImpl(wrappedExporter);
	}
	
	private static class ExporterWrapperImpl
			extends AbstractWrapper
			implements ExporterWrapper {
		
		private Exporter exporter = null;
		
		private ExporterWrapperImpl(Exporter exporter) {
			this.exporter = exporter;
			if (CfgExporter.class.isAssignableFrom(exporter.getClass())) {
				exporter.getProperties().put(
						ExporterConstants.METADATA_DESCRIPTOR, 
						new DummyMetadataDescriptor());
			} else {
				exporter.getProperties().put(
						ExporterConstants.METADATA_DESCRIPTOR,
						new ConfigurationMetadataDescriptor(new Configuration()));
			}
		}
		
		@Override 
		public Exporter getWrappedObject() { 
			return exporter;
		}
		
		@Override
		public void setConfiguration(ConfigurationWrapper configuration) {
			if (CfgExporter.class.isAssignableFrom(exporter.getClass())) {
				((CfgExporter)exporter).setCustomProperties(configuration.getProperties());
			}
			exporter.getProperties().put(
					ExporterConstants.METADATA_DESCRIPTOR, 
					new ConfigurationMetadataDescriptor((Configuration)configuration.getWrappedObject()));
		}
		
		@Override
		public void setArtifactCollector(ArtifactCollectorWrapper artifactCollectorWrapper) {
			exporter.getProperties().put(
					ExporterConstants.ARTIFACT_COLLECTOR, 
					artifactCollectorWrapper.getWrappedObject());
		}
		
		@Override
		public void setOutputDirectory(File dir) {
			exporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, dir);
		}

		@Override
		public void setTemplatePath(String[] templatePath) {
			exporter.getProperties().put(ExporterConstants.TEMPLATE_PATH, templatePath);
		}

		@Override 
		public void start() {
			exporter.start();
		}

		@Override
		public Properties getProperties() {
			return exporter.getProperties();
		}

		@Override
		public GenericExporterWrapper getGenericExporter() {
			if (exporter instanceof GenericExporter) {
				return GenericExporterWrapperFactory.createGenericExporterWrapper((GenericExporter)exporter);
			} else {
				return null;
			}
		}

		@Override
		public DdlExporterWrapper getHbm2DDLExporter() {
			if (exporter instanceof DdlExporter) {
				return DdlExporterWrapperFactory.createDdlExporterWrapper((DdlExporter)exporter);
			} else {
				return null;
			}
		}

		@Override
		public QueryExporterWrapper getQueryExporter() {
			if (exporter instanceof QueryExporter) {
				return QueryExporterWrapperFactory.createQueryExporterWrapper((QueryExporter)exporter);
			} else {
				return null;
			}
		}

		@Override
		public void setCustomProperties(Properties properties) {
			if (exporter instanceof CfgExporter) {
				((CfgExporter)exporter).setCustomProperties(properties);
			}
		}

		@Override
		public void setOutput(StringWriter stringWriter) {
			if (exporter instanceof CfgExporter) {
				((CfgExporter)exporter).setOutput(stringWriter);
			}
		}		
		
	}
	
}
