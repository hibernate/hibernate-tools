package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.cfg.NamingStrategy;
import org.hibernate.tool.orm.jbt.api.NamingStrategyWrapper;

public class NamingStrategyWrapperFactory {

	public static NamingStrategyWrapper createNamingStrategyWrapper(NamingStrategy wrappedNamingStrategy) {
		return new NamingStrategyWrapperImpl(wrappedNamingStrategy);
	}
	
	private static class NamingStrategyWrapperImpl implements NamingStrategyWrapper {
		
		private NamingStrategy namingStrategy = null;
		
		private NamingStrategyWrapperImpl(NamingStrategy namingStrategy) {
			this.namingStrategy = namingStrategy;
		}
		
		@Override 
		public NamingStrategy getWrappedObject() { 
			return namingStrategy; 
		}
		
		@Override
		public String collectionTableName(
				String ownerEntity, 
				String ownerEntityTable, 
				String associatedEntity, 
				String associatedEntityTable,
				String propertyName) { 
			return ((NamingStrategy)getWrappedObject()).collectionTableName(
					ownerEntity, 
					ownerEntityTable, 
					associatedEntity, 
					associatedEntityTable, 
					propertyName);
		}
		
		@Override
		public String columnName(String name) {
			return ((NamingStrategy)getWrappedObject()).columnName(name);
		}
		
		@Override
		public String propertyToColumnName(String name) {
			return ((NamingStrategy)getWrappedObject()).propertyToColumnName(name);
		}
		
		@Override
		public String tableName(String name) {
			return ((NamingStrategy)getWrappedObject()).tableName(name);
		}
		
		@Override 
		public String joinKeyColumnName(
				String primaryKeyColumnName,
				String primaryTableName) {
			return ((NamingStrategy)getWrappedObject()).joinKeyColumnName(
					primaryKeyColumnName,
					primaryTableName);
		}
		
		@Override
		public String classToTableName(String name) {
			return ((NamingStrategy)getWrappedObject()).classToTableName(name);
		}
		
		@Override
		public String getStrategyClassName() {
			return getWrappedObject().getClass().getName();
		}

	}
	
}
