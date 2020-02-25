package org.hibernate.tool.ant.Cfg2HbmWithPackageNameAndReverseNamingStrategy;

import org.hibernate.tool.api.reveng.TableIdentifier;
import org.hibernate.tool.internal.reveng.strategy.AbstractStrategy;

public class Strategy extends AbstractStrategy {

	public String tableToClassName(TableIdentifier tableIdentifier) {		
		return "Bar";		
	}

}
