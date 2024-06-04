package org.hibernate.tool.orm.jbt.internal.factory;

import java.util.Iterator;
import java.util.List;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.orm.jbt.api.PrimaryKeyWrapper;

public class PrimaryKeyWrapperFactory {

	public static PrimaryKeyWrapper createPrimaryKeyWrapper(PrimaryKey wrappedPrimaryKey) {
		return new PrimaryKeyWrapperImpl(wrappedPrimaryKey);
	}
	
	private static class PrimaryKeyWrapperImpl implements PrimaryKeyWrapper {
		
		private PrimaryKey primaryKey = null;
		
		private PrimaryKeyWrapperImpl(PrimaryKey primaryKey) {
			this.primaryKey = primaryKey;
		}
		
		@Override 
		public PrimaryKey getWrappedObject() { 
			return primaryKey; 
		}
		
		@Override 
		public void addColumn(Column column) { 
			primaryKey.addColumn(column); 
		}
		
		@Override 
		public int getColumnSpan() { 
			return primaryKey.getColumnSpan(); 
		}
		
		@Override 
		public List<Column> getColumns() { 
			return primaryKey.getColumns(); 
		}
		
		@Override 
		public Column getColumn(int i) { 
			return primaryKey.getColumn(i); 
		}
		
		@Override 
		public Table getTable() { 
			return primaryKey.getTable(); 
		}
		
		@Override 
		public boolean containsColumn(Column column) { 
			return primaryKey.containsColumn(column); 
		}
		
		@Override 
		public Iterator<Column> columnIterator() { 
			return getColumns().iterator(); 
		}
		
		@Override 
		public String getName() { 
			return primaryKey.getName(); 
		}

	}
	
}
