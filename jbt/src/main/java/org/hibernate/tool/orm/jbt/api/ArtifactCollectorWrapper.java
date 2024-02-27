package org.hibernate.tool.orm.jbt.api;

import java.io.File;
import java.util.Set;

import org.hibernate.tool.api.export.ArtifactCollector;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface ArtifactCollectorWrapper extends Wrapper {

	default Set<String> getFileTypes() { return ((ArtifactCollector)getWrappedObject()).getFileTypes(); }
	default void formatFiles() { ((ArtifactCollector)getWrappedObject()).formatFiles(); }
	default File[] getFiles(String string) { return ((ArtifactCollector)getWrappedObject()).getFiles(string); }

}
