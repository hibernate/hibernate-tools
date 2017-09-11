/*
 * Created on 2004-12-01
 */
package org.hibernate.tool.hbm2x;

import java.io.File;
import java.util.Properties;

import org.hibernate.boot.Metadata;
import org.hibernate.tool.metadata.MetadataDescriptor;

/**
 * @author max and david
 * @author koen
 */
public interface Exporter {
	
	/** 
	 * @param metadataSources An Hibernate {@link org.hibernate.tool.metadata.MetadataDescriptor} or subclass instance that defines the hibernate meta model to be exported.
	 */
	public void setMetadataSources(MetadataDescriptor metadataSources);
		

	public Metadata getMetadata();
	
	/**
	 * @param file basedirectory to be used for generated files.
	 */
	public void setOutputDirectory(File file);

	public File getOutputDirectory();
	
	/**
	 * @param templatePath array of directories used sequentially to lookup templates
	 */
	public void setTemplatePath(String[] templatePath);
	
	public String[] getTemplatePath();
		
	public Properties getProperties();
	
	/**
	 * 
	 * @param collector Instance to be consulted when adding a new file.
	 */
	public void setArtifactCollector(ArtifactCollector collector);
	
	/**
	 * 
	 * @return artifact collector
	 */
	public ArtifactCollector getArtifactCollector();
	
	/**
	 * Called when exporter should start generating its output
	 */
	public void start();
	
}
