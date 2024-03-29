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
package org.hibernate.tools.test.util.hprops;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.jupiter.api.Test;

import java.util.Properties;

public class AlternateHibernatePropsTest {
  private static final String connectString = "jdbc:h2:mem:test1;";

	@Test
	public void testExecuteDDL() throws Exception {

		Properties properties = JdbcUtil.getConnectionProperties(this);
		assertEquals(connectString, properties.get("url"));
		assertEquals("sa", properties.get("user"));
		assertEquals("123", properties.get("password"));

    JdbcUtil.createDatabase(this);
    JdbcUtil.dropDatabase(this);
	}
}