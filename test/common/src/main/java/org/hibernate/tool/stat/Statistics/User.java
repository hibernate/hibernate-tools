//$Id: User.java 5686 2005-02-12 07:27:32Z steveebersole $
package org.hibernate.tool.stat.Statistics;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Gavin King
 */
public class User {
	private String name;
	private String password;
	private Set<SessionAttribute> session = new HashSet<SessionAttribute>();
	User() {}
	public User(String n, String pw) {
		name=n;
		password = pw;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Set<SessionAttribute> getSession() {
		return session;
	}
	public void setSession(Set<SessionAttribute> session) {
		this.session = session;
	}
}
