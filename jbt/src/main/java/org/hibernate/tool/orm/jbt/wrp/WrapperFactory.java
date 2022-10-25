package org.hibernate.tool.orm.jbt.wrp;

import org.hibernate.tool.internal.export.common.DefaultArtifactCollector;

public class WrapperFactory {

	public Object createArtifactCollectorWrapper() {
		return new DefaultArtifactCollector();
	}

}
