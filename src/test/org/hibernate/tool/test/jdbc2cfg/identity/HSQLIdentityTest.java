package org.hibernate.tool.test.jdbc2cfg.identity;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.dialect.HSQLMetaDataDialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.HSQLDialect;

/**
 * @author Dmitry Geraskov
 *
 */
public class HSQLIdentityTest extends AbstractIdentityTest {

	/**
	 * @return
	 */
	protected String[] getDropSQL() {
		return new String[] {
				"DROP TABLE AUTOINC IF EXISTS",
				"DROP TABLE NOAUTOINC IF EXISTS",                
		};	
	}

	/**
	 * @return
	 */
	protected String[] getCreateSQL() {
		
		return new String[] {
				"CREATE TABLE AUTOINC (I identity, C CHAR(20), D CHAR(20))",
				"CREATE TABLE NOAUTOINC (I int, C CHAR(20), D CHAR(20))",
		};
	}
	
	public boolean appliesTo(Dialect dialect) {
		return dialect instanceof HSQLDialect;
	}
	
	protected void configure(JDBCMetaDataConfiguration configuration) {
		configuration.setProperty( "hibernatetool.metadatadialect", HSQLMetaDataDialect.class.getName() );
	}

	public static Test suite() {
		return new TestSuite(HSQLIdentityTest.class);
	}

}
