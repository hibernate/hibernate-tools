/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 *
 * Copyright 2004-2025 Red Hat, Inc.
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
package org.hibernate.tool.cfg.JDBCMetaDataConfiguration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.boot.Metadata;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author max
 * @author koen
 */
public class TestCase {

	@BeforeEach
	public void setUp() {
		JdbcUtil.createDatabase(this);
	}

	@AfterEach
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	@Test
	public void testReadFromJDBC() throws Exception {
		Metadata metadata = MetadataDescriptorFactory
				.createReverseEngineeringDescriptor(null, null)
				.createMetadata();
		assertNotNull(metadata.getEntityBinding("WithRealTimestamp"), "WithRealTimestamp");
		assertNotNull(metadata.getEntityBinding("NoVersion"), "NoVersion");
		assertNotNull(metadata.getEntityBinding("WithFakeTimestamp"), "WithFakeTimestamp");
		assertNotNull(metadata.getEntityBinding("WithVersion"), "WithVersion");
	}
	
	@Test
	public void testGetTable() throws Exception {
		assertNotNull(
				HibernateUtil.getTable(
						MetadataDescriptorFactory
							.createReverseEngineeringDescriptor(null, null)
							.createMetadata(), 
						JdbcUtil.toIdentifier(this, "WITH_REAL_TIMESTAMP")));
	}

}
