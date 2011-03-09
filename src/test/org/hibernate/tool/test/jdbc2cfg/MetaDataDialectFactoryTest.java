package org.hibernate.tool.test.jdbc2cfg;

import java.util.Properties;

import junit.framework.TestCase;

import org.hibernate.cfg.JDBCBinderException;
import org.hibernate.cfg.MetaDataDialectFactory;
import org.hibernate.cfg.reveng.dialect.H2MetaDataDialect;
import org.hibernate.cfg.reveng.dialect.HSQLMetaDataDialect;
import org.hibernate.cfg.reveng.dialect.JDBCMetaDataDialect;
import org.hibernate.cfg.reveng.dialect.MySQLMetaDataDialect;
import org.hibernate.cfg.reveng.dialect.OracleMetaDataDialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.dialect.Oracle9Dialect;
import org.hibernate.dialect.Oracle9iDialect;
import org.hibernate.dialect.OracleDialect;

public class MetaDataDialectFactoryTest extends TestCase {

	private MetaDataDialectFactory mdf;

	static class NoNameDialect extends Dialect {
		
	}
	
	static class H2NamedDialect extends Dialect {
		
	}
	
	protected void setUp() throws Exception {
		mdf = new MetaDataDialectFactory();
	}
	
	public void testCreateMetaDataDialect() {
		assertSameClass("Generic metadata for dialects with no specifics", JDBCMetaDataDialect.class, mdf.createMetaDataDialect(new NoNameDialect(), new Properties()));
		assertSameClass(H2MetaDataDialect.class, mdf.createMetaDataDialect(new H2NamedDialect(), new Properties()));
		assertSameClass(OracleMetaDataDialect.class, mdf.createMetaDataDialect(new OracleDialect(), new Properties()));		
		assertSameClass(MySQLMetaDataDialect.class, mdf.createMetaDataDialect(new MySQL5Dialect(), new Properties()));
		
		Properties p = new Properties();
		p.setProperty("hibernatetool.metadatadialect", H2MetaDataDialect.class.getCanonicalName());
		assertSameClass("property should override specific dialect", H2MetaDataDialect.class, mdf.createMetaDataDialect(new MySQL5Dialect(), p));
			
	}

	public void testCreateMetaDataDialectNonExistingOverride(Properties p) {
		p.setProperty("hibernatetool.metadatadialect", "DoesNotExists");
		try {
			mdf.createMetaDataDialect(new MySQL5Dialect(), p);
			fail();
		} catch (JDBCBinderException jbe) {
			// expected
		} catch(Exception e) {
			fail();
		}
	}

	public void testFromDialect() {
		assertSameClass("Generic metadata for dialects with no specifics", null, mdf.fromDialect(new NoNameDialect()));
		
		assertSameClass(OracleMetaDataDialect.class, mdf.fromDialect(new Oracle8iDialect()));
		assertSameClass(OracleMetaDataDialect.class, mdf.fromDialect(new Oracle9Dialect()));
		assertSameClass(OracleMetaDataDialect.class, mdf.fromDialect(new Oracle10gDialect()));
		assertSameClass(OracleMetaDataDialect.class, mdf.fromDialect(new Oracle9iDialect()));
		assertSameClass(MySQLMetaDataDialect.class, mdf.fromDialect(new MySQL5InnoDBDialect()));
		assertSameClass(H2MetaDataDialect.class, mdf.fromDialect(new H2Dialect()));
		assertSameClass(HSQLMetaDataDialect.class, mdf.fromDialect(new HSQLDialect()));
		
	}

	public void testFromDialectName() {
		assertSameClass(null, mdf.fromDialectName("BlahBlah"));
		assertSameClass(OracleMetaDataDialect.class, mdf.fromDialectName("mYorAcleDialect"));
		assertSameClass(OracleMetaDataDialect.class, mdf.fromDialectName(Oracle8iDialect.class.getName()));
		assertSameClass(OracleMetaDataDialect.class, mdf.fromDialectName(Oracle9Dialect.class.getName()));
		assertSameClass(MySQLMetaDataDialect.class, mdf.fromDialectName(MySQL5InnoDBDialect.class.getName()));
		assertSameClass(H2MetaDataDialect.class, mdf.fromDialectName(H2Dialect.class.getName()));
		assertSameClass(HSQLMetaDataDialect.class, mdf.fromDialectName(HSQLDialect.class.getName()));
		
	}

	public void assertSameClass(Class clazz, Object instance) {
		if(clazz==null && instance==null) {
			assertEquals(null,null);
			return;
		}
		if(clazz==null) {
			assertEquals(null, instance);
			return;
		}
		if(instance==null) {
			assertEquals(clazz.getCanonicalName(), null);
			return;
		}
		assertEquals(clazz.getCanonicalName(), instance.getClass().getName());
	}
	
	public void assertSameClass(String msg, Class clazz, Object instance) {
		if(clazz==null && instance==null) {
			assertEquals(null,null);
			return;
		}
		if(clazz==null) {
			assertEquals(msg, null, instance);
			return;
		}
		if(instance==null) {
			assertEquals(msg, clazz.getCanonicalName(), null);
			return;
		}
		assertEquals(msg, clazz.getCanonicalName(), instance.getClass().getName());
	}
}
