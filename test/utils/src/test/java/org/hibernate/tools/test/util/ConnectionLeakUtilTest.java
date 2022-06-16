/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2018-2020 Red Hat, Inc.
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
package org.hibernate.tools.test.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConnectionLeakUtilTest {
	
	private ConnectionLeakUtil connectionLeakUtil = null;
	
	@BeforeEach
	public void before() {
		connectionLeakUtil = ConnectionLeakUtil.forH2();
		connectionLeakUtil.initialize();
	}
	
	@Test
	public void testLeaks() throws Exception {
		try {
			DriverManager.getConnection("jdbc:h2:mem:test", "sa", "");
			connectionLeakUtil.assertNoLeaks();
			throw new RuntimeException("should not happen");
		} catch (AssertionError e) {
			assertEquals("1 connections are leaked.", e.getMessage());
		}
	}
	
	@Test
	public void testNoLeaks() throws Exception {
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:test", "sa", "");
		connection.close();
		connectionLeakUtil.assertNoLeaks();
	}
	
}
