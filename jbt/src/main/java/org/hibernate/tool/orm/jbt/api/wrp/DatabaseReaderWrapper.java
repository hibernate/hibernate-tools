package org.hibernate.tool.orm.jbt.api.wrp;

import java.util.List;
import java.util.Map;

public interface DatabaseReaderWrapper extends Wrapper {
	Map<String, List<TableWrapper>> collectDatabaseTables();
}

