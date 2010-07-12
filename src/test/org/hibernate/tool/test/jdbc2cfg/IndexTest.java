/*
 * Created on 2004-11-24
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.Index;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;

/**
 * @author max
 *
 */
public class IndexTest extends JDBCMetaDataBinderTestCase {

	/* (non-Javadoc)
	 * @see org.hibernate.console.test.PopulatorTestCase#getCreateSQL()
	 */
	protected String[] getCreateSQL() {
		
		return new String[] {
				"create table withIndex (first int, second int, third int)",
				"create index myIndex on withIndex(first,third)",
				"create unique index otherIdx on withIndex(third)",
		};
	}

	protected String[] getDropSQL() {
		return new String[] {
				"drop index otherIdx",
				"drop index myIndex",
				"drop table withIndex",
		};
	}

	public void testUniqueKey() {
		
		Table table = getTable(identifier("withIndex") );
		
		UniqueKey uniqueKey = table.getUniqueKey(identifier("otherIdx") );
		assertNotNull(uniqueKey);
		
		assertEquals(1, uniqueKey.getColumnSpan() );
		
		Column keyCol = uniqueKey.getColumn(0);
		assertTrue(keyCol.isUnique() );
		
		assertSame(keyCol, table.getColumn(keyCol) );
		
	}
	
	public void testWithIndex() {
		
		Table table = getTable(identifier("withIndex") );
		
		assertEqualIdentifiers("withIndex", table.getName() );
		
		assertNull("there should be no pk", table.getPrimaryKey() );
		Iterator iterator = table.getIndexIterator();
	
		
		int cnt=0;
		while(iterator.hasNext() ) {
			iterator.next();
			cnt++;
		}
		assertEquals(1, cnt);
		
		Index index = table.getIndex(identifier("myIndex") );
		
		assertNotNull("No index ?", index);
		assertEqualIdentifiers("myIndex", index.getName() );
		
		assertEquals(2, index.getColumnSpan() );
		
		assertSame(index.getTable(), table);
		Iterator cols = index.getColumnIterator();
		Column col1 = (Column) cols.next();
		Column col2 = (Column) cols.next();
		
		assertEqualIdentifiers("first", col1.getName() );
		assertEqualIdentifiers("third", col2.getName() );
		
		Column example = new Column();
		example.setName(col2.getName() );
		assertSame("column with same name should be same instance!", table.getColumn(example), col2);			

	}
	
	public static Test suite() {
		return new TestSuite(IndexTest.class);
	}
}
