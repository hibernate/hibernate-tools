package org.hibernate.tool.hbm2x.visitor;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.Value;
import org.hibernate.tool.hbm2x.Cfg2HbmTool;
import org.hibernate.tool.hbm2x.Cfg2JavaTool;
import org.hibernate.type.CompositeCustomType;
import org.hibernate.type.CustomType;
import org.hibernate.type.Type;
import org.hibernate.type.TypeFactory;

public class JavaTypeFromValueVisitor extends DefaultValueVisitor {

	
	private boolean preferRawTypeNames = true;
	private Configuration cfg;

	public JavaTypeFromValueVisitor(Configuration cfg) {
		super( true );
		this.cfg = cfg;
	}
	
	// special handling for Map's to avoid initialization of comparators that depends on the keys/values which might not be generated yet.
	public Object accept(Map o) {
		if ( o.isSorted() ) {
			return "java.util.SortedMap";
		}
		return super.accept(o);
	}
	
	// special handling for Set's to avoid initialization of comparators that depends on the keys/values which might not be generated yet.
	public Object accept(Set o) {
		if ( o.isSorted() ) {
			return "java.util.SortedSet";
		}
		return super.accept(o);
	}

	public Object accept(Component value) {
		// composite-element breaks without it.
		return value.getComponentClassName();
	}
		
	public Object accept(OneToOne o) {
		return acceptToOne(o);
	}
	
	public Object accept(ManyToOne o) {
		return acceptToOne(o);
	}
	
	private Object acceptToOne(ToOne value) {
		String referencedEntityName = value.getReferencedEntityName();
		PersistentClass classMapping = cfg.getClassMapping(referencedEntityName);
		if (classMapping != null) {
			/* If it has a proxy then we should reference this instead, as the object we get given may be
			 * a proxy rather than the concrete type.
			 */
			if (classMapping.getProxyInterfaceName() != null) {
				return classMapping.getProxyInterfaceName();
			} else {
				return classMapping.getEntityName();
			}
		} else {
			return referencedEntityName;
		}
	}
	
	public Object accept(OneToMany value) {
		return value.getAssociatedClass().getClassName();
	}
	
	private String toName(Class c) {

		if ( c.isArray() ) {
			Class a = c.getComponentType();
			
			return a.getName() + "[]";
		}
		else {
			return c.getName();
		}
	}

	protected Object handle(Value o) {
		Value value = (Value) o;
		try {
			// have to attempt calling gettype to decide if its custom type.
			Type type = value.getType();
			if(type instanceof CustomType || type instanceof CompositeCustomType) {
				return toName( type.getReturnedClass() );
			}
		} catch(HibernateException he) {
			// ignore
		}

		if ( preferRawTypeNames && value.isSimpleValue() ) {
			// this logic make us use the raw typename if it is something else than an Hibernate type. So, if user wrote long we will use long...if he meant to have a Long then he should use the java.lang.Long version.
			String typename = ( (SimpleValue) value ).getTypeName();
			if ( !Cfg2JavaTool.isNonPrimitiveTypeName( typename ) ) {
				String val = ( (SimpleValue) value ).getTypeName();
				if(val!=null) return val; // val can be null when type is any 
			}
		} 
	
	return toName( value.getType().getReturnedClass() );

	}
	
	
}
