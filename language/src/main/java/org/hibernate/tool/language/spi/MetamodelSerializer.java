/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 *
 * Copyright 2023-2025 Red Hat, Inc.
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
package org.hibernate.tool.language.spi;

import jakarta.persistence.metamodel.Metamodel;

/**
 * Contract used to provide the LLM with a textual representation of the
 * Hibernate metamodel, that is, the classes and mapping information
 * that constitute the persistence layer.
 */
public interface MetamodelSerializer {
	/**
	 * Utility method that generates a textual representation of the mapping information
	 * contained in the provided {@link Metamodel metamodel} instance. The representation
	 * does not need to follow a strict scheme, and is more akin to natural language,
	 * as it's mainly meant for consumption by a LLM.
	 *
	 * @param metamodel the metamodel instance containing information on the persistence structures
	 *
	 * @return the textual representation of the provided {@link Metamodel metamodel}
	 */
	String toString(Metamodel metamodel);
}
