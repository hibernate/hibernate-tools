package org.hibernate.tool.hbm2x.hbm2hbmxml;

import java.util.ArrayList;
import java.util.List;

public class User2 {
	private String name;
	private List groups = new ArrayList();
	
	User2() {}
	
	public User2(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	

	void setName(String name) {
		this.name = name;
	}

	public List getGroups() {
		return groups;
	}
	
	void setGroups(List groups) {
		this.groups = groups;
	}
	
}
