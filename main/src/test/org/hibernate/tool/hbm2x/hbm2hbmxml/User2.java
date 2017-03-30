package org.hibernate.tool.hbm2x.hbm2hbmxml;

import java.util.ArrayList;
import java.util.List;

public class User2 {
	private String name;
	private List<Group2> groups = new ArrayList<Group2>();
	
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

	public List<Group2> getGroups() {
		return groups;
	}
	
	void setGroups(List<Group2> groups) {
		this.groups = groups;
	}
	
}
