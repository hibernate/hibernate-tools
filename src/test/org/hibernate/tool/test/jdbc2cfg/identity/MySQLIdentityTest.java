package org.hibernate.tool.test.jdbc2cfg.identity;

import java.sql.SQLException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.dialect.MySQLMetaDataDialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;

/**
 * @author max
 *
 */
public class MySQLIdentityTest extends AbstractIdentityTest {

	
	/**
	 * @return
	 */
	protected String[] getDropSQL() {
		return new String[] {
				"drop table `autoinc`",
				"drop table `noautoinc`",                
		};	
	}

	/**
	 * @return
	 */
	protected String[] getCreateSQL() {
		
		return new String[] {
				"CREATE TABLE `autoinc` (`id` int(11) NOT NULL auto_increment,  `data` varchar(100) default NULL,  PRIMARY KEY  (`id`))",
				"CREATE TABLE `noautoinc` (`id` int(11) NOT NULL,  `data` varchar(100) default NULL,  PRIMARY KEY  (`id`))",
		};
	}
	
	public boolean appliesTo(Dialect dialect) {
		return dialect instanceof MySQLDialect;
	}
	
	protected void configure(JDBCMetaDataConfiguration configuration) {
		configuration.setProperty( "hibernatetool.metadatadialect", MySQLMetaDataDialect.class.getName() );
	}
	
	
	//public void assertNoTables() throws SQLException {
		// can't do that on mysql
	//}

	public static Test suite() {
		return new TestSuite(MySQLIdentityTest.class);
	}
    
}
