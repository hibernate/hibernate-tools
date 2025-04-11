/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 *
 * Copyright 2022-2025 Red Hat, Inc.
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
package org.hibernate.tool.orm.jbt.internal.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;
import org.hibernate.jpa.boot.spi.PersistenceXmlParser;

public class JpaMappingFileHelper {

	public static List<String> findMappingFiles(String persistenceUnitName) {
		List<String> result = new ArrayList<String>();
		Collection<PersistenceUnitDescriptor> persistenceUnits = locatePersistenceUnits();
		for (PersistenceUnitDescriptor descriptor : persistenceUnits) {
			if (descriptor.getName().equals(persistenceUnitName)) {
				result.addAll(descriptor.getMappingFileNames());
			}
		}
		return result;
	}
	
	private static Collection<PersistenceUnitDescriptor> locatePersistenceUnits() {
		final Collection<PersistenceUnitDescriptor> units;
		try {
			var parser = PersistenceXmlParser.create();
			final List<URL> xmlUrls = parser.getClassLoaderService().locateResources( "META-INF/persistence.xml" );
			if ( xmlUrls.isEmpty() ) {
				units = List.of();
			}
			else {
				units = parser.parse( xmlUrls ).values();
			}
		}
		catch (Exception e) {
			throw new RuntimeException( "Unable to locate persistence units", e );
		}
		return units;
	}
}
