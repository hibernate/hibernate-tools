package org.hibernate.tool.test.jdbc2cfg.identity;

import java.sql.SQLException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;

/**
 * @author max
 *
 */
public class SQLServerIdentityTest extends JDBCMetaDataBinderTestCase {

	
	/**
	 * @return
	 */
	protected String[] getDropSQL() {
		return new String[] {
				"drop table identity",
				"drop table guid",                
		};	
	}

	/**
	 * @return
	 */
	protected String[] getCreateSQL() {
		
		return new String[] {
				"CREATE TABLE identity (id INT NOT NULL IDENTITY, data varchar(100),CONSTRAINT PK_identity PRIMARY KEY (id))",
				"CREATE TABLE guid (uid UNIQUEIDENTIFIER DEFAULT newid() NOT NULL, data varchar(100), CONSTRAINT PK_guid PRIMARY KEY (uid))",
		};
	}
	
	public boolean appliesTo(Dialect dialect) {
		return dialect instanceof SQLServerDialect;
	}
	
	protected void configure(JDBCMetaDataConfiguration configuration) {
		configuration.setProperty( "hibernatetool.metadatadialect", SQLServerDialect.class.getName() );
	}
	
	
	//public void assertNoTables() throws SQLException {
		// can't do that on mysql
	//}

	public static Test suite() {
		return new TestSuite(SQLServerIdentityTest.class);
	}
	
	public void testIdentity() throws SQLException {
		PersistentClass classMapping = cfg.getClassMapping(toClassName("identity") );
		assertNotNull(classMapping);
	
		assertEquals("identity", ((SimpleValue)classMapping.getIdentifierProperty().getValue()).getIdentifierGeneratorStrategy());		
	}
	
	public void testGuid() throws SQLException {
		PersistentClass classMapping = cfg.getClassMapping(toClassName("guid") );
		assertNotNull(classMapping);
	
		assertEquals("guid", ((SimpleValue)classMapping.getIdentifierProperty().getValue()).getIdentifierGeneratorStrategy());		
	}
    
}
