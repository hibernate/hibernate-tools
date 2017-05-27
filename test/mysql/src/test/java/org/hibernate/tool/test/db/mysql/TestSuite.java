package org.hibernate.tool.test.db.mysql;

import org.hibernate.tool.test.db.CommonTestSuite;
import org.hibernate.tools.test.util.DbSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(DbSuite.class)
@SuiteClasses({ CommonTestSuite.class })
public class TestSuite {}
