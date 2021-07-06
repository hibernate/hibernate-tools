/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2004-2021 Red Hat, Inc.
 *
 * Licensed under the GNU Lesser General Public License (LGPL), 
 * version 2.1 or later (the "License").
 * You may not use this file except in compliance with the License.
 * You may read the licence in the 'lgpl.txt' file in the root folder of 
 * project or obtain a copy at
 *
 *     http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import org.junit.jupiter.api.Test;

/**
 * @author max
 * @author koen
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
	
	@Test
	public void testReadOnlySpecificSchema() {
		OverrideRepository or = new OverrideRepository();
		or.addSchemaSelection(new SchemaSelection(null, "OVRTEST"));
		ReverseEngineeringStrategy res = or.getReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy());
		List<Table> tables = getTables(MetadataDescriptorFactory
				.createJdbcDescriptor(res, null, true)
				.createMetadata());
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

	@Test
	public void testOverlapping() {	
		OverrideRepository or = new OverrideRepository();
		or.addSchemaSelection(new SchemaSelection(null, "OVRTEST"));
		or.addSchemaSelection(new SchemaSelection(null, null, "MASTER"));
		or.addSchemaSelection(new SchemaSelection(null, null, "CHILD"));
		ReverseEngineeringStrategy res = 
				or.getReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy());
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
	
	@Test
	public void testUseDefault() {
		Properties properties = new Properties();
		properties.setProperty(Environment.DEFAULT_SCHEMA, "OVRTEST");
		properties.setProperty(Environment.DEFAULT_SCHEMA, "OVRTEST");
		List<Table> tables = getTables(MetadataDescriptorFactory
				.createJdbcDescriptor(null, properties, true)
				.createMetadata());
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
