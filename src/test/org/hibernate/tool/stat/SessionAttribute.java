//$Id: SessionAttribute.java 5686 2005-02-12 07:27:32Z steveebersole $
package org.hibernate.tool.stat;


/**
 * @author Gavin King
 */
public class SessionAttribute {
	private Long id;
	private String name;
	private String stringData;
	
	SessionAttribute() {}
	public SessionAttribute(String name) {
		this.name = name;
	}
	public SessionAttribute(String name, String str) {
		this.name = name;
		this.stringData = str;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStringData() {
		return stringData;
	}
	public void setStringData(String stringData) {
		this.stringData = stringData;
	}
	
	public Long getId() {
		return id;
	}
}
