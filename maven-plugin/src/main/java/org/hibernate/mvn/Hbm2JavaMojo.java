package org.hibernate.mvn;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.hbm2x.POJOExporter;

import java.io.File;

import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_SOURCES;

/**
 * Mojo to generate Java JPA Entities from an existing database.
 * <p>
 * See: https://docs.jboss.org/tools/latest/en/hibernatetools/html_single/#d0e4821
 */
@Mojo(
		name = "hbm2java", 
		defaultPhase = GENERATE_SOURCES, 
		requiresDependencyResolution = ResolutionScope.RUNTIME)
public class Hbm2JavaMojo extends AbstractHbm2xMojo {

    /** The directory into which the JPA entities will be generated. */
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
        POJOExporter pojoExporter = new POJOExporter();
        pojoExporter.setMetadataDescriptor(metadataDescriptor);
        pojoExporter.setOutputDirectory(outputDirectory);
        if (templatePath != null) {
            getLog().info("Setting template path to: " + templatePath);
            pojoExporter.setTemplatePath(new String[]{templatePath});
        }
        pojoExporter.getProperties().setProperty("ejb3", String.valueOf(ejb3));
        pojoExporter.getProperties().setProperty("jdk5", String.valueOf(jdk5));
        getLog().info("Starting POJO export to directory: " + outputDirectory + "...");
        pojoExporter.start();
    }


}
