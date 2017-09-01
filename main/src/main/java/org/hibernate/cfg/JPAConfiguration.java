package org.hibernate.cfg;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.hibernate.HibernateException;
import org.hibernate.boot.Metadata;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;

public class JPAConfiguration extends Configuration {
	
	private Properties properties = new Properties();
	private Metadata metadata = null;
	
	public JPAConfiguration(
			final String persistenceUnit, 
			final Properties properties) {
		this(createEntityManagerFactoryBuilder(persistenceUnit, properties));
	}
	
	private JPAConfiguration(EntityManagerFactoryBuilderImpl entityManagerFactoryBuilder) {
		EntityManagerFactory entityManagerFactory = 
				entityManagerFactoryBuilder.build();
		metadata = entityManagerFactoryBuilder.getMetadata();
		properties.putAll(entityManagerFactory.getProperties());
	}
	
	public Metadata getMetadata() {
		return metadata;
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	private static class PersistenceProvider extends HibernatePersistenceProvider {
		public EntityManagerFactoryBuilderImpl getEntityManagerFactoryBuilder(
				String persistenceUnit, 
				Properties properties) {
			EntityManagerFactoryBuilderImpl result = (EntityManagerFactoryBuilderImpl)getEntityManagerFactoryBuilderOrNull(
					persistenceUnit, 
					properties);
			if (result == null) {
				throw new HibernateException(
						"Persistence unit not found: '" + persistenceUnit + "'."
					);
			}
			return result;
		}
	}

	private static EntityManagerFactoryBuilderImpl createEntityManagerFactoryBuilder(
			final String persistenceUnit, 
			final Properties properties) {
		return new PersistenceProvider().getEntityManagerFactoryBuilder(
				persistenceUnit, 
				properties);
	}
	
}
