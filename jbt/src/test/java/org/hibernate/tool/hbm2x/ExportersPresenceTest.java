/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 *
 * Copyright 2024-2025 Red Hat, Inc.
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
package org.hibernate.tool.hbm2x;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class ExportersPresenceTest {
	
	@Test
	public void testHbm2DDLExporter() {
		try {
			ClassLoader cl = getClass().getClassLoader();
			Class<?> ddlExporterClass = cl.loadClass("org.hibernate.tool.hbm2x.Hbm2DDLExporter");
			assertNotNull(ddlExporterClass);
		} catch (Throwable t) {
			fail(t);
		}
	}

	@Test
	public void testPOJOExporter() {
		try {
			ClassLoader cl = getClass().getClassLoader();
			Class<?> pojoExporterClass = cl.loadClass("org.hibernate.tool.hbm2x.POJOExporter");
			assertNotNull(pojoExporterClass);
		} catch (Throwable t) {
			fail(t);
		}
	}

	@Test
	public void testHibernateMappingExporter() {
		try {
			ClassLoader cl = getClass().getClassLoader();
			Class<?> hibernateMappingExporterClass = cl.loadClass("org.hibernate.tool.hbm2x.HibernateMappingExporter");
			assertNotNull(hibernateMappingExporterClass);
		} catch (Throwable t) {
			fail(t);
		}
	}

	@Test
	public void testDAOExporter() {
		try {
			ClassLoader cl = getClass().getClassLoader();
			Class<?> daoExporterClass = cl.loadClass("org.hibernate.tool.hbm2x.DAOExporter");
			assertNotNull(daoExporterClass);
		} catch (Throwable t) {
			fail(t);
		}
	}

	@Test
	public void testGenericExporter() {
		try {
			ClassLoader cl = getClass().getClassLoader();
			Class<?> genericExporterClass = cl.loadClass("org.hibernate.tool.hbm2x.GenericExporter");
			assertNotNull(genericExporterClass);
		} catch (Throwable t) {
			fail(t);
		}
	}

	@Test
	public void testHibernateConfigurationExporter() {
		try {
			ClassLoader cl = getClass().getClassLoader();
			Class<?> hibernateConfigurationExporterClass = cl.loadClass("org.hibernate.tool.hbm2x.HibernateConfigurationExporter");
			assertNotNull(hibernateConfigurationExporterClass);
		} catch (Throwable t) {
			fail(t);
		}
	}

	@Test
	public void testQueryExporter() {
		try {
			ClassLoader cl = getClass().getClassLoader();
			Class<?> hibernateConfigurationExporterClass = cl.loadClass("org.hibernate.tool.hbm2x.QueryExporter");
			assertNotNull(hibernateConfigurationExporterClass);
		} catch (Throwable t) {
			fail(t);
		}
	}

	@Test
	public void testDocExporter() {
		try {
			ClassLoader cl = getClass().getClassLoader();
			Class<?> docExporterClass = cl.loadClass("org.hibernate.tool.hbm2x.DocExporter");
			assertNotNull(docExporterClass);
		} catch (Throwable t) {
			fail(t);
		}
	}

}
