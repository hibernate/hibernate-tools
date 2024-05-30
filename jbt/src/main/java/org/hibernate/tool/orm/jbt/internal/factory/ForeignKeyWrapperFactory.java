package org.hibernate.tool.orm.jbt.internal.factory;

import java.util.Iterator;
import java.util.List;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.orm.jbt.api.ForeignKeyWrapper;

public class ForeignKeyWrapperFactory {

	public static ForeignKeyWrapper createForeignKeyWrapper(final ForeignKey wrappedForeignKey) {
		return new ForeignKeyWrapperImpl(wrappedForeignKey);
	}
	
	private static class ForeignKeyWrapperImpl implements ForeignKeyWrapper {
		
		private ForeignKey foreignKey = null;
		
		private ForeignKeyWrapperImpl(ForeignKey foreignKey) {
			this.foreignKey = foreignKey;
		}
		
		@Override 
		public ForeignKey getWrappedObject() { 
			return foreignKey; 
		}
		
		@Override
		public Table getReferencedTable() { 
			return ((ForeignKey)getWrappedObject()).getReferencedTable(); 
		}
		
		@Override
		public Iterator<Column> columnIterator() { 
			return ((ForeignKey)getWrappedObject()).getColumns().iterator(); 
		}
		
		@Override
		public boolean isReferenceToPrimaryKey() { 
			return ((ForeignKey)getWrappedObject()).isReferenceToPrimaryKey(); 
		}
		
		@Override
		public List<Column> getReferencedColumns() { 
			return ((ForeignKey)getWrappedObject()).getReferencedColumns(); 
		}

		@Override
		public boolean containsColumn(Column column) { 
			return ((ForeignKey)getWrappedObject()).containsColumn(column); 
		}

		
	}
	
}
