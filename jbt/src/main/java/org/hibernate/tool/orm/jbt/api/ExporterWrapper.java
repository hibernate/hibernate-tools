package org.hibernate.tool.orm.jbt.api;

import java.io.File;
import java.io.StringWriter;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.internal.export.common.GenericExporter;
import org.hibernate.tool.internal.export.ddl.DdlExporter;
import org.hibernate.tool.internal.export.query.QueryExporter;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface ExporterWrapper extends Wrapper {
	
	void setConfiguration(Configuration configuration);
	
	void setArtifactCollector(ArtifactCollectorWrapper artifactCollectorWrapper);
	
	void setOutputDirectory(File dir);

	void setTemplatePath(String[] templatePath);

	void start();

	Properties getProperties();

	GenericExporter getGenericExporter();

	DdlExporter getHbm2DDLExporter();

	QueryExporter getQueryExporter();

	void setCustomProperties(Properties properties);

	void setOutput(StringWriter stringWriter);

}
