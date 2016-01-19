package org.hibernate.cfg;

import javax.persistence.EntityManagerFactory;

import org.hibernate.boot.Metadata;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;

public class JPAConfiguration extends Configuration {
	
	private Metadata metadata = null;
	
	public JPAConfiguration(EntityManagerFactoryBuilderImpl entityManagerFactoryBuilder) {
		EntityManagerFactory entityManagerFactory = 
				entityManagerFactoryBuilder.build();
		metadata = entityManagerFactoryBuilder.getMetadata();
		getProperties().putAll(entityManagerFactory.getProperties());
	}
	
	public Metadata getMetadata() {
		return metadata;
	}
	
}
