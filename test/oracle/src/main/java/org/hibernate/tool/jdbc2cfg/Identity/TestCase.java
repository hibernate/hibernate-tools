/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2018-2021 Red Hat, Inc.
 *
 * Licensed under the GNU Lesser General Public License (LGPL), 
 * version 2.1 or later (the "License").
 * You may not use this file except in compliance with the License.
 * You may read the licence in the 'lgpl.txt' file in the root folder of 
 * project or obtain a copy at
 *
 *     http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.tool.jdbc2cfg.Identity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.SQLException;

import org.hibernate.boot.Metadata;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * @author max
 * @author koen
 */
public class TestCase {

	private Metadata metadata = null;

	@BeforeEach
	public void setUp() {
		JdbcUtil.createDatabase(this);
		metadata = MetadataDescriptorFactory
				.createReverseEngineeringDescriptor(null, null)
				.createMetadata();
	}

	@AfterEach
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	// TODO HBX-1412: Enable the test below and make sure it passes.
	@Disabled
	@Test
	public void testIdentity() throws SQLException {

		PersistentClass classMapping = metadata.getEntityBinding("Autoinc");
		assertNotNull(classMapping);
		assertEquals(
				"identity", 
				((SimpleValue)classMapping
						.getIdentifierProperty()
						.getValue())
					.getIdentifierGeneratorStrategy());
		
		classMapping = metadata.getEntityBinding("Noautoinc");
		assertEquals(
				"assigned", 
				((SimpleValue)classMapping
						.getIdentifierProperty()
						.getValue())
					.getIdentifierGeneratorStrategy());
	}

}
