package org.hibernate.cfg.reveng;

public class DefaulAssociationInfo implements AssociationInfo {

	String cascade;
	String fetch;
	Boolean insert;
	Boolean update;
	
	public String getCascade() {
		return cascade;
	}
	public void setCascade(String cascade) {
		this.cascade = cascade;
	}
	public String getFetch() {
		return fetch;
	}
	public void setFetch(String fetch) {
		this.fetch = fetch;
	}
	public Boolean getInsert() {
		return insert;
	}
	public void setInsert(Boolean insert) {
		this.insert = insert;
	}
	public Boolean getUpdate() {
		return update;
	}
	public void setUpdate(Boolean update) {
		this.update = update;
	}

}
