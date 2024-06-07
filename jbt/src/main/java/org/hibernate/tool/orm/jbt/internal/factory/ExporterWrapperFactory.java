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
import org.hibernate.tool.orm.jbt.api.wrp.ExporterWrapper;
import org.hibernate.tool.orm.jbt.util.ConfigurationMetadataDescriptor;
import org.hibernate.tool.orm.jbt.util.DummyMetadataDescriptor;

public class ExporterWrapperFactory {

	public static ExporterWrapper createExporterWrapper(final Exporter wrappedExporter) {
		return new ExporterWrapperImpl(wrappedExporter);
	}
	
	private static class ExporterWrapperImpl implements ExporterWrapper {
		
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
		public ExporterWrapper getGenericExporter() {
			if (exporter instanceof GenericExporter) {
				return this;
			} else {
				return null;
			}
		}

		@Override
		public ExporterWrapper getHbm2DDLExporter() {
			if (exporter instanceof DdlExporter) {
				return this;
			} else {
				return null;
			}
		}

		@Override
		public ExporterWrapper getQueryExporter() {
			if (exporter instanceof QueryExporter) {
				return this;
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
