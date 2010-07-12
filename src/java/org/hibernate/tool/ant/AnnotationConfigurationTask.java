/*
 * Created on 25-Feb-2005
 *
 */
package org.hibernate.tool.ant;

import org.apache.tools.ant.BuildException;
import org.hibernate.cfg.Configuration;
import org.hibernate.util.ReflectHelper;

/**
 * Class that uses reflection to load AnnotatioConfiguration.
 * Done to avoid jdk 1.5 compile dependency in tools.
 * 
 * @author max
 *
 */
public class AnnotationConfigurationTask extends ConfigurationTask {

	public AnnotationConfigurationTask() {	
		setDescription("Hibernate Annotation/EJB3 Configuration");
	}
	
	protected Configuration createConfiguration() {
		try {
		Class clazz = ReflectHelper.classForName("org.hibernate.cfg.AnnotationConfiguration", AnnotationConfigurationTask.class);
		return (Configuration) clazz.newInstance();
		} 
		catch(Throwable t) {
			throw new BuildException("Problems in creating a AnnotationConfiguration. Have you remembered to add it to the classpath ?",t);
		}
	}

	protected void validateParameters() throws BuildException {
		super.validateParameters();
		if(getConfigurationFile()==null) {
			log("No hibernate.cfg.xml configuration provided. Annotated classes/packages is only configurable via hibernate.cfg.xml");
		}
	}
	
}
