package org.hibernate.tool.orm.jbt.api.wrp;

import java.util.Properties;

public interface DdlExporterWrapper extends Wrapper {
	
	void setExport(boolean b);
	Properties getProperties();

}
