package org.hibernate.tool.proxies;

public class ClassBSubB extends ClassB implements ProxyBSubB {

	private String valueB;

	public String getValueB() {
		return valueB;
	}

	public void setValueB(String valueB) {
		this.valueB = valueB;
	}
	
}
