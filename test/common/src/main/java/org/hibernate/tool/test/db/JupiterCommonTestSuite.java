package org.hibernate.tool.test.db;

import org.junit.jupiter.api.Nested;

public class JupiterCommonTestSuite {
	
	@Nested public class AntHibernateToolTests extends org.hibernate.tool.ant.AntHibernateTool.TestCase {}
	@Nested public class Cfg2HbmNoError extends org.hibernate.tool.ant.Cfg2HbmNoError.TestCase {}
	@Nested public class Cfg2HbmWithCustomReverseNamingStrategy extends org.hibernate.tool.ant.Cfg2HbmWithCustomReverseNamingStrategy.TestCase {}
	@Nested public class Cfg2HbmWithInvalidReverseNamingStrategy extends org.hibernate.tool.ant.Cfg2HbmWithInvalidReverseNamingStrategy.TestCase {}
	@Nested public class Cfg2HbmWithPackageName extends org.hibernate.tool.ant.Cfg2HbmWithPackageName.TestCase {}

}
