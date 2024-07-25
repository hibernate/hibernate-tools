package org.hibernate.tool.orm.jbt.internal.util;

import java.util.Iterator;

import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;

public interface ExtendedConfiguration {
	
	void setEntityResolver(EntityResolver entityResolver);
	
	void setNamingStrategy(ImplicitNamingStrategy namingStrategy);
	
	Configuration configure(Document document);
	
	void buildMappings();
	
	Iterator<PersistentClass> getClassMappings();
	
	void setPreferBasicCompositeIds(boolean b);
	
	void setReverseEngineeringStrategy(RevengStrategy strategy);
	
	void readFromJDBC();
	
	PersistentClass getClassMapping(String string);
	
	ImplicitNamingStrategy getNamingStrategy();
	
	EntityResolver getEntityResolver();
	
	Iterator<Table> getTableMappings();
	
}
