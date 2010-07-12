package org.hibernate.cfg.reveng;

import java.util.Iterator;
import java.util.Map;

import org.hibernate.mapping.Table;

// split up to readonly/writeable interface
/**
 * Only intended to be used internally in reveng. *not* public api.
 */
public interface DatabaseCollector {

	public Iterator iterateTables();

	public Table addTable(String schema, String catalog, String name);

	public void setOneToManyCandidates(Map oneToManyCandidates);

	public Table getTable(String schema, String catalog, String name);

	public Map getOneToManyCandidates();

	public void addSuggestedIdentifierStrategy(String catalog, String schema, String name, String strategy);
	
	public String getSuggestedIdentifierStrategy(String catalog, String schema, String name);
	
	
}