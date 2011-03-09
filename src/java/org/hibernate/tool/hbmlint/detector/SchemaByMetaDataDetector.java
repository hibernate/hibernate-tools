package org.hibernate.tool.hbmlint.detector;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCReaderFactory;
import org.hibernate.cfg.Settings;
import org.hibernate.cfg.reveng.DatabaseCollector;
import org.hibernate.cfg.reveng.DefaultDatabaseCollector;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.JDBCReader;
import org.hibernate.cfg.reveng.JDBCToHibernateTypeHelper;
import org.hibernate.cfg.reveng.SchemaSelection;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.Mapping;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.IdentifierCollection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Table;
import org.hibernate.tool.hbmlint.Issue;
import org.hibernate.tool.hbmlint.IssueCollector;
import org.hibernate.util.StringHelper;

public class SchemaByMetaDataDetector extends RelationalModelDetector {

	public String getName() {
		return "schema";
	}
	
	JDBCReader reader;

	private TableSelectorStrategy tableSelector;

	private DatabaseCollector dbc;

	private Dialect dialect;

	private Mapping mapping;

	/** current table as read from the database */
	Table currentDbTable = null;

	public SchemaByMetaDataDetector() {

	}

	public void initialize(Configuration cfg, Settings settings) {
		super.initialize( cfg, settings );
		dialect = settings.getDialect();

		tableSelector = new TableSelectorStrategy(
				new DefaultReverseEngineeringStrategy() );
		reader = JDBCReaderFactory.newJDBCReader( cfg.getProperties(),
				settings, tableSelector );
		dbc = new DefaultDatabaseCollector(reader.getMetaDataDialect());
		mapping = cfg.buildMapping();
	}

	public void visit(Configuration cfg, IssueCollector collector) {
		super.visit( cfg, collector );
		
		visitGenerators(cfg, collector);
				
	}
	
	public void visitGenerators(Configuration cfg, IssueCollector collector) {
		Iterator iter = iterateGenerators(cfg);
		
		Set sequences = Collections.EMPTY_SET;
		if(dialect.supportsSequences()) {
			sequences = reader.readSequences(dialect.getQuerySequencesString());
		}

		// TODO: move this check into something that could check per class or collection instead.
		while ( iter.hasNext() ) {
			PersistentIdentifierGenerator generator = (PersistentIdentifierGenerator) iter.next();
			Object key = generator.generatorKey();
			if ( !isSequence(key, sequences) && !isTable( key ) ) {
				collector.reportIssue( new Issue( "MISSING_ID_GENERATOR", Issue.HIGH_PRIORITY, "Missing sequence or table: " + key));
			}
		}

		
	}

	private boolean isSequence(Object key, Set sequences) {
		if(key instanceof String) {
			if ( sequences.contains( key ) ) {
				return true;
			} else {
				String[] strings = StringHelper.split(".", (String) key);
				if(strings.length==3) {
					return sequences.contains(strings[2]);
				} else if (strings.length==2) {
					return sequences.contains(strings[1]);
				}
			}
		}
		return false;
	}

	private boolean isTable(Object key) throws HibernateException {
		// BIG HACK - should probably utilize the table cache before going to the jdbcreader :(
		if(key instanceof String) {
			String[] strings = StringHelper.split(".", (String) key);
			if(strings.length==1) {
				tableSelector.clearSchemaSelections();
				tableSelector.addSchemaSelection( new SchemaSelection(null,null, strings[0]) );
				List list = reader.readDatabaseSchema( dbc, null, null );
				return !list.isEmpty();
			} else if(strings.length==3) {
				tableSelector.clearSchemaSelections();
				tableSelector.addSchemaSelection( new SchemaSelection(strings[0],strings[1], strings[2]) );
				List list = reader.readDatabaseSchema( dbc, null, null );
				return !list.isEmpty();
			} else if (strings.length==2) {
				tableSelector.clearSchemaSelections();
				tableSelector.addSchemaSelection( new SchemaSelection(null,strings[0], strings[1]) );
				List list = reader.readDatabaseSchema( dbc, null, null );
				return !list.isEmpty();
			}
		}
		return false;
	}
	
	public void visit(Configuration cfg, Table table, IssueCollector pc) {

		if ( table.isPhysicalTable() ) {
			setSchemaSelection( table );

			List list = reader.readDatabaseSchema( dbc, null, null );

			if ( list.isEmpty() ) {
				pc.reportIssue( new Issue( "SCHEMA_TABLE_MISSING",
						Issue.HIGH_PRIORITY, "Missing table "
								+ Table.qualify( table.getCatalog(), table
										.getSchema(), table.getName() ) ) );
				return;
			}
			else if ( list.size() > 1 ) {
				pc.reportIssue( new Issue( "SCHEMA_TABLE_MISSING",
						Issue.NORMAL_PRIORITY, "Found "
								+ list.size()
								+ " tables for "
								+ Table.qualify( table.getCatalog(), table
										.getSchema(), table.getName() ) ) );
				return;
			}
			else {
				currentDbTable = (Table) list.get( 0 );
				visitColumns(cfg,table,pc);				
			}
		}
		else {
			// log?			
		}
	}

	String table(Table t) {
		return Table.qualify( t.getCatalog(), t.getSchema(), t.getName() );
	}
	
	public void visit(Configuration cfg, Table table, Column col,
			IssueCollector pc) {
		if ( currentDbTable == null ) {
			return;
		}

		Column dbColumn = currentDbTable
				.getColumn( new Column( col.getName() ) );

		if ( dbColumn == null ) {
			pc.reportIssue( new Issue( "SCHEMA_COLUMN_MISSING",
					Issue.HIGH_PRIORITY, table(table) + " is missing column: " + col.getName() ) );
		}
		else {
			//TODO: this needs to be able to know if a type is truly compatible or not. Right now it requires an exact match.
			//String sqlType = col.getSqlType( dialect, mapping );
			int dbTypeCode = dbColumn.getSqlTypeCode().intValue();
			int modelTypeCode = col
								.getSqlTypeCode( mapping );
			// TODO: sqltype name string
			if ( !(dbTypeCode == modelTypeCode ) ) {
				pc.reportIssue( new Issue( "SCHEMA_COLUMN_TYPE_MISMATCH",
						Issue.NORMAL_PRIORITY, table(table) + " has a wrong column type for "
								+ col.getName() + ", expected: "
								+ JDBCToHibernateTypeHelper.getJDBCTypeName(modelTypeCode) + " but was " + JDBCToHibernateTypeHelper.getJDBCTypeName(dbTypeCode) + " in db") );
			}
		}
	}

	private void setSchemaSelection(Table table) {
		tableSelector.clearSchemaSelections();
		tableSelector.addSchemaSelection( new SchemaSelection( table
				.getCatalog(), table.getSchema(), table.getName() ) );

	}

	/**
	 * 
	 * @param cfg 
	 * @return iterator over all the IdentifierGenerator's found in the entitymodel and return a list of unique IdentifierGenerators
	 * @throws MappingException
	 */
	private Iterator iterateGenerators(Configuration cfg) throws MappingException {

		TreeMap generators = new TreeMap();
		String defaultCatalog = getSettings().getDefaultCatalogName();
		String defaultSchema = getSettings().getDefaultSchemaName();

		Iterator iter = cfg.getClassMappings();
		while ( iter.hasNext() ) {
			PersistentClass pc = (PersistentClass) iter.next();

			if ( !pc.isInherited() ) {

				IdentifierGenerator ig = pc.getIdentifier()
						.createIdentifierGenerator(
								cfg.getIdentifierGeneratorFactory(),
								dialect,
								defaultCatalog,
								defaultSchema,
								(RootClass) pc
							);

				if ( ig instanceof PersistentIdentifierGenerator ) {
					generators.put( ( (PersistentIdentifierGenerator) ig ).generatorKey(), ig );
				}

			}
		}

		iter = getConfiguration().getCollectionMappings();
		while ( iter.hasNext() ) {
			Collection collection = (Collection) iter.next();

			if ( collection.isIdentified() ) {

				IdentifierGenerator ig = ( (IdentifierCollection) collection ).getIdentifier()
						.createIdentifierGenerator(
								getConfiguration().getIdentifierGeneratorFactory(),
								dialect,
								defaultCatalog,
								defaultSchema,
								null
							);

				if ( ig instanceof PersistentIdentifierGenerator ) {
					generators.put( ( (PersistentIdentifierGenerator) ig ).generatorKey(), ig );
				}

			}
		}

		return generators.values().iterator();
	}

}
