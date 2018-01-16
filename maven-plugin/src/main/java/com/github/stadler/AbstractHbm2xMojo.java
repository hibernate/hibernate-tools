package com.github.stadler;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;

import java.io.File;

public abstract class AbstractHbm2xMojo extends AbstractMojo {

    // For reveng strategy
    @Parameter
    private String packageName;
    @Parameter
    private File revengFile;
    @Parameter(defaultValue = "true")
    private boolean detectManyToMany;
    @Parameter(defaultValue = "true")
    private boolean detectOneToOne;
    @Parameter(defaultValue = "true")
    private boolean detectOptimisticLock;
    @Parameter(defaultValue = "true")
    private boolean createCollectionForForeignKey;
    @Parameter(defaultValue = "true")
    private boolean createManyToOneForForeignKey;

    // For configuration
    @Parameter(defaultValue = "${project.basedir}/src/main/hibernate/hibernate.cfg.xml")
    private File configFile;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Starting " + this.getClass().getSimpleName() + "...");
        ReverseEngineeringStrategy strategy = setupReverseEngineeringStrategy();
        JDBCMetaDataConfiguration cfg = setupJdbcMetaDataConfiguration(strategy);
        executeExporter(cfg);
        getLog().info("Finished " + this.getClass().getSimpleName() + "!");
    }

    private ReverseEngineeringStrategy setupReverseEngineeringStrategy() {
        ReverseEngineeringStrategy strategy = new DefaultReverseEngineeringStrategy();

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

    private JDBCMetaDataConfiguration setupJdbcMetaDataConfiguration(ReverseEngineeringStrategy strategy) {
        JDBCMetaDataConfiguration cfg = new JDBCMetaDataConfiguration();
        cfg.setReverseEngineeringStrategy(strategy);

        getLog().info("Configuring using file: " + configFile + "...");
        cfg.configure(configFile);

        getLog().info("Reading from JDBC...");
        cfg.readFromJDBC();
        return cfg;
    }

    protected abstract void executeExporter(Configuration cfg);
}
