/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2017-2020 Red Hat, Inc.
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

package org.hibernate.tool.jdbc2cfg.DefaultReverseEngineeringStrategy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.DelegatingReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.mapping.Column;
import org.junit.jupiter.api.Test;

/**
 * @author max
 * @author koen
 *
 */
public class TestCase {
	
	ReverseEngineeringStrategy rns = new DefaultReverseEngineeringStrategy();
	
	@Test
	public void testColumnKeepCase() {
		assertEquals("name", rns.columnToPropertyName(null, "name") );		
		assertEquals("nameIsValid", rns.columnToPropertyName(null, "nameIsValid") );
	}
	
	@Test
	public void testColumnUpperToLower() {
		assertEquals("name", rns.columnToPropertyName(null, "NAME") );
		assertEquals("name", rns.columnToPropertyName(null, "Name") );
	}
	
	@Test
	public void testColumnRemoveChars() {
		assertEquals("name", rns.columnToPropertyName(null, "_Name") );
		assertEquals("name", rns.columnToPropertyName(null, "_name") );
		assertEquals("name", rns.columnToPropertyName(null, "_name") );
	}
	
	@Test
	public void testColumnToCamelCase() {
		assertEquals("labelForField", rns.columnToPropertyName(null, "LABEL_FOR_FIELD") );
		assertEquals("nameToMe", rns.columnToPropertyName(null, "_name-To-Me") );
	}
	
	@Test
	public void testColumnChangeCamelCase() {
		assertEquals("labelForField", rns.columnToPropertyName(null, "LabelForField") );	
	}
	
	@Test
	public void testTableKeepCase() {
		assertEquals("SickPatients", rns.tableToClassName(new TableIdentifier("SickPatients") ) );
	}
	
	@Test
	public void testTableUpperToLower() {
		assertEquals("Patients", rns.tableToClassName(new TableIdentifier("PATIENTS") ) );
		assertEquals("Patients", rns.tableToClassName(new TableIdentifier("patients") ) );
	}
	
	@Test
	public void testTableRemoveChars() {
		assertEquals("Patients", rns.tableToClassName(new TableIdentifier("_Patients") ) );
		assertEquals("Patients", rns.tableToClassName(new TableIdentifier("_patients") ) );
		assertEquals("Patients", rns.tableToClassName(new TableIdentifier("_patients") ) );
		assertEquals("PatientInterventions", rns.tableToClassName(new TableIdentifier("_PATIENT_INTERVENTIONS") ) );
	}
	
	@Test
	public void testTableToCamelCase() {
		assertEquals("SickPatients", rns.tableToClassName(new TableIdentifier("Sick_Patients") ) );
		assertEquals("SickPatients", rns.tableToClassName(new TableIdentifier("_Sick-Patients") ) );
	}
	
	@Test
	public void testTableKeepCamelCase() {
		assertEquals("SickPatients", rns.tableToClassName(new TableIdentifier("SickPatients") ) );
	}
    
	@Test
    public void testBasicForeignKeyNames() {
        assertEquals("products", rns.foreignKeyToCollectionName("something", new TableIdentifier("product"), null, new TableIdentifier("order"), null, true ) );
        assertEquals("willies", rns.foreignKeyToCollectionName("something", new TableIdentifier("willy"), null, new TableIdentifier("order"), null, true ) );
		assertEquals("boxes", rns.foreignKeyToCollectionName("something", new TableIdentifier("box"), null, new TableIdentifier("order"), null, true ) );
        assertEquals("order", rns.foreignKeyToEntityName("something", new TableIdentifier("product"), null, new TableIdentifier("order"), null, true ) );
    }
	
	@Test
    public void testCustomClassNameStrategyWithCollectionName() {
    	
    	ReverseEngineeringStrategy custom = new DelegatingReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy()) {
    		public String tableToClassName(TableIdentifier tableIdentifier) {
    			return super.tableToClassName( tableIdentifier ) + "Impl";
    		}
    	};

    	custom.setSettings( new ReverseEngineeringSettings(custom) );
    	
    	TableIdentifier productTable = new TableIdentifier("product");
		assertEquals("ProductImpl", custom.tableToClassName( productTable ));
    	
        assertEquals("productImpls", custom.foreignKeyToCollectionName("something", productTable, null, new TableIdentifier("order"), null, true ) );
        /*assertEquals("willies", custom.foreignKeyToCollectionName("something", new TableIdentifier("willy"), null, new TableIdentifier("order"), null, true ) );
		assertEquals("boxes", custom.foreignKeyToCollectionName("something", new TableIdentifier("box"), null, new TableIdentifier("order"), null, true ) );
        assertEquals("order", custom.foreignKeyToEntityName("something", productTable, null, new TableIdentifier("order"), null, true ) );*/
    }
    
	@Test
    public void testForeignKeyNamesToPropertyNames() {
    	
    	String fkName = "something";
		TableIdentifier fromTable = new TableIdentifier("company");
		List<Column> fromColumns = new ArrayList<Column>();
		
		TableIdentifier toTable = new TableIdentifier("address");
		List<Column> toColumns = new ArrayList<Column>();
		
		assertEquals("address", rns.foreignKeyToEntityName(fkName, fromTable, fromColumns, toTable, toColumns, true) );
		assertEquals("companies", rns.foreignKeyToCollectionName(fkName, fromTable, fromColumns, toTable, toColumns, true) );
		
		fkName = "billing";
		fromColumns.clear();		
		fromColumns.add(new Column("bill_adr") );
		assertEquals("addressByBillAdr", rns.foreignKeyToEntityName(fkName, fromTable, fromColumns, toTable, toColumns, false) );
		assertEquals("companiesForBillAdr", rns.foreignKeyToCollectionName(fkName, fromTable, fromColumns, toTable, toColumns, false) );
		
		fromColumns.add(new Column("bill_adrtype") );
		assertEquals("addressByBilling", rns.foreignKeyToEntityName(fkName, fromTable, fromColumns, toTable, toColumns, false) );
		assertEquals("companiesForBilling", rns.foreignKeyToCollectionName(fkName, fromTable, fromColumns, toTable, toColumns, false) );
    }

	@Test
    public void testPreferredTypes() {
    	assertEquals("int",rns.columnToHibernateTypeName(null, "bogus",Types.INTEGER,0,0,0, false, false));
    	assertEquals("java.lang.Integer",rns.columnToHibernateTypeName(null, "bogus",Types.INTEGER,0,0,0, true, false), "because nullable it should not be int");
    	assertEquals("java.lang.Integer",rns.columnToHibernateTypeName(null, "bogus",Types.NUMERIC,0,9,0, true, false));
       	assertEquals("java.lang.Integer",rns.columnToHibernateTypeName(null, "bogus",Types.INTEGER,0,0,0, true, false));			
       	assertEquals("serializable",rns.columnToHibernateTypeName(new TableIdentifier("sdf"), "bogus",-567,0,0,0, false, false));
       	
       	assertEquals("string",rns.columnToHibernateTypeName(new TableIdentifier("sdf"), "bogus",12,0,0,0, false, false));
    }
    
	@Test
    public void testReservedKeywordsHandling() {
    	assertEquals("class_", rns.columnToPropertyName(new TableIdentifier("blah"), "class"));    	
    }
     
}
