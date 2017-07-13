package org.hibernate.tool.hbm2x.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.hibernate.boot.Metadata;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.id.enhanced.TableGenerator;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.JoinedIterator;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.Formula;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.mapping.Value;
import org.hibernate.tool.hbm2x.Cfg2JavaTool;
import org.hibernate.type.ForeignKeyDirection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityPOJOClass extends BasicPOJOClass {

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    private PersistentClass clazz;

	public EntityPOJOClass(PersistentClass clazz, Cfg2JavaTool cfg) {
		super(clazz, cfg);
		this.clazz = clazz;
		init();
	}

	protected String getMappedClassName() {
		return clazz.getClassName();
	}

	/**
	 * @return whatever the class (or interface) extends (null if it does not extend anything)
	 */
	public String getExtends() {
		String extendz = "";

		if ( isInterface() ) {
			if ( clazz.getSuperclass() != null ) {
				extendz = clazz.getSuperclass().getClassName();
			}
			if ( clazz.getMetaAttribute( EXTENDS ) != null ) {
				if ( !"".equals( extendz ) ) {
					extendz += ",";
				}
				extendz += getMetaAsString( EXTENDS, "," );
			}
		}
		else if ( clazz.getSuperclass() != null ) {
			if ( c2j.getPOJOClass(clazz.getSuperclass()).isInterface() ) {
				// class cannot extend it's superclass because the superclass is marked as an interface
			}
			else {
				extendz = clazz.getSuperclass().getClassName();
			}
		}
		else if ( clazz.getMetaAttribute( EXTENDS ) != null ) {
			extendz = getMetaAsString( EXTENDS, "," );
		}

		return "".equals( extendz ) ? null : extendz;
	}


	@SuppressWarnings("unchecked")
	public String getImplements() {
		List<String> interfaces = new ArrayList<String>();

		//			implement proxy, but NOT if the proxy is the class it self!
		if ( clazz.getProxyInterfaceName() != null && ( !clazz.getProxyInterfaceName().equals( clazz.getClassName() ) ) ) {
			interfaces.add( clazz.getProxyInterfaceName() );
		}

		if ( !isInterface() ) {
			if ( clazz.getSuperclass() != null && c2j.getPOJOClass(clazz.getSuperclass()).isInterface() ) {
				interfaces.add( clazz.getSuperclass().getClassName() );
			}
			if ( clazz.getMetaAttribute( IMPLEMENTS ) != null ) {
				interfaces.addAll( clazz.getMetaAttribute( IMPLEMENTS ).getValues() );
			}
			interfaces.add( Serializable.class.getName() ); // TODO: is this "nice" ? shouldn't it be a user choice ?
		}
		else {
			// interfaces can't implement suff
		}


		if ( interfaces.size() > 0 ) {
			StringBuffer sbuf = new StringBuffer();
			for ( Iterator<String> iter = interfaces.iterator(); iter.hasNext() ; ) {
				//sbuf.append(JavaTool.shortenType(iter.next().toString(), pc.getImports() ) );
				sbuf.append( iter.next() );
				if ( iter.hasNext() ) sbuf.append( "," );
			}
			return sbuf.toString();
		}
		else {
			return null;
		}
	}

	public Iterator<Property> getAllPropertiesIterator() {
		return getAllPropertiesIterator(clazz);
	}


	@SuppressWarnings("unchecked")
	public Iterator<Property> getAllPropertiesIterator(PersistentClass pc) {
		List<Property> properties = new ArrayList<Property>();
		List<Iterator<Property>> iterators = new ArrayList<Iterator<Property>>();
		if ( pc.getSuperclass() == null ) {
			// only include identifier for the root class.
			if ( pc.hasIdentifierProperty() ) {
				properties.add( pc.getIdentifierProperty() );
			}
			else if ( pc.hasEmbeddedIdentifier() ) {
				Component embeddedComponent = (Component) pc.getIdentifier();
				iterators.add( embeddedComponent.getPropertyIterator() );
			}
			/*if(clazz.isVersioned() ) { // version is already in property set
				properties.add(clazz.getVersion() );
			}*/
		}


		//		iterators.add( pc.getPropertyIterator() );
		// Need to skip <properties> element which are defined via "embedded" components
		// Best if we could return an intelligent iterator, but for now we just iterate explicitly.
		Iterator<Property> pit = pc.getPropertyIterator();
		while(pit.hasNext())
		{
			Property element = (Property) pit.next();
			if ( element.getValue() instanceof Component
					&& element.getPropertyAccessorName().equals( "embedded" )) {
				Component component = (Component) element.getValue();
				// need to "explode" property to get proper sequence in java code.
				Iterator<Property> embeddedProperty = component.getPropertyIterator();
				while(embeddedProperty.hasNext()) {
					properties.add(embeddedProperty.next());
				}
			} else {
				properties.add(element);
			}
		}

		iterators.add( properties.iterator() );

		Iterator<Property>[] it = (Iterator<Property>[]) iterators.toArray( new Iterator[iterators.size()] );
		return new SkipBackRefPropertyIterator( new JoinedIterator<Property>( it ) );
	}

	public boolean isComponent() {
		return false;
	}


	public boolean hasIdentifierProperty() {
		return clazz.hasIdentifierProperty() && clazz instanceof RootClass;
	}

	public Property getIdentifierProperty() {
		return clazz.getIdentifierProperty();
	}

	public String generateAnnTableUniqueConstraint() {
		if ( ! ( clazz instanceof Subclass ) ) {
			Table table = clazz.getTable();
			return generateAnnTableUniqueConstraint( table );
		}
		return "";
	}

	protected String generateAnnTableUniqueConstraint(Table table) {
		Iterator<UniqueKey> uniqueKeys = table.getUniqueKeyIterator();
		List<String> cons = new ArrayList<String>();
		while ( uniqueKeys.hasNext() ) {
			UniqueKey key = (UniqueKey) uniqueKeys.next();
			if (table.hasPrimaryKey() && table.getPrimaryKey().getColumns().equals(key.getColumns())) {
				continue;
			}
			AnnotationBuilder constraint = AnnotationBuilder.createAnnotation( importType("javax.persistence.UniqueConstraint") );
			constraint.addQuotedAttributes( "columnNames", new IteratorTransformer<Column>(key.getColumnIterator()) {
				public String transform(Column column) {
					return column.getName();
				}
			});
			cons.add( constraint.getResult() );
		}

		AnnotationBuilder builder = AnnotationBuilder.createAnnotation( "dummyAnnotation" );
		builder.addAttributes( "dummyAttribute", cons.iterator() );
		String attributeAsString = builder.getAttributeAsString( "dummyAttribute" );
		return attributeAsString==null?"":attributeAsString;
	}


	public String generateAnnIdGenerator() {
		KeyValue identifier = clazz.getIdentifier();
		String strategy = null;
		Properties properties = null;
		StringBuffer wholeString = new StringBuffer( "    " );
		if ( identifier instanceof Component ) {

			wholeString.append( AnnotationBuilder.createAnnotation( importType("javax.persistence.EmbeddedId") ).getResult());
		}
		else if ( identifier instanceof SimpleValue ) {
			SimpleValue simpleValue = (SimpleValue) identifier;
			strategy = simpleValue.getIdentifierGeneratorStrategy();
			properties = c2j.getFilteredIdentifierGeneratorProperties(simpleValue);
			StringBuffer idResult = new StringBuffer();
			AnnotationBuilder builder = AnnotationBuilder.createAnnotation( importType("javax.persistence.Id") );
			idResult.append(builder.getResult());
			idResult.append(" ");

			boolean isGenericGenerator = false; //TODO: how to handle generic now??
			if ( !"assigned".equals( strategy ) ) {

				if ( !"native".equals( strategy ) ) {
					if ( "identity".equals( strategy ) ) {
						builder.resetAnnotation( importType("javax.persistence.GeneratedValue") );
						builder.addAttribute( "strategy", staticImport("javax.persistence.GenerationType", "IDENTITY" ) );
						idResult.append(builder.getResult());
					}
					else if ( "sequence".equals( strategy ) ) {
						builder.resetAnnotation( importType("javax.persistence.GeneratedValue") )
							.addAttribute( "strategy", staticImport("javax.persistence.GenerationType", "SEQUENCE" ) )
						    .addQuotedAttribute( "generator", clazz.getClassName()+"IdGenerator" );
						idResult.append(builder.getResult());
						idResult.append(" ");

                        // TODO: manage attributes schema + catalog added to @SequenceGenerator since JPA 2.0
						builder.resetAnnotation( importType("javax.persistence.SequenceGenerator") )
							.addQuotedAttribute( "name", clazz.getClassName()+"IdGenerator" ) 
							.addQuotedAttribute( "sequenceName", properties.getProperty(  org.hibernate.id.enhanced.SequenceStyleGenerator.SEQUENCE_PARAM, null ) );
							//	TODO HA does not support initialValue and allocationSize
						idResult.append(builder.getResult());
					}
					else if ( TableGenerator.class.getName().equals( strategy ) ) {
						builder.resetAnnotation( importType("javax.persistence.GeneratedValue") )
						.addAttribute( "strategy", staticImport("javax.persistence.GenerationType", "TABLE" ) )
					    .addQuotedAttribute( "generator", "generator" );
						idResult.append(builder.getResult());
						buildAnnTableGenerator( wholeString, properties );
					}
					else {
						isGenericGenerator = true;
						builder.resetAnnotation( importType("javax.persistence.GeneratedValue") );
						builder.addQuotedAttribute( "generator", "generator" );
						idResult.append(builder.getResult());
					}
				} else {
					builder.resetAnnotation( importType("javax.persistence.GeneratedValue") );
					idResult.append(builder.getResult());
				}
			}
			if ( isGenericGenerator ) {
				builder.resetAnnotation( importType("org.hibernate.annotations.GenericGenerator") )
					.addQuotedAttribute( "name", "generator" )
					.addQuotedAttribute( "strategy", strategy);

				List<AnnotationBuilder> params = new ArrayList<AnnotationBuilder>();
				//wholeString.append( "parameters = {  " );
				if ( properties != null ) {
					Enumeration<?> propNames = properties.propertyNames();
					while ( propNames.hasMoreElements() ) {

						String propertyName = (String) propNames.nextElement();
						AnnotationBuilder parameter = AnnotationBuilder.createAnnotation( importType("org.hibernate.annotations.Parameter") )
									.addQuotedAttribute( "name", propertyName )
									.addQuotedAttribute( "value", properties.getProperty( propertyName ) );
						params.add( parameter );
					}
				}
				builder.addAttributes( "parameters", params.iterator() );
				wholeString.append(builder.getResult());
			}
			wholeString.append( idResult );
		}
		return wholeString.toString();
	}

	private void buildAnnTableGenerator(StringBuffer wholeString, Properties properties) {

		AnnotationBuilder builder = AnnotationBuilder.createAnnotation( importType("javax.persistence.TableGenerator") );
		builder.addQuotedAttribute( "name", "generator" );
		builder.addQuotedAttribute( "table", properties.getProperty( "generatorTableName", "hibernate_sequences" ) );
		if ( ! isPropertyDefault( PersistentIdentifierGenerator.CATALOG, properties ) ) {
			builder.addQuotedAttribute( "catalog", properties.getProperty( PersistentIdentifierGenerator.CATALOG, "") );
		}
		if ( ! isPropertyDefault( PersistentIdentifierGenerator.SCHEMA, properties ) ) {
			builder.addQuotedAttribute( "schema", properties.getProperty( PersistentIdentifierGenerator.SCHEMA, "") );
		}
		if (! isPropertyDefault( TableGenerator.SEGMENT_VALUE_PARAM, properties ) ) {
			builder.addQuotedAttribute( "pkColumnValue", properties.getProperty( TableGenerator.SEGMENT_VALUE_PARAM, "") );
		}
		if ( ! isPropertyDefault( TableGenerator.INCREMENT_PARAM, properties, "50" ) ) {
			builder.addAttribute( "allocationSize", properties.getProperty( TableGenerator.INCREMENT_PARAM, "50" ) );
		}
		if (! isPropertyDefault( TableGenerator.SEGMENT_COLUMN_PARAM, properties ) ) {
			builder.addQuotedAttribute( "pkColumnName", properties.getProperty( TableGenerator.SEGMENT_COLUMN_PARAM, "") );
		}
		if (! isPropertyDefault( TableGenerator.VALUE_COLUMN_PARAM, properties ) ) {
			builder.addQuotedAttribute( "valueColumnName", properties.getProperty( TableGenerator.VALUE_COLUMN_PARAM, "") );
		}
		wholeString.append( builder.getResult() + "\n    " );
	}

	private boolean isPropertyDefault(String property, Properties properties) {
		return StringHelper.isEmpty( properties.getProperty( property ) );
	}

	private boolean isPropertyDefault(String property, Properties properties, String defaultValue) {
		String propertyValue = properties.getProperty( property );
		return propertyValue != null && propertyValue.equals( defaultValue );
	}

	@SuppressWarnings("unchecked")
	public String generateJoinColumnsAnnotation(Property property, Metadata md) {
		boolean insertable = property.isInsertable();
		boolean updatable = property.isUpdateable();
		Value value = property.getValue();
		int span;
		Iterator<Selectable> columnIterator;
		Iterator<Selectable> referencedColumnsIterator = null;
		if (value != null && value instanceof Collection) {
			Collection collection = (Collection) value;
			span = collection.getKey().getColumnSpan();
			columnIterator = collection.getKey().getColumnIterator();
		}
		else {
			span = property.getColumnSpan();
			columnIterator = property.getColumnIterator();
		}

		if(property.getValue() instanceof ToOne) {
			String referencedEntityName = ((ToOne)property.getValue()).getReferencedEntityName();
			PersistentClass target = md.getEntityBinding(referencedEntityName);
			if(target!=null) {
				referencedColumnsIterator = target.getKey().getColumnIterator();
			}
		}

		StringBuffer annotations = new StringBuffer( "    " );
		if ( span == 1 ) {
				Selectable selectable = columnIterator.next();
				buildJoinColumnAnnotation( selectable, null, annotations, insertable, updatable );
		}
		else {
			Iterator<Selectable> columns = columnIterator;
			annotations.append("@").append( importType("javax.persistence.JoinColumns") ).append("( { " );
			buildArrayOfJoinColumnAnnotation( columns, referencedColumnsIterator, annotations, insertable, updatable );
			annotations.append( " } )" );
		}
		return annotations.toString();
	}

	private void buildArrayOfJoinColumnAnnotation(
			Iterator<Selectable> columns, Iterator<Selectable> referencedColumnsIterator, StringBuffer annotations, boolean insertable,
			boolean updatable
	) {
		while ( columns.hasNext() ) {
			Selectable selectable = columns.next();
            Selectable referencedColumn = null;
            if(referencedColumnsIterator!=null) {
            	referencedColumn = referencedColumnsIterator.next();
            }

			if ( selectable.isFormula() ) {
				//TODO formula in multicolumns not supported by annotations
				//annotations.append("/* TODO formula in multicolumns not supported by annotations */");
			}
			else {
				annotations.append( "\n        " );
				buildJoinColumnAnnotation( selectable, referencedColumn, annotations, insertable, updatable );
				annotations.append( ", " );
			}
		}
		annotations.setLength( annotations.length() - 2 );
	}

	private void buildJoinColumnAnnotation(
			Selectable selectable, Selectable referencedColumn, StringBuffer annotations, boolean insertable, boolean updatable
	) {
		if ( selectable.isFormula() ) {
			//TODO not supported by HA
		}
		else {
			Column column = (Column) selectable;
			annotations.append("@").append( importType("javax.persistence.JoinColumn") )
					.append("(name=\"" ).append( column.getName() ).append( "\"" );
					//TODO handle referenced column name, this is a hard one
			        if(referencedColumn!=null) {
			         annotations.append(", referencedColumnName=\"" ).append( referencedColumn.getText() ).append( "\"" );
			        }

					appendCommonColumnInfo(annotations, column, insertable, updatable);
			//TODO support secondary table
			annotations.append( ")" );
		}
	}

	public String[] getCascadeTypes(Property property) {
		StringTokenizer st =  new StringTokenizer( property.getCascade(), ", ", false );
		List<String> types = new ArrayList<String>();
		while ( st.hasMoreElements() ) {
			String element = ( (String) st.nextElement() ).toLowerCase();
			if ( "persist".equals( element ) ) {
				types.add(importType( "javax.persistence.CascadeType" ) + ".PERSIST");
			}
			else if ( "merge".equals( element ) ) {
				types.add(importType( "javax.persistence.CascadeType") + ".MERGE");
			}
			else if ( "delete".equals( element ) ) {
				types.add(importType( "javax.persistence.CascadeType") + ".REMOVE");
			}
			else if ( "refresh".equals( element ) ) {
				types.add(importType( "javax.persistence.CascadeType") + ".REFRESH");
			}
			// TODO: add CascadeType.DETACH since JPA 2.0
			else if ( "all".equals( element ) ) {
				types.add(importType( "javax.persistence.CascadeType") + ".ALL");
			}
			else if (!"none".equals( element )) {
                log.warn("Cascade type '{}' unmanaged for javax.persistence.CascadeType", element);
            }
		}
		return types.toArray( new String[types.size()] );
	}

	public String generateManyToOneAnnotation(Property property) {
		StringBuffer buffer = new StringBuffer(AnnotationBuilder.createAnnotation( importType("javax.persistence.ManyToOne") )
				.addAttribute( "cascade", getCascadeTypes(property))
				.addAttribute( "fetch", getFetchType(property))
				.getResult());
		buffer.append(getHibernateCascadeTypeAnnotation(property));
		return buffer.toString();
	}

	public boolean isSharedPkBasedOneToOne(OneToOne oneToOne){
		Iterator<Selectable> joinColumnsIt = oneToOne.getColumnIterator();
		Set<Selectable> joinColumns = new HashSet<Selectable>();
		while ( joinColumnsIt.hasNext() ) {
			joinColumns.add( joinColumnsIt.next() );
		}

		if ( joinColumns.size() == 0 )
			return false;

		Iterator<?> idColumnsIt = getIdentifierProperty().getColumnIterator();
		while ( idColumnsIt.hasNext() ) {
			if (!joinColumns.contains(idColumnsIt.next()) )
				return false;
		}

		return true;
	}

	public String generateOneToOneAnnotation(Property property, Metadata md) {
		OneToOne oneToOne = (OneToOne)property.getValue();

		boolean pkIsAlsoFk = isSharedPkBasedOneToOne(oneToOne);

		AnnotationBuilder ab = AnnotationBuilder.createAnnotation( importType("javax.persistence.OneToOne") )
			.addAttribute( "cascade", getCascadeTypes(property))
			.addAttribute( "fetch", getFetchType(property));

		if ( oneToOne.getForeignKeyType().equals(ForeignKeyDirection.TO_PARENT) ){
			ab.addQuotedAttribute("mappedBy", getOneToOneMappedBy(md, oneToOne));
		}

		StringBuffer buffer = new StringBuffer(ab.getResult());
		buffer.append(getHibernateCascadeTypeAnnotation(property));

		if ( pkIsAlsoFk && oneToOne.getForeignKeyType().equals(ForeignKeyDirection.FROM_PARENT) ){
			AnnotationBuilder ab1 = AnnotationBuilder.createAnnotation( importType("javax.persistence.PrimaryKeyJoinColumn") );
			buffer.append(ab1.getResult());
		}

		return buffer.toString();
	}

	public String getHibernateCascadeTypeAnnotation(Property property) {
		StringTokenizer st =  new StringTokenizer( property.getCascade(), ", ", false );
		String cascadeType = null;
		StringBuffer cascade = new StringBuffer();
		while ( st.hasMoreElements() ) {
			String element = ( (String) st.nextElement() ).toLowerCase();
			if ( "all-delete-orphan".equals( element ) ) {
				if (cascadeType == null) cascadeType = importType( "org.hibernate.annotations.CascadeType");
				cascade.append( cascadeType ).append(".ALL").append(", ")
						.append( cascadeType ).append(".DELETE_ORPHAN").append(", ");
			}
			else if ( "delete-orphan".equals( element ) ) {
				if (cascadeType == null) cascadeType = importType( "org.hibernate.annotations.CascadeType");
				cascade.append( cascadeType ).append(".DELETE_ORPHAN").append(", ");
			}
			else if ( "save-update".equals( element ) ) {
				if (cascadeType == null) cascadeType = importType( "org.hibernate.annotations.CascadeType");
				cascade.append( cascadeType ).append(".SAVE_UPDATE").append(", ");
			}
			else if ( "replicate".equals( element ) ) {
				if (cascadeType == null) cascadeType = importType( "org.hibernate.annotations.CascadeType");
				cascade.append( cascadeType ).append(".REPLICATE").append(", ");
			}
			else if ( "lock".equals( element ) ) {
				if (cascadeType == null) cascadeType = importType( "org.hibernate.annotations.CascadeType");
				cascade.append( cascadeType ).append(".LOCK").append(", ");
			}
			else if ( "evict".equals( element ) ) {
				if (cascadeType == null) cascadeType = importType( "org.hibernate.annotations.CascadeType");
				cascade.append( cascadeType ).append(".EVICT").append(", ");
			}
		}
		if ( cascade.length() >= 2 ) {
			String hibernateCascade = importType("org.hibernate.annotations.Cascade");
			cascade.insert(0, "@" + hibernateCascade + "( {");
			cascade.setLength( cascade.length() - 2 );
			cascade.append("} )");
		}
		return cascade.toString();
	}

	public String getFetchType(Property property) {
		Value value = property.getValue();
		String fetchType = importType( "javax.persistence.FetchType");
		boolean lazy = false;
		if ( value instanceof ToOne ) {
			lazy = ( (ToOne) value ).isLazy();
		}
		else if ( value instanceof Collection ) {
			lazy = ( (Collection) value ).isLazy();
		}
		else {
			//we're not collection neither *toone so we are looking for property fetching
			lazy = property.isLazy();
		}
		if ( lazy ) {
			return fetchType + "." + "LAZY";
		}
		else {
			return fetchType + "." + "EAGER";
		}
	}

	public Object getDecoratedObject() {
		return clazz;
	}

	public String generateCollectionAnnotation(Property property, Metadata md) {
		StringBuffer annotation = new StringBuffer();
		Value value = property.getValue();
		if ( value != null && value instanceof Collection) {
			Collection collection = (Collection) value;
			if ( collection.isOneToMany() ) {
				String mappedBy = null;
				AnnotationBuilder ab = AnnotationBuilder.createAnnotation( importType( "javax.persistence.OneToMany") );
				ab.addAttribute( "cascade", getCascadeTypes( property ) );
				ab.addAttribute( "fetch", getFetchType (property) );
				if ( collection.isInverse() ) {
					mappedBy = getOneToManyMappedBy( md, collection );
					ab.addQuotedAttribute( "mappedBy", mappedBy );
				}
				annotation.append( ab.getResult() );

				if (mappedBy == null) annotation.append("\n").append( generateJoinColumnsAnnotation(property, md) );
			}
			else {
				//TODO do the @OneToMany @JoinTable
				//TODO composite element
				String mappedBy = null;
				AnnotationBuilder ab = AnnotationBuilder.createAnnotation( importType( "javax.persistence.ManyToMany") );
				ab.addAttribute( "cascade", getCascadeTypes( property ) );
				ab.addAttribute( "fetch", getFetchType (property) );

				if ( collection.isInverse() ) {
					mappedBy = getManyToManyMappedBy( md, collection );
					ab.addQuotedAttribute( "mappedBy", mappedBy );
				}
				annotation.append(ab.getResult());
				if (mappedBy == null) {
					annotation.append("\n    @");
					annotation.append( importType( "javax.persistence.JoinTable") ).append( "(name=\"" );
					Table table = collection.getCollectionTable();

					annotation.append( table.getName() );
					annotation.append( "\"" );
					if ( StringHelper.isNotEmpty( table.getSchema() ) ) {
						annotation.append(", schema=\"").append( table.getSchema() ).append("\"");
					}
					if ( StringHelper.isNotEmpty( table.getCatalog() ) ) {
						annotation.append(", catalog=\"").append( table.getCatalog() ).append("\"");
					}
					String uniqueConstraint = generateAnnTableUniqueConstraint(table);
					if ( uniqueConstraint.length() > 0 ) {
						annotation.append(", uniqueConstraints=").append(uniqueConstraint);
					}
					annotation.append( ", joinColumns = { ");
					buildArrayOfJoinColumnAnnotation(
							collection.getKey().getColumnIterator(),
							null,
							annotation,
							property.isInsertable(),
							property.isUpdateable()
					);
					annotation.append( " }");
					annotation.append( ", inverseJoinColumns = { ");
					buildArrayOfJoinColumnAnnotation(
							collection.getElement().getColumnIterator(),
							null,
							annotation,
							property.isInsertable(),
							property.isUpdateable()
					);
					annotation.append( " }");
					annotation.append(")");
				}

			}
			String hibernateCascade = getHibernateCascadeTypeAnnotation( property );
			if (hibernateCascade.length() > 0) annotation.append("\n    ").append(hibernateCascade);
		}
		return annotation.toString();
	}

	private String getManyToManyMappedBy(Metadata md, Collection collection) {
		String mappedBy;
		Iterator<Selectable> joinColumnsIt = collection.getKey().getColumnIterator();
		Set<Selectable> joinColumns = new HashSet<Selectable>();
		while ( joinColumnsIt.hasNext() ) {
			joinColumns.add( joinColumnsIt.next() );
		}
		ManyToOne manyToOne = (ManyToOne) collection.getElement();
		PersistentClass pc = md.getEntityBinding(manyToOne.getReferencedEntityName());
		Iterator<?> properties = pc.getPropertyClosureIterator();
		//TODO we should check the table too
		boolean isOtherSide = false;
		mappedBy = "unresolved";
		while ( ! isOtherSide && properties.hasNext() ) {
			Property collectionProperty = (Property) properties.next();
			Value collectionValue = collectionProperty.getValue();
			if ( collectionValue != null && collectionValue instanceof Collection ) {
				Collection realCollectionValue = (Collection) collectionValue;
				if ( ! realCollectionValue.isOneToMany() ) {
					if ( joinColumns.size() == realCollectionValue.getElement().getColumnSpan() ) {
						isOtherSide = true;
						Iterator<?> it = realCollectionValue.getElement().getColumnIterator();
						while ( it.hasNext() ) {
							Object column = it.next();
							if (! joinColumns.contains( column ) ) {
								isOtherSide = false;
								break;
							}
						}
						if (isOtherSide) {
							mappedBy = collectionProperty.getName();
						}
					}
				}
			}
		}
		return mappedBy;
	}

	private String getOneToManyMappedBy(Metadata md, Collection collection) {
		String mappedBy;
		Iterator<Selectable> joinColumnsIt = collection.getKey().getColumnIterator();
		Set<Selectable> joinColumns = new HashSet<Selectable>();
		while ( joinColumnsIt.hasNext() ) {
			joinColumns.add( joinColumnsIt.next() );
		}
		OneToMany oneToMany = (OneToMany) collection.getElement();
		PersistentClass pc = md.getEntityBinding(oneToMany.getReferencedEntityName());
		Iterator<?> properties = pc.getPropertyClosureIterator();
		//TODO we should check the table too
		boolean isOtherSide = false;
		mappedBy = "unresolved";
		while ( ! isOtherSide && properties.hasNext() ) {
			Property manyProperty = (Property) properties.next();
			Value manyValue = manyProperty.getValue();
			if ( manyValue != null && manyValue instanceof ManyToOne ) {
				if ( joinColumns.size() == manyValue.getColumnSpan() ) {
					isOtherSide = true;
					Iterator<?> it = manyValue.getColumnIterator();
					while ( it.hasNext() ) {
						Object column = it.next();
						if (! joinColumns.contains( column ) ) {
							isOtherSide = false;
							break;
						}
					}
					if (isOtherSide) {
						mappedBy = manyProperty.getName();
					}
				}

			}
		}
		return mappedBy;
	}

	private String getOneToOneMappedBy(Metadata md, OneToOne oneToOne) {
		String mappedBy;
		Iterator<Selectable> joinColumnsIt = oneToOne.getColumnIterator();
		Set<Selectable> joinColumns = new HashSet<Selectable>();
		while ( joinColumnsIt.hasNext() ) {
			joinColumns.add( joinColumnsIt.next() );
		}
		PersistentClass pc = md.getEntityBinding(oneToOne.getReferencedEntityName());
		String referencedPropertyName = oneToOne.getReferencedPropertyName();
		if ( referencedPropertyName != null )
			return referencedPropertyName;

		Iterator<?> properties = pc.getPropertyClosureIterator();
		//TODO we should check the table too
		boolean isOtherSide = false;
		mappedBy = "unresolved";


		while ( ! isOtherSide && properties.hasNext() ) {
			Property oneProperty = (Property) properties.next();
			Value manyValue = oneProperty.getValue();
			if ( manyValue != null && ( manyValue instanceof OneToOne || manyValue instanceof ManyToOne ) ) {
				if ( joinColumns.size() == manyValue.getColumnSpan() ) {
					isOtherSide = true;
					Iterator<?> it = manyValue.getColumnIterator();
					while ( it.hasNext() ) {
						Object column = it.next();
						if (! joinColumns.contains( column ) ) {
							isOtherSide = false;
							break;
						}
					}
					if (isOtherSide) {
						mappedBy = oneProperty.getName();
					}
				}

			}
		}
		return mappedBy;
	}

	public boolean isSubclass() {
		return clazz.getSuperclass()!=null;
	}

	public List<Property> getPropertyClosureForFullConstructor() {
		return getPropertyClosureForFullConstructor(clazz);
	}

	protected List<Property> getPropertyClosureForFullConstructor(PersistentClass pc) {
		List<Property> l = new ArrayList<Property>(getPropertyClosureForSuperclassFullConstructor( pc ));
		l.addAll(getPropertiesForFullConstructor( pc ));
		return l;
	}

	public List<Property> getPropertiesForFullConstructor() {
		return getPropertiesForFullConstructor(clazz);
	}

	protected List<Property> getPropertiesForFullConstructor(PersistentClass pc) {
		List<Property> result = new ArrayList<Property>();

		for ( Iterator<Property> myFields = getAllPropertiesIterator(pc); myFields.hasNext() ; ) {
			Property field = (Property) myFields.next();
			// TODO: if(!field.isGenerated() ) ) {
			if(field.equals(pc.getIdentifierProperty()) && !isAssignedIdentifier(pc, field)) {
				continue; // dont add non assigned identifiers
			} else if(field.equals(pc.getVersion())) {
				continue; // version prop
			} else if(field.isBackRef()) {
				continue;
			} else if(isFormula(field)) {
				continue;
			} else {
				result.add( field );
			}
		}

		return result;
	}

	private boolean isFormula(Property field) {
		Value value = field.getValue();
		boolean foundFormula = false;

		if(value!=null && value.getColumnSpan()>0) {
			Iterator<Selectable> columnIterator = value.getColumnIterator();
			while ( columnIterator.hasNext() ) {
				Selectable element = columnIterator.next();
				if(!(element instanceof Formula)) {
					return false;
				} else {
					foundFormula = true;
				}
			}
		} else {
			return false;
		}
		return foundFormula;
	}

	public List<Property> getPropertyClosureForSuperclassFullConstructor() {
		return getPropertyClosureForSuperclassFullConstructor(clazz);
	}

	public List<Property> getPropertyClosureForSuperclassFullConstructor(PersistentClass pc) {
		List<Property> result = new ArrayList<Property>();
		if ( pc.getSuperclass() != null ) {
			// The correct sequence is vital here, as the subclass should be
			// able to invoke the fullconstructor based on the sequence returned
			// by this method!
			result.addAll( getPropertyClosureForSuperclassFullConstructor( pc.getSuperclass() ) );
			result.addAll( getPropertiesForFullConstructor( pc.getSuperclass() ) );
		}

		return result;
	}


	public List<Property> getPropertyClosureForMinimalConstructor() {
		return getPropertyClosureForMinimalConstructor(clazz);
	}

	protected List<Property> getPropertyClosureForMinimalConstructor(PersistentClass pc) {
		List<Property> l = new ArrayList<Property>(getPropertyClosureForSuperclassMinConstructor( pc ));
		l.addAll(getPropertiesForMinimalConstructor( pc ));
		return l;
	}

	public List<Property> getPropertiesForMinimalConstructor() {
		return getPropertiesForMinimalConstructor(clazz);
	}

	protected List<Property> getPropertiesForMinimalConstructor(PersistentClass pc) {
		List<Property> result = new ArrayList<Property>();

		for ( Iterator<Property> myFields = getAllPropertiesIterator(pc); myFields.hasNext() ; ) {
			Property property = (Property) myFields.next();
			if(property.equals(pc.getIdentifierProperty())) {
				if(isAssignedIdentifier(pc, property)) {
					result.add(property);
				} else {
					continue;
				}
			} else if (property.equals(pc.getVersion())) {
				continue; // the version property should not be in the result.
			} else if( isRequiredInConstructor(property) ) {
				result.add(property);
			}
		}

		return result;
	}

	protected boolean isAssignedIdentifier(PersistentClass pc, Property property) {
		if(property.equals(pc.getIdentifierProperty())) {
			if(property.getValue().isSimpleValue()) {
				SimpleValue sv = (SimpleValue) property.getValue();
				if("assigned".equals(sv.getIdentifierGeneratorStrategy())) {
					return true;
				}
			}
		}
		return false;
	}

	public List<Property> getPropertyClosureForSuperclassMinimalConstructor() {
		return getPropertyClosureForSuperclassMinConstructor(clazz);
	}

	protected List<Property> getPropertyClosureForSuperclassMinConstructor(PersistentClass pc) {
		List<Property> result = new ArrayList<Property>();
		if ( pc.getSuperclass() != null ) {
			// The correct sequence is vital here, as the subclass should be
			// able to invoke the fullconstructor based on the sequence returned
			// by this method!
			result.addAll( getPropertyClosureForSuperclassMinConstructor( pc.getSuperclass() ) );
			result.addAll( getPropertiesForMinimalConstructor( pc.getSuperclass() ) );
		}

		return result;
	}

	public POJOClass getSuperClass(){
		if (!isSubclass())
			return null;
		return new EntityPOJOClass(clazz.getSuperclass(),c2j);
	}


	public String toString() {
		return "Entity: " + (clazz==null?"<none>":clazz.getEntityName());
	}

	public boolean hasVersionProperty() {
		return clazz.isVersioned() && clazz instanceof RootClass;
	}

	/*
	 * @see org.hibernate.tool.hbm2x.pojo.POJOClass#getVersionProperty()
	 */
	public Property getVersionProperty()
	{
		return clazz.getVersion();
	}

}
