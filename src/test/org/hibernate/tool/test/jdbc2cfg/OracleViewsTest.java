/*
 * Created on 2004-11-23
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import java.sql.SQLException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.dialect.OracleMetaDataDialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.Oracle9Dialect;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;

/**
 * @author max
 *
 */
public class OracleViewsTest extends JDBCMetaDataBinderTestCase {

	
	/**
	 * @return
	 */
	protected String[] getDropSQL() {
		return new String[] {
				"drop table basic",
				"drop table somecolumnsnopk",
                "drop table multikeyed",
                "drop view basicView",
                "drop synonym weirdname"
		};	
	}

	/**
	 * @return
	 */
	protected String[] getCreateSQL() {
		
		return new String[] {
				"create table basic ( a int not null, primary key (a) )",
				"create table somecolumnsnopk ( pk varchar(25) not null, b char, c int not null )",
                "create table multikeyed ( orderid varchar(10), customerid varchar(10), name varchar(10), primary key(orderid, customerid) )",
                "create view basicView as select a from basic",
                "create synonym weirdname for multikeyed",
                "comment on table basic is 'a basic comment'",
                "comment on column basic.a is 'a solid key'"
		};
	}

	public boolean appliesTo(Dialect dialect) {
		return dialect instanceof Oracle9Dialect;
	}
	
	protected void configure(JDBCMetaDataConfiguration configuration) {
		configuration.setProperty( "hibernatetool.metadatadialect", OracleMetaDataDialect.class.getName() );
	}
	
	public void testViewAndSynonyms() throws SQLException {
				PersistentClass classMapping = cfg.getClassMapping(toClassName("basicview") );
				assertNotNull(classMapping);
			
				classMapping = cfg.getClassMapping(toClassName("weirdname") );
				assertTrue("If this is not-null synonyms apparently work!",classMapping==null);

				// get comments
				Table table = getTable(identifier("basic"));
				assertEquals("a basic comment", table.getComment());
				assertEquals("a solid key", table.getPrimaryKey().getColumn(0).getComment());
				
				table = getTable(identifier("multikeyed"));
				assertNull(table.getComment());
				assertNull(table.getColumn(0).getComment());
	}
	

	public static Test suite() {
		return new TestSuite(OracleViewsTest.class);
	}
    
}
