package org.hibernate.tool.orm.jbt.util;

import java.util.Map;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;

public class HibernateToolsPersistenceProvider extends HibernatePersistenceProvider {

	static EntityManagerFactoryBuilderImpl createEntityManagerFactoryBuilder(
			final String persistenceUnit, 
			final Map<Object, Object> properties) {
		return new HibernateToolsPersistenceProvider()
				.getEntityManagerFactoryBuilder(
						persistenceUnit, 
						properties);
	}	

	private EntityManagerFactoryBuilderImpl getEntityManagerFactoryBuilder(
			String persistenceUnit, 
			Map<Object, Object> properties) {
		return (EntityManagerFactoryBuilderImpl)getEntityManagerFactoryBuilderOrNull(
				persistenceUnit, 
				properties);
	}
	
}
