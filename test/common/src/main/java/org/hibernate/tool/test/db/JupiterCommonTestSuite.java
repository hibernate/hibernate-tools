package org.hibernate.tool.test.db;

import org.junit.jupiter.api.Nested;

public class JupiterCommonTestSuite {
	
	@Nested public class AntHibernateToolTests extends org.hibernate.tool.ant.AntHibernateTool.TestCase {}
	@Nested public class Cfg2HbmNoError extends org.hibernate.tool.ant.Cfg2HbmNoError.TestCase {}
	@Nested public class Cfg2HbmWithCustomReverseNamingStrategy extends org.hibernate.tool.ant.Cfg2HbmWithCustomReverseNamingStrategy.TestCase {}
	@Nested public class Cfg2HbmWithInvalidReverseNamingStrategy extends org.hibernate.tool.ant.Cfg2HbmWithInvalidReverseNamingStrategy.TestCase {}
	@Nested public class Cfg2HbmWithPackageName extends org.hibernate.tool.ant.Cfg2HbmWithPackageName.TestCase {}
	@Nested public class Cfg2HbmWithPackageNameAndReverseNamingStrategy extends org.hibernate.tool.ant.Cfg2HbmWithPackageNameAndReverseNamingStrategy.TestCase {}
	@Nested public class EJB3Configuration extends org.hibernate.tool.ant.EJB3Configuration.TestCase {}
	@Nested public class GenericExport extends org.hibernate.tool.ant.GenericExport.TestCase {}
	@Nested public class Hbm2JavaConfiguration extends org.hibernate.tool.ant.Hbm2JavaConfiguration.TestCase {}
	@Nested public class Hbm2JavaEJB3Configuration extends org.hibernate.tool.ant.Hbm2JavaEJB3Configuration.TestCase {}
	@Nested public class HbmLint extends org.hibernate.tool.ant.HbmLint.TestCase {}
	@Nested public class JDBCConfiguration extends org.hibernate.tool.ant.JDBCConfiguration.TestCase {}

}
