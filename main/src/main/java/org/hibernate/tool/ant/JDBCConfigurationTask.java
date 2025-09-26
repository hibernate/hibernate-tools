/*
 * Created on 15-Feb-2005
 *
 */
package org.hibernate.tool.ant;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.boot.cfgxml.internal.ConfigLoader;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.util.ReflectHelper;


/**
 * @author max
 * @author <a href='mailto:the_mindstorm@evolva.ro'>Alexandru Popescu</a>
 */
public class JDBCConfigurationTask extends ConfigurationTask {
	//not expfosed here.
    private boolean preferBasicCompositeIds = true;
    
    private String reverseEngineeringStrategyClass;
    private String packageName;
	private Path revengFiles;

	private boolean detectOneToOne = true;
	private boolean detectManyToMany = true;
	private boolean detectOptimisticLock = true;
    
	public JDBCConfigurationTask() {
		setDescription("JDBC Configuration (for reverse engineering)");
	}
	protected MetadataDescriptor createMetadataDescriptor() {
		Properties properties = loadProperties();
		ReverseEngineeringStrategy res = createReverseEngineeringStrategy();
		return MetadataDescriptorFactory
				.createJdbcDescriptor(
						res, 
						properties, 
						preferBasicCompositeIds);
	}
	
	private ReverseEngineeringStrategy createReverseEngineeringStrategy() {
		DefaultReverseEngineeringStrategy defaultStrategy = new DefaultReverseEngineeringStrategy();
		
		ReverseEngineeringStrategy strategy = defaultStrategy;
				
		if(revengFiles!=null) {
			OverrideRepository or = new OverrideRepository();
			
			String[] fileNames = revengFiles.list();
			for (int i = 0; i < fileNames.length; i++) {
				or.addFile(new File(fileNames[i]) );
			}
			strategy = or.getReverseEngineeringStrategy(defaultStrategy);			
		}
		
		if(reverseEngineeringStrategyClass!=null) {
			strategy = loadreverseEngineeringStrategy(reverseEngineeringStrategyClass, strategy);			
		}
		
		ReverseEngineeringSettings qqsettings = 
			new ReverseEngineeringSettings(strategy).setDefaultPackageName(packageName)
			.setDetectManyToMany( detectManyToMany )
			.setDetectOneToOne( detectOneToOne )
			.setDetectOptimisticLock( detectOptimisticLock );
	
		defaultStrategy.setSettings(qqsettings);
		strategy.setSettings(qqsettings);
		
		return strategy;
	}

    
    public void setPackageName(String pkgName) {
        packageName = pkgName;
    }
    
    public void setReverseStrategy(String fqn) {
        reverseEngineeringStrategyClass = fqn;
    }
    
	public void setRevEngFile(Path p) {
		revengFiles = p;		
	}
	
	public void setPreferBasicCompositeIds(boolean b) {
		preferBasicCompositeIds = b;
	}
	
	public void setDetectOneToOne(boolean b) {
		detectOneToOne = b;
	}
	
	public void setDetectManyToMany(boolean b) {
		detectManyToMany = b;
	}
	
	public void setDetectOptimisticLock(boolean b) {
		detectOptimisticLock = b;
	}

    private ReverseEngineeringStrategy loadreverseEngineeringStrategy(final String className, ReverseEngineeringStrategy delegate) 
    throws BuildException {
        try {
            Class<?> clazz = ReflectHelper.classForName(className);			
			Constructor<?> constructor = clazz.getConstructor(new Class[] { ReverseEngineeringStrategy.class });
            return (ReverseEngineeringStrategy) constructor.newInstance(new Object[] { delegate }); 
        } 
        catch (NoSuchMethodException e) {
			try {
				getProject().log("Could not find public " + className + "(ReverseEngineeringStrategy delegate) constructor on ReverseEngineeringStrategy. Trying no-arg version.",Project.MSG_VERBOSE);			
				Class<?> clazz = ReflectHelper.classForName(className);						
				ReverseEngineeringStrategy rev = (ReverseEngineeringStrategy) clazz.newInstance();
				getProject().log("Using non-delegating strategy, thus packagename and revengfile will be ignored.", Project.MSG_INFO);
				return rev;
			} 
			catch (Exception eq) {
				throw new BuildException("Could not create or find " + className + " with default no-arg constructor", eq);
			}
		} 
        catch (Exception e) {
			throw new BuildException("Could not create or find " + className + " with one argument delegate constructor", e);
		} 
    }

	private Map<String, Object> loadCfgXmlFile() {
		return new ConfigLoader(new BootstrapServiceRegistryBuilder().build())
				.loadConfigXmlFile(getConfigurationFile())
				.getConfigurationValues();
	}

	private Properties loadProperties() {
		Properties result = new Properties();
		if (getPropertyFile() != null) {
			result.putAll(loadPropertiesFile());
		}
		if (getConfigurationFile() != null) {
			result.putAll(loadCfgXmlFile());
		}
		return result;
	}
}
