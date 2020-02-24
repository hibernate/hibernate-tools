package org.hibernate.tool.ant.Cfg2HbmWithCustomReverseNamingStrategy;

import org.hibernate.tool.api.reveng.TableIdentifier;
import org.hibernate.tool.internal.reveng.strategy.AbstractRevengStrategy;

public class Strategy extends AbstractRevengStrategy {

	public String tableToClassName(TableIdentifier tableIdentifier) {		
		return "foo.Bar";		
	}

}
