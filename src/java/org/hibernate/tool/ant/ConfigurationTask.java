/*
 * Created on 13-Feb-2005
 *
 */
package org.hibernate.tool.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.util.ReflectHelper;
import org.xml.sax.EntityResolver;

/**
 * @author max
 *
 */
public class ConfigurationTask extends Task {

	private List fileSets = new ArrayList();
	private Configuration cfg;
	private File configurationFile;
	private File propertyFile;
	protected String entityResolver;
	private String namingStrategy;
	
	public ConfigurationTask() {
		setDescription("Standard Configuration");
	}
	
	public void addConfiguredFileSet(FileSet fileSet) {
		fileSets.add(fileSet);	 	
	}

	/**
	 * @return
	 */
	public final Configuration getConfiguration() {
		if(cfg==null) {
			cfg = createConfiguration();
			doConfiguration(cfg);
			cfg.buildMappings(); // needed otherwise not all assocations are made!
		}
		return cfg;
	}

	protected Configuration createConfiguration() {
		return new Configuration();
	}
	
	/**
	 * 
	 */
	protected void doConfiguration(Configuration configuration) {	
		validateParameters();		
		
		if (entityResolver != null) {
			try {
				Class resolver = ReflectHelper.classForName(entityResolver, this.getClass());
				Object object = resolver.newInstance();
				
				configuration.setEntityResolver((EntityResolver) object);
				getProject().log("Using " + entityResolver + " as entity resolver");
			}
			catch (Exception e) {
				throw new BuildException("Could not create or find " + entityResolver + " class to use for entity resolvement");
			}			
		}
		if (namingStrategy != null) {
			try {
				Class resolver = ReflectHelper.classForName(namingStrategy, this.getClass());
				Object object = resolver.newInstance();
				
				configuration.setNamingStrategy((NamingStrategy) object);
				getProject().log("Using " + namingStrategy + " as naming strategy");
			}
			catch (Exception e) {
				throw new BuildException("Could not create or find " + namingStrategy + " class to use for naming strategy");
			}			
		}
		
		if (configurationFile != null) configuration.configure( configurationFile );
		addMappings(getFiles() );
		Properties p = getProperties();
		if(p!=null) {		
			Properties overrides = new Properties();
			overrides.putAll(configuration.getProperties());
			overrides.putAll(p);
			configuration.setProperties(overrides);
		}		
	}

	protected Properties getProperties() {
		if (propertyFile!=null) { 
			Properties properties = new Properties(); // TODO: should we "inherit" from the ant projects properties ?
			FileInputStream is = null;
			try {
				is = new FileInputStream(propertyFile);
				properties.load(is);
				return properties;
			} 
			catch (FileNotFoundException e) {
				throw new BuildException(propertyFile + " not found.",e);					
			} 
			catch (IOException e) {
				throw new BuildException("Problem while loading " + propertyFile,e);				
			}
			finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
					}
				}
			}
		} else {
			return null;
		}
	}
	
	
	protected void validateParameters() throws BuildException {
				// noop
	}

	/**
	 * @param files
	 */
	private void addMappings(File[] files) {
		for (int i = 0; i < files.length; i++) {
			File filename = files[i];
			boolean added = addFile(filename);
			if(!added) {
				log(filename + " not added to Configuration", Project.MSG_VERBOSE);
			}
		}		
	}

	/**
	 * @param filename
	 */
	protected boolean addFile(File filename) {
		try {
			if ( filename.getName().endsWith(".jar") ) {
				cfg.addJar( filename );
				return true;
			}
			else {
				cfg.addFile(filename);
				return true;
			}
		} 
		catch (HibernateException he) {
			throw new BuildException("Failed in building configuration when adding " + filename, he);
		}
	}

	private File[] getFiles() {

		List files = new LinkedList();
		for ( Iterator i = fileSets.iterator(); i.hasNext(); ) {

			FileSet fs = (FileSet) i.next();
			DirectoryScanner ds = fs.getDirectoryScanner( getProject() );

			String[] dsFiles = ds.getIncludedFiles();
			for (int j = 0; j < dsFiles.length; j++) {
				File f = new File(dsFiles[j]);
				if ( !f.isFile() ) {
					f = new File( ds.getBasedir(), dsFiles[j] );
				}

				files.add( f );
			}
		}

		return (File[]) files.toArray(new File[files.size()]);
	}

	
	public File getConfigurationFile() {
		return configurationFile;
	}
	public void setConfigurationFile(File configurationFile) {
		this.configurationFile = configurationFile;
	}
	public File getPropertyFile() {
		return propertyFile;
	}
	public void setPropertyFile(File propertyFile) {
		this.propertyFile = propertyFile;
	}
	
	public void setEntityResolver(String entityResolverName) {
		this.entityResolver = entityResolverName;
	}
	
	public void setNamingStrategy(String namingStrategy) {
		this.namingStrategy = namingStrategy;
	}
}
