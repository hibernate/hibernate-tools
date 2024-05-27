package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.Session;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tool.orm.jbt.api.ClassMetadataWrapper;
import org.hibernate.type.Type;

public class ClassMetadataWrapperFactory {

	public static ClassMetadataWrapper createClassMetadataWrapper(final EntityPersister entityPersister) {
		return new ClassMetadataWrapperImpl(entityPersister);
	}
	
	private static class ClassMetadataWrapperImpl implements ClassMetadataWrapper {
		
		private EntityPersister wrappedClassMetadata = null;
		
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
		public Type[] getPropertyTypes() { 
			return wrappedClassMetadata.getPropertyTypes(); 
		}
		
		@Override
		public Class<?> getMappedClass() { 
			return wrappedClassMetadata.getMappedClass(); 
		}
		
		@Override 
		public Type getIdentifierType() { 
			return wrappedClassMetadata.getIdentifierType(); 
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
		public Object getIdentifier(Object object, Session session) { 
			return wrappedClassMetadata.getIdentifier(object, (SharedSessionContractImplementor)session); 
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

	}
	
}
