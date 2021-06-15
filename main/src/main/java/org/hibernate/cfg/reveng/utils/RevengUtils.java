package org.hibernate.cfg.reveng.utils;

import org.hibernate.cfg.reveng.AssociationInfo;

public class RevengUtils {
	
	public static AssociationInfo createAssociationInfo(			
			String cascade, 
			String fetch, 
			Boolean insert, 
			Boolean update) {
		return new AssociationInfo() {
			@Override
			public String getCascade() {
				return cascade;
			}
			@Override
			public String getFetch() {
				return fetch;
			}
			@Override
			public Boolean getUpdate() {
				return update;
			}
			@Override
			public Boolean getInsert() {
				return insert;
			}
			
		};
	}
}
