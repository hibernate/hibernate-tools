package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.query.spi.QueryImplementor;
import org.hibernate.type.Type;

public class QueryWrapperFactory {
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static QueryWrapper<?> createQueryWrapper(Query<?> query) {
		return (QueryWrapper<?>)Proxy.newProxyInstance(
				QueryWrapperFactory.class.getClassLoader(), 
				new Class[] { QueryWrapper.class }, 
				new QueryWrapperInvocationHandler(query));
	}
	
    static interface QueryExtension<T> extends Wrapper {
    	@Override Query<T> getWrappedObject();
		void setParameterList(String name, List<Object> list, Object anything);
		void setParameter(String string, Object value, Object anything);
		void setParameter(int i, Object value, Object anything);
		default String[] getReturnAliases() { return new String[0]; }
		default Type[] getReturnTypes() { return new Type[0]; }
    }
    
    
    static interface QueryWrapper<T> extends QueryExtension<T>, QueryImplementor<T> {}
    
    private static class QueryWrapperInvocationHandler<T> implements InvocationHandler, QueryExtension<T> {
    	
    	private Query<T> query = null;
    	
    	private QueryWrapperInvocationHandler(Query<T> q) {
    		query = q;
    	}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (isQueryExtensionMethod(method)) {
				return method.invoke(this, args);
			} else {
				return method.invoke(query, args);
			}
		}
		
		@Override
		public void setParameterList(String name, List<Object> list, Object anything) {
			query.setParameterList(name, list);
		}

		@Override
		public void setParameter(String name, Object value, Object anything) {
			query.setParameter(name, value);
;		}

		@Override
		public void setParameter(int position, Object value, Object anything) {
			query.setParameter(position, value);
		}
		
    	@Override 
    	public Query<T> getWrappedObject() { return query; }

    	
    }
    
    private static boolean isQueryExtensionMethod(Method m) {
    	boolean result = true;
    	try {
			QueryExtension.class.getMethod(m.getName(), m.getParameterTypes());
		} catch (NoSuchMethodException e) {
			result = false;
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}
    	return result;
    }

}
