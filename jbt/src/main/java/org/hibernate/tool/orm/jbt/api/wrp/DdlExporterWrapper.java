package org.hibernate.tool.orm.jbt.api.wrp;

import java.util.Properties;

import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface DdlExporterWrapper extends Wrapper {
	
	void setExport(boolean b);
	Properties getProperties();

}
