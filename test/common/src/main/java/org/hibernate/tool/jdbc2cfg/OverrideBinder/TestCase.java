/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.jdbc2cfg.OverrideBinder;

import java.sql.Types;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Environment;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.MetaAttribute;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.api.reveng.RevengStrategy.SchemaSelection;
import org.hibernate.tool.api.reveng.TableIdentifier;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.hibernate.tool.internal.reveng.strategy.OverrideRepository;
import org.hibernate.tool.internal.reveng.strategy.SQLTypeMapping;
import org.hibernate.tool.internal.reveng.strategy.TableFilter;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author max
 * @author koen
 */
public class TestCase {
	
	private static final String OVERRIDETEST_REVENG_XML = "org/hibernate/tool/jdbc2cfg/OverrideBinder/overridetest.reveng.xml";
	private static final String TEST_REVENG_XML = "org/hibernate/tool/jdbc2cfg/OverrideBinder/test.reveng.xml";
	private static final String DOC_REVENG_XML = "org/hibernate/tool/jdbc2cfg/OverrideBinder/docexample.reveng.xml";
	private static final String SCHEMA_REVENG_XML = "org/hibernate/tool/jdbc2cfg/OverrideBinder/schemaselection.reveng.xml";

	private Metadata metadata = null;

	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);
		OverrideRepository or = new OverrideRepository();
		or.addResource(OVERRIDETEST_REVENG_XML);
		RevengStrategy res = or.getReverseEngineeringStrategy(
				new DefaultStrategy() );
		metadata = MetadataDescriptorFactory
				.createReverseEngineeringDescriptor(res, null)
				.createMetadata();
	}

	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	@Test
	public void testReadTypeMappings() {
		OverrideRepository or = new OverrideRepository();
				
		or.addResource(TEST_REVENG_XML);
		RevengStrategy repository = or.getReverseEngineeringStrategy(null);

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
		RevengStrategy repository = or.getReverseEngineeringStrategy(new DefaultStrategy());

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
		RevengStrategy repository = or.getReverseEngineeringStrategy(new DefaultStrategy());

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
		
		OverrideRepository ox = new OverrideRepository();
		ox.addSchemaSelection(createSchemaSelection(null, null, "DUMMY.*"));
		RevengStrategy strategy = ox.getReverseEngineeringStrategy(new DefaultStrategy());
		Metadata md = MetadataDescriptorFactory
				.createReverseEngineeringDescriptor(strategy, null)
				.createMetadata();
		
		Iterator<Table> tableMappings = md.collectTableMappings().iterator();
		Table t = (Table) tableMappings.next();
		Assert.assertEquals(t.getName(), "DUMMY");
		Assert.assertFalse(tableMappings.hasNext());
	}

	@Test
	public void testColumnTypeMappings() {
		OverrideRepository or = new OverrideRepository();
		
		or.addResource(OVERRIDETEST_REVENG_XML);
		RevengStrategy repository = or.getReverseEngineeringStrategy(null);

		Assert.assertNull(repository.columnToHibernateTypeName(TableIdentifier.create(null, null, "blah"), "bogus",0,0,0,0, false, false));
		Assert.assertNull(repository.columnToHibernateTypeName(TableIdentifier.create(null, null, "ORDERS"), "CUSTID",0,0,0,0, false, false));
		Assert.assertEquals("string", repository.columnToHibernateTypeName(TableIdentifier.create(null, null, "ORDERS"), "NAME",0,0,0,0, false, false));
		
		PersistentClass classMapping = metadata.getEntityBinding("Orders");
		
		Property property = classMapping.getProperty("completed");		
		Assert.assertEquals("boolean because of not null", "boolean", ((SimpleValue)property.getValue()).getTypeName());
		
		property = classMapping.getProperty("verified");
		Assert.assertEquals("java.lang.Boolean because of null","java.lang.Boolean", ((SimpleValue)property.getValue()).getTypeName());
		
		classMapping = metadata.getEntityBinding("MiscTypes");
		
		property = classMapping.getIdentifierProperty();
		
		Assert.assertFalse(((SimpleValue)property.getValue()).isNullable());
		Assert.assertEquals("java.lang.Long because of primary key", "java.lang.Long", ((SimpleValue)property.getValue()).getTypeName());
	}

	@Test
	public void testColumnPropertyNameMappings() {
		OverrideRepository or = new OverrideRepository();
		
		or.addResource(OVERRIDETEST_REVENG_XML);
		RevengStrategy repository = or.getReverseEngineeringStrategy(null);

		Assert.assertNull(repository.columnToPropertyName(TableIdentifier.create(null, null, "blah"), "bogus"));
		Assert.assertNull(repository.columnToPropertyName(TableIdentifier.create(null, null, "ORDERS"), "cust_id"));
		Assert.assertEquals("orderName", repository.columnToPropertyName(TableIdentifier.create(null, null, "ORDERS"), "NAME"));
	}
	
	@Test
	public void testMetaAttributeMappings() {
		PersistentClass classMapping = metadata.getEntityBinding( "Orders" );
		Assert.assertEquals("order table value", classMapping.getMetaAttribute( "order-meta" ).getValue());
		
		Property property = classMapping.getProperty("orderName");
		Assert.assertEquals("order column value", property.getMetaAttribute( "order-meta" ).getValue());
		//TODO: test sequence of meta
	}
	
	@Test
	public void testIdGenerator() {
		OverrideRepository or = new OverrideRepository();
		
		or.addResource(OVERRIDETEST_REVENG_XML);
		RevengStrategy repository = or.getReverseEngineeringStrategy(null);

		TableIdentifier miscTable = TableIdentifier.create(null,null, "MISC_TYPES");
		Assert.assertEquals("sequence",repository.getTableIdentifierStrategyName(miscTable));
		Map<?,?> props = repository.getTableIdentifierProperties(miscTable);
		Assert.assertEquals("seq_table", props.get("table"));
		
		Assert.assertNull(repository.getTableIdentifierStrategyName(TableIdentifier.create(null, null, "blah")));
		Assert.assertNull(repository.getTableIdentifierProperties(TableIdentifier.create(null, null, "blah")));
		TableIdentifier ordersTable = TableIdentifier.create(null,null, "ORDERS");
		
		Assert.assertEquals("customOrderId", repository.tableToIdentifierPropertyName(ordersTable));
		Assert.assertEquals(null, repository.tableToIdentifierPropertyName(TableIdentifier.create(null, null, "blah")));
		
		Assert.assertEquals("CustomOID", repository.tableToCompositeIdName(ordersTable));
		Assert.assertEquals(null, repository.tableToCompositeIdName(TableIdentifier.create(null, null, "blah")));
		
		List<String> primaryKeyColumnNames = repository.getPrimaryKeyColumnNames(TableIdentifier.create(null, null, "blah"));
		Assert.assertNull(primaryKeyColumnNames);
		
		primaryKeyColumnNames = repository.getPrimaryKeyColumnNames(ordersTable);
		Assert.assertNotNull(primaryKeyColumnNames);
		Assert.assertEquals(2, primaryKeyColumnNames.size());
		Assert.assertEquals("ORDERID", primaryKeyColumnNames.get(0));
		Assert.assertEquals("CUSTID", primaryKeyColumnNames.get(1));
		Assert.assertFalse(repository.excludeColumn(ordersTable, "CUSTID"));
		
		// applied
		PersistentClass classMapping = metadata.getEntityBinding("Orders");
		SimpleValue sv = (SimpleValue) classMapping.getIdentifier();
		Assert.assertEquals("CustomOID", ((Component)sv).getComponentClassName());
		
		Assert.assertEquals(2,classMapping.getIdentifierProperty().getColumnSpan());	
		
		Property identifierProperty = classMapping.getIdentifierProperty();
		Assert.assertEquals("customOrderId", identifierProperty.getName());
		
		classMapping = metadata.getEntityBinding("MiscTypes");
		sv = (SimpleValue) classMapping.getIdentifier(); 
		Assert.assertEquals("sequence", sv.getIdentifierGeneratorStrategy()); // will fail if default schema is not set since then there is no match in the override binder		
		
		Assert.assertNotNull(sv.getIdentifierGeneratorProperties());
		Assert.assertEquals("seq_table", sv.getIdentifierGeneratorProperties().getProperty("table"));
		
	}
	
	@Test
	public void testReadExcludeTables() {
		OverrideRepository or = new OverrideRepository();
		
		or.addResource(OVERRIDETEST_REVENG_XML);
		RevengStrategy repository = or.getReverseEngineeringStrategy(null);
		
		Assert.assertTrue(repository.excludeTable(TableIdentifier.create(null,null, "DoNotWantIt") ) );
		Assert.assertFalse(repository.excludeTable(TableIdentifier.create(null,null, "NotListedThere") ) );
		Assert.assertFalse(repository.excludeTable(TableIdentifier.create("cat","sch", "WantedTable") ) );
		Assert.assertFalse(repository.excludeTable(TableIdentifier.create("BAD","SCHEMA", "WantedTable") ) ); 
		Assert.assertTrue(repository.excludeTable(TableIdentifier.create("BAD","SCHEMA", "SomethingElse") ) );
		
	}
	
	@Test
	public void testReadPackageName() {
		OverrideRepository or = new OverrideRepository();
		
		or.addResource(OVERRIDETEST_REVENG_XML);
		RevengStrategy repository = or.getReverseEngineeringStrategy(new DefaultStrategy());
		
		Assert.assertEquals("org.werd.Q", repository.tableToClassName(TableIdentifier.create("q","Werd", "Q") ) );
		Assert.assertEquals("Notknown", repository.tableToClassName(TableIdentifier.create(null,null, "notknown") ) );
		
		Assert.assertEquals("org.werd.MyWorld", repository.tableToClassName(TableIdentifier.create(null,"Werd", "TBL_PKG") ) );
		Assert.assertEquals("other.MyWorld", repository.tableToClassName(TableIdentifier.create(null,"Werd", "TBL_OTHER") ) );
		
	}
	
	@Test
	public void testRevEngExclude() {
		
		Assert.assertNull(HibernateUtil.getTable(
				metadata, 
				JdbcUtil.toIdentifier(this, "DEFUNCT_TABLE") ) );
		Table foundTable = HibernateUtil.getTable(
				metadata, 
				JdbcUtil.toIdentifier(this, "INTHEMIDDLE") );
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
		
		RevengStrategy res = or.getReverseEngineeringStrategy(null);
		Assert.assertEquals("boolean",res.columnToHibernateTypeName(null,null, Types.BINARY, 1, SQLTypeMapping.UNKNOWN_PRECISION, SQLTypeMapping.UNKNOWN_SCALE, false, false) );
		Assert.assertEquals(null,res.columnToHibernateTypeName(null,null, Types.LONGVARCHAR, 1, SQLTypeMapping.UNKNOWN_PRECISION, SQLTypeMapping.UNKNOWN_SCALE, false, false) );
		Assert.assertEquals("yes_no",res.columnToHibernateTypeName(null,null, Types.BIT, SQLTypeMapping.UNKNOWN_LENGTH, SQLTypeMapping.UNKNOWN_PRECISION, SQLTypeMapping.UNKNOWN_SCALE, false, false) );
	}
	
	@Test
	public void testTableExclude() {
		TableFilter tf = new TableFilter();
		tf.setMatchName("max");
		tf.setExclude(Boolean.TRUE);
		Assert.assertTrue(tf.exclude(TableIdentifier.create(null, null, "max") ).booleanValue() );
		Assert.assertNull(tf.exclude(TableIdentifier.create(null, null, "maxnotexact") ) );
		tf.setMatchName(".*max");
		Assert.assertTrue(tf.exclude(TableIdentifier.create(null, null, "max") ).booleanValue() );
		Assert.assertNull(tf.exclude(TableIdentifier.create(null, null, "maxnotending") ) );
		Assert.assertTrue(tf.exclude(TableIdentifier.create(null, null, "endingWithmax") ).booleanValue() );
		tf.setMatchName("max.*");
		Assert.assertTrue(tf.exclude(TableIdentifier.create(null, null, "max") ).booleanValue() );
		tf.setMatchName(".*max.*");
		Assert.assertTrue(tf.exclude(TableIdentifier.create(null, null, "max") ).booleanValue() );
		Assert.assertNull(tf.exclude(TableIdentifier.create(null, null, "notxam") ) );
		Assert.assertTrue(tf.exclude(TableIdentifier.create(null, null, "heremaxsub") ).booleanValue() );
	}
	
	@Test
	public void testColumnExclude() {
		
		OverrideRepository or = new OverrideRepository();
		or.addResource(OVERRIDETEST_REVENG_XML);
		
		RevengStrategy reverseEngineeringStrategy = or.getReverseEngineeringStrategy();
		
		Assert.assertFalse(reverseEngineeringStrategy.excludeColumn(TableIdentifier.create(null, null, "EXCOLUMNS"), "blah"));
		Assert.assertFalse(reverseEngineeringStrategy.excludeColumn(TableIdentifier.create(null, null, "EXCOLUMNS"), "NAME"));
		Assert.assertTrue(reverseEngineeringStrategy.excludeColumn(TableIdentifier.create(null, null, "EXCOLUMNS"), "EXCOLUMN"));
		
		Table table = HibernateUtil.getTable(metadata, JdbcUtil.toIdentifier(this, "EXCOLUMNS"));
		Assert.assertNotNull(table);
		
		Assert.assertNotNull(table.getColumn(new Column("name")));
		Assert.assertNull(table.getColumn(new Column("excolumn")));
		
	}
	
	@Test
	public void testSimpleUserDefinedForeignKeys() {
		
		Table table = HibernateUtil.getTable(metadata, JdbcUtil.toIdentifier(this, "ORDERS") );
		
		Iterator<?> foreignKeyIterator = table.getForeignKeyIterator();
		ForeignKey fk = (ForeignKey) foreignKeyIterator.next();
		Assert.assertEquals(fk.getReferencedTable().getName(), JdbcUtil.toIdentifier(this, "CUSTOMER") );
		
		PersistentClass classMapping = metadata.getEntityBinding("Orders");
		classMapping.getProperty("customer");
		
		classMapping = metadata.getEntityBinding("Customer");
		classMapping.getProperty("orderses");
			
	}
	
	@Test
	public void testCompositeUserDefinedForeignKeys() {
		
		Table table = HibernateUtil.getTable(metadata, JdbcUtil.toIdentifier(this, "CHILDREN") );
		
		Iterator<?> foreignKeyIterator = table.getForeignKeyIterator();
		ForeignKey fk = (ForeignKey) foreignKeyIterator.next();
		Assert.assertEquals(fk.getReferencedTable().getName(), JdbcUtil.toIdentifier(this, "PARENT") );
		Assert.assertEquals(2, fk.getReferencedColumns().size());
		Assert.assertEquals("child_to_parent", fk.getName());
		
		PersistentClass classMapping = metadata.getEntityBinding("Children");
		Property property = classMapping.getProperty("propertyParent");
		Assert.assertEquals(2,property.getColumnSpan());
		
		classMapping = metadata.getEntityBinding("Parent");
		property = classMapping.getProperty("propertyChildren");	
			
	}
		
	@Test
	public void testTypes() {		
		PersistentClass classMapping = metadata.getEntityBinding("MiscTypes");
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
		
		RevengStrategy res = new OverrideRepository().addResource(OVERRIDETEST_REVENG_XML).getReverseEngineeringStrategy(new DefaultStrategy());
		
		TableIdentifier tableIdentifier = TableIdentifier.create(null, null, "TblTest");
		Assert.assertEquals("org.test.Test", res.tableToClassName(tableIdentifier));		
		
		tableIdentifier = TableIdentifier.create(
				Environment
					.getProperties()
					.getProperty(AvailableSettings.DEFAULT_CATALOG), 
				"Werd", 
				"Testy");
		Assert.assertEquals("org.werd.Testy", res.tableToClassName(tableIdentifier));
		
		tableIdentifier = TableIdentifier.create(null, null, "Nothing");
		Assert.assertEquals("Nothing", res.tableToClassName(tableIdentifier));
		
	}
	
	@Test
	public void testMetaAttributes() {
		
		RevengStrategy res = new OverrideRepository().addResource(OVERRIDETEST_REVENG_XML).getReverseEngineeringStrategy(new DefaultStrategy());
		
		TableIdentifier tableIdentifier = TableIdentifier.create(null, null, "TblTest");
		Map<String,MetaAttribute> attributes = res.tableToMetaAttributes(tableIdentifier);
		Assert.assertNotNull(attributes);
		Assert.assertEquals(attributes.size(),1);
		MetaAttribute ma = (MetaAttribute) attributes.get("use-in-test");
		Assert.assertEquals(ma.getName(), "use-in-test");
		Assert.assertEquals(ma.getValue(), "true");
				
		tableIdentifier = TableIdentifier.create(
				Environment
					.getProperties()
					.getProperty(AvailableSettings.DEFAULT_CATALOG), 
				"Werd", 
				"Testy");
		attributes = res.tableToMetaAttributes( tableIdentifier );
		Assert.assertNotNull(attributes);
		ma = attributes.get( "werd-meta" );
		Assert.assertEquals(ma.getName(), "werd-meta");
		Assert.assertEquals(ma.getValues().size(), 2);				
	
		tableIdentifier = TableIdentifier.create(null, "Werd", "MetaTable");
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
	
		tableIdentifier = TableIdentifier.create(null, null, "Nothing");
		Assert.assertEquals(null, res.tableToMetaAttributes(tableIdentifier));
		
		Assert.assertNull(res.columnToMetaAttributes(TableIdentifier.create(null, null, "Nothing"), "bogus"));
		Assert.assertNull(res.columnToMetaAttributes( TableIdentifier.create(null, "Werd", "MetaTable"), "bogusColumn" ));
		attributes = res.columnToMetaAttributes( TableIdentifier.create(null, "Werd", "MetaTable"), "MetaColumn" );
		Assert.assertEquals(1, attributes.size());
		ma = attributes.get("specific-column");
		Assert.assertEquals("specific-column",ma.getName());
		Assert.assertEquals("yes a column with meta",ma.getValue());
		
	}
	
	private SchemaSelection createSchemaSelection(String matchCatalog, String matchSchema, String matchTable) {
		return new SchemaSelection() {
			@Override
			public String getMatchCatalog() {
				return matchCatalog;
			}
			@Override
			public String getMatchSchema() {
				return matchSchema;
			}
			@Override
			public String getMatchTable() {
				return matchTable;
			}		
		};
	}
	
}
