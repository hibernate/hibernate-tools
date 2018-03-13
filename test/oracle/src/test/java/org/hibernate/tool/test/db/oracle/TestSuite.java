package org.hibernate.tool.test.db.oracle;

import org.hibernate.tools.test.util.DbSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(DbSuite.class)
@SuiteClasses({
	org.hibernate.cfg.reveng.dialect.TestCase.class,
	org.hibernate.tool.jdbc2cfg.CompositeIdOrder.TestCase.class,
	org.hibernate.tool.jdbc2cfg.Views.TestCase.class,
	org.hibernate.tool.test.db.CommonTestSuite.class
})
public class TestSuite {}
