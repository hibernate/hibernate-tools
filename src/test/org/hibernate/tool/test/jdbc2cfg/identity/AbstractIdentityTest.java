package org.hibernate.tool.test.jdbc2cfg.identity;

import java.sql.SQLException;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;

public abstract class AbstractIdentityTest extends JDBCMetaDataBinderTestCase {

	public void testAutoIncrement() throws SQLException {
		PersistentClass classMapping = cfg.getMetadata().getEntityBinding(toClassName("autoinc"));
		assertNotNull(classMapping);
	
		assertEquals("identity", ((SimpleValue)classMapping.getIdentifierProperty().getValue()).getIdentifierGeneratorStrategy());
		
		classMapping = cfg.getMetadata().getEntityBinding(toClassName("noautoinc"));
		assertEquals("assigned", ((SimpleValue)classMapping.getIdentifierProperty().getValue()).getIdentifierGeneratorStrategy());
		
}

}
