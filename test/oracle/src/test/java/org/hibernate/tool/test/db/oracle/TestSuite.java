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
package org.hibernate.tool.test.db.oracle;

import org.hibernate.tool.test.db.DbTestSuite;
import org.junit.jupiter.api.Nested;

public class TestSuite {
	
	@Nested public class OracleTestSuite extends DbTestSuite {}
	@Nested public class DialectTestCase extends org.hibernate.cfg.reveng.dialect.TestCase {}
	@Nested public class CompositeOrderTestCase extends org.hibernate.tool.jdbc2cfg.CompositeIdOrder.TestCase {}
	@Nested public class ViewsTestCase extends org.hibernate.tool.jdbc2cfg.Views.TestCase {}
	
}
