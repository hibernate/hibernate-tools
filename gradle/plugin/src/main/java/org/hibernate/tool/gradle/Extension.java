package org.hibernate.tool.gradle;

import org.gradle.api.Project;

public class Extension {
	
	public String sqlToRun = "";
	public String hibernateProperties = "hibernate.properties";
	public String outputFolder = "generated-sources";
	public String packageName = "";
	public String revengStrategy = null;
	public String revengFile = null;
	public Boolean generateAnnotations = true;
	public Boolean useGenerics = true;
	
	public Extension(Project project) {}
	
}
