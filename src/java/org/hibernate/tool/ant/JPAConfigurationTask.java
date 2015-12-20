package org.hibernate.tool.ant;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.internal.util.ReflectHelper;

public class JPAConfigurationTask extends ConfigurationTask {
	
	private String persistenceUnit = null;

	public JPAConfigurationTask() {
		setDescription("JPA Configuration");
	}
	
	protected Configuration createConfiguration() {
		try {
			Map<Object, Object> overrides = new HashMap<Object, Object>();
			Properties p = getProperties();
			
			if(p!=null) {
				overrides.putAll( p );
			}

			Class<?> hibernatePersistanceProviderClass = ReflectHelper.classForName("org.hibernate.jpa.HibernatePersistenceProvider", JPAConfigurationTask.class);
			Object hibernatePersistanceProvider = hibernatePersistanceProviderClass.newInstance();
			
			Method getEntityManagerFactoryBuilderOrNull = hibernatePersistanceProviderClass.getDeclaredMethod(
					"getEntityManagerFactoryBuilderOrNull", 
					new Class[] { String.class, Map.class });
			getEntityManagerFactoryBuilderOrNull.setAccessible(true);
			Object entityManagerFactoryBuilder = 
					getEntityManagerFactoryBuilderOrNull.invoke(
							hibernatePersistanceProvider, 
							new Object[] { persistenceUnit, overrides});
			
			if (entityManagerFactoryBuilder == null) {
				throw new BuildException(
						"Persistence unit not found: '" + 
						persistenceUnit + 
						"'.");
			}
			
			Method build =
					entityManagerFactoryBuilder.getClass().getMethod(
							"build", new Class[0]);
			build.invoke(entityManagerFactoryBuilder, (Object[])null);
			
			Method getHibernateConfiguration = 
					entityManagerFactoryBuilder.getClass().getMethod(
							"getHibernateConfiguration", new Class[0]);
			return (Configuration)getHibernateConfiguration.invoke(
							entityManagerFactoryBuilder, (Object[])null);
			
		} 
		catch(HibernateException he) {
			throw new BuildException(he);
		}
		catch(BuildException be) {
			throw be;
		}
		catch(Exception t) {
			Throwable cause = t.getCause();
			if (cause != null) {
				throw new BuildException(cause);
			} else {
				t.printStackTrace();
				throw new BuildException("Problems in creating a configuration for JPA. Have you remembered to add hibernate EntityManager jars to the classpath ?",t);	
			}
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
