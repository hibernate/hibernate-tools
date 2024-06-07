package org.hibernate.tool.orm.jbt.internal.factory;

import java.io.File;
import java.util.Set;

import org.hibernate.tool.api.export.ArtifactCollector;
import org.hibernate.tool.internal.export.common.DefaultArtifactCollector;
import org.hibernate.tool.orm.jbt.api.wrp.ArtifactCollectorWrapper;

public class ArtifactCollectorWrapperFactory {
	
	public static ArtifactCollectorWrapper createArtifactCollectorWrapper() {
		return new ArtifactCollectorWrapperImpl();
	}
	
	private static class ArtifactCollectorWrapperImpl implements ArtifactCollectorWrapper {
		
		private ArtifactCollector wrappedArtifactCollector = new DefaultArtifactCollector();
		
		@Override 
		public ArtifactCollector getWrappedObject() { 
			return wrappedArtifactCollector; 
		}

		@Override
		public Set<String> getFileTypes() { 
			return ((ArtifactCollector)getWrappedObject()).getFileTypes(); 
		}

		@Override
		public void formatFiles() { 
			((ArtifactCollector)getWrappedObject()).formatFiles(); 
		}
		
		@Override
		public File[] getFiles(String string) { 
			return ((ArtifactCollector)getWrappedObject()).getFiles(string); 
		}
	}
	
}
