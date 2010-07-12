package org.hibernate.tool.hbm2x.doc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Settings;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.tool.hbm2x.Cfg2JavaTool;
import org.hibernate.tool.hbm2x.ConfigurationNavigator;
import org.hibernate.tool.hbm2x.pojo.ComponentPOJOClass;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.hibernate.type.Type;

/**
 * This helper class is used expose hibernate mapping information to the
 * templates.
 * 
 * @author Ricardo C. Moral
 * @author <a href="mailto:abhayani@jboss.org">Amit Bhayani</a>
 */
public final class DocHelper {

	/** used to sort pojoclass according to their declaration name */
    static final Comparator POJOCLASS_COMPARATOR = new Comparator() {
		public int compare(Object o1, Object o2) {
			POJOClass that = (POJOClass) o1;
			POJOClass what = (POJOClass) o2;
				
			return that.getDeclarationName().compareTo(what.getDeclarationName());				
		}
	};
	
	/**
	 * Used to sort properties according to their name.
	 */
	private static final Comparator PROPERTY_COMPARATOR = new Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			Property property1 = (Property) o1;
			Property property2 = (Property) o2;
			
			return property1.getName().compareTo(property2.getName());
		}
	};

	/**
     * Name to use if the schema is not specified.
     */
    public static final String DEFAULT_NO_SCHEMA_NAME = "default";
    
    /**
     * Name to use if there are no packages specified for any class
     */
    public static final String DEFAULT_NO_PACKAGE = "All Entities";

    /**
     * Hibernate Configuration.
     */
    private Configuration cfg;

    /**
     * Map with Tables keyed by Schema FQN. The keys are Strings and the values
     * are Lists of Tables
     */
    private Map tablesBySchema = new HashMap();
    
    /**
     * Map with classes keyed by package name. PackageName is String key and values 
     * are List of POJOClass
     */
    private Map classesByPackage = new HashMap();
    
    /**
     * Lits of all POJOClass
     */
    private List classes = new ArrayList();
    
    /**
     * Map where the keys are column names (tableFQN.column) and the values are
     * lists with the Value instances where those columns referenced.
     */
    private Map valuesByColumn = new HashMap();

    /**
     * Holds intances of Property keyed by Value objects.
     */
    private Map propsByValue = new HashMap();

    /**
     * List with all the tables.
     */
    private List tables = new ArrayList();

    /**
     * Map that holds the Schema FQN for each Table. The keys are Table
     * instances and the values are Strings with the Schema FQN for that table.
     */
    private Map tableSchemaNames = new HashMap();

    /**
     * The Dialect.
     */
    private Dialect dialect;
    
    /**
     * Constructor.
     * 
     * @param cfg Hibernate configuration.
     */
    public DocHelper(Configuration cfg, Cfg2JavaTool cfg2JavaTool) {

        super();

        if (cfg == null) {
            throw new IllegalArgumentException(
                    "Hibernate Configuration cannot be null");
        }

        this.cfg = cfg;

        dialect = cfg.buildSettings().getDialect(); // TODO: get it from somewhere "cached".
        
        Settings settings = cfg.buildSettings();

        String defaultCatalog = settings.getDefaultCatalogName();

        String defaultSchema = settings.getDefaultSchemaName();      
        

        if (defaultSchema == null) {
            defaultSchema = DEFAULT_NO_SCHEMA_NAME;
        }

        Iterator tablesIter = cfg.getTableMappings();
        
        while (tablesIter.hasNext() ) {
        	Table table = (Table) tablesIter.next();
        	
        	if(!table.isPhysicalTable()) {
        		continue; 
        	}
            tables.add(table);

            StringBuffer sb = new StringBuffer();

            String catalog = table.getCatalog();
            if (catalog == null) {
                catalog = defaultCatalog;
            }
            if (catalog != null) {
                sb.append(catalog + ".");
            }

            String schema = table.getSchema();
            if (schema == null) {
                schema = defaultSchema;
            }

            sb.append(schema);

            String qualSchemaName = sb.toString();

            tableSchemaNames.put(table, qualSchemaName);

            List tableList = (List) tablesBySchema.get(qualSchemaName);
            if (tableList == null) {
                tableList = new ArrayList();
                tablesBySchema.put(qualSchemaName, tableList);
            }
            tableList.add(table);

            Iterator columns = table.getColumnIterator();
            while (columns.hasNext() ) {
                Column column = (Column) columns.next();
                String columnFQN = getQualifiedColumnName(table, column);
                List values = (List) valuesByColumn.get(columnFQN);
                if (values == null) {
                    values = new ArrayList();
                    valuesByColumn.put(columnFQN, values);
                }
                values.add(column.getValue() );
            }
        }
        
        Map components = new HashMap();

        Iterator classesItr = cfg.getClassMappings();
        while (classesItr.hasNext() ) {
            PersistentClass clazz = (PersistentClass) classesItr.next();
            
            POJOClass pojoClazz = cfg2JavaTool.getPOJOClass(clazz);
            ConfigurationNavigator.collectComponents(components, pojoClazz);						
            
            this.processClass(pojoClazz);
            
            Iterator properties = clazz.getPropertyIterator();
            while (properties.hasNext() ) {
                Property property = (Property) properties.next();
                Value value = property.getValue();                
                List props = (List) propsByValue.get(value);
                if (props == null) {
                    props = new ArrayList();
                    propsByValue.put(value, props);
                }
                props.add(property);
            }
        }
        
        Iterator iterator = components.values().iterator();
		while ( iterator.hasNext() ) {					
			Component component = (Component) iterator.next();
			ComponentPOJOClass element = new ComponentPOJOClass(component,cfg2JavaTool);
			this.processClass(element);
		}
    }
    
    
    /**
     * Populate classes List and classesByPackage Map
     * @param pojoClazz
     */
    private void processClass(POJOClass pojoClazz){       
        
        classes.add(pojoClazz);        
        String packageName = pojoClazz.getPackageName();
        
        if("".equals(packageName)){
        	packageName = DEFAULT_NO_PACKAGE;
        }       
        
        List classList = (List)classesByPackage.get(packageName);
        if(classList == null){
        	classList = new ArrayList();
        	classesByPackage.put(packageName, classList);
        }
        classList.add(pojoClazz);

}    

    /**
     * Returns the Hibernate Configuration.
     * 
     * @return the Hibernate Configuration.
     */
    public Configuration getCfg() {

        return cfg;
    }

    /**
     * Return a Map with the tables keyed by Schema. The keys are the schema
     * names and the values are Lists of tables.
     * 
     * @return a Map with the tables keyed by Schema Name.
     */
    public Map getTablesBySchema() {

        return tablesBySchema;
    }
    
    /**
     * return a Map which has List of POJOClass as value keyed by package name as String. 
     * @return
     */
    public Map getClassesByPackage(){
    	return classesByPackage;
    }
 

    /**
     * Returns a list with all the schemas.
     * 
     * @return a list with all the schemas.
     */
    public List getSchemas() {

        List schemas = new ArrayList(tablesBySchema.keySet() );
        Collections.sort(schemas);

        return schemas;
    }
    
    
    /**
     * Return a sorted List of packages
     * @return
     */
    public List getPackages(){
    	List packages = new ArrayList(classesByPackage.keySet());
    	Collections.sort(packages);
    	return packages;
    }
    
    

    /**
     * Return the list of tables for a particular schema.
     * 
     * @param schema the name of the schema.
     * 
     * @return a list with all the tables.
     */
    public List getTables(String schema) {

        List list = (List) tablesBySchema.get(schema);
        return list;
    }
    
    /**
     * return a sorted List of POJOClass corresponding to packageName passed
     * @param packageName packageName other than DEFAULT_NO_PACKAGE
     * @return a sorted List of POJOClass
     */
    public List getClasses(String packageName){
    	List clazzes = (List)classesByPackage.get(packageName);
    	List orderedClasses = new ArrayList(clazzes);
    	Collections.sort(orderedClasses, POJOCLASS_COMPARATOR);
    	return orderedClasses;
    }

    /**
     * Return all the tables.
     * 
     * @return all the tables.
     */
    public List getTables() {

        return tables;
    }
    
    /**
     * Return a sorted List of all POJOClass
     * @return
     */
    public List getClasses(){
    	List orderedClasses = new ArrayList(classes);
    	Collections.sort(orderedClasses, POJOCLASS_COMPARATOR);
    	return orderedClasses;
    }
    
    /**
     * Returns the qualified schema name for a table. The qualified schema name
     * will include the catalog name if one is specified.
     * 
     * @param table the table.
     * 
     * @return the qualified schema name for the table.
     */
    public String getQualifiedSchemaName(Table table) {

        return (String) tableSchemaNames.get(table);
    }

    /**
     * Returns the qualified name of a table.
     * 
     * @param table the table.
     * 
     * @return the qualified name of the table.
     */
    public String getQualifiedTableName(Table table) {

        String qualifiedSchemaName = getQualifiedSchemaName(table);

        return qualifiedSchemaName + '.' + table.getName();
    }
    
    public String getPropertyType(Property p){
    	Value v = p.getValue();
    	Type t;
    	String propertyString = "N/D";    	
    	try{
	    	t = v.getType();	    	
	    	propertyString = t.getReturnedClass().getName();
	    	
    	}
    	catch(Exception ex){
    		//TODO we should try to get the value from value here
    		//Eat Exception??
    	}
    	
    	return propertyString;
    }

    /**
     * Returns the qualified name of a column.
     * 
     * @param table the table.
     * @param column the column
     * 
     * @return the FQN of the column.
     */
    public String getQualifiedColumnName(Table table, Column column) {

        String qualifiedTableName = getQualifiedTableName(table);

        return qualifiedTableName + '.' + column.getName();
    }

    /**
     * Get the SQL type name for a column.
     * 
     * @param column the column.
     * 
     * @return a String with the SQL type name.
     */
    public String getSQLTypeName(Column column) {

        try {
            return column.getSqlType(dialect, null);
        } 
        catch (HibernateException ex) {

            // TODO: Fix this when we find a way to get the type or
            // the mapping.

            return "N/D";
        }
    }

    /**
     * Returns the values that use the specified column.
     * 
     * @param table the table.
     * @param column the column.
     * 
     * @return a list with the values.
     */
    public List getValues(Table table, Column column) {

        String columnFQN = getQualifiedColumnName(table, column);
        List values = (List) valuesByColumn.get(columnFQN);
        if (values != null) {

            return values;
        } 
        else {

            return new ArrayList();
        }
    }

    /**
     * Returns the properties that map to a column.
     * 
     * @param table the table.
     * @param column the column.
     * 
     * @return a list of properties.
     */
    public List getProperties(Table table, Column column) {

        List result = new ArrayList();
        Iterator values = getValues(table, column).iterator();
        while (values.hasNext() ) {
            Value value = (Value) values.next();
            List props = (List) propsByValue.get(value);
            if (props != null) {
                result.addAll(props);
            }
        }

        return result;
    }
    
    /**
     * Method used in class.vm template to get the ComponentPOJO class corresponding to Property
     * if its of Type Component. 
     * @param property Get ComponentPOJO corresponding to this Property
     * @return POJOClass for Property
     */
    //TODO We haven't taken into account Array?
    public POJOClass getComponentPOJO(Property property){
    	if (property.getValue() instanceof Component) {
    		Component comp = (Component) property.getValue();
    		ComponentPOJOClass componentPOJOClass = new ComponentPOJOClass(comp, new Cfg2JavaTool());    		
    		return componentPOJOClass;
    	}
      	else{
    		return null;
    	}
    }
    
    public List getInheritanceHierarchy(POJOClass pc) {
    	if(pc.isSubclass()) {
    		List superClasses = new ArrayList();

    		POJOClass superClass = pc.getSuperClass();
    		while (superClass != null)
    		{
    			superClasses.add(superClass);
    			superClass = superClass.getSuperClass();
    		}

    		return superClasses; 
    	} else {
    		return Collections.EMPTY_LIST;
    	}
    }
    
    public List getOrderedProperties(POJOClass pojoClass)
    {
    	List orderedProperties = getAllProperties(pojoClass);

    	Collections.sort(orderedProperties, PROPERTY_COMPARATOR);
    	
    	return orderedProperties;
    }
    
    public List getSimpleProperties(POJOClass pojoClass)
    {
    	List properties = getAllProperties(pojoClass);
    	
    	if (pojoClass.hasIdentifierProperty())
    		properties.remove(pojoClass.getIdentifierProperty());
    	
    	// TODO: do we need to also remove component id properties?
    	
    	if (pojoClass.hasVersionProperty())
    		properties.remove(pojoClass.getVersionProperty());
    	
    	return properties;
    }
    
    public List getOrderedSimpleProperties(POJOClass pojoClass)
    {
    	List orderedProperties = getSimpleProperties(pojoClass);

    	Collections.sort(orderedProperties, PROPERTY_COMPARATOR);
    	
    	return orderedProperties;
    }
    
    private List getAllProperties(POJOClass pojoClass)
    {
    	List properties = new ArrayList();
    	
    	for (Iterator iterator = pojoClass.getAllPropertiesIterator(); iterator.hasNext(); )
    		properties.add(iterator.next());
    	
    	return properties;
    }
}
