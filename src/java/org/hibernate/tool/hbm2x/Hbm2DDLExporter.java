/*
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.tool.hbm2x;

import java.io.File;
import java.util.Iterator;

import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.integrator.spi.IntegratorService;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.spi.SessionFactoryServiceRegistryFactory;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;

/**
 * Schema Export (.ddl) code generation. 
 * 
 * @author Vitali
 * 
 */
public class Hbm2DDLExporter extends AbstractExporter {

	protected boolean exportToDatabase = true; 
	protected boolean scriptToConsole = true;
	protected boolean schemaUpdate = false;
	protected String delimiter = ";";
	protected boolean drop = false;
	protected boolean create = true;
	protected boolean format = false;

	protected String outputFileName = null;
	protected boolean haltOnError = false;

	public Hbm2DDLExporter() {
	}

	public Hbm2DDLExporter(Configuration cfg, File outputdir) {
		super(cfg, outputdir);
	}

	protected boolean setupBoolProperty(String property, boolean defaultVal) {
		if (!getProperties().containsKey(property)) {
			return defaultVal;
		}
		return Boolean.parseBoolean(getProperties().getProperty(property));
	}

	protected void setupContext() {

		exportToDatabase = setupBoolProperty("exportToDatabase", exportToDatabase);
		scriptToConsole = setupBoolProperty("scriptToConsole", scriptToConsole);
		schemaUpdate = setupBoolProperty("schemaUpdate", schemaUpdate);
		delimiter = getProperties().getProperty("delimiter", delimiter);
		drop = setupBoolProperty("drop", drop);
		create = setupBoolProperty("create", create);
		format = setupBoolProperty("format", format);
		outputFileName = getProperties().getProperty("outputFileName", outputFileName);
		haltOnError = setupBoolProperty("haltOnError", haltOnError);
		super.setupContext();
	}

	protected void cleanUpContext() {
		super.cleanUpContext();
	}

	protected void doStart() {
		final Configuration configuration = getConfiguration();
		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
		builder.applySettings( configuration.getProperties() );
		ServiceRegistry serviceRegistry = builder.build();
		integrateEnvers( configuration, serviceRegistry );
		if (schemaUpdate) {
			SchemaUpdate update = new SchemaUpdate(serviceRegistry, configuration);
			if(outputFileName == null && delimiter == null && haltOnError && format) 
				update.execute(scriptToConsole, exportToDatabase);
			else {				
				if (null != outputFileName) {
					File outputFile = new File(getOutputDirectory(), outputFileName);
					update.setOutputFile(outputFile.getPath());				
					log.debug("delimiter ='"+ delimiter + "'");
					update.setDelimiter(delimiter);
					update.setFormat(Boolean.valueOf(format));						
				}
				
				if (haltOnError) {
					update.setHaltOnError(Boolean.valueOf(haltOnError));
				}				
				update.execute(scriptToConsole, exportToDatabase);
				if (!update.getExceptions().isEmpty()) {
					int i = 1;
					for (Iterator<?> iterator = update.getExceptions().iterator(); iterator
							.hasNext(); i++) {
						Throwable element = (Throwable) iterator.next();
						log.warn("Error #" + i + ": ", element);
					}
					log.error(i - 1 + " errors occurred while performing Hbm2DDLExporter.");
					if (haltOnError) {
						throw new ExporterException(
								"Errors while performing Hbm2DDLExporter");
					}
				}					
			}
		} else {
			SchemaExport export = new SchemaExport(serviceRegistry, configuration);
			if (null != outputFileName) {
				export.setOutputFile(new File(getOutputDirectory(),
						outputFileName).toString());
			}
			if (null != delimiter) {
				export.setDelimiter(delimiter);
			}
			export.setHaltOnError(haltOnError);
			export.setFormat(format);
			if (drop && create) {
				// not just drop or create but both!
				export.execute(scriptToConsole, exportToDatabase, false, false);
			} else {
				export.execute(scriptToConsole, exportToDatabase, drop, create);
			}
		}		
	}

	/**
	 * Integrate Hibernate Envers extension if present in the classpath.
	 */
	private void integrateEnvers(Configuration configuration, ServiceRegistry serviceRegistry) {
		// Omit building SessionFactory.
		// TODO: Update this part when Integrator does not need SessionFactory for applying Hibernate extensions.
		for ( Integrator integrator : serviceRegistry.getService( IntegratorService.class ).getIntegrators() ) {
			if ( "org.hibernate.envers.event.EnversIntegrator".equals( integrator.getClass().getName() ) ) {
				integrator.integrate(
						configuration,
						null,
						serviceRegistry.getService( SessionFactoryServiceRegistryFactory.class ).buildServiceRegistry( null, configuration )
				);
				break;
			}
		}
	}

	public void setExport(boolean export) {
		exportToDatabase = export;
	}

	/**
	 * Run SchemaUpdate instead of SchemaExport
	 */
	public void setUpdate(boolean update) {
		this.schemaUpdate = update;
	}

	/**
	 * Output sql to console ? (default true)
	 */
	public void setConsole(boolean console) {
		this.scriptToConsole = console;
	}

	/**
	 * Format the generated sql
	 */
	public void setFormat(boolean format) {
		this.format = format;
	}

	/**
	 * File out put name (default: empty)
	 */
	public void setOutputFileName(String fileName) {
		outputFileName = fileName;
	}

	public void setDrop(boolean drop) {
		this.drop = drop;
	}

	public void setCreate(boolean create) {
		this.create = create;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setHaltonerror(boolean haltOnError) {
		this.haltOnError = haltOnError;
	}
}
