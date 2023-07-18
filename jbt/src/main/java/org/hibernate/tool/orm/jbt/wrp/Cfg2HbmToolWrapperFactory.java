package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.tool.internal.export.hbm.Cfg2HbmTool;
import org.hibernate.tool.internal.export.hbm.HBMTagForValueVisitor;

public class Cfg2HbmToolWrapperFactory {
	
	public static Cfg2HbmToolWrapper createCfg2HbmToolWrapper() {
		return (Cfg2HbmToolWrapper)Proxy.newProxyInstance(
				Cfg2HbmToolWrapperFactory.class.getClassLoader(), 
				new Class[] { Cfg2HbmToolWrapper.class }, 
				new Cfg2HbmToolInvocationHandler(new Cfg2HbmTool()));
	}
	
	static interface Cfg2HbmToolWrapper extends Wrapper {
		@Override Cfg2HbmTool getWrappedObject();
		default String getTag(PersistentClass pc) {
			return getWrappedObject().getTag(pc);
		}
		default String getTag(Property p) {
			if (p instanceof Wrapper) {
				p = (Property)((Wrapper)p).getWrappedObject();
			}
			PersistentClass persistentClass = p.getPersistentClass();
			if(persistentClass!=null) {
				Property v = persistentClass.getVersion();
				if (v instanceof Wrapper) {
					v = (Property)((Wrapper)v).getWrappedObject();
				}
				if(v==p) {
					String typeName = ((SimpleValue)p.getValue()).getTypeName();
					if("timestamp".equals(typeName) || "dbtimestamp".equals(typeName)) {
						return "timestamp";
					} else {
						return "version";
					}
				}
			}
			String toolTag = (String) p.getValue().accept(HBMTagForValueVisitor.INSTANCE);
			if ("component".equals(toolTag) && "embedded".equals(p.getPropertyAccessorName())){
				toolTag = "properties";
			}
			return toolTag;
		}
	}
	
	private static class Cfg2HbmToolInvocationHandler implements InvocationHandler, Cfg2HbmToolWrapper {
		
		private Cfg2HbmTool delegate = null;
		
		public Cfg2HbmToolInvocationHandler(Cfg2HbmTool cfg2HbmTool) {
			delegate = cfg2HbmTool;
		}

		@Override
		public Cfg2HbmTool getWrappedObject() {
			return delegate;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return method.invoke(this, args);
		}
		
	}

}
