package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.mapping.PersistentClass;

public class PersistentClassWrapperFactory {
	
	public static Object createPersistentClassWrapper(PersistentClass target) {
		return Proxy.newProxyInstance(
				PersistentClassWrapperFactory.class.getClassLoader(), 
				new Class[] { PersistentClassWrapper.class }, 
				new PersistentClassWrapperInvocationHandler(target));
	}
	
	static interface PersistentClassWrapper {
	}
	
	static class PersistentClassWrapperInvocationHandler implements InvocationHandler {
		
		private PersistentClassWrapperImpl wrapper = null;

		public PersistentClassWrapperInvocationHandler(PersistentClass target) {
			wrapper = new PersistentClassWrapperImpl(target);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return method.invoke(wrapper, args);
		}
		
	}
	
	static class PersistentClassWrapperImpl implements PersistentClassWrapper {
		
		private PersistentClass target = null;
		
		public PersistentClassWrapperImpl(PersistentClass target) {
			this.target = target;
		}

	}

}
