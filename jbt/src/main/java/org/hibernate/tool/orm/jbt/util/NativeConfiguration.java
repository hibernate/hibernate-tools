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

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.mapping.PersistentClass;
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
	
	public void setNamingStrategy(NamingStrategy namingStrategy) {
		// The method Configuration.setNamingStrategy() is not supported 
		// anymore from Hibernate 5+.
		// Naming strategies can be configured using the 
		// AvailableSettings.IMPLICIT_NAMING_STRATEGY property.
		// Only caching the NamingStrategy for bookkeeping purposes
		this.namingStrategy = namingStrategy;
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
		return getMetadata().getEntityBindings().iterator();
	}
	
	private Metadata getMetadata() {
		if (metadata == null) {
			buildMetadata();
		}
		return metadata;
	}
	
	private void buildMetadata() {
		MetadataSources metadataSources = MetadataHelper.getMetadataSources(this);
		getStandardServiceRegistryBuilder().applySettings(getProperties());
		metadata = metadataSources.buildMetadata(getStandardServiceRegistryBuilder().build());
	}
	
}
