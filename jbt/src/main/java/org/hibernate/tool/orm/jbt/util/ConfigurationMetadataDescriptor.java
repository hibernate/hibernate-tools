package org.hibernate.tool.orm.jbt.util;

import java.util.Properties;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.api.metadata.MetadataDescriptor;

public class ConfigurationMetadataDescriptor implements MetadataDescriptor {
	
	private Configuration configuration = null;
	
	public ConfigurationMetadataDescriptor(Configuration configuration) {
		this.configuration = configuration;
	}
	
	@Override
	public Metadata createMetadata() {
		return MetadataHelper.getMetadata(configuration);
	}

	@Override
	public Properties getProperties() {
		return configuration.getProperties();
	}

}
