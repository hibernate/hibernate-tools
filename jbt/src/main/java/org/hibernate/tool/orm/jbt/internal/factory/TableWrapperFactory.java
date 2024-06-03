package org.hibernate.tool.orm.jbt.internal.factory;

import java.util.Iterator;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.orm.jbt.api.TableWrapper;

public class TableWrapperFactory {

	public static TableWrapper createTableWrapper(Table wrappedTable) {
		return new TableWrapperImpl(wrappedTable);
	}
	
	private static class TableWrapperImpl implements TableWrapper {
		
		private Table table = null;
		
		private TableWrapperImpl(Table table) {
			this.table = table;
		}
		
		@Override 
		public Table getWrappedObject() { 
			return table; 
		}
		
		@Override
		public String getName() { 
			return table.getName(); 
		}

		@Override
		public void addColumn(Column column) { 
			table.addColumn(column); 
		}

		@Override
		public String getCatalog() { 
			return table.getCatalog(); 
		}

		@Override
		public String getSchema() { 
			return table.getSchema(); 
		}

		@Override
		public PrimaryKey getPrimaryKey() { 
			return table.getPrimaryKey(); 
		}

		@Override
		public Iterator<Column> getColumnIterator() { 
			return table.getColumns().iterator(); 
		}

		@Override
		public String getComment() { 
			return table.getComment(); 
		}

		@Override
		public String getRowId() { 
			return table.getRowId(); 
		}

		@Override
		public String getSubselect() { 
			return table.getSubselect(); 
		}

		@Override
		public boolean hasDenormalizedTables() { 
			return table.hasDenormalizedTables(); 
		}

		@Override
		public boolean isAbstract() { 
			return table.isAbstract(); 
		}

		@Override
		public boolean isAbstractUnionTable() { 
			return table.isAbstractUnionTable(); 
		}

		@Override
		public boolean isPhysicalTable() { 
			return table.isPhysicalTable(); 
		}

		@Override
		public KeyValue getIdentifierValue() { 
			return table.getIdentifierValue(); 
		}

		
		
	}

}
