package org.hibernate.tool.proxies;

public class ClassC {

	private int id;
	private ProxyB myClassB;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ProxyB getMyClassB() {
		return myClassB;
	}

	public void setMyClassB(ProxyB myClassB) {
		this.myClassB = myClassB;
	}
	
}
