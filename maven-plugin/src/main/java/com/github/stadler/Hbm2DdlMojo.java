package com.github.stadler;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.hibernate.tool.util.MetadataHelper;

import java.io.File;
import java.util.EnumSet;
import java.util.Set;

import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_RESOURCES;

/**
 * Mojo to generate DDL Scripts from an existing database.
 * <p>
 * See https://docs.jboss.org/tools/latest/en/hibernatetools/html_single/#d0e4651
 */
@Mojo(name = "hbm2ddl", defaultPhase = GENERATE_RESOURCES)
public class Hbm2DdlMojo extends AbstractHbm2xMojo {

    @Parameter(defaultValue = "${project.build.directory}/generated-resources/")
    private File outputDirectory;
    @Parameter(defaultValue = "schema.ddl")
    private String outputFileName;
    @Parameter(defaultValue = "SCRIPT")
    private Set<TargetType> targetTypes;
    @Parameter(defaultValue = "CREATE")
    private SchemaExport.Action schemaExportAction;
    @Parameter(defaultValue = ";")
    private String delimiter;
    @Parameter(defaultValue = "true")
    private boolean format;
    @Parameter(defaultValue = "true")
    private boolean haltOnError;


    @Override
    protected void executeExporter(Configuration cfg) {
        Metadata metadata = MetadataHelper.getMetadata(cfg);

        SchemaExport export = new SchemaExport();
        export.setOutputFile(new File(outputDirectory, outputFileName).toString());
        export.setDelimiter(delimiter);
        export.setHaltOnError(haltOnError);
        export.setFormat(format);
        export.execute(EnumSet.copyOf(this.targetTypes), schemaExportAction, metadata);
    }
}
