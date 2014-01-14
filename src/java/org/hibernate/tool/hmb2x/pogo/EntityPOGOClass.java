package org.hibernate.tool.hmb2x.pogo;

import java.util.Iterator;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.hbm2x.Cfg2GroovyTool;
import org.hibernate.tool.hbm2x.pojo.EntityPOJOClass;

/**
 * @author Rand McNeely
 * 
 */
public class EntityPOGOClass extends EntityPOJOClass {

    private Cfg2GroovyTool cfg;

	public EntityPOGOClass(PersistentClass clazz, Cfg2GroovyTool cfg) {
		super(clazz, cfg);
		this.cfg = cfg;
	}

	public String generateAnnColumnAnnotation(Property property) {
		return cfg.getPOGOHelper().transformStaticArrayInitializer(super
				.generateAnnColumnAnnotation(property));
	}

	public String generateAnnIdGenerator() {
		return cfg.getPOGOHelper().transformStaticArrayInitializer(super
				.generateAnnIdGenerator());
	}

	public String generateAnnTableUniqueConstraint() {
		return cfg.getPOGOHelper().transformStaticArrayInitializer(super
				.generateAnnTableUniqueConstraint());
	}

	public String generateBasicAnnotation(Property property) {
		return cfg.getPOGOHelper().transformStaticArrayInitializer(super
				.generateBasicAnnotation(property));
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected String generateEquals(String thisName, String otherName,
			Iterator allPropertiesIterator, boolean useGenerics) {
		return cfg.getPOGOHelper().joinStatements(super.generateEquals(thisName, otherName,
				allPropertiesIterator, useGenerics));
	}
	
	@Override
    public String getFieldModifiers(Property property) {
        return cfg.getPOGOHelper().convertFieldModifer(super.getFieldModifiers(property));
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
