package org.hibernate.tool.ant;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2x.DAOExporter;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.util.MetadataHelper;

/**
 * @author Dennis Byrne
 */
public class Hbm2DAOExporterTask extends Hbm2JavaExporterTask {

	public Hbm2DAOExporterTask(HibernateToolTask parent) {
		super(parent);
	}
	
	protected Exporter configureExporter(Exporter exp) {
		DAOExporter exporter = (DAOExporter)exp;
		super.configureExporter(exp);
		return exporter;
	}
	
	protected Exporter createExporter() {
		Exporter result = new DAOExporter();
		Configuration configuration = (Configuration)parent.getMetadataSources();
		result.getProperties().putAll(configuration.getProperties());
		result.setMetadata(MetadataHelper.getMetadata(configuration));
		result.setOutputDirectory(parent.getDestDir());
		return result;
	}

	public String getName() {
		return "hbm2dao (Generates a set of DAOs)";
	}

}
