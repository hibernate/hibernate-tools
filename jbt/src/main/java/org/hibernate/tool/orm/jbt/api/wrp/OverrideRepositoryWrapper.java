package org.hibernate.tool.orm.jbt.api.wrp;

import java.io.File;

public interface OverrideRepositoryWrapper extends Wrapper {
	
	void addFile(File file);
	
	RevengStrategyWrapper getReverseEngineeringStrategy(RevengStrategyWrapper res);
	
	void addTableFilter(TableFilterWrapper tf);

}
