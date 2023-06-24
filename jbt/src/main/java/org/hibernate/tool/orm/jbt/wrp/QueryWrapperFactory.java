package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.query.Query;
import org.hibernate.query.spi.QueryImplementor;

public class QueryWrapperFactory {
	
	public static QueryWrapper<?> createQueryWrapper(Query<?> query) {
		return (QueryWrapper<?>)Proxy.newProxyInstance(
				QueryWrapperFactory.class.getClassLoader(), 
				new Class[] { QueryWrapper.class }, 
				new QueryWrapperInvocationHandler(query));
	}
	
    static interface QueryWrapper<T> extends QueryImplementor<T>, Wrapper {
		
	}
    
    private static class QueryWrapperInvocationHandler implements InvocationHandler {
    	
    	private QueryWrapperInvocationHandler(Query<?> q) {
    	}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return null;
		}
    	
    }

}
