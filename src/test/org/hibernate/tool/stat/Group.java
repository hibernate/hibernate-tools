//$Id: Group.java 5686 2005-02-12 07:27:32Z steveebersole $
package org.hibernate.tool.stat;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Gavin King
 */
public class Group {
	private String name;
	private Set users = new HashSet();
	Group() {}
	public Group(String n) {
		name = n;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Set getUsers() {
		return users;
	}
	public void setUsers(Set users) {
		this.users = users;
	}
	public void addUser(User user) {
		users.add(user);
		
	}
}
