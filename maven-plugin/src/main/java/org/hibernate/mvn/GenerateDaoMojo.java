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
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.hbm2x.DAOExporter;

/**
 * Mojo to generate Data Access Objects (DAOs) from an existing database.
 * <p>
 * See: https://docs.jboss.org/tools/latest/en/hibernatetools/html_single/#d0e4821
 */
@Mojo(
		name = "hbm2dao", 
		defaultPhase = GENERATE_SOURCES,
		requiresDependencyResolution = ResolutionScope.RUNTIME)
public class GenerateDaoMojo extends AbstractHbm2xMojo {

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
        DAOExporter daoExporter = new DAOExporter();
        daoExporter.setMetadataDescriptor(metadataDescriptor);
        daoExporter.setOutputDirectory(outputDirectory);
        if (templatePath != null) {
            getLog().info("Setting template path to: " + templatePath);
            daoExporter.setTemplatePath(new String[]{templatePath});
        }
        daoExporter.getProperties().setProperty("ejb3", String.valueOf(ejb3));
        daoExporter.getProperties().setProperty("jdk5", String.valueOf(jdk5));
        getLog().info("Starting DAO export to directory: " + outputDirectory + "...");
        daoExporter.start();
    }


}
