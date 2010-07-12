/*
 * Created on 2004-11-24
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.tool.JDBCMetaDataBinderTestCase;



/**
 * @author max
 *
 */
public class NoPrimaryKeyTest extends JDBCMetaDataBinderTestCase {

	protected String[] getCreateSQL() {
		
		return new String[] {
				"create table G0 ( AN_ID VARCHAR(20), CONSTRAINT \"C0\" PRIMARY KEY (\"AN_ID\") )", 
				"create table G1 ( AN_ID VARCHAR(20), CONSTRAINT \"C1\" FOREIGN KEY (\"AN_ID\") REFERENCES \"G0\")"
		};
	}

	protected String[] getDropSQL() {
		
		return new String[]  {
				"drop table G1",
				"drop table G0"								
		};
	}
	
	public void testMe() throws Exception {
		cfg.buildMappings();
	}
	
	public static Test suite() {
		return new TestSuite(NoPrimaryKeyTest.class);
	}
}
