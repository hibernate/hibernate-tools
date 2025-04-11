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

import org.hibernate.boot.model.naming.EntityNaming;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitBasicColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitCollectionTableNameSource;
import org.hibernate.boot.model.naming.ImplicitEntityNameSource;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.ImplicitPrimaryKeyJoinColumnNameSource;
import org.hibernate.boot.model.source.spi.AttributePath;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.tool.orm.jbt.api.wrp.NamingStrategyWrapper;
import org.hibernate.tool.orm.jbt.internal.util.ReflectUtil;
import org.hibernate.tool.orm.jbt.internal.wrp.AbstractWrapper;

public class NamingStrategyWrapperFactory {
	
	public static NamingStrategyWrapper createNamingStrategyWrapper(String className) {
		return createNamingStrategyWrapper((ImplicitNamingStrategy)ReflectUtil.createInstance(className));
	}

	static NamingStrategyWrapper createNamingStrategyWrapper(ImplicitNamingStrategy wrappedNamingStrategy) {
		return new NamingStrategyWrapperImpl(wrappedNamingStrategy);
	}
	
	private static class NamingStrategyWrapperImpl 
			extends AbstractWrapper
			implements NamingStrategyWrapper {
		
		private ImplicitNamingStrategy namingStrategy = null;
		
		private NamingStrategyWrapperImpl(ImplicitNamingStrategy namingStrategy) {
			this.namingStrategy = namingStrategy;
		}
		
		@Override 
		public ImplicitNamingStrategy getWrappedObject() { 
			return namingStrategy; 
		}
		
		@Override
		public String collectionTableName(
				String ownerEntity, 
				String ownerEntityTable, 
				String associatedEntity, 
				String associatedEntityTable,
				String propertyName) { 
			ImplicitCollectionTableNameSource ictns = new ImplicitCollectionTableNameSource() {			
				@Override public MetadataBuildingContext getBuildingContext() { return null; }				
				@Override public Identifier getOwningPhysicalTableName() { return Identifier.toIdentifier(ownerEntityTable); }				
				@Override public AttributePath getOwningAttributePath() { return AttributePath.parse(propertyName); }
				@Override
				public EntityNaming getOwningEntityNaming() {
					return new EntityNaming() {			
						@Override public String getJpaEntityName() { return ownerEntity; }	
						@Override public String getEntityName() { return ownerEntity; }			
						@Override public String getClassName() { return ownerEntity; }
					};
				}				
			};
			return ((ImplicitNamingStrategy)getWrappedObject()).determineCollectionTableName(ictns).getText();
		}
		
		@Override
		public String columnName(String name) {
			ImplicitBasicColumnNameSource ibcns = new ImplicitBasicColumnNameSource() {
				@Override public MetadataBuildingContext getBuildingContext() { return null; }
				@Override public AttributePath getAttributePath() { return AttributePath.parse(name); }
				@Override public boolean isCollectionElement() { return false; }			
			};
			return ((ImplicitNamingStrategy)getWrappedObject()).determineBasicColumnName(ibcns).getText();
		}
		
		@Override
		public String propertyToColumnName(String name) {
			return columnName(name);
		}
		
		@Override
		public String tableName(String name) {
			ImplicitEntityNameSource iens = new ImplicitEntityNameSource() {				
				@Override public MetadataBuildingContext getBuildingContext() { return null; }		
				@Override
				public EntityNaming getEntityNaming() {
					return new EntityNaming() {			
						@Override public String getJpaEntityName() { return name; }	
						@Override public String getEntityName() { return name; }			
						@Override public String getClassName() { return name; }
					};
				}
			};
			return ((ImplicitNamingStrategy)getWrappedObject()).determinePrimaryTableName(iens).getText();
		}
		
		@Override
		public String classToTableName(String name) {
			return tableName(name);
		}
		
		@Override 
		public String joinKeyColumnName(
				String primaryKeyColumnName,
				String primaryTableName) {
			ImplicitPrimaryKeyJoinColumnNameSource ipkjcns = new ImplicitPrimaryKeyJoinColumnNameSource() {	
				@Override public MetadataBuildingContext getBuildingContext() { return null; }
				@Override public Identifier getReferencedTableName() { return Identifier.toIdentifier(primaryTableName); }			
				@Override public Identifier getReferencedPrimaryKeyColumnName() { return Identifier.toIdentifier(primaryKeyColumnName); }
			};
			return ((ImplicitNamingStrategy)getWrappedObject()).determinePrimaryKeyJoinColumnName(ipkjcns).getText();

		}
		
		@Override
		public String getStrategyClassName() {
			return getWrappedObject().getClass().getName();
		}

	}
	
}
