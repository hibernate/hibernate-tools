package org.hibernate.tool.orm.jbt.util;

import java.util.Map;
import java.util.Properties;

import jakarta.persistence.EntityManagerFactory;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;

public class JpaConfiguration extends Configuration {

	Metadata metadata = null;
	SessionFactory sessionFactory;
	
	String persistenceUnit;
	
	public JpaConfiguration(
			String persistenceUnit, 
			Map<Object, Object> properties) {
		this.persistenceUnit = persistenceUnit;
		if (properties != null) {
			getProperties().putAll(properties);
		}
	}
	
	public Metadata getMetadata() {
		if (metadata == null) {
			initialize();
		}
		return metadata;
	}
	
	@Override
	public SessionFactory buildSessionFactory() {
		if (sessionFactory == null) {
			initialize();
		}
		return sessionFactory;
	}
	
	@Override
	public Configuration setProperties(Properties properties) {
		super.setProperties(properties);
		metadata = null;
		sessionFactory = null;
		return this;
	}
	
	@Override
	public Configuration addProperties(Properties properties) {
		super.addProperties(properties);
		metadata = null;
		sessionFactory = null;
		return this;
	}
	
	public String getPersistenceUnit() {
		return persistenceUnit;
	}
	
	void initialize() {
		EntityManagerFactoryBuilderImpl entityManagerFactoryBuilder = 
				HibernateToolsPersistenceProvider
					.createEntityManagerFactoryBuilder(
							persistenceUnit, 
							getProperties());
		EntityManagerFactory entityManagerFactory = 
				entityManagerFactoryBuilder.build();
		sessionFactory = (SessionFactory)entityManagerFactory;
		metadata = entityManagerFactoryBuilder.getMetadata();
		getProperties().putAll(entityManagerFactory.getProperties());
	}
}
