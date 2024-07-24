/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2016-2020 Red Hat, Inc.
 *
 * Licensed under the GNU Lesser General Public License (LGPL), 
 * version 2.1 or later (the "License").
 * You may not use this file except in compliance with the License.
 * You may read the licence in the 'lgpl.txt' file in the root folder of 
 * project or obtain a copy at
 *
 *     http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.mvn;

import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_SOURCES;

import java.io.File;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;

/**
 * Mojo to generate hbm.xml files from an existing database.
 * <p>
 * See: https://docs.jboss.org/tools/latest/en/hibernatetools/html_single/#d0e4821
 */
@Mojo(name = "generateHbm", defaultPhase = GENERATE_SOURCES)
public class GenerateHbmMojo extends AbstractHbm2xMojo {

    /** The directory into which the DAOs will be generated. */
    @Parameter(defaultValue = "${project.basedir}/src/main/resources")
    private File outputDirectory;

    @Parameter
    private String templatePath;

    protected void executeExporter(MetadataDescriptor metadataDescriptor) {
    	try {
    		HibernateMappingExporter hbmExporter = new HibernateMappingExporter();
	        hbmExporter.setMetadataDescriptor(metadataDescriptor);
	        hbmExporter.setOutputDirectory(outputDirectory);
	        if (templatePath != null) {
	            getLog().info("Setting template path to: " + templatePath);
	            hbmExporter.setTemplatePath(new String[] {templatePath});
	        }
	        getLog().info("Starting HBM export to directory: " + outputDirectory + "...");
	        hbmExporter.start();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }


}
