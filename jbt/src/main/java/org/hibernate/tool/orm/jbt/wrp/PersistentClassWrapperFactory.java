package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;

public class PersistentClassWrapperFactory {
	
	public static Object createRootClassWrapper() {
		return Proxy.newProxyInstance(
				PersistentClassWrapperFactory.class.getClassLoader(), 
				new Class[] { PersistentClassWrapper.class }, 
				new PersistentClassWrapperInvocationHandler(new RootClassWrapperImpl()));
	}
	
	public static Object createSingleTableSubclassWrapper(PersistentClassWrapper superClassWrapper) {
		return Proxy.newProxyInstance(
				PersistentClassWrapperFactory.class.getClassLoader(), 
				new Class[] { PersistentClassWrapper.class }, 
				new PersistentClassWrapperInvocationHandler(
						new SingleTableSubclassWrapperImpl(superClassWrapper.getWrappedObject())));
	}
	
	static class PersistentClassWrapperInvocationHandler implements InvocationHandler {
		
		private PersistentClassWrapper wrapper = null;

		public PersistentClassWrapperInvocationHandler(PersistentClassWrapper wrapper) {
			this.wrapper = wrapper;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return method.invoke(wrapper, args);
		}
		
	}
	
	static class RootClassWrapperImpl extends RootClass implements PersistentClassWrapper {		

		public RootClassWrapperImpl() {
			super(DummyMetadataBuildingContext.INSTANCE);
		}

		@Override
		public PersistentClass getWrappedObject() {
			return this;
		}

	}
	
	static class SingleTableSubclassWrapperImpl 
			extends SingleTableSubclass 
			implements PersistentClassWrapper {

		public SingleTableSubclassWrapperImpl(PersistentClass superclass) {
			super(superclass, DummyMetadataBuildingContext.INSTANCE);
		}

		@Override
		public PersistentClass getWrappedObject() {
			return this;
		}
		
	}
	
}
