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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.junit.jupiter.api.Test;

public class DummyMetadataBuildingContextTest {
	
	@Test
	public void testInstance() {
		assertNotNull(DummyMetadataBuildingContext.INSTANCE);
		StandardServiceRegistry serviceRegistry = DummyMetadataBuildingContext.INSTANCE
				.getBootstrapContext().getServiceRegistry();
		JdbcServices jdbcServices = serviceRegistry.getService(JdbcServices.class);
		Dialect dialect = jdbcServices.getDialect();
		assertTrue(dialect instanceof MockDialect);
	}
	
}
