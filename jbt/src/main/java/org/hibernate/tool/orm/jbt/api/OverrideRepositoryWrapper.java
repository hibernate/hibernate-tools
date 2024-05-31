package org.hibernate.tool.orm.jbt.api;

import java.io.File;

import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.reveng.strategy.TableFilter;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface OverrideRepositoryWrapper extends Wrapper {
	
	void addFile(File file);
	
	RevengStrategy getReverseEngineeringStrategy(RevengStrategy res);
	
	void addTableFilter(TableFilter tf);

}
