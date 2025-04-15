/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 *
 * Copyright 2018-2025 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.tool.api.export;

import java.io.File;
import java.util.Set;

public interface ArtifactCollector {
	
	/**
	 * Called to inform that a file has been created by the exporter.
	 */
	void addFile(File file, String type);

	int getFileCount(String type);

	File[] getFiles(String type);

	Set<String> getFileTypes();

	void formatFiles();

}