package org.hibernate.tool.hbm2x.hbm2hbmxml.MapAndAnyTest;

import java.util.Set;
import java.util.HashSet;

/**
 * todo: describe Address
 *
 * @author Steve Ebersole
 */
public class Address {
	private Long id;
	private Set<String> lines = new HashSet<String>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<String> getLines() {
		return lines;
	}

	public void setLines(Set<String> lines) {
		this.lines = lines;
	}
}
