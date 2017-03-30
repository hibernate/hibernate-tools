package org.hibernate.tool.hbm2x.hbm2hbmxml;

/**
 * todo: describe Person
 *
 * @author Steve Ebersole
 */
public class Person2 {
	private Long id;
	private String name;
	private Object data;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
