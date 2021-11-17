package org.hibernate.tool.hbm2x.Hbm2JavaProxiesTest;

public class ClassBSubA extends ClassB implements ProxyBSubA {

	private String valueA;

	public String getValueA() {
		return valueA;
	}

	public void setValueA(String valueA) {
		this.valueA = valueA;
	}
	
}
