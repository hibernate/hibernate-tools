package org.hibernate.tool.orm.jbt.api;

import java.io.File;

import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface OverrideRepositoryWrapper extends Wrapper {
	
	void addFile(File file);
	
	RevengStrategyWrapper getReverseEngineeringStrategy(RevengStrategyWrapper res);
	
	void addTableFilter(TableFilterWrapper tf);

}
