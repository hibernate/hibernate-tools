package org.hibernate.mvn;

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
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;

public abstract class AbstractHbm2xMojo extends AbstractMojo {

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
    @Parameter(defaultValue = "org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy")
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
	        ReverseEngineeringStrategy strategy = setupReverseEngineeringStrategy();
            executeExporter(createJdbcDescriptor(strategy, loadPropertiesFile()));
	        getLog().info("Finished " + this.getClass().getSimpleName() + "!");
    	} finally {
    		Thread.currentThread().setContextClassLoader(original);
    	}
    }

    private ReverseEngineeringStrategy setupReverseEngineeringStrategy() throws MojoFailureException {
        ReverseEngineeringStrategy strategy;
        try {
            strategy = ReverseEngineeringStrategy.class.cast(Class.forName(revengStrategy).newInstance());
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | ClassCastException e) {
            throw new MojoFailureException(revengStrategy + " not instanced.", e);
        }

        if (revengFile != null) {
            OverrideRepository override = new OverrideRepository();
            override.addFile(revengFile);
            strategy = override.getReverseEngineeringStrategy(strategy);
        }

        ReverseEngineeringSettings settings =
                new ReverseEngineeringSettings(strategy)
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

    private MetadataDescriptor createJdbcDescriptor(ReverseEngineeringStrategy strategy, Properties properties) {
        return MetadataDescriptorFactory
                .createJdbcDescriptor(
                        strategy,
                        properties,
                        preferBasicCompositeIds);
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
