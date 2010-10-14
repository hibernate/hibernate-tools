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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.util.ReflectHelper;

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
		if (schemaUpdate) {
			SchemaUpdate update = new SchemaUpdate(configuration);
			
			// classic schemaupdate execution, will work with all releases
			if(outputFileName == null && delimiter == null && haltOnError && format) 
				update.execute(scriptToConsole, exportToDatabase);

			// at least one of the parameter unmanaged by 
			// hibernate core prior versions is set 
			else {
				
				/* working with reflection as no idea what hibernate core version is used */
				try {
					Class schemaUpdateClass = SchemaUpdate.class;
					
					if (null != outputFileName) {
						Method setOutputFile = schemaUpdateClass.getMethod("setOutputFile", 
								new Class[] {String.class});
						setOutputFile.invoke(update, new Object[] {new File(getOutputDirectory(),
								outputFileName).toString()});
										
						log.debug("delimiter ='"+ delimiter + "'");
						Method setDelimiter = schemaUpdateClass.getMethod("setDelimiter", 
								new Class[] {String.class});
						setDelimiter.invoke(update, new Object[] {delimiter});
						
						Method setFormat = schemaUpdateClass.getMethod("setFormat", 
								new Class[] {boolean.class});
						setFormat.invoke(update, new Object[] {Boolean.valueOf(format)});
						
					}
					
					if (haltOnError) {
						Method setHaltOnError = schemaUpdateClass.getMethod("setHaltOnError", 
								new Class[] {boolean.class});
						setHaltOnError.invoke(update, new Object[] {Boolean.valueOf(haltOnError)});
					}
					
					update.execute(scriptToConsole, exportToDatabase);
					if (!update.getExceptions().isEmpty()) {
						int i = 1;
						for (Iterator iterator = update.getExceptions().iterator(); iterator
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
					
				} catch (NoSuchMethodException e) {
					log.error( "Error during DDL export, this version of hibernate doesn't support following " +
							"SchemaUpdate parameters: haltonerror = true, format= true, delimiter and outputfilename" + 
							" either update hibernate3.jar or don't used the involved parameters", e );
				} catch (IllegalArgumentException e) {
					log.error( "Error during DDL export, this version of hibernate doesn't support following " +
							"SchemaUpdate parameters: haltonerror = true, format= true, delimiter and outputfilename" + 
							" either update hibernate3.jar or don't used the involved parameters", e );
				} catch (InvocationTargetException e) {
					log.error( "Error during DDL export, this version of hibernate doesn't support following " +
							"SchemaUpdate parameters: haltonerror = true, format= true, delimiter and outputfilename" + 
							" either update hibernate3.jar or don't used the involved parameters", e );
				} catch (IllegalAccessException e) {
					log.error( "Error during DDL export, this version of hibernate doesn't support following " +
							"SchemaUpdate parameters: haltonerror = true, format= true, delimiter and outputfilename" + 
							" either update hibernate3.jar or don't used the involved parameters", e );
				}
			}

		} else {
			SchemaExport export = new SchemaExport(configuration);
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
				export.create(scriptToConsole, exportToDatabase);
			} else {
				export.execute(scriptToConsole, exportToDatabase, drop, create);
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
