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
package org.hibernate.tool.maven;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.export.ExporterFactory;
import org.hibernate.tool.api.export.ExporterType;
import org.hibernate.tool.api.metadata.MetadataDescriptor;

import java.io.File;

import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_SOURCES;

/**
 * Mojo to generate Data Access Objects (DAOs) from an existing database.
 * <p>
 * See: https://docs.jboss.org/tools/latest/en/hibernatetools/html_single/#d0e4821
 */
@Mojo(name = "hbm2dao", defaultPhase = GENERATE_SOURCES)
public class GenerateDaoMojo extends AbstractGenerationMojo {

    /** The directory into which the DAOs will be generated. */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/")
    private File outputDirectory;

    /** Code will contain EJB 3 features, e.g. using annotations from javax.persistence
     * and org.hibernate.annotations. */
    @Parameter(defaultValue = "false")
    private boolean ejb3;
    
    /** Code will contain JDK 5 constructs such as generics and static imports. */
    @Parameter(defaultValue = "false")
    private boolean jdk5;

    /** A path used for looking up user-edited templates. */
    @Parameter
    private String templatePath;

    protected void executeExporter(MetadataDescriptor metadataDescriptor) {
        Exporter pojoExporter = ExporterFactory.createExporter(ExporterType.DAO);
        pojoExporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
        pojoExporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputDirectory);
        if (templatePath != null) {
            getLog().info("Setting template path to: " + templatePath);
            pojoExporter.getProperties().put(ExporterConstants.TEMPLATE_PATH, new String[] {templatePath});
        }
        pojoExporter.getProperties().setProperty("ejb3", String.valueOf(ejb3));
        pojoExporter.getProperties().setProperty("jdk5", String.valueOf(jdk5));
        getLog().info("Starting DAO export to directory: " + outputDirectory + "...");
        pojoExporter.start();
    }


}
