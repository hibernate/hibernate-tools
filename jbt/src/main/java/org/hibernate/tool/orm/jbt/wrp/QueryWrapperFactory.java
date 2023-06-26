package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

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
    	@Override default Query<?> getWrappedObject() { return (Query<?>)this; }
		default void setParameterList(String name, List<Object> list, Object anything) {
			getWrappedObject().setParameterList(name, list);
		}
	}
    
    private static class QueryWrapperInvocationHandler implements InvocationHandler {
    	
    	private Query<?> query = null;
    	
    	private QueryWrapperInvocationHandler(Query<?> q) {
    		query = q;
    	}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (isSetParamterListMethod(method, args)) {
				return query.setParameterList((String)args[0], (List<?>)args[1]);
			} else if (method.getName().equals("getWrappedObject") && (args == null || args.length == 0)) {
				return query;
			}
			return method.invoke(query, args);
		}
		
		private boolean isSetParamterListMethod(Method m, Object[] args) {
			return m.getName().equals("setParameterList")
					&& args.length == 3
					&& args[0] instanceof String 
					&& args[1] instanceof List<?>;
		}
    	
    }

}
