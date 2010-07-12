package org.hibernate.cfg.reveng;

public interface AssociationInfo {

		String getCascade();
		
		String getFetch();
		
		Boolean getUpdate();
		Boolean getInsert();					

}
