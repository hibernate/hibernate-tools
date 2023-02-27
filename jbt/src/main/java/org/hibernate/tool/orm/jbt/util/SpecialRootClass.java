package org.hibernate.tool.orm.jbt.util;

import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;

public class SpecialRootClass extends RootClass {

	public SpecialRootClass(Property property, MetadataBuildingContext buildingContext) {
		super(buildingContext);
	}

}
