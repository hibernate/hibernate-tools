/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import java.sql.Types;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
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
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author max
 * @author koen
 *
 */
public class OverrideBinderTest {
	
	private static final String OVERRIDETEST_REVENG_XML = "org/hibernate/tool/test/jdbc2cfg/overridetest.reveng.xml";
	private static final String TEST_REVENG_XML = "org/hibernate/tool/test/jdbc2cfg/test.reveng.xml";
	private static final String DOC_REVENG_XML = "org/hibernate/tool/test/jdbc2cfg/docexample.reveng.xml";
	private static final String SCHEMA_REVENG_XML = "org/hibernate/tool/test/jdbc2cfg/schemaselection.reveng.xml";

	private static final String[] CREATE_SQL = new String[] {
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
	
	private static final String[] DROP_SQL = new String[] {
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

	private JDBCMetaDataConfiguration jmdcfg = null;

	@Before
	public void setUp() {
		JdbcUtil.establishJdbcConnection(this);
		JdbcUtil.executeSql(this, CREATE_SQL);
		jmdcfg = new JDBCMetaDataConfiguration();
		OverrideRepository or = new OverrideRepository();
		or.addResource(OVERRIDETEST_REVENG_XML);
		jmdcfg.setReverseEngineeringStrategy(
				or.getReverseEngineeringStrategy(
						new DefaultReverseEngineeringStrategy() ) );
		jmdcfg.readFromJDBC();
	}

	@After
	public void tearDown() {
		JdbcUtil.executeSql(this, DROP_SQL);
		JdbcUtil.releaseJdbcConnection(this);
	}

	@Test
	public void testReadTypeMappings() {
		OverrideRepository or = new OverrideRepository();
				
		or.addResource(TEST_REVENG_XML);
		ReverseEngineeringStrategy repository = or.getReverseEngineeringStrategy(null);

		Assert.assertEquals("int", repository.columnToHibernateTypeName(null, null, Types.INTEGER, 5, SQLTypeMapping.UNKNOWN_PRECISION, SQLTypeMapping.UNKNOWN_SCALE, false, false) );
		Assert.assertEquals("long", repository.columnToHibernateTypeName(null, null, Types.INTEGER, SQLTypeMapping.UNKNOWN_LENGTH, SQLTypeMapping.UNKNOWN_PRECISION, SQLTypeMapping.UNKNOWN_SCALE, false, false) );
		Assert.assertEquals("byte[]", repository.columnToHibernateTypeName(null, null, Types.BIGINT, SQLTypeMapping.UNKNOWN_LENGTH, 5, SQLTypeMapping.UNKNOWN_SCALE, false, false) );
		Assert.assertEquals("java.math.BigInteger", repository.columnToHibernateTypeName(null, null, Types.BIGINT, SQLTypeMapping.UNKNOWN_LENGTH, 2, 3, false, false) );
		Assert.assertEquals("string", repository.columnToHibernateTypeName(null, null, Types.CHAR, 1, 10, SQLTypeMapping.UNKNOWN_SCALE, false, false) );
		//Assert.assertEquals("string", repository.jdbcToHibernateType(Types.CHAR, 1, SQLTypeMapping.UNKNOWN_PRECISION, SQLTypeMapping.UNKNOWN_SCALE) );
		
		Assert.assertEquals("Long", repository.columnToHibernateTypeName(null, null, Types.NUMERIC, 1, 10, 0, false, false) );
		Assert.assertEquals("java.lang.Long", repository.columnToHibernateTypeName(null, null, Types.NUMERIC, 1, 10, SQLTypeMapping.UNKNOWN_SCALE, false, false) );
		Assert.assertEquals("java.lang.Long", repository.columnToHibernateTypeName(null, null, Types.NUMERIC, 1, 10, 43, false, false) );
		
		// nullability
		Assert.assertEquals("nonnull-float", repository.columnToHibernateTypeName(null, null, Types.FLOAT, SQLTypeMapping.UNKNOWN_LENGTH, SQLTypeMapping.UNKNOWN_PRECISION,17,false, false) );
		Assert.assertEquals("null-float", repository.columnToHibernateTypeName(null, null, Types.FLOAT, SQLTypeMapping.UNKNOWN_LENGTH, SQLTypeMapping.UNKNOWN_PRECISION,17,true, false) );
		
		Assert.assertEquals("onlynotnull", repository.columnToHibernateTypeName(null, null, Types.FLOAT, SQLTypeMapping.UNKNOWN_LENGTH, SQLTypeMapping.UNKNOWN_PRECISION,SQLTypeMapping.UNKNOWN_SCALE,false, false) );
		Assert.assertEquals("donotcare", repository.columnToHibernateTypeName(null, null, Types.FLOAT, SQLTypeMapping.UNKNOWN_LENGTH, SQLTypeMapping.UNKNOWN_PRECISION,SQLTypeMapping.UNKNOWN_SCALE,true, false) );
		
		
	}
	
	@Test
	public void testDocExample() {
		OverrideRepository or = new OverrideRepository();
		
		or.addResource(DOC_REVENG_XML);
		ReverseEngineeringStrategy repository = or.getReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy());

		Assert.assertEquals("int", repository.columnToHibernateTypeName(null, "ID", Types.INTEGER, SQLTypeMapping.UNKNOWN_LENGTH, 10, SQLTypeMapping.UNKNOWN_SCALE, false, false) );
		Assert.assertEquals("your.package.TrimStringUserType", repository.columnToHibernateTypeName(null, "NAME", Types.VARCHAR, 30, SQLTypeMapping.UNKNOWN_PRECISION, SQLTypeMapping.UNKNOWN_SCALE, true, false) );
		Assert.assertEquals("char", repository.columnToHibernateTypeName(null, "INITIAL", Types.VARCHAR, 1, SQLTypeMapping.UNKNOWN_PRECISION, SQLTypeMapping.UNKNOWN_SCALE, true, false) );
		Assert.assertEquals("java.lang.Character", repository.columnToHibernateTypeName(null, "CODE", Types.VARCHAR, 1, SQLTypeMapping.UNKNOWN_PRECISION, SQLTypeMapping.UNKNOWN_SCALE, false, false) );
		Assert.assertEquals("big_decimal", repository.columnToHibernateTypeName(null, "SALARY", Types.NUMERIC, SQLTypeMapping.UNKNOWN_LENGTH, 15, SQLTypeMapping.UNKNOWN_SCALE, true, false) );
		Assert.assertEquals("java.lang.Long", repository.columnToHibernateTypeName(null, "AGE", Types.NUMERIC, SQLTypeMapping.UNKNOWN_LENGTH, 3, SQLTypeMapping.UNKNOWN_SCALE, true, false) );
		
	}
	
	@Test
	public void testSchemaSelection() {
		OverrideRepository or = new OverrideRepository();
		
		or.addResource(SCHEMA_REVENG_XML);
		ReverseEngineeringStrategy repository = or.getReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy());

		List<?> schemaSelectors = repository.getSchemaSelections();
		
		Assert.assertNotNull(schemaSelectors);
		Assert.assertEquals(4,schemaSelectors.size());
		
		SchemaSelection ss;
		ss = (SchemaSelection) schemaSelectors.get(0);
		Assert.assertEquals(null,ss.getMatchCatalog());
		Assert.assertEquals(null,ss.getMatchSchema());
		Assert.assertEquals(null,ss.getMatchTable());
		
		ss = (SchemaSelection) schemaSelectors.get(1);
		Assert.assertEquals(null,ss.getMatchCatalog());
		Assert.assertEquals("OVRTEST",ss.getMatchSchema());
		Assert.assertEquals(null,ss.getMatchTable());
		
		ss = (SchemaSelection) schemaSelectors.get(2);
		Assert.assertEquals("UBERCATALOG",ss.getMatchCatalog());
		Assert.assertEquals("OVRTEST",ss.getMatchSchema());
		Assert.assertEquals(null,ss.getMatchTable());
		
		ss = (SchemaSelection) schemaSelectors.get(3);
		Assert.assertEquals("PUBLIC.*",ss.getMatchCatalog());
		Assert.assertEquals("OVRTEST",ss.getMatchSchema());
		Assert.assertEquals(".*",ss.getMatchTable());
		
		JDBCMetaDataConfiguration configuration = new JDBCMetaDataConfiguration();
		
		OverrideRepository ox = new OverrideRepository();
		ox.addSchemaSelection(new SchemaSelection(null, null, "DUMMY.*"));
		configuration.setReverseEngineeringStrategy(ox.getReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy()));
		configuration.readFromJDBC();
		
		Iterator<Table> tableMappings = configuration.getMetadata().collectTableMappings().iterator();
		Table t = (Table) tableMappings.next();
		Assert.assertEquals(t.getName(), "DUMMY");
		Assert.assertFalse(tableMappings.hasNext());
	}

	@Test
	public void testColumnTypeMappings() {
		OverrideRepository or = new OverrideRepository();
		
		or.addResource(OVERRIDETEST_REVENG_XML);
		ReverseEngineeringStrategy repository = or.getReverseEngineeringStrategy(null);

		Assert.assertNull(repository.columnToHibernateTypeName(new TableIdentifier("blah"), "bogus",0,0,0,0, false, false));
		Assert.assertNull(repository.columnToHibernateTypeName(new TableIdentifier("ORDERS"), "CUSTID",0,0,0,0, false, false));
		Assert.assertEquals("string", repository.columnToHibernateTypeName(new TableIdentifier(null, null, "ORDERS"), "NAME",0,0,0,0, false, false));
		
		PersistentClass classMapping = jmdcfg.getMetadata().getEntityBinding("Orders");
		
		Property property = classMapping.getProperty("completed");		
		Assert.assertEquals("boolean because of not null", "boolean", ((SimpleValue)property.getValue()).getTypeName());
		
		property = classMapping.getProperty("verified");
		Assert.assertEquals("java.lang.Boolean because of null","java.lang.Boolean", ((SimpleValue)property.getValue()).getTypeName());
		
		classMapping = jmdcfg.getMetadata().getEntityBinding("MiscTypes");
		
		property = classMapping.getIdentifierProperty();
		
		Assert.assertFalse(((SimpleValue)property.getValue()).isNullable());
		Assert.assertEquals("java.lang.Long because of primary key", "java.lang.Long", ((SimpleValue)property.getValue()).getTypeName());
	}

	@Test
	public void testColumnPropertyNameMappings() {
		OverrideRepository or = new OverrideRepository();
		
		or.addResource(OVERRIDETEST_REVENG_XML);
		ReverseEngineeringStrategy repository = or.getReverseEngineeringStrategy(null);

		Assert.assertNull(repository.columnToPropertyName(new TableIdentifier("blah"), "bogus"));
		Assert.assertNull(repository.columnToPropertyName(new TableIdentifier("ORDERS"), "cust_id"));
		Assert.assertEquals("orderName", repository.columnToPropertyName(new TableIdentifier(null, null, "ORDERS"), "NAME"));
	}
	
	@Test
	public void testMetaAttributeMappings() {
		PersistentClass classMapping = jmdcfg.getMetadata().getEntityBinding( "Orders" );
		Assert.assertEquals("order table value", classMapping.getMetaAttribute( "order-meta" ).getValue());
		
		Property property = classMapping.getProperty("orderName");
		Assert.assertEquals("order column value", property.getMetaAttribute( "order-meta" ).getValue());
		//TODO: test sequence of meta
	}
	
	@Test
	public void testIdGenerator() {
		OverrideRepository or = new OverrideRepository();
		
		or.addResource(OVERRIDETEST_REVENG_XML);
		ReverseEngineeringStrategy repository = or.getReverseEngineeringStrategy(null);

		TableIdentifier miscTable = new TableIdentifier(null,null, "MISC_TYPES");
		Assert.assertEquals("sequence",repository.getTableIdentifierStrategyName(miscTable));
		Map<?,?> props = repository.getTableIdentifierProperties(miscTable);
		Assert.assertEquals("seq_table", props.get("table"));
		
		Assert.assertNull(repository.getTableIdentifierStrategyName(new TableIdentifier("blah")));
		Assert.assertNull(repository.getTableIdentifierProperties(new TableIdentifier("blah")));
		TableIdentifier ordersTable = new TableIdentifier(null,null, "ORDERS");
		
		Assert.assertEquals("customOrderId", repository.tableToIdentifierPropertyName(ordersTable));
		Assert.assertEquals(null, repository.tableToIdentifierPropertyName(new TableIdentifier("blah")));
		
		Assert.assertEquals("CustomOID", repository.tableToCompositeIdName(ordersTable));
		Assert.assertEquals(null, repository.tableToCompositeIdName(new TableIdentifier("blah")));
		
		List<String> primaryKeyColumnNames = repository.getPrimaryKeyColumnNames(new TableIdentifier("blah"));
		Assert.assertNull(primaryKeyColumnNames);
		
		primaryKeyColumnNames = repository.getPrimaryKeyColumnNames(ordersTable);
		Assert.assertNotNull(primaryKeyColumnNames);
		Assert.assertEquals(2, primaryKeyColumnNames.size());
		Assert.assertEquals("ORDERID", primaryKeyColumnNames.get(0));
		Assert.assertEquals("CUSTID", primaryKeyColumnNames.get(1));
		Assert.assertFalse(repository.excludeColumn(ordersTable, "CUSTID"));
		
		// applied
		PersistentClass classMapping = jmdcfg.getMetadata().getEntityBinding("Orders");
		SimpleValue sv = (SimpleValue) classMapping.getIdentifier();
		Assert.assertEquals("CustomOID", ((Component)sv).getComponentClassName());
		
		Assert.assertEquals(2,classMapping.getIdentifierProperty().getColumnSpan());	
		
		Property identifierProperty = classMapping.getIdentifierProperty();
		Assert.assertEquals("customOrderId", identifierProperty.getName());
		
		classMapping = jmdcfg.getMetadata().getEntityBinding("MiscTypes");
		sv = (SimpleValue) classMapping.getIdentifier(); 
		Assert.assertEquals("sequence", sv.getIdentifierGeneratorStrategy()); // will fail if default schema is not set since then there is no match in the override binder		
		
		Assert.assertNotNull(sv.getIdentifierGeneratorProperties());
		Assert.assertEquals("seq_table", sv.getIdentifierGeneratorProperties().getProperty("table"));
		
	}
	
	@Test
	public void testReadExcludeTables() {
		OverrideRepository or = new OverrideRepository();
		
		or.addResource(OVERRIDETEST_REVENG_XML);
		ReverseEngineeringStrategy repository = or.getReverseEngineeringStrategy(null);
		
		Assert.assertTrue(repository.excludeTable(new TableIdentifier(null,null, "DoNotWantIt") ) );
		Assert.assertFalse(repository.excludeTable(new TableIdentifier(null,null, "NotListedThere") ) );
		Assert.assertFalse(repository.excludeTable(new TableIdentifier("cat","sch", "WantedTable") ) );
		Assert.assertFalse(repository.excludeTable(new TableIdentifier("BAD","SCHEMA", "WantedTable") ) ); 
		Assert.assertTrue(repository.excludeTable(new TableIdentifier("BAD","SCHEMA", "SomethingElse") ) );
		
	}
	
	@Test
	public void testReadPackageName() {
		OverrideRepository or = new OverrideRepository();
		
		or.addResource(OVERRIDETEST_REVENG_XML);
		ReverseEngineeringStrategy repository = or.getReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy());
		
		Assert.assertEquals("org.werd.Q", repository.tableToClassName(new TableIdentifier("q","Werd", "Q") ) );
		Assert.assertEquals("Notknown", repository.tableToClassName(new TableIdentifier(null,null, "notknown") ) );
		
		Assert.assertEquals("org.werd.MyWorld", repository.tableToClassName(new TableIdentifier(null,"Werd", "TBL_PKG") ) );
		Assert.assertEquals("other.MyWorld", repository.tableToClassName(new TableIdentifier(null,"Werd", "TBL_OTHER") ) );
		
	}
	
	@Test
	public void testRevEngExclude() {
		
		Assert.assertNull(jmdcfg.getTable(JdbcUtil.toIdentifier(this, "defunct_table") ) );
		Table foundTable = jmdcfg.getTable(JdbcUtil.toIdentifier(this, "inthemiddle") );
		Assert.assertNotNull(foundTable);
		Iterator<?> fkiter = foundTable.getForeignKeyIterator();
		ForeignKey fk1 = (ForeignKey) fkiter.next();
		Assert.assertNotNull(fk1);
		Assert.assertFalse(fkiter.hasNext() );
		
		
	}
	
	@Test
	public void testSQLTypeMappingComparisons() {
		SQLTypeMapping one = new SQLTypeMapping(Types.BIGINT, 5, SQLTypeMapping.UNKNOWN_PRECISION, SQLTypeMapping.UNKNOWN_SCALE,SQLTypeMapping.UNKNOWN_NULLABLE);
		SQLTypeMapping two = new SQLTypeMapping(Types.BIGINT, SQLTypeMapping.UNKNOWN_LENGTH, 3, SQLTypeMapping.UNKNOWN_SCALE, SQLTypeMapping.UNKNOWN_NULLABLE);
		SQLTypeMapping generic = new SQLTypeMapping(Types.BIGINT, SQLTypeMapping.UNKNOWN_LENGTH, SQLTypeMapping.UNKNOWN_PRECISION, SQLTypeMapping.UNKNOWN_SCALE, SQLTypeMapping.UNKNOWN_NULLABLE);
		SQLTypeMapping specific = new SQLTypeMapping(Types.BIGINT, 2, 3, 4, SQLTypeMapping.UNKNOWN_NULLABLE);
		SQLTypeMapping morespecific = new SQLTypeMapping(Types.BIGINT, 2, 3, 4, Boolean.TRUE);
		SQLTypeMapping equalmorespecific = new SQLTypeMapping(Types.BIGINT, 2, 3, 4, Boolean.TRUE);
		
		Assert.assertFalse(one.equals(two) );
		Assert.assertFalse(two.equals(one) );
		Assert.assertTrue(two.equals(two) );
		Assert.assertTrue(one.equals(one) );
		Assert.assertTrue(morespecific.equals(equalmorespecific));
		
		
		Assert.assertEquals(-1, one.compareTo(two) );
		Assert.assertEquals(1, two.compareTo(one) );
		
		Assert.assertEquals(1, generic.compareTo(one) );
		Assert.assertEquals(1, generic.compareTo(two) );
		Assert.assertEquals(1, generic.compareTo(specific) );
		
		Assert.assertEquals(-1, specific.compareTo(one) );
		Assert.assertEquals(-1, specific.compareTo(two) );
		Assert.assertEquals(-1, specific.compareTo(generic) );
		Assert.assertEquals(1, specific.compareTo(morespecific) );
		Assert.assertEquals(-1, morespecific.compareTo(specific) );
		
	}
	
	@Test
	public void testSqlTypeOverride() {
		
		OverrideRepository or = new OverrideRepository();
		
		SQLTypeMapping sqltype = new SQLTypeMapping(Types.BINARY);
		
		sqltype.setLength(1);
		sqltype.setHibernateType("boolean");
		or.addTypeMapping(sqltype);
		
		sqltype = new SQLTypeMapping(Types.BIT);
		
		sqltype.setHibernateType("yes_no");
		or.addTypeMapping(sqltype);
		
		ReverseEngineeringStrategy res = or.getReverseEngineeringStrategy(null);
		Assert.assertEquals("boolean",res.columnToHibernateTypeName(null,null, Types.BINARY, 1, SQLTypeMapping.UNKNOWN_PRECISION, SQLTypeMapping.UNKNOWN_SCALE, false, false) );
		Assert.assertEquals(null,res.columnToHibernateTypeName(null,null, Types.LONGVARCHAR, 1, SQLTypeMapping.UNKNOWN_PRECISION, SQLTypeMapping.UNKNOWN_SCALE, false, false) );
		Assert.assertEquals("yes_no",res.columnToHibernateTypeName(null,null, Types.BIT, SQLTypeMapping.UNKNOWN_LENGTH, SQLTypeMapping.UNKNOWN_PRECISION, SQLTypeMapping.UNKNOWN_SCALE, false, false) );
	}
	
	@Test
	public void testTableExclude() {
		TableFilter tf = new TableFilter();
		tf.setMatchName("max");
		tf.setExclude(Boolean.TRUE);
		Assert.assertTrue(tf.exclude(new TableIdentifier("max") ).booleanValue() );
		Assert.assertNull(tf.exclude(new TableIdentifier("maxnotexact") ) );
		tf.setMatchName(".*max");
		Assert.assertTrue(tf.exclude(new TableIdentifier("max") ).booleanValue() );
		Assert.assertNull(tf.exclude(new TableIdentifier("maxnotending") ) );
		Assert.assertTrue(tf.exclude(new TableIdentifier("endingWithmax") ).booleanValue() );
		tf.setMatchName("max.*");
		Assert.assertTrue(tf.exclude(new TableIdentifier("max") ).booleanValue() );
		tf.setMatchName(".*max.*");
		Assert.assertTrue(tf.exclude(new TableIdentifier("max") ).booleanValue() );
		Assert.assertNull(tf.exclude(new TableIdentifier("notxam") ) );
		Assert.assertTrue(tf.exclude(new TableIdentifier("heremaxsub") ).booleanValue() );
	}
	
	@Test
	public void testColumnExclude() {
		
		OverrideRepository or = new OverrideRepository();
		or.addResource(OVERRIDETEST_REVENG_XML);
		
		ReverseEngineeringStrategy reverseEngineeringStrategy = or.getReverseEngineeringStrategy();
		
		Assert.assertFalse(reverseEngineeringStrategy.excludeColumn(new TableIdentifier("EXCOLUMNS"), "blah"));
		Assert.assertFalse(reverseEngineeringStrategy.excludeColumn(new TableIdentifier("EXCOLUMNS"), "NAME"));
		Assert.assertTrue(reverseEngineeringStrategy.excludeColumn(new TableIdentifier("EXCOLUMNS"), "EXCOLUMN"));
		
		Table table = jmdcfg.getTable(JdbcUtil.toIdentifier(this, "excolumns"));
		Assert.assertNotNull(table);
		
		Assert.assertNotNull(table.getColumn(new Column("name")));
		Assert.assertNull(table.getColumn(new Column("excolumn")));
		
	}
	
	@Test
	public void testSimpleUserDefinedForeignKeys() {
		
		Table table = jmdcfg.getTable(JdbcUtil.toIdentifier(this, "Orders") );
		
		Iterator<?> foreignKeyIterator = table.getForeignKeyIterator();
		ForeignKey fk = (ForeignKey) foreignKeyIterator.next();
		Assert.assertEquals(fk.getReferencedTable().getName(), JdbcUtil.toIdentifier(this, "Customer") );
		
		PersistentClass classMapping = jmdcfg.getMetadata().getEntityBinding("Orders");
		classMapping.getProperty("customer");
		
		classMapping = jmdcfg.getMetadata().getEntityBinding("Customer");
		classMapping.getProperty("orderses");
			
	}
	
	@Test
	public void testCompositeUserDefinedForeignKeys() {
		
		Table table = jmdcfg.getTable(JdbcUtil.toIdentifier(this, "Children") );
		
		Iterator<?> foreignKeyIterator = table.getForeignKeyIterator();
		ForeignKey fk = (ForeignKey) foreignKeyIterator.next();
		Assert.assertEquals(fk.getReferencedTable().getName(), JdbcUtil.toIdentifier(this, "Parent") );
		Assert.assertEquals(2, fk.getReferencedColumns().size());
		Assert.assertEquals("child_to_parent", fk.getName());
		
		PersistentClass classMapping = jmdcfg.getMetadata().getEntityBinding("Children");
		Property property = classMapping.getProperty("propertyParent");
		Assert.assertEquals(2,property.getColumnSpan());
		
		classMapping = jmdcfg.getMetadata().getEntityBinding("Parent");
		property = classMapping.getProperty("propertyChildren");	
			
	}
		
	@Test
	public void testTypes() {		
		PersistentClass classMapping = jmdcfg.getMetadata().getEntityBinding("MiscTypes");
		Assert.assertEquals(
				"SomeUserType", 
				((SimpleValue)classMapping.getProperty("name").getValue()).getTypeName());
		Assert.assertEquals(
				"string", 
				((SimpleValue)classMapping.getProperty("shortname").getValue()).getTypeName());
		Assert.assertEquals(
				"yes_no", 
				((SimpleValue)classMapping.getProperty("flag").getValue()).getTypeName());		
	}
	
	@Test
	public void testTableToClass() {
		
		ReverseEngineeringStrategy res = new OverrideRepository().addResource(OVERRIDETEST_REVENG_XML).getReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy());
		
		TableIdentifier tableIdentifier = new TableIdentifier(null, null, "TblTest");
		Assert.assertEquals("org.test.Test", res.tableToClassName(tableIdentifier));		
		
		tableIdentifier = new TableIdentifier(
				jmdcfg.getProperty(AvailableSettings.DEFAULT_CATALOG), 
				"Werd", 
				"Testy");
		Assert.assertEquals("org.werd.Testy", res.tableToClassName(tableIdentifier));
		
		tableIdentifier = new TableIdentifier(null, null, "Nothing");
		Assert.assertEquals("Nothing", res.tableToClassName(tableIdentifier));
		
	}
	
	@Test
	public void testMetaAttributes() {
		
		ReverseEngineeringStrategy res = new OverrideRepository().addResource(OVERRIDETEST_REVENG_XML).getReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy());
		
		TableIdentifier tableIdentifier = new TableIdentifier(null, null, "TblTest");
		Map<String,MetaAttribute> attributes = res.tableToMetaAttributes(tableIdentifier);
		Assert.assertNotNull(attributes);
		Assert.assertEquals(attributes.size(),1);
		MetaAttribute ma = (MetaAttribute) attributes.get("use-in-test");
		Assert.assertEquals(ma.getName(), "use-in-test");
		Assert.assertEquals(ma.getValue(), "true");
				
		tableIdentifier = new TableIdentifier(
				jmdcfg.getProperty(AvailableSettings.DEFAULT_CATALOG), 
				"Werd", 
				"Testy");
		attributes = res.tableToMetaAttributes( tableIdentifier );
		Assert.assertNotNull(attributes);
		ma = attributes.get( "werd-meta" );
		Assert.assertEquals(ma.getName(), "werd-meta");
		Assert.assertEquals(ma.getValues().size(), 2);				
	
		tableIdentifier = new TableIdentifier(null, "Werd", "MetaTable");
		attributes = res.tableToMetaAttributes( tableIdentifier );
		Assert.assertNotNull(attributes);
		Assert.assertEquals(2, attributes.size());
		ma = attributes.get("specific-werd");
		Assert.assertEquals(ma.getName(), "specific-werd");
		Assert.assertEquals(ma.getValue(), "a one");
		
		ma = attributes.get( "werd-meta" );
		Assert.assertEquals(ma.getName(), "werd-meta");
		Assert.assertEquals(1, ma.getValues().size()); // as long as no inherit this should be one
		Assert.assertEquals("value three", ma.getValue());
	
		tableIdentifier = new TableIdentifier(null, null, "Nothing");
		Assert.assertEquals(null, res.tableToMetaAttributes(tableIdentifier));
		
		Assert.assertNull(res.columnToMetaAttributes(new TableIdentifier("Nothing"), "bogus"));
		Assert.assertNull(res.columnToMetaAttributes( new TableIdentifier(null, "Werd", "MetaTable"), "bogusColumn" ));
		attributes = res.columnToMetaAttributes( new TableIdentifier(null, "Werd", "MetaTable"), "MetaColumn" );
		Assert.assertEquals(1, attributes.size());
		ma = attributes.get("specific-column");
		Assert.assertEquals("specific-column",ma.getName());
		Assert.assertEquals("yes a column with meta",ma.getValue());
		
	}
	
}
