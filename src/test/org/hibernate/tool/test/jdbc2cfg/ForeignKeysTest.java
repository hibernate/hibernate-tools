/*
 * Created on 2004-11-24
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;
import org.hibernate.tool.hbm2ddl.SchemaExport;

/**
 * @author max
 *
 */
public class ForeignKeysTest extends JDBCMetaDataBinderTestCase {

	protected String[] getCreateSQL() {
		
		return new String[] {
				"create table master ( id char not null, name varchar(20), primary key (id) )",
				
				"create table child  ( childid character not null, masterref character, primary key (childid), foreign key (masterref) references master(id) )",
				
				"create table connection  ( conid int, name varchar(50), masterref character, childref1 character, childref2 character, primary key(conid), " +
				"constraint con2master foreign key (masterref) references master(id)," +
				"constraint childref1 foreign key  (childref1) references child(childid), " +
				"constraint childref2 foreign key  (childref2) references child(childid) " +
				")",
				// todo - link where pk is fk to something
						
		};
	}

	protected String[] getDropSQL() {
		
		return new String[]  {				
				"drop table connection",				
				"drop table child",
				"drop table master",					
		};
	}
	
	public void testMultiRefs() {
		
		Table table = getTable(identifier("connection") );
		
		ForeignKey foreignKey = getForeignKey(table, identifier("con2master") );		
		assertNotNull(foreignKey);
				
		assertEquals(toClassName("master"), foreignKey.getReferencedEntityName() );
        assertEquals(identifier("connection"), foreignKey.getTable().getName() );
		
		assertEquals(getTable(identifier("master") ), foreignKey.getReferencedTable() );
		assertNotNull(getForeignKey(table, identifier("childref1") ) );
		assertNotNull(getForeignKey(table, identifier("childref2") ) );
		assertNull(getForeignKey(table, identifier("dummy") ) );
		assertHasNext(3, table.getForeignKeyIterator() );
		
	}
	public void testMasterChild() {
		
		assertNotNull(getTable(identifier("master") ));
		Table child = getTable(identifier("child") );
		
		Iterator iterator = child.getForeignKeyIterator();
		
		ForeignKey fk = (ForeignKey) iterator.next();
		
		assertFalse("should only be one fk", iterator.hasNext() );
		
		assertEquals(1, fk.getColumnSpan() );
		
		assertSame(fk.getColumn(0), child.getColumn(new Column(identifier("masterref") ) ) );
		
		
	}

	
	
	
	public void testExport() {
		
		new SchemaExport(cfg).create(true, false);
		
	}
	
	public static Test suite() {
		return new TestSuite(ForeignKeysTest.class);
	}
}
