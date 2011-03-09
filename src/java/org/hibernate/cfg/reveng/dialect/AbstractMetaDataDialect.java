package org.hibernate.cfg.reveng.dialect;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.JDBCBinderException;
import org.hibernate.cfg.reveng.ReverseEngineeringRuntimeInfo;
import org.hibernate.exception.SQLExceptionConverter;
import org.hibernate.util.StringHelper;

/**
 * abstract base class for the metadatadialects to hold the
 * basic setup classes.
 *  
 * @author max
 *
 */
abstract public class AbstractMetaDataDialect implements MetaDataDialect {

	protected final Log log = LogFactory.getLog(this.getClass());
	
	private Connection connection;
	private DatabaseMetaData metaData;

	private ReverseEngineeringRuntimeInfo info;


	public void configure(ReverseEngineeringRuntimeInfo info) {
		this.info = info;
				
	}
	
	public void close() {
		metaData = null;
		if(connection != null) {
			try {
				info.getConnectionProvider().closeConnection(connection);				
			}
			catch (SQLException e) {
				throw getSQLExceptionConverter().convert(e, "Problem while closing connection", null);
			} finally {
				connection = null;
			}
		}
		info = null;		
	}
	
	protected DatabaseMetaData getMetaData() throws JDBCBinderException {
		if (metaData == null) {
			try {
				metaData = getConnection().getMetaData();				
			} 
			catch (SQLException e) {
				throw getSQLExceptionConverter().convert(e, "Getting database metadata", null);
			}
		}
		return metaData;
	}
	
	protected String getDatabaseStructure(String catalog, String schema) {
	      ResultSet schemaRs = null;
	      ResultSet catalogRs = null;
	      String nl = System.getProperty("line.separator");
	      StringBuffer sb = new StringBuffer(nl);
	      // Let's give the user some feedback. The exception
	      // is probably related to incorrect schema configuration.
	      sb.append("Configured schema:").append(schema).append(nl);
	      sb.append("Configured catalog:").append(catalog ).append(nl);

	      try {
	         schemaRs = getMetaData().getSchemas();
	         sb.append("Available schemas:").append(nl);
	         while (schemaRs.next() ) {
	            sb.append("  ").append(schemaRs.getString("TABLE_SCHEM") ).append(nl);
	         }
	      } 
	      catch (SQLException e2) {
	         log.warn("Could not get schemas", e2);
	         sb.append("  <SQLException while getting schemas>").append(nl);
	      } 
	      finally {
	         try {
	            schemaRs.close();
	         } 
	         catch (Exception ignore) {
	         }
	      }

	      try {
	         catalogRs = getMetaData().getCatalogs();
	         sb.append("Available catalogs:").append(nl);
	         while (catalogRs.next() ) {
	            sb.append("  ").append(catalogRs.getString("TABLE_CAT") ).append(nl);
	         }
	      } 
	      catch (SQLException e2) {
	         log.warn("Could not get catalogs", e2);
	         sb.append("  <SQLException while getting catalogs>").append(nl);
	      } 
	      finally {
	         try {
	            catalogRs.close();
	         } 
	         catch (Exception ignore) {
	         }
	      }
	      return sb.toString();
	   }

	protected Connection getConnection() throws SQLException {
		if(connection==null) {
			connection = info.getConnectionProvider().getConnection();
		}
		return connection;
	}
	
	public ReverseEngineeringRuntimeInfo getReverseEngineeringRuntimeInfo() {
		return info;
	}
	
	public void close(Iterator iterator) {
		if(iterator instanceof ResultSetIterator) {
			((ResultSetIterator)iterator).close();
		}
	}
	
	protected SQLExceptionConverter getSQLExceptionConverter() {
		return info.getSQLExceptionConverter();
	}
	
	public boolean needQuote(String name) {
		
		if(name==null) return false;
	
		// TODO: use jdbc metadata to decide on this. but for now we just handle the most typical cases.		
		if(name.indexOf('-')>0) return true;
		if(name.indexOf(' ')>0) return true;
		if(name.indexOf('.')>0) return true;
		return false;
	}
	
	protected String caseForSearch(String value) throws SQLException  {
		// TODO: handle quoted requests (just strip it ?)
		if(needQuote(value)) {
			if ( getMetaData().storesMixedCaseQuotedIdentifiers() ) {
				return value;
			} else if ( getMetaData().storesUpperCaseQuotedIdentifiers() ) { 
				return StringHelper.toUpperCase( value ); 
			} else if( getMetaData().storesLowerCaseQuotedIdentifiers() ) {
				return StringHelper.toLowerCase( value );
			} else {
				return value;
			}
		} else if ( getMetaData().storesMixedCaseQuotedIdentifiers() ) {
			return value;
		} else if ( getMetaData().storesUpperCaseIdentifiers() ) { 
			return StringHelper.toUpperCase( value ); 
		} else if( getMetaData().storesLowerCaseIdentifiers() ) {
			return StringHelper.toLowerCase( value );
		} else {
			return value;
		}		
	}

	
	public Iterator getSuggestedPrimaryKeyStrategyName(String catalog, String schema, String table) {
		Map m = new HashMap();
		m.put( "TABLE_CAT", catalog );
		m.put( "TABLE_SCHEMA", schema );
		m.put( "TABLE_NAME", table );
		m.put( "HIBERNATE_STRATEGY", null );
		List l = new ArrayList();
		l.add(m);
		return l.iterator();
	}
}
