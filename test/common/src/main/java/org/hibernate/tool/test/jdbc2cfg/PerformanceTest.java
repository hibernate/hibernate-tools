/*
 * Created on 2004-11-23
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import java.sql.SQLException;
import java.util.ArrayList;

import org.hibernate.MappingException;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.tools.test.util.JUnitUtil;
import org.hibernate.tools.test.util.JdbcUtil;
import org.hibernate.type.Type;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author max
 *
 */
public class PerformanceTest {

	private static final int TABLECOUNT = 200;
	private static final int COLCOUNT = 10;
	
	static String[] CREATE_SQL = null;
	static String[] DROP_SQL = null;

	private JDBCMetaDataConfiguration jmdcfg = null;

	@Before
	public void setUp() {
		jmdcfg = new JDBCMetaDataConfiguration();
		buildSQL();
		JdbcUtil.createDatabase(this);
	}
	
	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	@Test
	public void testBasic() throws SQLException {	
		jmdcfg.readFromJDBC();
		JUnitUtil.assertIteratorContainsExactly(
				"There should be " + TABLECOUNT + " tables!", 
				jmdcfg.getMetadata().collectTableMappings().iterator(), 
				TABLECOUNT);
		Table tab = (Table) jmdcfg.getMetadata().collectTableMappings().iterator().next();
		Assert.assertEquals(tab.getColumnSpan(), COLCOUNT+1);
	}
	
	private void buildSQL() {
		Dialect dia = jmdcfg.getServiceRegistry().getService(JdbcServices.class).getDialect();	
		Mapping map = new DummyMapping();	
		ArrayList<String> dropSQL = new ArrayList<String>(TABLECOUNT);
		ArrayList<String> createSQL = new ArrayList<String>(TABLECOUNT);
		Table lastTable = null;
		for(int tablecount=0;tablecount<TABLECOUNT;tablecount++) {
			Table table = new Table("PERFTEST" + tablecount);
			Column col = new Column("ID");
			SimpleValue sv = new SimpleValue((MetadataImplementor) jmdcfg.getMetadata(), table);
			sv.setTypeName("string");
			col.setValue(sv);			
			table.addColumn(col);
			PrimaryKey pk = new PrimaryKey(table);
			pk.addColumn(col);
			table.setPrimaryKey(pk);			
			for(int colcount=0;colcount<COLCOUNT;colcount++) {
				col = new Column("COL"+tablecount+"_"+colcount);
				sv = new SimpleValue((MetadataImplementor) jmdcfg.getMetadata(), table);
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
		CREATE_SQL = createSQL.toArray(new String[TABLECOUNT]);
		DROP_SQL = dropSQL.toArray(new String[TABLECOUNT]);
	}
	
	private static class DummyMapping implements Mapping {	
		public String getIdentifierPropertyName(String className) throws MappingException {
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
	}

}
