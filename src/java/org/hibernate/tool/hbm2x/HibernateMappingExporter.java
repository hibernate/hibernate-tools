/*
 * Created on 2004-12-03
 */
package org.hibernate.tool.hbm2x;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.hibernate.internal.util.StringHelper;

/**
 * @author david and max
 */
public class HibernateMappingExporter extends GenericExporter {
    
	protected HibernateMappingGlobalSettings globalSettings = new HibernateMappingGlobalSettings();
	
	protected void setupContext() {
		super.setupContext();
		getTemplateHelper().putInContext("hmgs", globalSettings);
	}
	
	public void setGlobalSettings(HibernateMappingGlobalSettings hgs) {
		this.globalSettings = hgs;
	}
	
	public void doStart() {
		exportGeneralSettings();		
		super.doStart();
	}

	private void exportGeneralSettings() {
		Cfg2HbmTool c2h = getCfg2HbmTool();
		Configuration cfg = getConfiguration();
		if(c2h.isImportData(cfg) && (c2h.isNamedQueries(cfg)) && (c2h.isNamedSQLQueries(cfg)) && (c2h.isFilterDefinitions(cfg))) {
			TemplateProducer producer = new TemplateProducer(getTemplateHelper(),getArtifactCollector());
			producer.produce(new HashMap<String, Object>(), "hbm/generalhbm.hbm.ftl", new File(getOutputDirectory(),"GeneralHbmSettings.hbm.xml"), getTemplateName(), "General Settings");
		}
	}
	
	public HibernateMappingExporter(Configuration cfg, File outputdir) {
    	super(cfg, outputdir);    	
    	init();
    }
    
	protected void init() {
		setTemplateName("hbm/hibernate-mapping.hbm.ftl");
    	setFilePattern("{package-name}/{class-name}.hbm.xml");    	
	}

	public HibernateMappingExporter() {
		init();		
	}

	protected String getClassNameForFile(POJOClass element) {
		return StringHelper.unqualify(((PersistentClass)element.getDecoratedObject()).getEntityName());
	}
	
	protected String getPackageNameForFile(POJOClass element) {
		return StringHelper.qualifier(((PersistentClass)element.getDecoratedObject()).getClassName());
	}
	
	
	protected void exportComponent(Map<String, Object> additionalContext, POJOClass element) {
		// we don't want component's exported.
	}
	
	public String getName() {
		return "hbm2hbmxml";
	}
}
