package org.hibernate.cfg.reveng;

import org.hibernate.connection.ConnectionProvider;
import org.hibernate.exception.SQLExceptionConverter;
import org.hibernate.mapping.Table;

/**
 * Provides runtime-only information for reverse engineering process.
 * e.g. current connection provider, exception converter etc. 
 * 
 * @author max
 *
 */
public class ReverseEngineeringRuntimeInfo {

	private final ConnectionProvider connectionProvider;
	private final SQLExceptionConverter SQLExceptionConverter;
	private final DatabaseCollector dbs;
	
	public static ReverseEngineeringRuntimeInfo createInstance(ConnectionProvider provider, SQLExceptionConverter sec, DatabaseCollector dbs) {
		return new ReverseEngineeringRuntimeInfo(provider,sec,dbs);
	}
	
	protected ReverseEngineeringRuntimeInfo(ConnectionProvider provider, SQLExceptionConverter sec, DatabaseCollector dbs) {
		this.connectionProvider = provider;
		this.SQLExceptionConverter = sec;
		this.dbs = dbs;
	}
	
	public ConnectionProvider getConnectionProvider() {
		return connectionProvider;
	}
	
	public SQLExceptionConverter getSQLExceptionConverter() {
		return SQLExceptionConverter;
	}
	
	/** Shorthand for {@link getTable(String,String,String)} **/
	public Table getTable(TableIdentifier ti) {
		return dbs.getTable(ti.getSchema(), ti.getCatalog(), ti.getName());
	}
	
	/**
	 * Look up the table identified by the parameters in the currently found tables. 
	 * Warning: The table might not be fully initialized yet.  
	 * 
	 * @param catalog
	 * @param schema
	 * @param name
	 * @return Table if found in processd tables, null if not
	 */
	public Table getTable(String catalog, String schema, String name) {
		return dbs.getTable(schema, catalog, name);				
	}
	
	
		
}
