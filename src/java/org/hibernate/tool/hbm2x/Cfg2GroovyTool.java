package org.hibernate.tool.hbm2x;

import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.hibernate.tool.hmb2x.pogo.ComponentPOGOClass;
import org.hibernate.tool.hmb2x.pogo.EntityPOGOClass;
import org.hibernate.tool.hmb2x.pogo.POGOHelper;

/**
 * 
 * @author Rand McNeely
 *
 */
public class Cfg2GroovyTool extends Cfg2JavaTool {
    private POGOHelper helper;
    private boolean generateConstructors;
    private boolean factoryCalled;

    public Cfg2GroovyTool(POGOHelper helper) {
        this.helper = helper;
    }
    @Override
    public POJOClass getPOJOClass(Component comp) {
        factoryCalled = true;
        return new ComponentPOGOClass(comp, this);
    }

    @Override
    public POJOClass getPOJOClass(PersistentClass comp) {
        factoryCalled = true;
        return new EntityPOGOClass(comp, this);
    }
    
    public POGOHelper getPOGOHelper() {
        // Auto-generated method stub
        return helper;
    }
    public void setGenerateConstructors(boolean generateConstructors) {
        if (factoryCalled) {
            throw new IllegalStateException("POGOClass factories have already been called.");
        }
        this.generateConstructors = generateConstructors;
    }
    public boolean isGenerateConstructors() {
        return generateConstructors;
    }

}
