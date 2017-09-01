package org.hibernate.tool.metadata;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.util.MetadataHelper;

public class NativeMetadataSources extends Configuration implements MetadataSources {
	
	public Metadata buildMetadata() {
		return MetadataHelper.getMetadata(this);
	}

}
