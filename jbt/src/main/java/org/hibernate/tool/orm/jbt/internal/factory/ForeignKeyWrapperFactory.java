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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.tool.orm.jbt.api.wrp.ColumnWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ForeignKeyWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.TableWrapper;
import org.hibernate.tool.orm.jbt.internal.wrp.AbstractWrapper;

public class ForeignKeyWrapperFactory {

	public static ForeignKeyWrapper createForeignKeyWrapper(final ForeignKey wrappedForeignKey) {
		return new ForeignKeyWrapperImpl(wrappedForeignKey);
	}
	
	private static class ForeignKeyWrapperImpl 
			extends AbstractWrapper
			implements ForeignKeyWrapper {
		
		private ForeignKey foreignKey = null;
		
		private List<ColumnWrapper> referencedColumns = null;
		
		private ForeignKeyWrapperImpl(ForeignKey foreignKey) {
			this.foreignKey = foreignKey;
		}
		
		@Override 
		public ForeignKey getWrappedObject() { 
			return foreignKey; 
		}
		
		@Override
		public TableWrapper getReferencedTable() { 
			return TableWrapperFactory.createTableWrapper(foreignKey.getReferencedTable()); 
		}
		
		@Override
		public Iterator<ColumnWrapper> columnIterator() { 
			Iterator<Column> columnIterator = foreignKey.getColumns().iterator(); 
			return new Iterator<ColumnWrapper>() {
				@Override
				public boolean hasNext() {
					return columnIterator.hasNext();
				}
				@Override
				public ColumnWrapper next() {
					return ColumnWrapperFactory.createColumnWrapper(columnIterator.next());
				}			
			};
		}
		
		@Override
		public boolean isReferenceToPrimaryKey() { 
			return ((ForeignKey)getWrappedObject()).isReferenceToPrimaryKey(); 
		}
		
		@Override
		public List<ColumnWrapper> getReferencedColumns() { 
			List<Column> columns = foreignKey.getReferencedColumns();
			if (referencedColumns == null) {
				initReferencedColumns(columns);
			} else {
				if (columns == null) {
					referencedColumns = null;
				} else if (referencedColumns.size() != columns.size()) {
					initReferencedColumns(columns);
				} else {
					syncReferencedColumns(columns);
				}
			}
			return referencedColumns; 
		}

		@Override
		public boolean containsColumn(ColumnWrapper column) { 
			return ((ForeignKey)getWrappedObject()).containsColumn((Column)column.getWrappedObject()); 
		}
		
		private void initReferencedColumns(List<Column> columns) {
			referencedColumns = new ArrayList<ColumnWrapper>(columns.size());
			for (int i = 0; i < columns.size(); i++) {
				referencedColumns.add(ColumnWrapperFactory.createColumnWrapper(columns.get(i)));
			}
		}

		private void syncReferencedColumns(List<Column> columns) {
			for (int i = 0; i < columns.size(); i++) {
				if (referencedColumns.get(i).getWrappedObject() != columns.get(i)) {
					referencedColumns.set(i, ColumnWrapperFactory.createColumnWrapper(columns.get(i)));
				}
			}
		}
		
	}
	
}
