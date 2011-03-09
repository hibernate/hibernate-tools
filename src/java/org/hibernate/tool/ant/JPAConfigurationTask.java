package org.hibernate.tool.ant;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.util.ReflectHelper;
import org.xml.sax.EntityResolver;

public class JPAConfigurationTask extends ConfigurationTask {
	
	private String persistenceUnit = null;

	public JPAConfigurationTask() {
		setDescription("JPA Configuration");
	}
	
	protected Configuration createConfiguration() {
		try {
			Map overrides = new HashMap();
			Properties p = getProperties();
			
			if(p!=null) {
				overrides.putAll( p );
			}

			Class clazz = ReflectHelper.classForName("org.hibernate.ejb.Ejb3Configuration", JPAConfigurationTask.class);
			Object ejb3cfg = clazz.newInstance();
			
			if(entityResolver!=null) {
				Class resolver = ReflectHelper.classForName(entityResolver, this.getClass());
				Object object = resolver.newInstance();
				Method method = clazz.getMethod("setEntityResolver", new Class[] { EntityResolver.class });
				method.invoke(ejb3cfg, new Object[] { object } );
			}
			
			Method method = clazz.getMethod("configure", new Class[] { String.class, Map.class });
			if ( method.invoke(ejb3cfg, new Object[] { persistenceUnit, overrides } ) == null ) {
				throw new BuildException("Persistence unit not found: '" + persistenceUnit + "'.");
			}
			
			method = clazz.getMethod("getHibernateConfiguration", new Class[0]);
			return (Configuration) method.invoke(ejb3cfg, null);
		} 
		catch(HibernateException he) {
			throw new BuildException(he);
		}
		catch(BuildException be) {
			throw be;
		}
		catch(Exception t) {
			throw new BuildException("Problems in creating a configuration for JPA. Have you remembered to add hibernate EntityManager jars to the classpath ?",t);			
		}
		
	}
	
	protected void doConfiguration(Configuration configuration) {
	}
	
	protected void validateParameters() throws BuildException {
		
	}
	
	public String getPersistenceUnit() {
		return persistenceUnit;
	}
	
	public void setPersistenceUnit(String persistenceUnit) {
		this.persistenceUnit = persistenceUnit;
	}
	
	public void setConfigurationFile(File configurationFile) {
		complain("configurationfile");
	}

	private void complain(String param) {
		throw new BuildException("<" + getTaskName() + "> currently only support autodiscovery from META-INF/persistence.xml. Thus setting the " + param + " attribute is not allowed");
	}
	
	
}
