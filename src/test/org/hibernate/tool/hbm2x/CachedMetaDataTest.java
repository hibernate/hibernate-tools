/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.hbm2x;

import java.util.Iterator;

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.JDBCReaderFactory;
import org.hibernate.cfg.Settings;
import org.hibernate.cfg.reveng.DatabaseCollector;
import org.hibernate.cfg.reveng.DefaultDatabaseCollector;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.JDBCReader;
import org.hibernate.cfg.reveng.ReverseEngineeringRuntimeInfo;
import org.hibernate.cfg.reveng.dialect.CachedMetaDataDialect;
import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.hibernate.mapping.Table;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;



/**
 * @author max
 *
 */
public class CachedMetaDataTest extends JDBCMetaDataBinderTestCase {

	protected void configure(JDBCMetaDataConfiguration configuration) {
		super.configure( configuration );
	}
	
	public class MockedMetaDataDialect implements MetaDataDialect {

		MetaDataDialect delegate;
		private boolean failOnDelegateAccess;

		public MockedMetaDataDialect(MetaDataDialect realMetaData) {
			delegate = realMetaData;
		}

		public void close() {
			delegate.close();
		}

		public void close(Iterator iterator) {
			delegate.close( iterator );
		}

		public void configure(ReverseEngineeringRuntimeInfo info) {
			delegate.configure(info);			
		}
		
		public Iterator getColumns(String catalog, String schema, String table, String column) {
			if(failOnDelegateAccess) {
				throw new IllegalStateException("delegate not accessible");
			} else {
				return delegate.getColumns( catalog, schema, table, column );
			}
		}

		public Iterator getExportedKeys(String catalog, String schema, String table) {
			if(failOnDelegateAccess) {
				throw new IllegalStateException("delegate not accessible");
			} else {
				return delegate.getExportedKeys( catalog, schema, table );
			}
		}

		public Iterator getIndexInfo(String catalog, String schema, String table) {
			if(failOnDelegateAccess) {
				throw new IllegalStateException("delegate not accessible");
			} else {
				return delegate.getIndexInfo( catalog, schema, table );
			}
		}

		public Iterator getPrimaryKeys(String catalog, String schema, String name) {
			if(failOnDelegateAccess) {
				throw new IllegalStateException("delegate not accessible");
			} else {
				return delegate.getPrimaryKeys( catalog, schema, name );
			}
		}

		public Iterator getTables(String catalog, String schema, String table) {
			if(failOnDelegateAccess) {
				throw new IllegalStateException("delegate not accessible");
			} else {
				return delegate.getTables( catalog, schema, table );
			}
		}

		public boolean needQuote(String name) {
			return delegate.needQuote( name );
		}

		public void setDelegate(Object object) {
			this.delegate = null;			
		}

		public void setFailOnDelegateAccess(boolean b) {
			failOnDelegateAccess = b;			
		}

		public Iterator getSuggestedPrimaryKeyStrategyName(String catalog, String schema, String name) {
			if(failOnDelegateAccess) {
				throw new IllegalStateException("delegate not accessible");
			} else {
				return delegate.getSuggestedPrimaryKeyStrategyName(catalog, schema, name);
			}
		}
		
	}
	
	public void testCachedDialect() {
		Settings buildSettings = cfg.buildSettings();
				
		MetaDataDialect realMetaData = JDBCReaderFactory.newMetaDataDialect( buildSettings.getDialect(), cfg.getProperties() );
		
		MockedMetaDataDialect mock = new MockedMetaDataDialect(realMetaData);
		CachedMetaDataDialect dialect = new CachedMetaDataDialect(mock);
		
		JDBCReader reader = JDBCReaderFactory.newJDBCReader( buildSettings, new DefaultReverseEngineeringStrategy(), dialect );
		
		DatabaseCollector dc = new DefaultDatabaseCollector(reader.getMetaDataDialect());
		reader.readDatabaseSchema( dc, null, null );

		validate( dc );		
		
		mock.setFailOnDelegateAccess(true);
		
		reader = JDBCReaderFactory.newJDBCReader( buildSettings, new DefaultReverseEngineeringStrategy(), dialect );
		
		dc = new DefaultDatabaseCollector(reader.getMetaDataDialect());
		reader.readDatabaseSchema( dc, null, null );

		validate(dc);
		
		
		
		
	}

	private void validate(DatabaseCollector dc) {
		Iterator iterator = dc.iterateTables();
		Table firstChild = (Table) iterator.next();
		assertTrue("CHILD".equals(firstChild.getName()) || 
				"MASTER".equals(firstChild.getName()));
		assertTrue(iterator.hasNext());
		
		iterator = dc.iterateTables();
		assertNotNull(iterator.next());
		assertNotNull(iterator.next());
		assertFalse(iterator.hasNext());
		
		
		assertHasNext("should have recorded one foreignkey to child table", 1, firstChild.getForeignKeyIterator() );
	}

	protected String[] getCreateSQL() {
		
		return new String[] {
				"create table master ( id char not null, name varchar(20), primary key (id) )",
				"create table child  ( childid char not null, masterref char, primary key (childid), foreign key (masterref) references master(id) )",				
		};
	}

	protected String[] getDropSQL() {
		
		return new String[]  {
				"drop table child",
				"drop table master",				
		};
	}	
	
}
