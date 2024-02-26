package org.hibernate.tool.orm.jbt.api;

import java.io.File;
import java.util.Set;

import org.hibernate.tool.api.export.ArtifactCollector;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface ArtifactCollectorWrapper extends Wrapper {

	default ArtifactCollector getWrappedObject() { return (ArtifactCollector)getWrappedObject();}
	default Set<String> getFileTypes() { return getWrappedObject().getFileTypes(); }
	default void formatFiles() { getWrappedObject().formatFiles(); }
	default File[] getFiles(String string) { return getWrappedObject().getFiles(string); }

}
