package org.hibernate.cfg.reveng.dialect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.cfg.reveng.ReverseEngineeringRuntimeInfo;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.exception.SQLExceptionConverter;

public class CachedMetaDataDialect implements MetaDataDialect {
	
	MetaDataDialect delegate;
	private Map cachedTables = new HashMap();
	private Map cachedColumns = new HashMap();
	private Map cachedExportedKeys = new HashMap();
	private Map cachedPrimaryKeys = new HashMap();
	private Map cachedIndexInfo = new HashMap();
	private Map cachedPrimaryKeyStrategyName = new HashMap();

	public CachedMetaDataDialect(MetaDataDialect realMetaData) {
		this.delegate = realMetaData;
	}
	
	public void close() {
		delegate.close();
	}

	public void configure(ReverseEngineeringRuntimeInfo info) {
        delegate.configure(info);       
    }
	
	public void close(Iterator iterator) {
		if(iterator instanceof CachedIterator) {
			CachedIterator ci = (CachedIterator) iterator;
			if(ci.getOwner()==this) {
				ci.store();
				return;
			} 
		}
		delegate.close( iterator );
	}

	

	public Iterator getColumns(String catalog, String schema, String table, String column) {
		StringKey sk = new StringKey(new String[] { catalog, schema, table, column });
		List cached = (List) cachedColumns.get( sk );
		if(cached==null) {
			cached = new ArrayList();
			return new CachedIterator(this, cachedColumns, sk, cached, delegate.getColumns( catalog, schema, table, column ));
		} else {
			return cached.iterator();
		}		
	}

	public Iterator getExportedKeys(String catalog, String schema, String table) {
		StringKey sk = new StringKey(new String[] { catalog, schema, table });
		List cached = (List) cachedExportedKeys.get( sk );
		if(cached==null) {
			cached = new ArrayList();
			return new CachedIterator(this, cachedExportedKeys, sk, cached, delegate.getExportedKeys( catalog, schema, table ));
		} else {
			return cached.iterator();
		}		
	}

	public Iterator getIndexInfo(String catalog, String schema, String table) {
		StringKey sk = new StringKey(new String[] { catalog, schema, table });
		List cached = (List) cachedIndexInfo.get( sk );
		if(cached==null) {
			cached = new ArrayList();
			return new CachedIterator(this, cachedIndexInfo, sk, cached, delegate.getIndexInfo( catalog, schema, table ));
		} else {
			return cached.iterator();
		}
	}

	public Iterator getPrimaryKeys(String catalog, String schema, String name) {
		StringKey sk = new StringKey(new String[] { catalog, schema, name });
		List cached = (List) cachedPrimaryKeys .get( sk );
		if(cached==null) {
			cached = new ArrayList();
			return new CachedIterator(this, cachedPrimaryKeys, sk, cached, delegate.getPrimaryKeys( catalog, schema, name ));
		} else {
			return cached.iterator();
		}
	}

	public Iterator getTables(String catalog, String schema, String table) {
		StringKey sk = new StringKey(new String[] { catalog, schema, table });
		List cached = (List) cachedTables.get( sk );
		if(cached==null) {
			cached = new ArrayList();
			return new CachedIterator(this, cachedTables, sk, cached, delegate.getTables( catalog, schema, table ));
		} else {
			return cached.iterator();
		}
	}

	public Iterator getSuggestedPrimaryKeyStrategyName(String catalog, String schema, String table) {
		StringKey sk = new StringKey(new String[] { catalog, schema, table });
		List cached = (List) cachedPrimaryKeyStrategyName.get( sk );
		if(cached==null) {
			cached = new ArrayList();
			return new CachedIterator(this, cachedPrimaryKeyStrategyName, sk, cached, delegate.getSuggestedPrimaryKeyStrategyName( catalog, schema, table ));
		} else {
			return cached.iterator();
		}
	}
	
	public boolean needQuote(String name) {
		return delegate.needQuote( name );
	}
	
	private static class StringKey {
		String[] keys;
		
		StringKey(String[] key) {
			this.keys=key;
		}
		
		public int hashCode() {
			if (keys == null)
	            return 0;
	 
	        int result = 1;
	 
	        for (int i = 0; i < keys.length; i++) {
				Object element = keys[i];
			    result = 31 * result + (element == null ? 0 : element.hashCode());
	        }
	        
	        return result;	 
		}
		
		public boolean equals(Object obj) {
			StringKey other = (StringKey) obj;
			String[] otherKeys = other.keys;
			
			if(otherKeys.length!=keys.length) {
				return false;
			}
			
			for (int i = otherKeys.length-1; i >= 0; i--) {
				if(!safeEquals(otherKeys[i],(keys[i]))) {
					return false;
				}
			}
			
			return true;
		}
		
		private boolean safeEquals(Object obj1, Object obj2) {
			if ( obj1 == null ) {
				return obj2 == null;
			}
	        return obj1.equals( obj2 );
		}
	}
	
	private static class CachedIterator implements Iterator {

		private List cache; 
		private StringKey target;
		private Map destination;
		private Iterator realIterator;
		final CachedMetaDataDialect owner;
		public CachedIterator(CachedMetaDataDialect owner, Map destination, StringKey sk, List cache, Iterator realIterator) {
			this.owner = owner;
			this.destination = destination;
			this.target = sk;
			this.realIterator = realIterator;
			this.cache = cache;
		}
		
		public CachedMetaDataDialect getOwner() {
			return owner;
		}

		public boolean hasNext() {			
			return realIterator.hasNext();
		}

		public Object next() {
			Map map = (Map) realIterator.next();
			cache.add(new HashMap(map)); // need to copy since MetaDataDialect might reuse it.
			return map;
		}

		public void remove() {
			realIterator.remove();
		}

		public void store() {
			destination.put( target, cache );
			if(realIterator.hasNext()) throw new IllegalStateException("CachedMetaDataDialect have not been fully initialized!");
			cache = null;
			target = null;
			destination = null;
			realIterator = null;			
		}
	}

	

		
}
