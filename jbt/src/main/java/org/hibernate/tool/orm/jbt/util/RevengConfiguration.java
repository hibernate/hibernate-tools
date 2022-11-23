package org.hibernate.tool.orm.jbt.util;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.api.metadata.MetadataConstants;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.api.reveng.RevengStrategy;

public class RevengConfiguration {

	Properties properties = new Properties();
	RevengStrategy revengStrategy;
	Metadata metadata;

	public Properties getProperties() {
		return properties;
	}
	
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	public Object getProperty(String key) {
		return this.properties.get(key);
	}
	
	public void setProperty(String key, String value) {
		properties.put(key, value);
	}

	public void addProperties(Properties properties) {
		this.properties.putAll(properties);
	}

	public Object getReverseEngineeringStrategy() {
		return revengStrategy;
	}

	public void setReverseEngineeringStrategy(RevengStrategy strategy) {
		this.revengStrategy = strategy;
	}

	public boolean preferBasicCompositeIds() {
		Object preferBasicCompositeIds = properties.get(MetadataConstants.PREFER_BASIC_COMPOSITE_IDS);
		return preferBasicCompositeIds == null ? true : ((Boolean)preferBasicCompositeIds).booleanValue();
	}

	public void setPreferBasicCompositeIds(boolean preferBasicCompositeIds) {
		properties.put(
				MetadataConstants.PREFER_BASIC_COMPOSITE_IDS, 
				Boolean.valueOf(preferBasicCompositeIds));
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public void readFromJDBC() {
		metadata = MetadataDescriptorFactory
				.createReverseEngineeringDescriptor(revengStrategy, properties)
				.createMetadata();
	}
	
	public Iterator<PersistentClass> getClassMappings() {
		if (metadata != null) {
			return metadata.getEntityBindings().iterator();
		} else {
			return Collections.emptyIterator();
		}
	}
	
	public Configuration addFile(File file) {
		throw new RuntimeException(
				"Method 'addFile' should not be called on instances of " +
				this.getClass().getName());
	}
		
}
