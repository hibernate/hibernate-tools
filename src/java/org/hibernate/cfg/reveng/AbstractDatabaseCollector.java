package org.hibernate.cfg.reveng;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.cfg.reveng.dialect.MetaDataDialect;

public abstract class AbstractDatabaseCollector implements DatabaseCollector {

	private Map oneToManyCandidates;
	protected final Map suggestedIdentifierStrategies;
	private MetaDataDialect metaDataDialect;

	public AbstractDatabaseCollector(MetaDataDialect metaDataDialect) {
		suggestedIdentifierStrategies = new HashMap();
		this.metaDataDialect = metaDataDialect;
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
	
	protected String quote(String name) {
		if (name == null)
			return name;
		if (metaDataDialect.needQuote(name)) {
			if (name.length() > 1 && name.charAt(0) == '`'
					&& name.charAt(name.length() - 1) == '`') {
				return name; // avoid double quoting
			}
			return "`" + name + "`";
		} else {
			return name;
		}
	}

}
