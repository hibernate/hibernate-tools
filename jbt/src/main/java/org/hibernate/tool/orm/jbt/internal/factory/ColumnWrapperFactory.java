package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.api.wrp.ColumnWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ConfigurationWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ValueWrapper;
import org.hibernate.tool.orm.jbt.util.MetadataHelper;

public class ColumnWrapperFactory {
	
	public static ColumnWrapper createColumnWrapper(final String name) {
		return new ColumnWrapperImpl(name);
	}
	
	public static ColumnWrapper createColumnWrapper(final Column column) {
		return new ColumnWrapperImpl(column);
	}
	
	private static class ColumnWrapperImpl implements ColumnWrapper {
		
		private Column wrappedColumn = null;
		
		private ValueWrapper valueWrapper = null;

		private ColumnWrapperImpl(Column column) {
			wrappedColumn = column;
		}
		
		private ColumnWrapperImpl(String name) {
			wrappedColumn = new Column(name);
		}
		
		@Override 
		public Column getWrappedObject() { 
			return wrappedColumn; 
		}
		
		@Override
		public String getName() { 
			return wrappedColumn.getName(); 
		}
		
		@Override
		public Integer getSqlTypeCode() { 
			return wrappedColumn.getSqlTypeCode(); 
		}
		
		@Override
		public String getSqlType() { 
			return wrappedColumn.getSqlType(); 
		}
		
		@Override
		public String getSqlType(ConfigurationWrapper configurationWrapper) { 
			return wrappedColumn.getSqlType(MetadataHelper.getMetadata((Configuration)configurationWrapper.getWrappedObject())); 
		}
		
		@Override
		public long getLength() { 
			Long length = wrappedColumn.getLength();
			return length == null ? Integer.MIN_VALUE : length; 
		}
		
		@Override
		public int getDefaultLength() { 
			return DEFAULT_LENGTH; 
		}
		
		@Override
		public int getPrecision() {
			Integer precision = wrappedColumn.getPrecision();
			return precision == null ? Integer.MIN_VALUE : precision;
		}
		
		@Override
		public int getDefaultPrecision() { 
			return DEFAULT_PRECISION; 
		}
		
		@Override
		public int getScale() {
			Integer scale = wrappedColumn.getScale();
			return scale == null ? Integer.MIN_VALUE : scale;
		}
		
		@Override
		public int getDefaultScale() { 
			return DEFAULT_SCALE; 
		}
		
		@Override
		public boolean isNullable() { 
			return wrappedColumn.isNullable(); 
		}
		
		@Override
		public ValueWrapper getValue() { 
			Value v = wrappedColumn.getValue();
			if (valueWrapper ==  null || valueWrapper.getWrappedObject() != v) {
				if (v != null) {
					valueWrapper = ValueWrapperFactory.createValueWrapper(v);
				} else {
					valueWrapper = null;
				}
			}
			return valueWrapper; 	
		}
		
		@Override
		public boolean isUnique() { 
			return wrappedColumn.isUnique();
		}
		
		@Override
		public void setSqlType(String sqlType) { 
			wrappedColumn.setSqlType(sqlType); 
		}
		
	}
	
}
