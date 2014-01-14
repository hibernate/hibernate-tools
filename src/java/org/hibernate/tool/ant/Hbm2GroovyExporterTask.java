package org.hibernate.tool.ant;

import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.POGOExporter;

/**
 * 
 * @author Rand McNeely
 *
 */
public class Hbm2GroovyExporterTask extends ExporterTask {

	boolean jdk5;
    boolean ejb3;
    boolean generateConstructors;

    public Hbm2GroovyExporterTask(HibernateToolTask parent) {
		super(parent);
	}

	protected Exporter configureExporter(Exporter exp) {
	    super.configureExporter(exp);
		POGOExporter exporter = (POGOExporter) exp;
		exporter.getProperties().setProperty("ejb3", "" + ejb3);
		exporter.getProperties().setProperty("jdk5", "" + jdk5);
		exporter.setGenerateConstructors(generateConstructors);
		return exporter;
	}

	protected Exporter createExporter() {
		return new POGOExporter();
	}

	public String getName() {
		return "hbm2groovy (Generates a set of .groovy files)";
	}

    public void setJdk5(boolean jdk5) {
        this.jdk5 = jdk5;
    }

    public void setEjb3(boolean ejb3) {
        this.ejb3 = ejb3;
    }

    public void setGenerateConstructors(boolean generateConstructors) {
        this.generateConstructors = generateConstructors;
    }

}
