package org.hibernate.tool.orm.jbt.api.wrp;

import java.util.List;
import java.util.Map;

import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface DatabaseReaderWrapper extends Wrapper {
	Map<String, List<TableWrapper>> collectDatabaseTables();
}

