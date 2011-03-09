package org.hibernate.tool.hbmlint.detector;

import java.util.Iterator;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.hbmlint.Detector;
import org.hibernate.tool.hbmlint.IssueCollector;

public abstract class EntityModelDetector extends Detector {

	public void visit(Configuration cfg, IssueCollector collector) {
		for (Iterator iter = cfg.getClassMappings(); iter.hasNext();) {
			PersistentClass clazz = (PersistentClass) iter.next();
			this.visit(cfg, clazz, collector);				
		}
	}
	
	public void visit(Configuration cfg, PersistentClass clazz, IssueCollector collector) {
		visitProperties( cfg, clazz, collector );
	}

	public void visitProperties(Configuration cfg, PersistentClass clazz, IssueCollector collector) {
		if(clazz.hasIdentifierProperty()) {
			this.visitProperty(getConfiguration(), clazz, clazz.getIdentifierProperty(), collector);								
		}
		Iterator propertyIterator = clazz.getPropertyIterator();
		while ( propertyIterator.hasNext() ) {
			Property property = (Property) propertyIterator.next();
			this.visitProperty(getConfiguration(), clazz, property, collector);					
			
		}
	}

	public void visitProperty(Configuration configuration, PersistentClass clazz, Property property, IssueCollector collector) {
		
	}
}
