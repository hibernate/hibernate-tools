package org.hibernate.tool.hbm2x;

import java.io.File;
import java.util.Map;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.hibernate.tool.hmb2x.pogo.POGOHelper;

/**
 * 
 * @author Rand McNeely
 *
 */
public class POGOExporter extends GenericExporter {
	private Cfg2GroovyTool c2g;

    public POGOExporter() {
    	init();
    }

    
    public POGOExporter(Configuration cfg, File outputdir) {
    	super(cfg, outputdir);
    	init();
    }
    

    @Override
    public Cfg2JavaTool getCfg2JavaTool() {
        return c2g;
    }

	@Override
    protected void exportPOJO(Map additionalContext, POJOClass element) {
        TemplateProducer producer = new TemplateProducer(getTemplateHelper(),getArtifactCollector());
        additionalContext.put("pojo", element);
        additionalContext.put("clazz", element.getDecoratedObject());
        additionalContext.put("groovy", true);
        String filename = resolveFilename( element );
        if(filename.endsWith(".groovy") && filename.indexOf('$')>=0) {
            log.warn("Filename for " + getClassNameForFile(element) + " contains a $. Innerclass generation is not supported.");
        }        
        producer.produce(additionalContext, getTemplateName(), new File(getOutputDirectory(),filename), getTemplateName(), element.toString());
    }

    protected void init() {
        setTemplateName("pojo/Pojo.ftl");
        setFilePattern("{package-name}/{class-name}.groovy");
        c2g = new Cfg2GroovyTool(new POGOHelper());
    }
    
    protected void setupContext() {
        //TODO: this safe guard should be in the root templates instead for each variable they depend on.
        if(!getProperties().containsKey("ejb3")) {
            getProperties().put("ejb3", "false");
        }
        if(!getProperties().containsKey("jdk5")) {
            getProperties().put("jdk5", "false");
        }   
        super.setupContext();
    }


    public void setGenerateConstructors(boolean generateConstructors) {
        c2g.setGenerateConstructors(generateConstructors);
    }

}
