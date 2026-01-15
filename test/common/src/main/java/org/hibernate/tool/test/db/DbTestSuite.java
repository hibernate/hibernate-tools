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
package org.hibernate.tool.test.db;

import org.junit.jupiter.api.Nested;

public class DbTestSuite {
	
	@Nested public class Cfg2HbmWithPackageName extends org.hibernate.tool.ant.Cfg2HbmWithPackageName.TestCase {}
	@Nested public class Cfg2HbmWithPackageNameAndReverseNamingStrategy extends org.hibernate.tool.ant.Cfg2HbmWithPackageNameAndReverseNamingStrategy.TestCase {}
	@Nested public class EJB3Configuration extends org.hibernate.tool.ant.EJB3Configuration.TestCase {}
	@Nested public class Hbm2JavaConfiguration extends org.hibernate.tool.ant.Hbm2JavaConfiguration.TestCase {}
	@Nested public class Hbm2JavaEJB3Configuration extends org.hibernate.tool.ant.Hbm2JavaEJB3Configuration.TestCase {}
	@Nested public class HbmLint extends org.hibernate.tool.ant.HbmLint.TestCase {}
	@Nested public class JDBCConfiguration extends org.hibernate.tool.ant.JDBCConfiguration.TestCase {}
	@Nested public class JDBCConfigWithRevEngXml extends org.hibernate.tool.ant.JDBCConfigWithRevEngXml.TestCase {}
	@Nested public class JPABogusPUnit extends org.hibernate.tool.ant.JPABogusPUnit.TestCase {}
	@Nested public class JPAPropertyOverridesPUnit extends org.hibernate.tool.ant.JPAPropertyOverridesPUnit.TestCase {}
	@Nested public class JPAPUnit extends org.hibernate.tool.ant.JPAPUnit.TestCase {}

}
