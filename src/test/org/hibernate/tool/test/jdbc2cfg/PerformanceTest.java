/*
 * Created on 2004-11-23
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.MappingException;
import org.hibernate.cfg.Mappings;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.Mapping;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;
import org.hibernate.type.Type;

/**
 * @author max
 *
 */
public class PerformanceTest extends JDBCMetaDataBinderTestCase {

	static final int TABLECOUNT = 200;
	static final int COLCOUNT = 10;
	
	List createSQL = new ArrayList();
	List dropSQL = new ArrayList();

	/**
	 * @return
	 */
	protected String[] getDropSQL() {
		return (String[]) dropSQL.toArray(new String[dropSQL.size()]);
	}

	/**
	 * @return
	 */
	protected String[] getCreateSQL() {
		
		Dialect dia = cfg.buildSettings().getDialect();
		
		Mapping map = new Mapping() {
		
			public String getIdentifierPropertyName(String className)
					throws MappingException {
				return null;
			}
		
			public Type getIdentifierType(String className) throws MappingException {
				return null;
			}

			public Type getReferencedPropertyType(String className, String propertyName) throws MappingException {
				return null;
			}
			
			public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
				return null;
			}
		
		};
		
		
		dropSQL = new ArrayList(TABLECOUNT);
		createSQL = new ArrayList(TABLECOUNT);
		Table lastTable = null;
		Mappings mappings = cfg.createMappings();
		for(int tablecount=0;tablecount<TABLECOUNT;tablecount++) {
			Table table = new Table("perftest" + tablecount);
			Column col = new Column("id");
			SimpleValue sv = new SimpleValue(mappings, table);
			sv.setTypeName("string");
			col.setValue(sv);			
			table.addColumn(col);
			PrimaryKey pk = new PrimaryKey();
			pk.addColumn(col);
			table.setPrimaryKey(pk);
			
			for(int colcount=0;colcount<COLCOUNT;colcount++) {
				col = new Column("col"+tablecount+"_"+colcount);
				sv = new SimpleValue(mappings, table);
				sv.setTypeName("string");
				col.setValue(sv);				
				table.addColumn(col);
			
			}
			
			
			createSQL.add(table.sqlCreateString(dia, map, null, null) );
			dropSQL.add(table.sqlDropString(dia, null, null) );
			
			if(lastTable!=null) {
				ForeignKey fk = new ForeignKey();
				fk.setName(col.getName() + lastTable.getName() + table.getName() );
				fk.addColumn(col);
				fk.setTable(table);
				fk.setReferencedTable(lastTable);
				createSQL.add(fk.sqlCreateString( dia, map, null,null) );							
				dropSQL.add(0,fk.sqlDropString( dia, null,null) );
			}
			
			lastTable = table;
			
			
		}
		
		
		
		return (String[]) createSQL.toArray(new String[createSQL.size()]);
	}

	
	public void testBasic() throws SQLException {
				
		assertHasNext("There should be three tables!",TABLECOUNT, cfg.getTableMappings() );
		
		Table tab = (Table) cfg.getTableMappings().next();
		assertEquals(tab.getColumnSpan(), COLCOUNT+1);
		
		
	}
	
    
 
	

	public static Test suite() {
		return new TestSuite(PerformanceTest.class);
	}
    
}
