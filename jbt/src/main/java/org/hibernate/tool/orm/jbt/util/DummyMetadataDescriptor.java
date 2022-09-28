package org.hibernate.tool.orm.jbt.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Properties;

import org.hibernate.boot.Metadata;
import org.hibernate.tool.api.metadata.MetadataDescriptor;

public class DummyMetadataDescriptor implements MetadataDescriptor {

	private static final MetadataInvocationHandler HANDLER = new MetadataInvocationHandler();	
	private static final Class<?>[] INTERFACES = new Class[] { Metadata.class };
	private static final ClassLoader LOADER = Metadata.class.getClassLoader();

	@Override
	public Metadata createMetadata() {
		return (Metadata)Proxy.newProxyInstance(LOADER, INTERFACES, HANDLER);
	}

	@Override
	public Properties getProperties() {
		return null;
	}

	private static class MetadataInvocationHandler implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if ("getEntityBindings".equals(method.getName()) || 
					"collectTableMappings".equals(method.getName())) {
				return Collections.emptySet();
			} else {
				return null;
			}
		}		
	}

}
