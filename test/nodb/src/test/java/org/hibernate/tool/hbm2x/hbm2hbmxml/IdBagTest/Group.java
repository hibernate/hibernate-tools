package org.hibernate.tool.hbm2x.hbm2hbmxml.IdBagTest;

public class Group {
	private String name;
	
	Group() {}
	
	public Group(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	void setName(String name) {
		this.name = name;
	}
	
}
