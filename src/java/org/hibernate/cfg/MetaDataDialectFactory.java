package org.hibernate.cfg;

import java.util.Properties;

import org.hibernate.cfg.reveng.dialect.H2MetaDataDialect;
import org.hibernate.cfg.reveng.dialect.HSQLMetaDataDialect;
import org.hibernate.cfg.reveng.dialect.JDBCMetaDataDialect;
import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.hibernate.cfg.reveng.dialect.MySQLMetaDataDialect;
import org.hibernate.cfg.reveng.dialect.OracleMetaDataDialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.dialect.Oracle9Dialect;
import org.hibernate.util.ReflectHelper;
//import org.hibernate.cfg.reveng.dialect.SQLServerMetaDataDialect;
//import org.hibernate.dialect.SQLServerDialect;

public class MetaDataDialectFactory {

	public MetaDataDialect createMetaDataDialect(Dialect dialect, Properties cfg) {
		String property = cfg.getProperty( "hibernatetool.metadatadialect" );
		MetaDataDialect mdd = fromClassName(property);
		if(mdd==null) {
			mdd = fromDialect(dialect);
		}
		if(mdd==null) {
			mdd = fromDialectName(dialect.getClass().getName());
		}
		if(mdd==null) {
			mdd = new JDBCMetaDataDialect();
		}
		return mdd;
	}

	private MetaDataDialect fromClassName(String property) {
		if ( property != null ) {
			try {
				return (MetaDataDialect) ReflectHelper.classForName( property,
						JDBCReaderFactory.class ).newInstance();
			}
			catch (Exception e) {
				throw new JDBCBinderException(
						"Could not load MetaDataDialect: " + property, e );
			}
		} else {
			return null;
		}
	}
	
	public MetaDataDialect fromDialect(Dialect dialect) {
		if(dialect!=null) {  
			if(dialect instanceof Oracle9Dialect) {
				return new OracleMetaDataDialect();
			} else if(dialect instanceof Oracle8iDialect) {
				return new OracleMetaDataDialect();
			} else if (dialect instanceof H2Dialect) {
				return new H2MetaDataDialect();
			} else if (dialect instanceof MySQLDialect) {
				return new MySQLMetaDataDialect();
			} else if (dialect instanceof HSQLDialect) {
				return new HSQLMetaDataDialect();
//			}else if (dialect instanceof SQLServerDialect) {
//				return new SQLServerMetaDataDialect();
			}			
		}
		return null;
	}
	
	public MetaDataDialect fromDialectName(String dialect) {
		if (dialect.toLowerCase().contains("oracle")) {
			return new OracleMetaDataDialect();
		}
		if (dialect.toLowerCase().contains("mysql")) {
			return new MySQLMetaDataDialect();
		}
		if (dialect.toLowerCase().contains("h2")) {
			return new H2MetaDataDialect();
		}
		if (dialect.toLowerCase().contains("hsql")) {
			return new HSQLMetaDataDialect();
		}
//		if (dialect.toLowerCase().contains("sqlserver")) {
//			return new SQLServerMetaDataDialect();
//		}
		return null;
	}


}
