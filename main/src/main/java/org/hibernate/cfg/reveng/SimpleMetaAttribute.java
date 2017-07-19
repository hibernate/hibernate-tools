package org.hibernate.cfg.reveng;

public class SimpleMetaAttribute {
	String value;
	boolean inheritable = true;

	public SimpleMetaAttribute(String value, boolean inherit) {
		this.value = value;
		this.inheritable = inherit;
	}

	public String toString() {
		return value;
	}

}

