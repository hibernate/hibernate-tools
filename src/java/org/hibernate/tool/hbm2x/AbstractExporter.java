/*
 * Created on 21-Dec-2004
 */
package org.hibernate.tool.hbm2x;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.internal.util.StringHelper;

/**
 * Base exporter for the template and direct output generation.
 * Sets up the template environment
 * 
 * @author max and david
 */
public abstract class AbstractExporter implements Exporter {

	protected Logger log = LoggerFactory.getLogger(this.getClass());
	
	private File outputdir;
	private Configuration configuration;
	protected String[] templatePaths = new String[0];
	private TemplateHelper vh;
	private Properties properties = new Properties();
	private ArtifactCollector collector = new ArtifactCollector();

	private Iterator iterator;

	private Cfg2HbmTool c2h;
	private Cfg2JavaTool c2j;

	public AbstractExporter(Configuration cfg, File outputdir) {
		this();
		setConfiguration(cfg);
		c2j.setConfiguration(cfg);
		setOutputDirectory(outputdir);		
	}

	public AbstractExporter() {
		c2h = new Cfg2HbmTool();
		c2j = new Cfg2JavaTool();		
	}
	
	public void setOutputDirectory(File outputdir) {
		this.outputdir = outputdir;		
	}

	public void setConfiguration(Configuration cfg) {
		configuration = cfg;
		c2j.setConfiguration(cfg);
	}

    /**
     * @param className
     * @return
     */
	protected File getFileForClassName(File baseDir, String className, String extension) {
    	String filename = StringHelper.unqualify(className) + extension;
    	String packagename = StringHelper.qualifier(className);
    	
    	return new File(getDirForPackage(baseDir, packagename), filename);
    }

    private File getDirForPackage(File baseDir, String packageName) {
        File dir = null;
    
        String p = packageName == null ? "" : packageName;
    
        dir = new File( baseDir, p.replace('.', File.separatorChar) );
    
        return dir;
    }

	public File getOutputDirectory() {
		return outputdir;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * Builds template context and performs file generation
	 * Subclasses mostly implement doStart() instead.
	 */
	public void start() {
		setTemplateHelper( new TemplateHelper() );
		setupTemplates();
		setupContext();
		doStart();
		cleanUpContext();		
		setTemplateHelper(null);
		getArtifactCollector().formatFiles();
	}
	
	abstract protected void doStart();

	public String[] getTemplatePaths() {
		return templatePaths;
	}

	public void setTemplatePath(String[] templatePaths) {
		this.templatePaths = templatePaths;
	}

	public String[] getTemplatePath() {
		return templatePaths;
	}
	
	static String toString(Object[] a) {
        if (a == null)
            return "null";
        if (a.length == 0)
            return "[]";
 
        StringBuffer buf = new StringBuffer();
 
        for (int i = 0; i < a.length; i++) {
            if (i == 0)
                buf.append('[');
            else
                buf.append(", ");
 
            buf.append(String.valueOf(a[i]));
        }
 
        buf.append("]");
        return buf.toString();
    }
 
	protected void setupTemplates() {
		if(log.isDebugEnabled()) {
			log.debug(getClass().getName() + " outputdir:" + getOutputDirectory() + " path: " + toString(templatePaths) );
		}
		getTemplateHelper().init(getOutputDirectory(), templatePaths);		
	}

	/**
	 * Setup the context variables used by the exporter. Subclasses should call super.setupContext() to ensure all needed variables are in the context. 
	 **/
	protected void setupContext() {
		getTemplateHelper().setupContext();		
		getTemplateHelper().putInContext("exporter", this);
		getTemplateHelper().putInContext("c2h", getCfg2HbmTool());
		getTemplateHelper().putInContext("c2j", getCfg2JavaTool());
		
		if(getOutputDirectory()!=null) getTemplateHelper().putInContext("outputdir", getOutputDirectory());
		if(getTemplatePaths()!=null) getTemplateHelper().putInContext("template_path", getTemplatePaths());
		
		if(getProperties()!=null) {
			iterator = getProperties().entrySet().iterator();
			while ( iterator.hasNext() ) {
				Map.Entry element = (Map.Entry) iterator.next();
				String key = element.getKey().toString();
				Object value = transformValue(element.getValue());
				getTemplateHelper().putInContext(key, value);
				if(key.startsWith(ExporterSettings.PREFIX_KEY)) {
					getTemplateHelper().putInContext(key.substring(ExporterSettings.PREFIX_KEY.length()), value);
					if(key.endsWith(".toolclass")) {
						try {
							Class toolClass = ReflectHelper.classForName(value.toString(), this.getClass());
							Object object = toolClass.newInstance();
							getTemplateHelper().putInContext(key.substring(ExporterSettings.PREFIX_KEY.length(),key.length()-".toolclass".length()), object);
						}
						catch (Exception e) {
							throw new ExporterException("Exception when instantiating tool " + element.getKey() + " with " + value,e);
						}
					} 
				}								
			}
		}
		getTemplateHelper().putInContext("artifacts", collector);
        if(getConfiguration()!=null) {
        	getTemplateHelper().putInContext("cfg", getConfiguration());
        }
	}
	
	// called to have "true"/"false" strings returned as real booleans in templates code.
	private Object transformValue(Object value) {
		if("true".equals(value)) {
			return Boolean.TRUE;
		}
		if("false".equals(value)) {
			return Boolean.FALSE;
		}
		return value;
	}

	protected void cleanUpContext() {
		if(getProperties()!=null) {
			iterator = getProperties().entrySet().iterator();
			while ( iterator.hasNext() ) {
				Map.Entry element = (Map.Entry) iterator.next();
				Object value = transformValue(element.getValue());
				String key = element.getKey().toString();
				if(key.startsWith(ExporterSettings.PREFIX_KEY)) {
					getTemplateHelper().removeFromContext(key.substring(ExporterSettings.PREFIX_KEY.length()), value);
				}
				getTemplateHelper().removeFromContext(key, value);
			}
		}

		if(getOutputDirectory()!=null) getTemplateHelper().removeFromContext("outputdir", getOutputDirectory());
		if(getTemplatePaths()!=null) getTemplateHelper().removeFromContext("template_path", getTemplatePaths());
			
		getTemplateHelper().removeFromContext("exporter", this);
		getTemplateHelper().removeFromContext("artifacts", collector);
        if(getConfiguration()!=null) {
        	getTemplateHelper().removeFromContext("cfg", getConfiguration());
        }
        
        getTemplateHelper().removeFromContext("c2h", getCfg2HbmTool());
		getTemplateHelper().removeFromContext("c2j", getCfg2JavaTool());		
	}

	protected void setTemplateHelper(TemplateHelper vh) {
		this.vh = vh;
	}

	protected TemplateHelper getTemplateHelper() {
		return vh;
	}
	
	public void setProperties(Properties properties) {
		this.properties = properties;
		
	}

	public void setArtifactCollector(ArtifactCollector collector) {
		this.collector = collector;
	}
	
	public ArtifactCollector getArtifactCollector() {
		return collector;
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	public String getName() {
		return this.getClass().getName();
	}
	
	public Cfg2HbmTool getCfg2HbmTool() {
		return c2h;
	}
	
	public Cfg2JavaTool getCfg2JavaTool() {
		return c2j;
	}
}
