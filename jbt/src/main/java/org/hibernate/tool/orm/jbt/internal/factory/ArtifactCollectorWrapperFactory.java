package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.tool.api.export.ArtifactCollector;
import org.hibernate.tool.internal.export.common.DefaultArtifactCollector;
import org.hibernate.tool.orm.jbt.api.ArtifactCollectorWrapper;

public class ArtifactCollectorWrapperFactory {
	
	public static ArtifactCollectorWrapper createArtifactCollectorWrapper() {
		ArtifactCollector wrappedArtifactCollector = new DefaultArtifactCollector();
		return new ArtifactCollectorWrapper() {
			@Override public ArtifactCollector getWrappedObject() { return wrappedArtifactCollector; }
		};
	}
	
}
