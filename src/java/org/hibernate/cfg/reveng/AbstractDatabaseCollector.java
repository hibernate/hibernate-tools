package org.hibernate.cfg.reveng;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractDatabaseCollector implements DatabaseCollector {

	private Map oneToManyCandidates;
	protected final Map suggestedIdentifierStrategies;

	public AbstractDatabaseCollector() {
		suggestedIdentifierStrategies = new HashMap();
	}
	
	public void setOneToManyCandidates(Map oneToManyCandidates) {
		this.oneToManyCandidates = oneToManyCandidates;
	}

	public Map getOneToManyCandidates() {
		return oneToManyCandidates;
	}

	public String getSuggestedIdentifierStrategy(String catalog, String schema, String name) {
		TableIdentifier identifier = new TableIdentifier(catalog, schema, name);
		return (String) suggestedIdentifierStrategies.get(identifier);
	}

	public void addSuggestedIdentifierStrategy(String catalog, String schema, String name, String idstrategy) {
		TableIdentifier identifier = new TableIdentifier(catalog, schema, name);
		suggestedIdentifierStrategies.put(identifier, idstrategy);
	}

}
