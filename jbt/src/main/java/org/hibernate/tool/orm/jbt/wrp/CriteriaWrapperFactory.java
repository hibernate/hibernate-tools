package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import jakarta.persistence.Query;

public class CriteriaWrapperFactory {

	public static Query createCriteriaWrapper(Query target) {
		return (Query)Proxy.newProxyInstance(
				CriteriaWrapperFactory.class.getClassLoader(), 
				new Class[] { CriteriaExtension.class }, 
				new CriteriaInvocationHandler(target));
	}
	
	private static interface CriteriaExtension extends Query {
		default List<?> list() {
			return getResultList();
		}
	}
	
	static class CriteriaInvocationHandler implements InvocationHandler {
		
		Query target = null;
		
		private CriteriaInvocationHandler(Query target) {
			this.target = target;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return method.invoke(target, args);
		}
		
	}

}
