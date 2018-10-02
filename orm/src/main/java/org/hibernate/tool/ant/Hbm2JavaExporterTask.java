/*
 * Created on 14-Feb-2005
 *
 */
package org.hibernate.tool.ant;

import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterFactory;
import org.hibernate.tool.api.export.ExporterType;
import org.hibernate.tool.internal.export.pojo.POJOExporter;

/**
 * @author max
 * 
 */
public class Hbm2JavaExporterTask extends ExporterTask {

	boolean ejb3 = false;

	boolean jdk5 = false;

	public Hbm2JavaExporterTask(HibernateToolTask parent) {
		super( parent );
	}

	public void setEjb3(boolean b) {
		ejb3 = b;
	}

	public void setJdk5(boolean b) {
		jdk5 = b;
	}

	protected Exporter configureExporter(Exporter exp) {
		super.configureExporter( exp );
        exp.getProperties().setProperty("ejb3", ""+ejb3);
        exp.getProperties().setProperty("jdk5", ""+jdk5);
		return exp;
	}

	protected Exporter createExporter() {
		return ExporterFactory.createExporter(ExporterType.POJO);
	}

	public String getName() {
		return "hbm2java (Generates a set of .java files)";
	}
}
