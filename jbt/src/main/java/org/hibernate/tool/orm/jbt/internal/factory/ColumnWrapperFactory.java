/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 *
 * Copyright 2024-2025 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.api.wrp.ColumnWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ConfigurationWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ValueWrapper;
import org.hibernate.tool.orm.jbt.internal.util.MetadataHelper;
import org.hibernate.tool.orm.jbt.internal.wrp.AbstractWrapper;

public class ColumnWrapperFactory {
	
	public static ColumnWrapper createColumnWrapper(final String name) {
		return new ColumnWrapperImpl(name);
	}
	
	public static ColumnWrapper createColumnWrapper(final Column column) {
		return new ColumnWrapperImpl(column);
	}
	
	private static class ColumnWrapperImpl
			extends AbstractWrapper
			implements ColumnWrapper {
		
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
