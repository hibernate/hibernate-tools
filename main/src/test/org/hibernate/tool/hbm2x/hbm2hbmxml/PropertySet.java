package org.hibernate.tool.hbm2x.hbm2hbmxml;

import java.util.Map;
import java.util.HashMap;

/**
 * todo: describe PropertySet
 *
 * @author Steve Ebersole
 */
public class PropertySet {
	private Long id;
	private String name;
	private PropertyValue someSpecificProperty;
	private Map<Object, Object> generalProperties = new HashMap<Object, Object>();

	public PropertySet() {
	}

	public PropertySet(String name) {
		this.name = name;
	}

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

	public PropertyValue getSomeSpecificProperty() {
		return someSpecificProperty;
	}

	public void setSomeSpecificProperty(PropertyValue someSpecificProperty) {
		this.someSpecificProperty = someSpecificProperty;
	}

	public Map<Object, Object> getGeneralProperties() {
		return generalProperties;
	}

	public void setGeneralProperties(Map<Object, Object> generalProperties) {
		this.generalProperties = generalProperties;
	}
}
