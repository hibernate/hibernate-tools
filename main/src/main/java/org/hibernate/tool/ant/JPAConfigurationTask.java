package org.hibernate.tool.ant;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JPAConfiguration;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;

public class JPAConfigurationTask extends ConfigurationTask {
	
	private String persistenceUnit = null;

	public JPAConfigurationTask() {
		setDescription("JPA Configuration");
	}
	
	protected Configuration createConfiguration() {
		try {
			Map<Object, Object> overrides = new HashMap<Object, Object>();
			Properties p = loadPropertiesFile();	
			if(p!=null) {
				overrides.putAll( p );
			} else { 
				p = new Properties();
			}
			EntityManagerFactoryBuilderImpl entityManagerFactoryBuilderImpl = 
					createEntityManagerFactoryBuilder(persistenceUnit, overrides);
			if (entityManagerFactoryBuilderImpl == null) {
				throw new BuildException(
						"Persistence unit not found: '" + 
						persistenceUnit + 
						"'.");
			}
			JPAConfiguration result = new JPAConfiguration(entityManagerFactoryBuilderImpl);
			p.putAll(result.getProperties());
			initProperties(p);
			return result;
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
	
	
	private static class PersistenceProvider extends HibernatePersistenceProvider {
		public EntityManagerFactoryBuilderImpl getEntityManagerFactoryBuilder(
				String persistenceUnit, 
				Map<Object, Object> properties) {
			return (EntityManagerFactoryBuilderImpl)getEntityManagerFactoryBuilderOrNull(
					persistenceUnit, 
					properties);
		}
	}

	private EntityManagerFactoryBuilderImpl createEntityManagerFactoryBuilder(
			final String persistenceUnit, 
			final Map<Object, Object> properties) {
		return new PersistenceProvider().getEntityManagerFactoryBuilder(
				persistenceUnit, 
				properties);
	}
	
}
