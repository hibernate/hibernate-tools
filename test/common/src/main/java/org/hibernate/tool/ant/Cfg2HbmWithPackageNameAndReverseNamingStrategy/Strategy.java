package org.hibernate.tool.ant.Cfg2HbmWithPackageNameAndReverseNamingStrategy;

import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableIdentifier;

public class Strategy extends DefaultReverseEngineeringStrategy {

	public String tableToClassName(TableIdentifier tableIdentifier) {		
		return "Bar";		
	}

}
