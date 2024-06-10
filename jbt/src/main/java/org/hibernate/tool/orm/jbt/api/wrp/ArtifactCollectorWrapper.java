package org.hibernate.tool.orm.jbt.api.wrp;

import java.io.File;
import java.util.Set;

public interface ArtifactCollectorWrapper extends Wrapper {

	Set<String> getFileTypes();
	void formatFiles();
	File[] getFiles(String string);

}
