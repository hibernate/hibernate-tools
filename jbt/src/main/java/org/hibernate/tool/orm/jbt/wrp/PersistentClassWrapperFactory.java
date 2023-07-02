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
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;
import org.hibernate.tool.orm.jbt.util.SpecialRootClass;

public class PersistentClassWrapperFactory {
	
	public static PersistentClassWrapper createRootClassWrapper() {
		return new RootClassWrapperImpl();
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
	
	public static PersistentClassWrapper createPersistentClassWrapper(PersistentClassWrapper persistentClassWrapper) {
		return (PersistentClassWrapper)Proxy.newProxyInstance(
				PersistentClassWrapperFactory.class.getClassLoader(), 
				new Class[] { PersistentClassWrapper.class }, 
				new PersistentClassWrapperInvocationHandler(
						persistentClassWrapper));
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
		@Override
		public Value getDiscriminator() {
			return wrapValueIfNeeded(super.getDiscriminator());
		}
		@Override
		public KeyValue getIdentifier() {
			return (KeyValue)wrapValueIfNeeded(super.getIdentifier());
		}
	}
	
	static class SingleTableSubclassWrapperImpl 
			extends SingleTableSubclass 
			implements PersistentClassWrapper {
		public SingleTableSubclassWrapperImpl(PersistentClass superclass) {
			super(superclass, DummyMetadataBuildingContext.INSTANCE);
		}
	}
	
	static class JoinedSubclassWrapperImpl
			extends JoinedSubclass
			implements PersistentClassWrapper {
		public JoinedSubclassWrapperImpl(PersistentClass superclass) {
			super(superclass, DummyMetadataBuildingContext.INSTANCE);
		}
	}
	
	static class SpecialRootClassWrapperImpl 
			extends SpecialRootClass
			implements PersistentClassWrapper {
		public SpecialRootClassWrapperImpl(Property property) {
			super(property);
		}
		@Override
		public Value getDiscriminator() {
			return wrapValueIfNeeded(super.getDiscriminator());
		}
		@Override
		public KeyValue getIdentifier() {
			return (KeyValue)wrapValueIfNeeded(super.getIdentifier());
		}
	}
	
	private static Value wrapValueIfNeeded(Value v) {
		return (v != null) && !(v instanceof Wrapper) ? ValueWrapperFactory.createValueWrapper(v) : v;
	}
	
}
