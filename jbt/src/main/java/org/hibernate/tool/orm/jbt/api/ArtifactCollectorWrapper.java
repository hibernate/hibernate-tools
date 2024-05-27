package org.hibernate.tool.orm.jbt.api;

import java.io.File;
import java.util.Set;

import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface ArtifactCollectorWrapper extends Wrapper {

	Set<String> getFileTypes();
	void formatFiles();
	File[] getFiles(String string);

}
