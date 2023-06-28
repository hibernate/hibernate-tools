package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.persister.entity.EntityPersister;

public class EntityPersisterWrapperFactory {
	
	public static Object create(EntityPersister delegate) {
		return Proxy.newProxyInstance(
				EntityPersisterWrapperFactory.class.getClassLoader(), 
				new Class[] { EntityPersisterWrapper.class }, 
				new EntityPersisterInvocationHandler(delegate));
	}
	
	static interface EntityPersisterExtension extends Wrapper {
		@Override EntityPersister getWrappedObject();
		default boolean isInstanceOfAbstractEntityPersister() { return true; }
		default Object getTuplizerPropertyValue(Object entity, int i) { 
			return getWrappedObject().getValue(entity, i); 
		}
		default Integer getPropertyIndexOrNull(String propertyName) {
			return getWrappedObject().getEntityMetamodel().getPropertyIndexOrNull(propertyName);
		}
	}
	
	static interface EntityPersisterWrapper extends EntityPersister, EntityPersisterExtension {}
	
	static class EntityPersisterInvocationHandler implements InvocationHandler, EntityPersisterExtension {
		
		private EntityPersister delegate;
		
		private EntityPersisterInvocationHandler(EntityPersister delegate) {
			this.delegate = delegate;
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (isEntityPersisterExtensionMethod(method)) {
				return method.invoke(this, args);
			} else {
				return method.invoke(delegate, args);
			}
		}

		@Override
		public EntityPersister getWrappedObject() {
			return delegate;
		}

	}
	
    private static boolean isEntityPersisterExtensionMethod(Method m) {
    	boolean result = true;
    	try {
			EntityPersisterExtension.class.getMethod(m.getName(), m.getParameterTypes());
		} catch (NoSuchMethodException e) {
			result = false;
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}
    	return result;
    }

}
