package org.hibernate.tool.orm.jbt.api;

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
import org.hibernate.tool.orm.jbt.util.ConfigurationMetadataDescriptor;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface ExporterWrapper extends Wrapper {
	
	default void setConfiguration(Configuration configuration) {
		if (CfgExporter.class.isAssignableFrom(getWrappedObject().getClass())) {
			((CfgExporter)getWrappedObject()).setCustomProperties(configuration.getProperties());
		}
		((Exporter)getWrappedObject()).getProperties().put(
				ExporterConstants.METADATA_DESCRIPTOR, 
				new ConfigurationMetadataDescriptor(configuration));
	}
	
	default void setArtifactCollector(ArtifactCollectorWrapper artifactCollectorWrapper) {
		((Exporter)getWrappedObject()).getProperties().put(
				ExporterConstants.ARTIFACT_COLLECTOR, 
				artifactCollectorWrapper.getWrappedObject());
	}
	
	default void setOutputDirectory(File dir) {
		((Exporter)getWrappedObject()).getProperties().put(ExporterConstants.DESTINATION_FOLDER, dir);
	}

	default void setTemplatePath(String[] templatePath) {
		((Exporter)getWrappedObject()).getProperties().put(ExporterConstants.TEMPLATE_PATH, templatePath);
	}

	default void start() {
		((Exporter)getWrappedObject()).start();
	}

	default Properties getProperties() {
		return ((Exporter)getWrappedObject()).getProperties();
	}

	default GenericExporter getGenericExporter() {
		if (getWrappedObject() instanceof GenericExporter) {
			return (GenericExporter)getWrappedObject();
		} else {
			return null;
		}
	}

	default DdlExporter getHbm2DDLExporter() {
		if (getWrappedObject() instanceof DdlExporter) {
			return (DdlExporter)getWrappedObject();
		} else {
			return null;
		}
	}

	default QueryExporter getQueryExporter() {
		if (getWrappedObject() instanceof QueryExporter) {
			return (QueryExporter)getWrappedObject();
		} else {
			return null;
		}
	}

	default void setCustomProperties(Properties properties) {
		if (getWrappedObject() instanceof CfgExporter) {
			((CfgExporter)getWrappedObject()).setCustomProperties(properties);
		}
	}

	default void setOutput(StringWriter stringWriter) {
		if (getWrappedObject() instanceof CfgExporter) {
			((CfgExporter)getWrappedObject()).setOutput(stringWriter);
		}
	}

}
