package org.hibernate.tool.orm.jbt.api;

import java.io.File;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.cfg.CfgExporter;
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

}
