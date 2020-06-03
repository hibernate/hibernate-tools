/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2017-2020 Red Hat, Inc.
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

import java.sql.SQLException;

import org.hibernate.boot.Metadata;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author max
 * @author koen
 */
public class TestCase {

	private Metadata metadata = null;

	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);
		metadata = MetadataDescriptorFactory
				.createReverseEngineeringDescriptor(null, null)
				.createMetadata();
	}

	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	// TODO HBX-1412: Enable the test below and make sure it passes.
	@Ignore
	@Test
	public void testIdentity() throws SQLException {

		PersistentClass classMapping = metadata.getEntityBinding("Autoinc");
		Assert.assertNotNull(classMapping);
		Assert.assertEquals(
				"identity", 
				((SimpleValue)classMapping
						.getIdentifierProperty()
						.getValue())
					.getIdentifierGeneratorStrategy());
		
		classMapping = metadata.getEntityBinding("Noautoinc");
		Assert.assertEquals(
				"assigned", 
				((SimpleValue)classMapping
						.getIdentifierProperty()
						.getValue())
					.getIdentifierGeneratorStrategy());
	}

}
