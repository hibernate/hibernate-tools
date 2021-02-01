package org.hibernate.tool.test.db;

import org.junit.jupiter.api.Nested;

public class JupiterCommonTestSuite {
	
	@Nested public class AntHibernateToolTests extends org.hibernate.tool.ant.AntHibernateTool.TestCase {}
	@Nested public class Cfg2HbmNoError extends org.hibernate.tool.ant.Cfg2HbmNoError.TestCase {}

}
