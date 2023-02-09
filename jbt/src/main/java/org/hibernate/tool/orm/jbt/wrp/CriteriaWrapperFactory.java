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
	
	static interface CriteriaExtension extends Query {
		List<?> list();
	}
	
	static class CriteriaInvocationHandler implements InvocationHandler {
		
		Query target = null;
		
		private CriteriaInvocationHandler(Query target) {
			this.target = target;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if ("list".equals(method.getName())) {
				return target.getResultList();
			}
			return method.invoke(target, args);
		}
		
	}

}
