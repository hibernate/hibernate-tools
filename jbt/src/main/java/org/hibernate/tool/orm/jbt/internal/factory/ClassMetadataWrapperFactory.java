package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tool.orm.jbt.api.ClassMetadataWrapper;
import org.hibernate.tool.orm.jbt.api.SessionWrapper;
import org.hibernate.tool.orm.jbt.api.TypeWrapper;
import org.hibernate.type.Type;

public class ClassMetadataWrapperFactory {

	public static ClassMetadataWrapper createClassMetadataWrapper(final EntityPersister entityPersister) {
		return new ClassMetadataWrapperImpl(entityPersister);
	}
	
	private static class ClassMetadataWrapperImpl implements ClassMetadataWrapper {
		
		private EntityPersister wrappedClassMetadata = null;
		private TypeWrapper[] propertyTypeWrappers = null;
		private TypeWrapper identifierTypeWrapper = null;
		
		private ClassMetadataWrapperImpl(EntityPersister entityPersister) {
			wrappedClassMetadata = entityPersister;
		}
		
		@Override 
		public EntityPersister getWrappedObject() { 
			return wrappedClassMetadata; 
		}
		
		@Override 
		public String getEntityName() { 
			return wrappedClassMetadata.getEntityName(); 
		
		}
		@Override 
		public String getIdentifierPropertyName() { 
			return wrappedClassMetadata.getIdentifierPropertyName(); 
		}
		
		@Override 
		public String[] getPropertyNames() { 
			return wrappedClassMetadata.getPropertyNames(); 
		}
		
		@Override 
		public TypeWrapper[] getPropertyTypes() { 
			if (propertyTypeWrappers == null) {
				initPropertyTypeWrappers();
			} else {
				syncPropertyTypeWrappers();
			}
			return propertyTypeWrappers; 
		}
		
		@Override
		public Class<?> getMappedClass() { 
			return wrappedClassMetadata.getMappedClass(); 
		}
		
		@Override 
		public TypeWrapper getIdentifierType() { 
			Type identifierType = wrappedClassMetadata.getIdentifierType();
			if (identifierTypeWrapper == null || identifierTypeWrapper.getWrappedObject() != identifierType) {
				identifierTypeWrapper = TypeWrapperFactory.createTypeWrapper(identifierType);
			}
			return identifierTypeWrapper; 
		}
		
		@Override 
		public Object getPropertyValue(Object object, String name) { 
			return wrappedClassMetadata.getPropertyValue(object, name); 
		}
		
		@Override 
		public boolean hasIdentifierProperty() { 
			return wrappedClassMetadata.hasIdentifierProperty(); 
		}
		
		@Override 
		public Object getIdentifier(Object object, SessionWrapper sessionWrapper) { 
			return wrappedClassMetadata.getIdentifier(object, (SharedSessionContractImplementor)sessionWrapper.getWrappedObject()); 
		}
		
		@Override 
		public boolean isInstanceOfAbstractEntityPersister() { 
			return getWrappedObject() instanceof AbstractEntityPersister; 
		}
		
		@Override 
		public Integer getPropertyIndexOrNull(String id) { 
			return wrappedClassMetadata.getEntityMetamodel().getPropertyIndexOrNull(id); 
		}
		
		@Override 
		public Object getTuplizerPropertyValue(Object entity, int i) { 
			return wrappedClassMetadata.getValue(entity, i); 
		}
		
		private void initPropertyTypeWrappers() {	
			Type[] propertyTypes = wrappedClassMetadata.getPropertyTypes();
			propertyTypeWrappers = new TypeWrapper[propertyTypes.length];
			for (int i = 0; i < propertyTypes.length; i++) {
				propertyTypeWrappers[i] = TypeWrapperFactory.createTypeWrapper(propertyTypes[i]);
			}
 		}
		
		private void syncPropertyTypeWrappers() {
			Type[] propertyTypes = wrappedClassMetadata.getPropertyTypes();
			if (propertyTypeWrappers.length != propertyTypes.length) {
				initPropertyTypeWrappers();
			} else {
				for (int i = 0; i < propertyTypes.length; i++) {
					if (propertyTypeWrappers[i].getWrappedObject() != propertyTypes[i]) {
						propertyTypeWrappers[i] = TypeWrapperFactory.createTypeWrapper(propertyTypes[i]);
					}
				}
			}
		}

	}
	
}
