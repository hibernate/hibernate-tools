package org.hibernate.tool.orm.jbt.api;

import java.io.File;

import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.reveng.strategy.OverrideRepository;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface OverrideRepositoryWrapper extends Wrapper {
	
	default void addFile(File file) {
		((OverrideRepository)getWrappedObject()).addFile(file);
	}
	
	default RevengStrategy getReverseEngineeringStrategy(RevengStrategy res) {
		return ((OverrideRepository)getWrappedObject()).getReverseEngineeringStrategy(res);
	}

}
