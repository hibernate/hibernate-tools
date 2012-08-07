/*
 * Created on 02-Dec-2004
 *
 */
package org.hibernate.tool.hbm2x;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategyUtil;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.IndexedCollection;
import org.hibernate.mapping.MetaAttributable;
import org.hibernate.mapping.MetaAttribute;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Value;
import org.hibernate.tool.hbm2x.pojo.ComponentPOJOClass;
import org.hibernate.tool.hbm2x.pojo.EntityPOJOClass;
import org.hibernate.tool.hbm2x.pojo.ImportContext;
import org.hibernate.tool.hbm2x.pojo.NoopImportContext;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.hibernate.tool.hbm2x.visitor.JavaTypeFromValueVisitor;
import org.hibernate.type.PrimitiveType;
import org.hibernate.type.Type;
import org.hibernate.util.StringHelper;

/**
 * Helper methods for javacode generation.
 * <p/>
 * 
 *
 * @author max
 */
public class Cfg2JavaTool {

	private static final Log log = LogFactory.getLog( Cfg2JavaTool.class );	
	
	public Cfg2JavaTool() {

	}

	public POJOClass getPOJOClass(Component comp) {		
		return new ComponentPOJOClass(comp, this);
	}
	
	public POJOClass getPOJOClass(PersistentClass comp) {		
		return new EntityPOJOClass(comp, this);
	}
	
	public String unqualify(String name) {
		return StringHelper.unqualify( name );
	}

	/**
	 * Returns all meta items as one large string.
	 *
	 */
	public String getMetaAsString(MetaAttributable pc, String attribute) {
		MetaAttribute c = pc.getMetaAttribute( attribute );

		return MetaAttributeHelper.getMetaAsString( c );
	}

	public boolean hasMetaAttribute(MetaAttributable pc, String attribute) {
		return pc.getMetaAttribute( attribute ) != null;
	}

	public String getMetaAsString(MetaAttributable pc, String attribute, String seperator) {
		return MetaAttributeHelper.getMetaAsString( pc.getMetaAttribute( attribute ), seperator );
	}

	public boolean getMetaAsBool(MetaAttributable ma, String attribute) {
		return getMetaAsBool( ma, attribute, false );
	}

	public boolean getMetaAsBool(MetaAttributable pc, String attribute, boolean defaultValue) {
		return MetaAttributeHelper.getMetaAsBool( pc.getMetaAttribute( attribute ), defaultValue );
	}



	/**
	 * Convert string into something that can be rendered nicely into a javadoc
	 * comment.
	 * Prefix each line with a star ('*').
	 *
	 * @param string
	 */
	public String toJavaDoc(String string, int indent) {
		StringBuffer result = new StringBuffer();

		if ( string != null ) {
			String[] lines = StringUtils.split( string, "\n\r\f" );
			for ( int i = 0; i < lines.length ; i++ ) {
				String docline = " * " + lines[i];
				if ( i < lines.length - 1 ) docline += "\n";
				result.append( StringUtils.leftPad( docline, docline.length() + indent ) );
			}
		}

		return result.toString();
	}

	public String getClassModifiers(MetaAttributable pc) {
		String classModifiers = null;

		// Get scope (backwards compatibility)
		if ( pc.getMetaAttribute( "scope-class" ) != null ) {
			classModifiers = getMetaAsString( pc, "scope-class" ).trim();
		}

		// Get modifiers
		if ( pc.getMetaAttribute( "class-modifier" ) != null ) {
			classModifiers = getMetaAsString( pc, "class-modifier" ).trim();
		}
		return classModifiers == null ? "public" : classModifiers;
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

	/**
	 * Method that tries to get the typename for a property WITHOUT reflection.
	 */
	/*public String getJavaTypeName(Property p) {
		String javaTypeName = getJavaTypeName( p, false );
		return javaTypeName;
	}*/


	/**
	 * Returns the typename for a property, using generics if this is a Set type and useGenerics is set to true.
	 */
	public String getJavaTypeName(Property p, boolean useGenerics) {
		return getJavaTypeName(p, useGenerics, new NoopImportContext());
	}

	public String getJavaTypeName(Property p, boolean useGenerics, ImportContext importContext) {
		String overrideType = getMetaAsString( p, "property-type" );
		if ( !StringHelper.isEmpty( overrideType ) ) {
			String importType = importContext.importType(overrideType);			
			if ( useGenerics && importType.indexOf( "<" )<0) {
				if ( p.getValue() instanceof Collection ) {
					String decl = getGenericCollectionDeclaration( (Collection) p.getValue(), true, importContext );
					return importType + decl;
				}
			}
			return importType;
		}
		else {
			String rawType = getRawTypeName( p, useGenerics, true, importContext );
			if(rawType==null) {
					throw new IllegalStateException("getJavaTypeName *must* return a value");				
			}
			return importContext.importType(rawType);
		}
	}
	
	private static final Map PRIMITIVES = new HashMap();

	static {
		PRIMITIVES.put( "char", "Character" );

		PRIMITIVES.put( "byte", "Byte" );
		PRIMITIVES.put( "short", "Short" );
		PRIMITIVES.put( "int", "Integer" );
		PRIMITIVES.put( "long", "Long" );

		PRIMITIVES.put( "boolean", "Boolean" );

		PRIMITIVES.put( "float", "Float" );
		PRIMITIVES.put( "double", "Double" );

	}

	private static Set NONPRIMITIVETYPES = null;

	static public boolean isNonPrimitiveTypeName(String typeName) {
		if ( NONPRIMITIVETYPES == null ) {
			NONPRIMITIVETYPES = new HashSet();
			Field[] fields = Hibernate.class.getFields();
			for ( int i = 0; i < fields.length ; i++ ) {
				Field field = fields[i];
				if ( Modifier.isStatic( field.getModifiers() ) ) {
					if ( Type.class.isAssignableFrom( field.getType() ) ) {
						try {
							Type type = (Type) field.get( Hibernate.class );
							if ( !PRIMITIVES.containsKey( type.getName() ) ) {
								NONPRIMITIVETYPES.add( type.getName() );
							}
						}
						catch (IllegalArgumentException e) {
							throw new ExporterException( "Could not create list of basic hibernate types", e );
						}
						catch (IllegalAccessException e) {
							throw new ExporterException( "Could not create list of basic hibernate types", e );
						}
					}
				}
			}
		}

		return NONPRIMITIVETYPES.contains( typeName );
	}

	private String getRawTypeName(Property p, boolean useGenerics, boolean preferRawTypeNames, ImportContext importContext) {
		Value value = p.getValue();
		try {			
			
			if ( value instanceof Array ) { // array has a string rep.inside.
				Array a = (Array) value;				
					
				if ( a.isPrimitiveArray() ) {
					return toName( value.getType().getReturnedClass() );
				}
				else if (a.getElementClassName()!=null){
					return a.getElementClassName() + "[]";
				} else {
					return getJavaTypeName(a.getElement(), preferRawTypeNames) + "[]";
				}
			}

			if ( value instanceof Component ) { // same for component.				
				Component component = ( (Component) value );
				if(component.isDynamic()) return "java.util.Map";
				return component.getComponentClassName();
			}
			
			if ( useGenerics ) {
				if ( value instanceof Collection ) {
					String decl = getGenericCollectionDeclaration( (Collection) value, preferRawTypeNames, importContext );
					return getJavaTypeName(value, preferRawTypeNames) + decl;
				}
			}

			return getJavaTypeName( value, preferRawTypeNames );			
		}
		catch (Exception e) {
			//e.printStackTrace();
			String msg = "Could not resolve type without exception for " + p + " Value: " + value;
			if ( value != null && value.isSimpleValue() ) {
				String typename = ( (SimpleValue) value ).getTypeName();
				log.warn( msg + ". Falling back to typename: " + typename );
				return typename;
			}
			else {
				throw new ExporterException( msg, e );
			}
		}
	}

	public String getGenericCollectionDeclaration(Collection collection, boolean preferRawTypeNames, ImportContext importContext) {
		Value element = collection.getElement();
		String elementType = importContext.importType(getJavaTypeName(element, preferRawTypeNames));
		String genericDecl = elementType;
		if(collection.isIndexed()) {
			IndexedCollection idxCol = (IndexedCollection) collection;
			if(!idxCol.isList()) {
				Value idxElement = idxCol.getIndex();
				String indexType = importContext.importType(getJavaTypeName(idxElement, preferRawTypeNames));
				genericDecl = indexType + "," + elementType;
			}
		} 
		String decl = "<" + genericDecl + ">";
		return decl;
	}
	
	/**
	 * @param simpleValue
	 * @return
	 */
	public Properties getFilteredIdentifierGeneratorProperties(SimpleValue simpleValue) {
		Properties p = simpleValue.getIdentifierGeneratorProperties();
		return Cfg2HbmTool.getFilteredIdentifierGeneratorProperties(p);
	}

	private String getJavaTypeName(Value value, boolean preferRawTypeNames) {
		return (String) value.accept( new JavaTypeFromValueVisitor() );
	}

	public String asParameterList(Iterator fields, boolean useGenerics, ImportContext ic) {
		StringBuffer buf = new StringBuffer();
		while ( fields.hasNext() ) {
			Property field = (Property) fields.next();
			buf.append( getJavaTypeName( field, useGenerics, ic ) )
					.append( " " )
					.append( field.getName() );
			if ( fields.hasNext() ) {
				buf.append( ", " );
			}
		}
		return buf.toString();
	}

	/**
	 * @param fields iterator on Property elements.
	 * @return "name, number, ..." for a property list, usable for method calls.
	 *         <p/>
	 *         TODO: handle this in a template ?
	 */
	public String asArgumentList(Iterator fields) {
		StringBuffer buf = new StringBuffer();
		while ( fields.hasNext() ) {
			Property field = (Property) fields.next();
			buf.append( field.getName() );
			if ( fields.hasNext() ) {
				buf.append( ", " );
			}
		}
		return buf.toString();
	}

	/**
	 * @param clazz persistent class.
	 * @return "String name, int number, ..." for a property list, usable for method declarations.
	 *         <p/>
	 *         TODO: handle this in a template ?
	 */
	public String asNaturalIdParameterList(PersistentClass clazz) {
		Iterator fields = clazz.getRootClass().getPropertyIterator();
		StringBuffer buf = new StringBuffer();
		while ( fields.hasNext() ) {
			Property field = (Property) fields.next();
			if ( field.isNaturalIdentifier() ) {
				buf.append( getJavaTypeName( field, false ) ) 
						.append( " " )
						.append( field.getName() )
						.append( ", " );
			}
		}
		return buf.substring( 0, buf.length() - 2 );
	}

	public String asParameterList(List fields, boolean useGenerics, ImportContext ic) {
		return asParameterList( fields.iterator(), useGenerics, ic );
	}

	public String asArgumentList(List fields) {
		return asArgumentList( fields.iterator() );
	}
	
	public String asFinderArgumentList(Configuration cfg, Map parameterTypes, ImportContext ctx) {
		StringBuffer buf = new StringBuffer();
		Iterator iter = parameterTypes.entrySet().iterator();
		while ( iter.hasNext() ) {
			Map.Entry entry = (Map.Entry) iter.next();
			String typename = null;
			Type type = null;
			if(entry.getValue() instanceof String) {
				try {
					type = cfg.getTypeResolver().heuristicType((String) entry.getValue());
				} catch(Throwable t) {
					type = null;
					typename = (String) entry.getValue();
				}
			}
			
			if(type!=null) {
				Class typeClass;
				if ( type instanceof PrimitiveType ) {
					typeClass = ( (PrimitiveType) type ).getPrimitiveClass();
				}
				else {
					typeClass = type.getReturnedClass();
				}
				typename = typeClass.getName();
			}
			buf.append( ctx.importType( typename ))
					.append( " " )
					.append( entry.getKey() );
			if ( iter.hasNext() ) buf.append( ", " );
		}
		return buf.toString();
	}

	
	
	public boolean isPrimitive(String typeName) {
		return PRIMITIVES.containsKey(typeName);
	}
	
	public boolean isComponent(Property property) {
		return isComponent(property.getValue());
	}	
	
	public boolean isComponent(Value value) {
		return ( value instanceof Component );
	}	
	
	// TODO: should consult exporter/cfg2java tool for cached POJOEntities....or maybe not since they
	// have their own state...
	public Iterator getPOJOIterator(final Iterator persistentClasses) {
		return new Iterator() {
		
			public Object next() {
				return getPOJOClass((PersistentClass)persistentClasses.next());
			}
		
			public boolean hasNext() {
				return persistentClasses.hasNext();
			}
		
			public void remove() {
				persistentClasses.remove();
			}
		
		};
		
		
	}


	public String simplePluralize(String str) {
		return ReverseEngineeringStrategyUtil.simplePluralize(str);
	}

	public boolean isArray(String typeName) {
		return typeName!=null && typeName.endsWith("[]");
	}
}
