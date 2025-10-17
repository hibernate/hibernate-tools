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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.api.metadata.MetadataConstants;
import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.api.reveng.RevengStrategyFactory;

public abstract class AbstractGenerationMojo extends AbstractMojo {

    // For reveng strategy
    /** The default package name to use when mappings for classes are created. */
    @Parameter
    private String packageName;
   
    /** The name of a property file, e.g. hibernate.properties. */
    @Parameter
    private File revengFile;
    
    /** The class name of the reverse engineering strategy to use.
     * Extend the DefaultReverseEngineeringStrategy and override the corresponding methods, e.g.
     * to adapt the generate class names or to provide custom type mappings. */
    @Parameter
    private String revengStrategy;

    /** If true, tables which are pure many-to-many link tables will be mapped as such.
     * A pure many-to-many table is one which primary-key contains exactly two foreign-keys pointing
     * to other entity tables and has no other columns. */
    @Parameter(defaultValue = "true")
    private boolean detectManyToMany;

    /** If true, a one-to-one association will be created for each foreignkey found. */
    @Parameter(defaultValue = "true")
    private boolean detectOneToOne;

    /** If true, columns named VERSION or TIMESTAMP with appropriate types will be mapped with the appropriate
     * optimistic locking corresponding to &lt;version&gt; or &lt;timestamp&gt;. */
    @Parameter(defaultValue = "true")
    private boolean detectOptimisticLock;

    /** If true, a collection will be mapped for each foreignkey. */
    @Parameter(defaultValue = "true")
    private boolean createCollectionForForeignKey;

    /** If true, a many-to-one association will be created for each foreignkey found. */
    @Parameter(defaultValue = "true")
    private boolean createManyToOneForForeignKey;

    // For configuration
    /** The name of a property file, e.g. hibernate.properties. */
    @Parameter(defaultValue = "${project.basedir}/src/main/resources/hibernate.properties")
    private File propertyFile;

    // Not exposed for now
    private boolean preferBasicCompositeIds = true;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    public void execute() throws MojoFailureException {
    	ClassLoader original = Thread.currentThread().getContextClassLoader();
    	try {
    		Thread.currentThread().setContextClassLoader(createExporterClassLoader(original));
	        getLog().info("Starting " + this.getClass().getSimpleName() + "...");
	        RevengStrategy strategy = setupReverseEngineeringStrategy();
            executeExporter(createJdbcDescriptor(strategy, loadPropertiesFile()));
	        getLog().info("Finished " + this.getClass().getSimpleName() + "!");
    	} finally {
    		Thread.currentThread().setContextClassLoader(original);
    	}
    }

    private RevengStrategy setupReverseEngineeringStrategy() {
    	File[] revengFiles = null;
    	if (revengFile != null) {
    		revengFiles = new File[] { revengFile };
    	}
        RevengStrategy strategy = 
        		RevengStrategyFactory.createReverseEngineeringStrategy(
        				revengStrategy, 
        				revengFiles);
        RevengSettings settings =
                new RevengSettings(strategy)
                        .setDefaultPackageName(packageName)
                        .setDetectManyToMany(detectManyToMany)
                        .setDetectOneToOne(detectOneToOne)
                        .setDetectOptimisticLock(detectOptimisticLock)
                        .setCreateCollectionForForeignKey(createCollectionForForeignKey)
                        .setCreateManyToOneForForeignKey(createManyToOneForForeignKey);
        strategy.setSettings(settings);
        return strategy;
    }

    private Properties loadPropertiesFile() throws MojoFailureException {
        try (FileInputStream is = new FileInputStream(propertyFile)) {
            Properties result = new Properties();
            result.load(is);
            return result;
        } catch (FileNotFoundException e) {
            getLog().error("Property file '" + propertyFile + "' cannot be found, aborting...");
            throw new MojoFailureException(propertyFile + " not found.", e);
        } catch (IOException e) {
            getLog().error("Property file '" + propertyFile + "' cannot be loaded, aborting...");
            throw new MojoFailureException("Problem while loading " + propertyFile, e);
        }
    }

    private MetadataDescriptor createJdbcDescriptor(RevengStrategy strategy, Properties properties) {
    	properties.put(MetadataConstants.PREFER_BASIC_COMPOSITE_IDS, preferBasicCompositeIds);
        return MetadataDescriptorFactory
                .createReverseEngineeringDescriptor(
                        strategy,
                        properties);
    }
    
    private ClassLoader createExporterClassLoader(ClassLoader parent) {
    	ArrayList<URL> urls = new ArrayList<URL>();
    	try {
			for (String cpe : project.getRuntimeClasspathElements()) {
				urls.add(new File(cpe).toURI().toURL());
			}
		} catch (DependencyResolutionRequiredException | MalformedURLException e) {
			throw new RuntimeException("Problem while constructing project classloader", e);
		}
    	return new URLClassLoader(urls.toArray(new URL[0]), parent);
    }

    protected abstract void executeExporter(MetadataDescriptor metadataDescriptor);
}
