package org.hibernate.tool.test.db.mysql;

import org.hibernate.tool.test.db.DbTestSuite;
import org.junit.jupiter.api.Nested;

public class TestSuite {
	
	@Nested
	public class MySqlTestSuite extends DbTestSuite {}

}
