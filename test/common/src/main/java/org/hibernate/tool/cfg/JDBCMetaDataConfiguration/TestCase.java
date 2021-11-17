/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2004-2021 Red Hat, Inc.
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
				.createJdbcDescriptor(null, null, true)
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
							.createJdbcDescriptor(null, null, true)
							.createMetadata(), 
						JdbcUtil.toIdentifier(this, "WITH_REAL_TIMESTAMP")));
	}

}
