/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2017-2021 Red Hat, Inc.
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
package org.hibernate.tool.test.db.oracle;

import org.hibernate.tool.test.db.DbTestSuite;
import org.junit.jupiter.api.Nested;

public class TestSuite {
	
	@Nested public class OracleTestSuite extends DbTestSuite {}
	@Nested public class DialectTestCase extends org.hibernate.cfg.reveng.dialect.TestCase {}
	@Nested public class CompositeOrderTestCase extends org.hibernate.tool.jdbc2cfg.CompositeIdOrder.TestCase {}
	@Nested public class ViewsTestCase extends org.hibernate.tool.jdbc2cfg.Views.TestCase {}
	
}
