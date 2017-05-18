/*
 * Created on 2004-11-24
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import java.util.Iterator;

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Index;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;
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
public class IndexTest {

	static final String[] CREATE_SQL = new String[] {
				"CREATE TABLE WITH_INDEX (FIRST INT, SECOND INT, THIRD INT)",
				"CREATE INDEX MY_INDEX ON WITH_INDEX(FIRST,THIRD)",
				"CREATE UNIQUE INDEX OTHER_IDX on WITH_INDEX(THIRD)",
		};

	static final String[] DROP_SQL = new String[] {
				"DROP TABLE WITH_INDEX"
		};

	private JDBCMetaDataConfiguration jmdcfg = null;

	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);
		jmdcfg = new JDBCMetaDataConfiguration();
		jmdcfg.readFromJDBC();
	}

	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	@Test
	public void testUniqueKey() {	
		Table table = jmdcfg.getTable(JdbcUtil.toIdentifier(this, "WITH_INDEX") );		
		UniqueKey uniqueKey = table.getUniqueKey(
				JdbcUtil.toIdentifier(this, "OTHER_IDX") );
		Assert.assertNotNull(uniqueKey);
		Assert.assertEquals(1, uniqueKey.getColumnSpan() );	
		Column keyCol = uniqueKey.getColumn(0);
		Assert.assertTrue(keyCol.isUnique() );
		Assert.assertSame(keyCol, table.getColumn(keyCol) );		
	}
	
	@Test
	public void testWithIndex() {		
		Table table = jmdcfg.getTable(JdbcUtil.toIdentifier(this, "WITH_INDEX"));
		Assert.assertEquals(
				JdbcUtil.toIdentifier(this, "WITH_INDEX"), 
				JdbcUtil.toIdentifier(this, table.getName()));	
		Assert.assertNull("there should be no pk", table.getPrimaryKey() );
		Iterator<Index> iterator = table.getIndexIterator();
		int cnt=0;
		while(iterator.hasNext() ) {
			iterator.next();
			cnt++;
		}
		Assert.assertEquals(1, cnt);	
		Index index = table.getIndex(JdbcUtil.toIdentifier(this, "MY_INDEX") );
		Assert.assertNotNull("No index ?", index);
		Assert.assertEquals(
				JdbcUtil.toIdentifier(this, "MY_INDEX"), 
				JdbcUtil.toIdentifier(this, index.getName()));	
		Assert.assertEquals(2, index.getColumnSpan() );	
		Assert.assertSame(index.getTable(), table);
		Iterator<Column> cols = index.getColumnIterator();
		Column col1 = cols.next();
		Column col2 = cols.next();	
		Assert.assertEquals(
				JdbcUtil.toIdentifier(this, "FIRST"), 
				JdbcUtil.toIdentifier(this, col1.getName()));
		Assert.assertEquals(
				JdbcUtil.toIdentifier(this, "THIRD"), 
				JdbcUtil.toIdentifier(this, col2.getName()));		
		Column example = new Column();
		example.setName(col2.getName() );
		Assert.assertSame(
				"column with same name should be same instance!", 
				table.getColumn(example), col2);			
	}
	
}
