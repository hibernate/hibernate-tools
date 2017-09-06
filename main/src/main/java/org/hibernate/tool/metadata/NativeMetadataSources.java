package org.hibernate.tool.metadata;

import java.io.File;
import java.util.Properties;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.util.MetadataHelper;

public class NativeMetadataSources extends Configuration implements MetadataSources {
	
	public NativeMetadataSources(
			File cfgXmlFile, 
			File[] mappingFiles, 
			Properties properties) {
		if (cfgXmlFile != null) {
			configure(cfgXmlFile);
		}
		if (mappingFiles != null) {
			for (File file : mappingFiles) {
				if (file.getName().endsWith(".jar")) {
					addJar(file); 
				} else {
					addFile(file);
				}
			}
		}
		if (properties != null) {
			getProperties().putAll(properties);
		}
	}
	
	public Metadata buildMetadata() {
		return MetadataHelper.getMetadata(this);
	}

}
