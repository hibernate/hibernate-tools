package org.hibernate.tool.hbm2x.Hbm2JavaProxiesTest;

public class ClassA {

	private int id;
	private ClassB myClassB;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ClassB getMyClassB() {
		return myClassB;
	}

	public void setMyClassB(ClassB myClassB) {
		this.myClassB = myClassB;
	}
	
}
