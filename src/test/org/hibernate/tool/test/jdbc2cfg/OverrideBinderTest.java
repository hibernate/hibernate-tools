/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import java.sql.Types;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.cfg.Environment;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.Settings;
import org.hibernate.cfg.SettingsFactory;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.SQLTypeMapping;
import org.hibernate.cfg.reveng.SchemaSelection;
import org.hibernate.cfg.reveng.TableFilter;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.MetaAttribute;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;

/**
 * @author max
 *
 */
public class OverrideBinderTest extends JDBCMetaDataBinderTestCase {
	
	private static final String OVERRIDETEST_REVENG_XML = "org/hibernate/tool/test/jdbc2cfg/overridetest.reveng.xml";
	private static final String TEST_REVENG_XML = "org/hibernate/tool/test/jdbc2cfg/test.reveng.xml";
	private static final String DOC_REVENG_XML = "org/hibernate/tool/test/jdbc2cfg/docexample.reveng.xml";
	private static final String SCHEMA_REVENG_XML = "org/hibernate/tool/test/jdbc2cfg/schemaselection.reveng.xml";

	public void testReadTypeMappings() {
		OverrideRepository or = buildOverrideRepository();
				
		or.addResource(TEST_REVENG_XML);
		ReverseEngineeringStrategy repository = or.getReverseEngineeringStrategy(null);

		assertEquals("int", repository.columnToHibernateTypeName(null, null, Types.INTEGER, 5, SQLTypeMapping.UNKNOWN_PRECISION, SQLTypeMapping.UNKNOWN_SCALE, false, false) );
		assertEquals("long", repository.columnToHibernateTypeName(null, null, Types.INTEGER, SQLTypeMapping.UNKNOWN_LENGTH, SQLTypeMapping.UNKNOWN_PRECISION, SQLTypeMapping.UNKNOWN_SCALE, false, false) );
		assertEquals("byte[]", repository.columnToHibernateTypeName(null, null, Types.BIGINT, SQLTypeMapping.UNKNOWN_LENGTH, 5, SQLTypeMapping.UNKNOWN_SCALE, false, false) );
		assertEquals("java.math.BigInteger", repository.columnToHibernateTypeName(null, null, Types.BIGINT, SQLTypeMapping.UNKNOWN_LENGTH, 2, 3, false, false) );
		assertEquals("string", repository.columnToHibernateTypeName(null, null, Types.CHAR, 1, 10, SQLTypeMapping.UNKNOWN_SCALE, false, false) );
		//assertEquals("string", repository.jdbcToHibernateType(Types.CHAR, 1, SQLTypeMapping.UNKNOWN_PRECISION, SQLTypeMapping.UNKNOWN_SCALE) );
		
		assertEquals("Long", repository.columnToHibernateTypeName(null, null, Types.NUMERIC, 1, 10, 0, false, false) );
		assertEquals("java.lang.Long", repository.columnToHibernateTypeName(null, null, Types.NUMERIC, 1, 10, SQLTypeMapping.UNKNOWN_SCALE, false, false) );
		assertEquals("java.lang.Long", repository.columnToHibernateTypeName(null, null, Types.NUMERIC, 1, 10, 43, false, false) );
		
		// nullability
		assertEquals("nonnull-float", repository.columnToHibernateTypeName(null, null, Types.FLOAT, SQLTypeMapping.UNKNOWN_LENGTH, SQLTypeMapping.UNKNOWN_PRECISION,17,false, false) );
		assertEquals("null-float", repository.columnToHibernateTypeName(null, null, Types.FLOAT, SQLTypeMapping.UNKNOWN_LENGTH, SQLTypeMapping.UNKNOWN_PRECISION,17,true, false) );
		
		assertEquals("onlynotnull", repository.columnToHibernateTypeName(null, null, Types.FLOAT, SQLTypeMapping.UNKNOWN_LENGTH, SQLTypeMapping.UNKNOWN_PRECISION,SQLTypeMapping.UNKNOWN_SCALE,false, false) );
		assertEquals("donotcare", repository.columnToHibernateTypeName(null, null, Types.FLOAT, SQLTypeMapping.UNKNOWN_LENGTH, SQLTypeMapping.UNKNOWN_PRECISION,SQLTypeMapping.UNKNOWN_SCALE,true, false) );
		
		
	}
	
	public void testDocExample() {
		OverrideRepository or = buildOverrideRepository();
		
		or.addResource(DOC_REVENG_XML);
		ReverseEngineeringStrategy repository = or.getReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy());

		assertEquals("int", repository.columnToHibernateTypeName(null, "ID", Types.INTEGER, SQLTypeMapping.UNKNOWN_LENGTH, 10, SQLTypeMapping.UNKNOWN_SCALE, false, false) );
		assertEquals("your.package.TrimStringUserType", repository.columnToHibernateTypeName(null, "NAME", Types.VARCHAR, 30, SQLTypeMapping.UNKNOWN_PRECISION, SQLTypeMapping.UNKNOWN_SCALE, true, false) );
		assertEquals("char", repository.columnToHibernateTypeName(null, "INITIAL", Types.VARCHAR, 1, SQLTypeMapping.UNKNOWN_PRECISION, SQLTypeMapping.UNKNOWN_SCALE, true, false) );
		assertEquals("java.lang.Character", repository.columnToHibernateTypeName(null, "CODE", Types.VARCHAR, 1, SQLTypeMapping.UNKNOWN_PRECISION, SQLTypeMapping.UNKNOWN_SCALE, false, false) );
		assertEquals("big_decimal", repository.columnToHibernateTypeName(null, "SALARY", Types.NUMERIC, SQLTypeMapping.UNKNOWN_LENGTH, 15, SQLTypeMapping.UNKNOWN_SCALE, true, false) );
		assertEquals("java.lang.Long", repository.columnToHibernateTypeName(null, "AGE", Types.NUMERIC, SQLTypeMapping.UNKNOWN_LENGTH, 3, SQLTypeMapping.UNKNOWN_SCALE, true, false) );
		
	}
	
	public void testSchemaSelection() {
		OverrideRepository or = buildOverrideRepository();
		
		or.addResource(SCHEMA_REVENG_XML);
		ReverseEngineeringStrategy repository = or.getReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy());

		List schemaSelectors = repository.getSchemaSelections();
		
		assertNotNull(schemaSelectors);
		assertEquals(4,schemaSelectors.size());
		
		SchemaSelection ss;
		ss = (SchemaSelection) schemaSelectors.get(0);
		assertEquals(null,ss.getMatchCatalog());
		assertEquals(null,ss.getMatchSchema());
		assertEquals(null,ss.getMatchTable());
		
		ss = (SchemaSelection) schemaSelectors.get(1);
		assertEquals(null,ss.getMatchCatalog());
		assertEquals("OVRTEST",ss.getMatchSchema());
		assertEquals(null,ss.getMatchTable());
		
		ss = (SchemaSelection) schemaSelectors.get(2);
		assertEquals("UBERCATALOG",ss.getMatchCatalog());
		assertEquals("OVRTEST",ss.getMatchSchema());
		assertEquals(null,ss.getMatchTable());
		
		ss = (SchemaSelection) schemaSelectors.get(3);
		assertEquals("PUBLIC.*",ss.getMatchCatalog());
		assertEquals("OVRTEST",ss.getMatchSchema());
		assertEquals(".*",ss.getMatchTable());
		
		JDBCMetaDataConfiguration configuration = new JDBCMetaDataConfiguration();
		
		OverrideRepository ox = new OverrideRepository();
		ox.addSchemaSelection(new SchemaSelection(null, null, "DUMMY.*"));
		configuration.setReverseEngineeringStrategy(ox.getReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy()));
		configuration.readFromJDBC();
		
		Iterator tableMappings = configuration.getTableMappings();
		Table t = (Table) tableMappings.next();
		assertEquals(t.getName(), "DUMMY");
		assertFalse(tableMappings.hasNext());
	}


	static Settings settings;
	
	private OverrideRepository buildOverrideRepository() {
		if(settings==null) {
			settings = new SettingsFactory() {
				// trick to get hibernate.properties settings for defaultschema/catalog in here
			}.buildSettings(Environment.getProperties());
		}
		//return new OverrideRepository(settings.getDefaultCatalogName(),settings.getDefaultSchemaName());
		return new OverrideRepository();
	}
	
	public void testColumnTypeMappings() {
		OverrideRepository or = buildOverrideRepository();
		
		or.addResource(OVERRIDETEST_REVENG_XML);
		ReverseEngineeringStrategy repository = or.getReverseEngineeringStrategy(null);

		assertNull(repository.columnToHibernateTypeName(new TableIdentifier("blah"), "bogus",0,0,0,0, false, false));
		assertNull(repository.columnToHibernateTypeName(new TableIdentifier("ORDERS"), "CUSTID",0,0,0,0, false, false));
		assertEquals("string", repository.columnToHibernateTypeName(new TableIdentifier(null, null, "ORDERS"), "NAME",0,0,0,0, false, false));
		
		PersistentClass classMapping = cfg.getClassMapping("Orders");
		
		Property property = classMapping.getProperty("completed");		
		assertEquals("boolean because of not null", "boolean", ((SimpleValue)property.getValue()).getTypeName());
		
		property = classMapping.getProperty("verified");
		assertEquals("java.lang.Boolean because of null","java.lang.Boolean", ((SimpleValue)property.getValue()).getTypeName());
		
		classMapping = cfg.getClassMapping("MiscTypes");
		
		property = classMapping.getIdentifierProperty();
		
		assertFalse(((SimpleValue)property.getValue()).isNullable());
		assertEquals("java.lang.Long because of primary key", "java.lang.Long", ((SimpleValue)property.getValue()).getTypeName());
	}

	public void testColumnPropertyNameMappings() {
		OverrideRepository or = buildOverrideRepository();
		
		or.addResource(OVERRIDETEST_REVENG_XML);
		ReverseEngineeringStrategy repository = or.getReverseEngineeringStrategy(null);

		assertNull(repository.columnToPropertyName(new TableIdentifier("blah"), "bogus"));
		assertNull(repository.columnToPropertyName(new TableIdentifier("ORDERS"), "cust_id"));
		assertEquals("orderName", repository.columnToPropertyName(new TableIdentifier(null, null, "ORDERS"), "NAME"));
	}
	
	public void testMetaAttributeMappings() {
		PersistentClass classMapping = cfg.getClassMapping( "Orders" );
		assertEquals("order table value", classMapping.getMetaAttribute( "order-meta" ).getValue());
		
		Property property = classMapping.getProperty("orderName");
		assertEquals("order column value", property.getMetaAttribute( "order-meta" ).getValue());
		//TODO: test sequence of meta
	}
	
	public void testIdGenerator() {
		OverrideRepository or = buildOverrideRepository();
		
		or.addResource(OVERRIDETEST_REVENG_XML);
		ReverseEngineeringStrategy repository = or.getReverseEngineeringStrategy(null);

		TableIdentifier miscTable = new TableIdentifier(null,null, "MISC_TYPES");
		assertEquals("sequence",repository.getTableIdentifierStrategyName(miscTable));
		Map props = repository.getTableIdentifierProperties(miscTable);
		assertEquals("seq_table", props.get("table"));
		
		assertNull(repository.getTableIdentifierStrategyName(new TableIdentifier("blah")));
		assertNull(repository.getTableIdentifierProperties(new TableIdentifier("blah")));
		TableIdentifier ordersTable = new TableIdentifier(null,null, "ORDERS");
		
		assertEquals("customOrderId", repository.tableToIdentifierPropertyName(ordersTable));
		assertEquals(null, repository.tableToIdentifierPropertyName(new TableIdentifier("blah")));
		
		assertEquals("CustomOID", repository.tableToCompositeIdName(ordersTable));
		assertEquals(null, repository.tableToCompositeIdName(new TableIdentifier("blah")));
		
		List primaryKeyColumnNames = repository.getPrimaryKeyColumnNames(new TableIdentifier("blah"));
		assertNull(primaryKeyColumnNames);
		
		primaryKeyColumnNames = repository.getPrimaryKeyColumnNames(ordersTable);
		assertNotNull(primaryKeyColumnNames);
		assertEquals(2, primaryKeyColumnNames.size());
		assertEquals("ORDERID", primaryKeyColumnNames.get(0));
		assertEquals("CUSTID", primaryKeyColumnNames.get(1));
		assertFalse(repository.excludeColumn(ordersTable, "CUSTID"));
		
		// applied
		PersistentClass classMapping = cfg.getClassMapping("Orders");
		SimpleValue sv = (SimpleValue) classMapping.getIdentifier();
		assertEquals("CustomOID", ((Component)sv).getComponentClassName());
		
		assertEquals(2,classMapping.getIdentifierProperty().getColumnSpan());	
		
		Property identifierProperty = classMapping.getIdentifierProperty();
		assertEquals("customOrderId", identifierProperty.getName());
		
		classMapping = cfg.getClassMapping("MiscTypes");
		sv = (SimpleValue) classMapping.getIdentifier(); 
		assertEquals("sequence", sv.getIdentifierGeneratorStrategy()); // will fail if default schema is not set since then there is no match in the override binder		
		
		assertNotNull(sv.getIdentifierGeneratorProperties());
		assertEquals("seq_table", sv.getIdentifierGeneratorProperties().getProperty("table"));
		
	}
	
	
	public void testReadExcludeTables() {
		OverrideRepository or = buildOverrideRepository();
		
		or.addResource(OVERRIDETEST_REVENG_XML);
		ReverseEngineeringStrategy repository = or.getReverseEngineeringStrategy(null);
		
		assertTrue(repository.excludeTable(new TableIdentifier(null,null, "DoNotWantIt") ) );
		assertFalse(repository.excludeTable(new TableIdentifier(null,null, "NotListedThere") ) );
		assertFalse(repository.excludeTable(new TableIdentifier("cat","sch", "WantedTable") ) );
		assertFalse(repository.excludeTable(new TableIdentifier("BAD","SCHEMA", "WantedTable") ) ); 
		assertTrue(repository.excludeTable(new TableIdentifier("BAD","SCHEMA", "SomethingElse") ) );
		
	}
	
	public void testReadPackageName() {
		OverrideRepository or = buildOverrideRepository();
		
		or.addResource(OVERRIDETEST_REVENG_XML);
		ReverseEngineeringStrategy repository = or.getReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy());
		
		assertEquals("org.werd.Q", repository.tableToClassName(new TableIdentifier("q","Werd", "Q") ) );
		assertEquals("Notknown", repository.tableToClassName(new TableIdentifier(null,null, "notknown") ) );
		
		assertEquals("org.werd.MyWorld", repository.tableToClassName(new TableIdentifier(null,"Werd", "TBL_PKG") ) );
		assertEquals("other.MyWorld", repository.tableToClassName(new TableIdentifier(null,"Werd", "TBL_OTHER") ) );
		
	}
	
	public Table findTable(String name) {
		Iterator tableIter = cfg.getTableMappings();
		
		Table table = null;
		while(tableIter.hasNext() ) {
			table = (Table) tableIter.next();
			if(table.getName().equals(name) ) {
				return table;
			}
		}
		return null;
	}
	
	public void testRevEngExclude() {
		
		assertNull(findTable(identifier("defunct_table") ) );
		Table foundTable = findTable(identifier("inthemiddle") );
		assertNotNull(foundTable);
		Iterator fkiter = foundTable.getForeignKeyIterator();
		ForeignKey fk1 = (ForeignKey) fkiter.next();
		assertNotNull(fk1);
		assertFalse(fkiter.hasNext() );
		
		
	}
	

	public void testSQLTypeMappingComparisons() {
		SQLTypeMapping one = new SQLTypeMapping(Types.BIGINT, 5, SQLTypeMapping.UNKNOWN_PRECISION, SQLTypeMapping.UNKNOWN_SCALE,SQLTypeMapping.UNKNOWN_NULLABLE);
		SQLTypeMapping two = new SQLTypeMapping(Types.BIGINT, SQLTypeMapping.UNKNOWN_LENGTH, 3, SQLTypeMapping.UNKNOWN_SCALE, SQLTypeMapping.UNKNOWN_NULLABLE);
		SQLTypeMapping generic = new SQLTypeMapping(Types.BIGINT, SQLTypeMapping.UNKNOWN_LENGTH, SQLTypeMapping.UNKNOWN_PRECISION, SQLTypeMapping.UNKNOWN_SCALE, SQLTypeMapping.UNKNOWN_NULLABLE);
		SQLTypeMapping specific = new SQLTypeMapping(Types.BIGINT, 2, 3, 4, SQLTypeMapping.UNKNOWN_NULLABLE);
		SQLTypeMapping morespecific = new SQLTypeMapping(Types.BIGINT, 2, 3, 4, Boolean.TRUE);
		SQLTypeMapping equalmorespecific = new SQLTypeMapping(Types.BIGINT, 2, 3, 4, Boolean.TRUE);
		
		assertFalse(one.equals(two) );
		assertFalse(two.equals(one) );
		assertTrue(two.equals(two) );
		assertTrue(one.equals(one) );
		assertTrue(morespecific.equals(equalmorespecific));
		
		
		assertEquals(-1, one.compareTo(two) );
		assertEquals(1, two.compareTo(one) );
		
		assertEquals(1, generic.compareTo(one) );
		assertEquals(1, generic.compareTo(two) );
		assertEquals(1, generic.compareTo(specific) );
		
		assertEquals(-1, specific.compareTo(one) );
		assertEquals(-1, specific.compareTo(two) );
		assertEquals(-1, specific.compareTo(generic) );
		assertEquals(1, specific.compareTo(morespecific) );
		assertEquals(-1, morespecific.compareTo(specific) );
		
	}
	public void testSqlTypeOverride() {
		
		OverrideRepository or = buildOverrideRepository(); 
		
		SQLTypeMapping sqltype = new SQLTypeMapping(Types.BINARY);
		
		sqltype.setLength(1);
		sqltype.setHibernateType("boolean");
		or.addTypeMapping(sqltype);
		
		sqltype = new SQLTypeMapping(Types.BIT);
		
		sqltype.setHibernateType("yes_no");
		or.addTypeMapping(sqltype);
		
		ReverseEngineeringStrategy res = or.getReverseEngineeringStrategy(null);
		assertEquals("boolean",res.columnToHibernateTypeName(null,null, Types.BINARY, 1, SQLTypeMapping.UNKNOWN_PRECISION, SQLTypeMapping.UNKNOWN_SCALE, false, false) );
		assertEquals(null,res.columnToHibernateTypeName(null,null, Types.LONGVARCHAR, 1, SQLTypeMapping.UNKNOWN_PRECISION, SQLTypeMapping.UNKNOWN_SCALE, false, false) );
		assertEquals("yes_no",res.columnToHibernateTypeName(null,null, Types.BIT, SQLTypeMapping.UNKNOWN_LENGTH, SQLTypeMapping.UNKNOWN_PRECISION, SQLTypeMapping.UNKNOWN_SCALE, false, false) );
	}
	
	public void testTableExclude() {
		TableFilter tf = new TableFilter();
		tf.setMatchName("max");
		tf.setExclude(Boolean.TRUE);
		assertTrue(tf.exclude(new TableIdentifier("max") ).booleanValue() );
		assertNull(tf.exclude(new TableIdentifier("maxnotexact") ) );
		tf.setMatchName(".*max");
		assertTrue(tf.exclude(new TableIdentifier("max") ).booleanValue() );
		assertNull(tf.exclude(new TableIdentifier("maxnotending") ) );
		assertTrue(tf.exclude(new TableIdentifier("endingWithmax") ).booleanValue() );
		tf.setMatchName("max.*");
		assertTrue(tf.exclude(new TableIdentifier("max") ).booleanValue() );
		tf.setMatchName(".*max.*");
		assertTrue(tf.exclude(new TableIdentifier("max") ).booleanValue() );
		assertNull(tf.exclude(new TableIdentifier("notxam") ) );
		assertTrue(tf.exclude(new TableIdentifier("heremaxsub") ).booleanValue() );
	}
	
	public void testColumnExclude() {
		
		OverrideRepository or = buildOverrideRepository();
		or.addResource(OVERRIDETEST_REVENG_XML);
		
		ReverseEngineeringStrategy reverseEngineeringStrategy = or.getReverseEngineeringStrategy();
		
		assertFalse(reverseEngineeringStrategy.excludeColumn(new TableIdentifier("EXCOLUMNS"), "blah"));
		assertFalse(reverseEngineeringStrategy.excludeColumn(new TableIdentifier("EXCOLUMNS"), "NAME"));
		assertTrue(reverseEngineeringStrategy.excludeColumn(new TableIdentifier("EXCOLUMNS"), "EXCOLUMN"));
		
		Table table = findTable(identifier("excolumns"));
		assertNotNull(table);
		
		assertNotNull(table.getColumn(new Column("name")));
		assertNull(table.getColumn(new Column("excolumn")));
		
	}
	
	public void testSimpleUserDefinedForeignKeys() {
		
		Table table = findTable(identifier("Orders") );
		
		Iterator foreignKeyIterator = table.getForeignKeyIterator();
		ForeignKey fk = (ForeignKey) foreignKeyIterator.next();
		assertEquals(fk.getReferencedTable().getName(), identifier("Customer") );
		
		PersistentClass classMapping = cfg.getClassMapping("Orders");
		classMapping.getProperty("customer");
		
		classMapping = cfg.getClassMapping("Customer");
		classMapping.getProperty("orderses");
			
	}
	
	public void testCompositeUserDefinedForeignKeys() {
		
		Table table = findTable(identifier("Children") );
		
		Iterator foreignKeyIterator = table.getForeignKeyIterator();
		ForeignKey fk = (ForeignKey) foreignKeyIterator.next();
		assertEquals(fk.getReferencedTable().getName(), identifier("Parent") );
		assertEquals(2, fk.getReferencedColumns().size());
		assertEquals("child_to_parent", fk.getName());
		
		PersistentClass classMapping = cfg.getClassMapping("Children");
		Property property = classMapping.getProperty("propertyParent");
		assertEquals(2,property.getColumnSpan());
		
		classMapping = cfg.getClassMapping("Parent");
		property = classMapping.getProperty("propertyChildren");	
			
	}
		
	public void testTypes() {
		
		PersistentClass classMapping = cfg.getClassMapping("MiscTypes");
		
		
		assertEquals("SomeUserType", getPropertyTypeName(classMapping.getProperty("name") ) );
		assertEquals("string", getPropertyTypeName(classMapping.getProperty("shortname") ) );
		assertEquals("yes_no", getPropertyTypeName(classMapping.getProperty("flag") ) );
		
	}
	
	public void testTableToClass() {
		
		ReverseEngineeringStrategy res = buildOverrideRepository().addResource(OVERRIDETEST_REVENG_XML).getReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy());
		
		TableIdentifier tableIdentifier = new TableIdentifier(null, null, "TblTest");
		assertEquals("org.test.Test", res.tableToClassName(tableIdentifier));		
		
		tableIdentifier = new TableIdentifier(settings.getDefaultCatalogName(), "Werd", "Testy");
		assertEquals("org.werd.Testy", res.tableToClassName(tableIdentifier));
		
		tableIdentifier = new TableIdentifier(null, null, "Nothing");
		assertEquals("Nothing", res.tableToClassName(tableIdentifier));
		
	}
	
	public void testMetaAttributes() {
		
		ReverseEngineeringStrategy res = buildOverrideRepository().addResource(OVERRIDETEST_REVENG_XML).getReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy());
		
		TableIdentifier tableIdentifier = new TableIdentifier(null, null, "TblTest");
		Map attributes = res.tableToMetaAttributes(tableIdentifier);
		assertNotNull(attributes);
		assertEquals(attributes.size(),1);
		MetaAttribute ma = (MetaAttribute) attributes.get("use-in-test");
		assertEquals(ma.getName(), "use-in-test");
		assertEquals(ma.getValue(), "true");
				
		tableIdentifier = new TableIdentifier(settings.getDefaultCatalogName(), "Werd", "Testy");
		attributes = res.tableToMetaAttributes( tableIdentifier );
		assertNotNull(attributes);
		ma = (MetaAttribute) attributes.get( "werd-meta" );
		assertEquals(ma.getName(), "werd-meta");
		assertEquals(ma.getValues().size(), 2);				
	
		tableIdentifier = new TableIdentifier(null, "Werd", "MetaTable");
		attributes = res.tableToMetaAttributes( tableIdentifier );
		assertNotNull(attributes);
		assertEquals(2, attributes.size());
		ma = (MetaAttribute) attributes.get("specific-werd");
		assertEquals(ma.getName(), "specific-werd");
		assertEquals(ma.getValue(), "a one");
		
		ma = (MetaAttribute) attributes.get( "werd-meta" );
		assertEquals(ma.getName(), "werd-meta");
		assertEquals(1, ma.getValues().size()); // as long as no inherit this should be one
		assertEquals("value three", ma.getValue());
	
		tableIdentifier = new TableIdentifier(null, null, "Nothing");
		assertEquals(null, res.tableToMetaAttributes(tableIdentifier));
		
		assertNull(res.columnToMetaAttributes(new TableIdentifier("Nothing"), "bogus"));
		assertNull(res.columnToMetaAttributes( new TableIdentifier(null, "Werd", "MetaTable"), "bogusColumn" ));
		attributes = res.columnToMetaAttributes( new TableIdentifier(null, "Werd", "MetaTable"), "MetaColumn" );
		assertEquals(1, attributes.size());
		ma = (MetaAttribute) attributes.get("specific-column");
		assertEquals("specific-column",ma.getName());
		assertEquals("yes a column with meta",ma.getValue());
		
	}
	
	protected void configure(JDBCMetaDataConfiguration configuration) {
		super.configure(configuration);		
		OverrideRepository or = new OverrideRepository();
		or.addResource(OVERRIDETEST_REVENG_XML);
		configuration.setReverseEngineeringStrategy(or.getReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy() ) );
	}

	private String getPropertyTypeName(Property property) {
		return ( (SimpleValue)property.getValue() ).getTypeName();
	}


	
	
	protected String[] getCreateSQL() {
		
		return new String[] {
				"create table dummy (id numeric(10,0) not null, primary key (id) )",
				"create table defunct_table ( id numeric(10,0) not null, name varchar(20), shortname varchar(5), flag varchar(1), dumid numeric(10,0), primary key (id), foreign key (dumid) references dummy)",                
                "create table misc_types ( id numeric(10,0) not null, name varchar(20), shortname varchar(5), flag varchar(1), primary key (id) )",
                "create table inthemiddle ( miscid numeric(10,0), defunctid numeric(10,0), foreign key (miscid) references misc_types, foreign key (defunctid) references defunct_table )",
                "create table customer ( custid varchar(10), name varchar(20) )",
                "create table orders ( orderid varchar(10), name varchar(20),  custid varchar(10), completed numeric(1,0) not null, verified numeric(1) )",
                "create table parent ( id varchar(10), name varchar(20))",
                "create table children ( id varchar(10), parentid varchar(10), name varchar(20) )",
                "create table excolumns (id varchar(12), name varchar(20), excolumn numeric(10,0) )"
		};
	}

	protected String[] getDropSQL() {
		
		return new String[]  {
				"drop table excolumns",
				"drop table parent",
				"drop table children",
				"drop table customer",
				"drop table orders",
				"drop table inthemiddle",
				"drop table misc_types",
				"drop table defunct_table",
				"drop table dummy",
				
		};
	}
	public static Test suite() {
		return new TestSuite(OverrideBinderTest.class);
	}

}
