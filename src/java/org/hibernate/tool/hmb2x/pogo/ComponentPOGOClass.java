package org.hibernate.tool.hmb2x.pogo;

import java.util.Iterator;

import org.hibernate.mapping.Component;
import org.hibernate.mapping.Property;
import org.hibernate.tool.hbm2x.Cfg2GroovyTool;
import org.hibernate.tool.hbm2x.Cfg2JavaTool;
import org.hibernate.tool.hbm2x.pojo.ComponentPOJOClass;

/**
 * @author Rand McNeely
 *
 */
public class ComponentPOGOClass extends ComponentPOJOClass {
    

    private Cfg2GroovyTool cfg;

    public ComponentPOGOClass(Component component, Cfg2GroovyTool cfg) {
        super(component, cfg);   
        this.cfg = cfg;
    }
    
    @Override
    public String getFieldModifiers(Property property) {
        // Auto-generated method stub
        return cfg.getPOGOHelper().convertFieldModifer(super.getFieldModifiers(property));
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    protected String generateEquals(String thisName, String otherName, Iterator allPropertiesIterator,
            boolean useGenerics) {
        return cfg.getPOGOHelper().joinStatements(super.generateEquals(thisName, otherName, allPropertiesIterator, useGenerics));
    }
    
    @Override
    public String generateAnnColumnAnnotation(Property property) {
        return cfg.getPOGOHelper().transformStaticArrayInitializer(super.generateAnnColumnAnnotation(property));
    }
    
    @Override
    public boolean needsMinimalConstructor() {
        return cfg.isGenerateConstructors() && super.needsMinimalConstructor();
    }

    @Override
    public boolean needsFullConstructor() {
        return cfg.isGenerateConstructors() && super.needsFullConstructor();
    }

}
