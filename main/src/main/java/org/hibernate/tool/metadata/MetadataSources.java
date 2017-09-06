package org.hibernate.tool.metadata;

import java.util.Properties;

import org.hibernate.boot.Metadata;

public interface MetadataSources {
	
	Metadata buildMetadata();
	
	Properties getProperties();

}
