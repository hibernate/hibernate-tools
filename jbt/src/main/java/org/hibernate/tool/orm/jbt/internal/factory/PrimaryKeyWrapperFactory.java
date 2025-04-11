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
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.orm.jbt.api.wrp.ColumnWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.PrimaryKeyWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.TableWrapper;
import org.hibernate.tool.orm.jbt.internal.wrp.AbstractWrapper;

public class PrimaryKeyWrapperFactory {

	public static PrimaryKeyWrapper createPrimaryKeyWrapper(PrimaryKey wrappedPrimaryKey) {
		return new PrimaryKeyWrapperImpl(wrappedPrimaryKey);
	}
	
	private static class PrimaryKeyWrapperImpl 
			extends AbstractWrapper
			implements PrimaryKeyWrapper {
		
		private PrimaryKey primaryKey = null;
		
		private PrimaryKeyWrapperImpl(PrimaryKey primaryKey) {
			this.primaryKey = primaryKey;
		}
		
		@Override 
		public PrimaryKey getWrappedObject() { 
			return primaryKey; 
		}
		
		@Override 
		public void addColumn(ColumnWrapper column) { 
			primaryKey.addColumn((Column)column.getWrappedObject()); 
		}
		
		@Override 
		public int getColumnSpan() { 
			return primaryKey.getColumnSpan(); 
		}
		
		@Override 
		public List<ColumnWrapper> getColumns() { 
			List<ColumnWrapper> result = new ArrayList<ColumnWrapper>();
			for (Column c : primaryKey.getColumns()) {
				result.add(ColumnWrapperFactory.createColumnWrapper(c));
			}
			return result; 
		}
		
		@Override 
		public ColumnWrapper getColumn(int i) { 
			return ColumnWrapperFactory.createColumnWrapper(primaryKey.getColumn(i)); 
		}
		
		@Override 
		public TableWrapper getTable() { 
			Table t = primaryKey.getTable();
			return t == null ? null : TableWrapperFactory.createTableWrapper(t); 
		}
		
		@Override 
		public boolean containsColumn(ColumnWrapper column) { 
			return primaryKey.containsColumn((Column)column.getWrappedObject()); 
		}
		
		@Override 
		public Iterator<ColumnWrapper> columnIterator() { 
			return getColumns().iterator(); 
		}
		
		@Override 
		public String getName() { 
			return primaryKey.getName(); 
		}

	}
	
}
