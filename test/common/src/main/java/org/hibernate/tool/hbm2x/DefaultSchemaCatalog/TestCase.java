/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.hbm2x.DefaultSchemaCatalog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Environment;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.api.reveng.RevengStrategy.SchemaSelection;
import org.hibernate.tool.api.reveng.TableIdentifier;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.hibernate.tool.internal.reveng.strategy.OverrideRepository;
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
	
	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);
	}
	
	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}
	
	@Test
	public void testReadOnlySpecificSchema() {
		OverrideRepository or = new OverrideRepository();
		or.addSchemaSelection(createSchemaSelection(null, "OVRTEST", null));
		RevengStrategy res = or.getReverseEngineeringStrategy(new DefaultStrategy());
		List<Table> tables = getTables(MetadataDescriptorFactory
				.createReverseEngineeringDescriptor(res, null)
				.createMetadata());
		Assert.assertEquals(2,tables.size());	
		Table catchild = (Table) tables.get(0);
		Table catmaster = (Table) tables.get(1);	
		if(catchild.getName().equals("CATMASTER")) {
			catchild = (Table) tables.get(1);
			catmaster = (Table) tables.get(0);
		} 	
		TableIdentifier masterid = TableIdentifier.create(catmaster);
		TableIdentifier childid = TableIdentifier.create(catchild);
		Assert.assertEquals(TableIdentifier.create(null, "OVRTEST", "CATMASTER"), masterid);
		Assert.assertEquals(TableIdentifier.create(null, "OVRTEST", "CATCHILD"), childid);	
	}

	@Test
	public void testOverlapping() {	
		OverrideRepository or = new OverrideRepository();
		or.addSchemaSelection(createSchemaSelection(null, "OVRTEST", null));
		or.addSchemaSelection(createSchemaSelection(null, null, "MASTER"));
		or.addSchemaSelection(createSchemaSelection(null, null, "CHILD"));
		RevengStrategy res = 
				or.getReverseEngineeringStrategy(new DefaultStrategy());
		Metadata metadata = MetadataDescriptorFactory
				.createReverseEngineeringDescriptor(res, null)
				.createMetadata();
		Set<TableIdentifier> tables = new HashSet<TableIdentifier>();
		Iterator<Table> iter = metadata.collectTableMappings().iterator();
		while(iter.hasNext()) {
			Table element = iter.next();
			boolean added = tables.add(TableIdentifier.create(element));
			if(!added) 
				Assert.fail("duplicate table found for " + element); 
		}
		Assert.assertEquals(4,tables.size());					
	}
	
	@Test
	public void testUseDefault() {
		Properties properties = new Properties();
		properties.setProperty(Environment.DEFAULT_SCHEMA, "OVRTEST");
		properties.setProperty(Environment.DEFAULT_SCHEMA, "OVRTEST");
		List<Table> tables = getTables(MetadataDescriptorFactory
				.createReverseEngineeringDescriptor(null, properties)
				.createMetadata());
		Assert.assertEquals(2,tables.size());
		Table catchild = (Table) tables.get(0);
		Table catmaster = (Table) tables.get(1);
		if(catchild.getName().equals("CATMASTER")) {
			catchild = (Table) tables.get(1);
			catmaster = (Table) tables.get(0);
		} 	
		TableIdentifier masterid = TableIdentifier.create(catmaster);
		TableIdentifier childid = TableIdentifier.create(catchild);
		Assert.assertEquals("jdbcreader has not nulled out according to default schema", TableIdentifier.create(null, null, "CATMASTER"), masterid);
		Assert.assertEquals("jdbcreader has not nulled out according to default schema", TableIdentifier.create(null, null, "CATCHILD"), childid);
	}

	private List<Table> getTables(Metadata metadata) {
		List<Table> list = new ArrayList<Table>();
		Iterator<Table> iter = metadata.collectTableMappings().iterator();
		while(iter.hasNext()) {
			Table element = iter.next();
			list.add(element);
		}
		return list;
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
