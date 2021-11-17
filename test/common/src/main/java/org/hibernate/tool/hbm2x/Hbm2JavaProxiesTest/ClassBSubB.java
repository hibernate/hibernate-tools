package org.hibernate.tool.hbm2x.Hbm2JavaProxiesTest;

public class ClassBSubB extends ClassB implements ProxyBSubB {

	private String valueB;

	public String getValueB() {
		return valueB;
	}

	public void setValueB(String valueB) {
		this.valueB = valueB;
	}
	
}
