/*
 * Created on 2004-12-01
 */
package org.hibernate.tool.hbm2x;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.hbm2x.pojo.ComponentPOJOClass;
import org.hibernate.tool.hbm2x.pojo.POJOClass;

/**
 * @author max and david
 */
public class ConfigurationNavigator {

	private static final Log log = LogFactory.getLog(POJOExporter.class);
	
	/**
	 * @param cfg
	 * @param exporter
	 * @param file
	 */
	public void export(Configuration cfg, ConfigurationVisitor exporter) {

		Map components = new HashMap();
		
		for (Iterator classes = cfg.getClassMappings(); classes.hasNext(); ) {
		    if(exporter.startMapping(cfg) ) {
		        PersistentClass clazz = (PersistentClass) classes.next();
		        collectComponents(components,clazz);
		        
		        if(exporter.startPersistentClass(clazz) ) {
		            if(clazz.hasIdentifierProperty() ) {
		                exporter.startIdentifierProperty(clazz.getIdentifierProperty() );
		                exporter.endIdentifierProperty(clazz.getIdentifierProperty() );
		            } 
		            else if (clazz.hasEmbeddedIdentifier() ) {
						exporter.startEmbeddedIdentifier( (Component)clazz.getKey() );
						exporter.endEmbeddedIdentifier( (Component)clazz.getKey() );
		            }
		            Iterator unjoinedPropertyIterator = clazz.getUnjoinedPropertyIterator();
		            while(unjoinedPropertyIterator.hasNext() ) {
		                Property prop = (Property)unjoinedPropertyIterator.next();
		                exporter.startProperty(prop);
		                exporter.endProperty(prop);
		            }
		        } 
		        exporter.endPersistentClass(clazz);
		    } 
		    else {
		        exporter.endMapping(cfg);
		    }
		}
		
		for(Iterator comps = components.values().iterator(); comps.hasNext(); ) {
			Component component = (Component)comps.next();
			exporter.startComponent(component);
		}
		
		if (exporter.startGeneralConfiguration(cfg) )
			exporter.endGeneralConfiguration(cfg);

	}

	/**
	 * @param clazz
	 */
	public static void collectComponents(Map components, PersistentClass clazz) {
		Iterator iter = new Cfg2JavaTool().getPOJOClass(clazz).getAllPropertiesIterator();
		collectComponents( components, iter );		
	}

	public static void collectComponents(Map components, POJOClass clazz) {
		Iterator iter = clazz.getAllPropertiesIterator();
		collectComponents( components, iter );		
	}
	
	private static void collectComponents(Map components, Iterator iter) {
		while(iter.hasNext()) {
			Property property = (Property) iter.next();
			if (!"embedded".equals(property.getPropertyAccessorName()) && // HBX-267, embedded property for <properties> should not be generated as component. 
				property.getValue() instanceof Component) {
				Component comp = (Component) property.getValue();
				addComponent( components, comp );			
			} 
			else if (property.getValue() instanceof Collection) {
				// compisite-element in collection
				Collection collection = (Collection) property.getValue();				
				if ( collection.getElement() instanceof Component) {
					Component comp = (Component) collection.getElement();				
					addComponent(components, comp);				
				}
			}
		}
	}

	private static void addComponent(Map components, Component comp) {
		if(!comp.isDynamic()) {
			Component existing = (Component) components.put(comp.getComponentClassName(), comp);
			
			if(existing!=null) {
				log.warn("Component " + existing.getComponentClassName() + " found more than once! Will only generate the last found.");
			}
		} else {
			log.debug("dynamic-component found. Ignoring it as a component, but will collect any embedded components.");
		}	
		collectComponents( components, new ComponentPOJOClass(comp, new Cfg2JavaTool()).getAllPropertiesIterator());		
	}

}
