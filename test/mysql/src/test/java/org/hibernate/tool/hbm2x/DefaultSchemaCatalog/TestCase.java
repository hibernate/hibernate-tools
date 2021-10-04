/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.hbm2x.DefaultSchemaCatalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.SchemaSelection;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


/**
 * @author max
 * @author koen
 * TODO HBX-1399: Investigate how to make org.hibernate.tool.hbm2x.DefaultSchemaCatalog.TestCase run on MySQL
 */
public class TestCase {
	
	@BeforeEach
	public void setUp() {
		JdbcUtil.createDatabase(this);
	}
	
	@AfterEach
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}
	
	@Disabled
	@Test
	public void testReadOnlySpecificSchema() {		
		OverrideRepository or = new OverrideRepository();
		or.addSchemaSelection(new SchemaSelection(null, "OVRTEST"));
		ReverseEngineeringStrategy res = or.getReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy());
		Metadata metadata = MetadataDescriptorFactory
				.createJdbcDescriptor(res, null, true)
				.createMetadata();
		List<Table> tables = getTables(metadata);
		assertEquals(2,tables.size());	
		Table catchild = (Table) tables.get(0);
		Table catmaster = (Table) tables.get(1);	
		if(catchild.getName().equals("CATMASTER")) {
			catchild = (Table) tables.get(1);
			catmaster = (Table) tables.get(0);
		} 	
		TableIdentifier masterid = TableIdentifier.create(catmaster);
		TableIdentifier childid = TableIdentifier.create(catchild);
		assertEquals(new TableIdentifier(null, "OVRTEST", "CATMASTER"), masterid);
		assertEquals(new TableIdentifier(null, "OVRTEST", "CATCHILD"), childid);	
	}

	@Disabled
	@Test
	public void testOverlapping() {	
		OverrideRepository or = new OverrideRepository();
		or.addSchemaSelection(new SchemaSelection(null, "OVRTEST"));
		or.addSchemaSelection(new SchemaSelection(null, null, "MASTER"));
		or.addSchemaSelection(new SchemaSelection(null, null, "CHILD"));
		ReverseEngineeringStrategy res = or.getReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy());
		Metadata metadata = MetadataDescriptorFactory
				.createJdbcDescriptor(res, null, true)
				.createMetadata();
		Set<TableIdentifier> tables = new HashSet<TableIdentifier>();
		Iterator<Table> iter = metadata.collectTableMappings().iterator();
		while(iter.hasNext()) {
			Table element = iter.next();
			boolean added = tables.add(TableIdentifier.create(element));
			if(!added) 
				fail("duplicate table found for " + element); 
		}
		assertEquals(4,tables.size());					
	}
	
	@Disabled
	@Test
	public void testUseDefault() {
		Properties properties = new Properties();
		properties.setProperty(Environment.DEFAULT_SCHEMA, "OVRTEST");
		properties.setProperty(Environment.DEFAULT_SCHEMA, "OVRTEST");
		Metadata metadata = MetadataDescriptorFactory
				.createJdbcDescriptor(null, properties, true)
				.createMetadata();
		List<Table> tables = getTables(metadata);
		assertEquals(2,tables.size());
		Table catchild = (Table) tables.get(0);
		Table catmaster = (Table) tables.get(1);
		if(catchild.getName().equals("CATMASTER")) {
			catchild = (Table) tables.get(1);
			catmaster = (Table) tables.get(0);
		} 	
		TableIdentifier masterid = TableIdentifier.create(catmaster);
		TableIdentifier childid = TableIdentifier.create(catchild);
		assertEquals(new TableIdentifier(null, null, "CATMASTER"), masterid, "jdbcreader has not nulled out according to default schema");
		assertEquals(new TableIdentifier(null, null, "CATCHILD"), childid, "jdbcreader has not nulled out according to default schema");
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

}
