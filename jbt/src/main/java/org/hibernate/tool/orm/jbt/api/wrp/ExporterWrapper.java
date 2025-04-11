/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 *
 * Copyright 2024-2025 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.tool.orm.jbt.api.wrp;

import java.io.File;
import java.io.StringWriter;
import java.util.Properties;

public interface ExporterWrapper extends Wrapper {
	
	void setConfiguration(ConfigurationWrapper configuration);
	
	void setArtifactCollector(ArtifactCollectorWrapper artifactCollectorWrapper);
	
	void setOutputDirectory(File dir);

	void setTemplatePath(String[] templatePath);

	void start();

	Properties getProperties();

	GenericExporterWrapper getGenericExporter();

	DdlExporterWrapper getHbm2DDLExporter();

	QueryExporterWrapper getQueryExporter();

	void setCustomProperties(Properties properties);

	void setOutput(StringWriter stringWriter);

}
