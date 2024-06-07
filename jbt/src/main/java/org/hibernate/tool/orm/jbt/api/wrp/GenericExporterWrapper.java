package org.hibernate.tool.orm.jbt.api.wrp;

import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface GenericExporterWrapper extends Wrapper {
	
	void setFilePattern(String filePattern);
	
	void setTemplateName(String templateName) ;
	
	void setForEach(String forEach);
	
	String getFilePattern();
	
	String getTemplateName();

}
