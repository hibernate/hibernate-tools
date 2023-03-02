package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Table;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;
import org.hibernate.tool.orm.jbt.util.SpecialRootClass;

public class PersistentClassWrapperFactory {
	
	public static PersistentClassWrapper createRootClassWrapper() {
		return (PersistentClassWrapper)Proxy.newProxyInstance(
				PersistentClassWrapperFactory.class.getClassLoader(), 
				new Class[] { PersistentClassWrapper.class }, 
				new PersistentClassWrapperInvocationHandler(new RootClassWrapperImpl()));
	}
	
	public static PersistentClassWrapper createSingleTableSubclassWrapper(PersistentClassWrapper superClassWrapper) {
		return (PersistentClassWrapper)Proxy.newProxyInstance(
				PersistentClassWrapperFactory.class.getClassLoader(), 
				new Class[] { PersistentClassWrapper.class }, 
				new PersistentClassWrapperInvocationHandler(
						new SingleTableSubclassWrapperImpl(superClassWrapper.getWrappedObject())));
	}
	
	public static PersistentClassWrapper createJoinedSubclassWrapper(PersistentClassWrapper superClassWrapper) {
		return (PersistentClassWrapper)Proxy.newProxyInstance(
				PersistentClassWrapperFactory.class.getClassLoader(), 
				new Class[] { PersistentClassWrapper.class }, 
				new PersistentClassWrapperInvocationHandler(
						new JoinedSubclassWrapperImpl(superClassWrapper.getWrappedObject())));
	}
	
	public static PersistentClassWrapper createSpecialRootClassWrapper(Property property) {
		return (PersistentClassWrapper)Proxy.newProxyInstance(
				PersistentClassWrapperFactory.class.getClassLoader(), 
				new Class[] { PersistentClassWrapper.class }, 
				new PersistentClassWrapperInvocationHandler(
						new SpecialRootClassWrapperImpl(property)));
	}
	
	static class PersistentClassWrapperInvocationHandler implements InvocationHandler {
		
		private PersistentClassWrapper wrapper = null;

		public PersistentClassWrapperInvocationHandler(PersistentClassWrapper wrapper) {
			this.wrapper = wrapper;
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			try {
				return method.invoke(wrapper, args);
			} catch (InvocationTargetException t) {
				throw t.getTargetException();
			}
		}	
		
	}
	
	static class RootClassWrapperImpl extends RootClass implements PersistentClassWrapper {		
		public RootClassWrapperImpl() {
			super(DummyMetadataBuildingContext.INSTANCE);
		}
	}
	
	static class SingleTableSubclassWrapperImpl 
			extends SingleTableSubclass 
			implements PersistentClassWrapper {
		public SingleTableSubclassWrapperImpl(PersistentClass superclass) {
			super(superclass, DummyMetadataBuildingContext.INSTANCE);
		}
		public void setTable(Table table) {
			throw new RuntimeException("Method 'setTable' cannot be called for SingleTableSubclass");
		}
		public void setIdentifier(KeyValue value) {
			throw new RuntimeException("Method 'setIdentifier' cannot be called for SingleTableSubclass");
		}
	}
	
	static class JoinedSubclassWrapperImpl
			extends JoinedSubclass
			implements PersistentClassWrapper {
		public JoinedSubclassWrapperImpl(PersistentClass superclass) {
			super(superclass, DummyMetadataBuildingContext.INSTANCE);
		}
		public void setIdentifier(KeyValue value) {
			super.setKey(value);
		}
	}
	
	static class SpecialRootClassWrapperImpl 
			extends SpecialRootClass
			implements PersistentClassWrapper {
		public SpecialRootClassWrapperImpl(Property property) {
			super(property);
		}
	}
	
}
