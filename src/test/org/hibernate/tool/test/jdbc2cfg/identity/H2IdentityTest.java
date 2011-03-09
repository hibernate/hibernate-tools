/*
 * Created on 2004-11-23
 *
 */
package org.hibernate.tool.test.jdbc2cfg.identity;

import java.sql.SQLException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.dialect.H2MetaDataDialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.SimpleValue;

/**
 * @author max
 *
 */
public class H2IdentityTest extends AbstractIdentityTest {

	
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
				"CREATE TABLE `autoinc` (`id` int(11) NOT NULL identity,  `data` varchar(100) default NULL,  PRIMARY KEY  (`id`))",
				"CREATE TABLE `noautoinc` (`id` int(11) NOT NULL,  `data` varchar(100) default NULL,  PRIMARY KEY  (`id`))",
		};
	}
	
	public boolean appliesTo(Dialect dialect) {
		return dialect instanceof H2Dialect;
	}
	
	protected void configure(JDBCMetaDataConfiguration configuration) {
		configuration.setProperty( "hibernatetool.metadatadialect", H2MetaDataDialect.class.getName() );
	}
	

	public static Test suite() {
		return new TestSuite(H2IdentityTest.class);
	}
    
}
