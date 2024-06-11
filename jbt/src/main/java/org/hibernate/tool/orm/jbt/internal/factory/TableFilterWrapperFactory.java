package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.tool.internal.reveng.strategy.TableFilter;
import org.hibernate.tool.orm.jbt.api.wrp.TableFilterWrapper;
import org.hibernate.tool.orm.jbt.internal.wrp.AbstractWrapper;

public class TableFilterWrapperFactory {
	
	public static TableFilterWrapper createTableFilterWrapper() {
		return createTableFilterWrapper(new TableFilter());
	}

	private static TableFilterWrapper createTableFilterWrapper(TableFilter wrappedTableFilter) {
		return new TableFilterWrapperImpl(wrappedTableFilter);
	}
	
	private static class TableFilterWrapperImpl 
			extends AbstractWrapper
			implements TableFilterWrapper {
		
		private TableFilter  tableFilter = null;
		
		private TableFilterWrapperImpl(TableFilter tableFilter) {
			this.tableFilter = tableFilter;
		}
		
		@Override 
		public TableFilter getWrappedObject() { 
			return tableFilter; 
		}
		
		@Override 
		public void setExclude(boolean b) { 
			tableFilter.setExclude(b); 
		}

		@Override 
		public void setMatchCatalog(String s) { 
			tableFilter.setMatchCatalog(s); 
		}

		@Override 
		public void setMatchSchema(String s) { 
			tableFilter.setMatchSchema(s); 
		}

		@Override 
		public void setMatchName(String s) { 
			tableFilter.setMatchName(s); 
		}

		@Override 
		public Boolean getExclude() { 
			return tableFilter.getExclude(); 
		}

		@Override 
		public String getMatchCatalog() { 
			return tableFilter.getMatchCatalog(); 
		}

		@Override 
		public String getMatchSchema() { 
			return tableFilter.getMatchSchema(); 
		}

		@Override 
		public String getMatchName() { 
			return tableFilter.getMatchName(); 
		}

	}

}
