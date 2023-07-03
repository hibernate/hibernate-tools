package org.hibernate.tool.orm.jbt.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.orm.jbt.wrp.DelegatingPersistentClassWrapperImpl;
import org.hibernate.tool.orm.jbt.wrp.SessionFactoryWrapper;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;

public class NativeConfiguration extends Configuration {
	
	@SuppressWarnings("unused")
	private EntityResolver entityResolver = null;
	
	@SuppressWarnings("unused")
	private NamingStrategy namingStrategy = null;
	
	private Metadata metadata = null;
	
	public void setEntityResolver(EntityResolver entityResolver) {
		// This method is not supported anymore in class Configuration from Hibernate 5+
		// Only caching the EntityResolver for bookkeeping purposes
		this.entityResolver = entityResolver;
	}
	
	public EntityResolver getEntityResolver() {
		// This method is not supported anymore in class Configuration from Hibernate 5+
		// Returning the cached EntityResolver for bookkeeping purposes
		return entityResolver;
	}
	
	public void setNamingStrategy(NamingStrategy namingStrategy) {
		// The method Configuration.setNamingStrategy() is not supported 
		// anymore from Hibernate 5+.
		// Naming strategies can be configured using the 
		// AvailableSettings.IMPLICIT_NAMING_STRATEGY property.
		// Only caching the NamingStrategy for bookkeeping purposes
		this.namingStrategy = namingStrategy;
	}
	
	public NamingStrategy getNamingStrategy() {
		// This method is not supported anymore from Hibernate 5+
		// Returning the cached NamingStrategy for bookkeeping purposes
		return namingStrategy;
	}
	
	public Configuration configure(Document document) {
		File tempFile = null;
		Configuration result = null;
		metadata = null;
		try {
			tempFile = File.createTempFile(document.toString(), "cfg.xml");
			DOMSource domSource = new DOMSource(document);
			StringWriter stringWriter = new StringWriter();
			StreamResult stream = new StreamResult(stringWriter);
		    TransformerFactory tf = TransformerFactory.newInstance();
		    Transformer transformer = tf.newTransformer();
		    transformer.transform(domSource, stream);
		    FileWriter fileWriter = new FileWriter(tempFile);
		    fileWriter.write(stringWriter.toString());
		    fileWriter.close();
			result = configure(tempFile);
		} catch(IOException | TransformerException e) {
			throw new RuntimeException("Problem while configuring", e);
		} finally {
			tempFile.delete();
		}
		return result;
	}
	
	public void buildMappings() {
		buildMetadata();
	}
	
	public Iterator<PersistentClass> getClassMappings() {
		final Iterator<PersistentClass> iterator = getMetadata().getEntityBindings().iterator();
		return new Iterator<PersistentClass>() {
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}
			@Override
			public PersistentClass next() {
				return new DelegatingPersistentClassWrapperImpl(iterator.next());
			}
			
		};
	}
	
	public PersistentClass getClassMapping(String name) {
		PersistentClass pc = getMetadata().getEntityBinding(name);
		return pc == null ? null : new DelegatingPersistentClassWrapperImpl(pc);
	}
	
	public Iterator<Table> getTableMappings() {
		return getMetadata().collectTableMappings().iterator();
	}
	
	public void setPreferBasicCompositeIds(boolean b) {
		throw new RuntimeException(
				"Method 'setPreferBasicCompositeIds' should not be called on instances of " +
				this.getClass().getName());
	}
		
	public void setReverseEngineeringStrategy(RevengStrategy strategy) {
		throw new RuntimeException(
				"Method 'setReverseEngineeringStrategy' should not be called on instances of " +
				this.getClass().getName());
	}
	
	public void readFromJDBC() {
		throw new RuntimeException(
				"Method 'readFromJDBC' should not be called on instances of " +
				this.getClass().getName());
	}
	
	public Metadata getMetadata() {
		if (metadata == null) {
			buildMetadata();
		}
		return metadata;
	}
	
	@Override
	public SessionFactory buildSessionFactory() {
		return new SessionFactoryWrapper(super.buildSessionFactory());
	}
	
	private void buildMetadata() {
		MetadataSources metadataSources = MetadataHelper.getMetadataSources(this);
		getStandardServiceRegistryBuilder().applySettings(getProperties());
		metadata = metadataSources.buildMetadata(getStandardServiceRegistryBuilder().build());
	}
	
}
