package org.hibernate.tool.hbm2x;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;

/**
 * 
 * @author max
 */
public interface ConfigurationVisitor {

	boolean startPersistentClass(PersistentClass clazz) throws ExporterException;
	void endPersistentClass(PersistentClass clazz) throws ExporterException;
    
	boolean startComponent(Component component) throws ExporterException;
    void endComponent(Component componenet) throws ExporterException;
	
	void finish() throws ExporterException;
    
    boolean startProperty(Property prop);
    void endProperty(Property prop);
    
	void startEmbeddedIdentifier(Component component);
	void endEmbeddedIdentifier(Component component);
	
	void startIdentifierProperty(Property identifierProperty);
    void endIdentifierProperty(Property identifierProperty);
    
    boolean startMapping(Configuration cfg);
    void endMapping(Configuration cfg);

	boolean startGeneralConfiguration(Configuration cfg) throws ExporterException;
	void endGeneralConfiguration(Configuration cfg) throws ExporterException;

}
