package org.hibernate.tool.cfg.MetaDataDialectFactoryTest;

import java.util.Properties;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.dialect.Oracle9iDialect;
import org.hibernate.tool.api.dialect.MetaDataDialectFactory;
import org.hibernate.tool.internal.dialect.H2MetaDataDialect;
import org.hibernate.tool.internal.dialect.HSQLMetaDataDialect;
import org.hibernate.tool.internal.dialect.JDBCMetaDataDialect;
import org.hibernate.tool.internal.dialect.MySQLMetaDataDialect;
import org.hibernate.tool.internal.dialect.OracleMetaDataDialect;
import org.hibernate.tool.internal.reveng.JdbcBinderException;
import org.junit.Assert;
import org.junit.Test;

public class TestCase {

	private static class NoNameDialect extends Dialect {}
	
	private static class H2NamedDialect extends Dialect {}
	
	@Test
	public void testCreateMetaDataDialect() {
		assertSameClass(
				"Generic metadata for dialects with no specifics", 
				JDBCMetaDataDialect.class, 
				MetaDataDialectFactory.createMetaDataDialect(
						new NoNameDialect(), 
						new Properties()));
		assertSameClass(
				H2MetaDataDialect.class, 
				MetaDataDialectFactory.createMetaDataDialect(new H2NamedDialect(), new Properties()));
		assertSameClass(
				OracleMetaDataDialect.class, 
				MetaDataDialectFactory.createMetaDataDialect(
						new Oracle9iDialect(), 
						new Properties()));		
		assertSameClass(
				MySQLMetaDataDialect.class, 
				MetaDataDialectFactory.createMetaDataDialect(
						new MySQL5Dialect(), 
						new Properties()));
		Properties p = new Properties();
		p.setProperty(
				"hibernatetool.metadatadialect", 
				H2MetaDataDialect.class.getCanonicalName());
		assertSameClass(
				"property should override specific dialect", 
				H2MetaDataDialect.class, 
				MetaDataDialectFactory.createMetaDataDialect(new MySQL5Dialect(), p));			
	}

	@Test
	public void testCreateMetaDataDialectNonExistingOverride() {
		Properties p = new Properties();
		p.setProperty("hibernatetool.metadatadialect", "DoesNotExists");
		try {
			MetaDataDialectFactory.createMetaDataDialect(new MySQL5Dialect(), p);
			Assert.fail();
		} catch (JdbcBinderException jbe) {
			// expected
		} catch(Exception e) {
			Assert.fail();
		}
	}

	@Test
	public void testFromDialect() {
		assertSameClass(
				"Generic metadata for dialects with no specifics", 
				null, 
				MetaDataDialectFactory.fromDialect(new NoNameDialect()));	
		assertSameClass(
				OracleMetaDataDialect.class, 
				MetaDataDialectFactory.fromDialect(new Oracle8iDialect()));
		assertSameClass(
				OracleMetaDataDialect.class, 
				MetaDataDialectFactory.fromDialect(new Oracle9iDialect()));
		assertSameClass(
				OracleMetaDataDialect.class, 
				MetaDataDialectFactory.fromDialect(new Oracle10gDialect()));
		assertSameClass(
				MySQLMetaDataDialect.class, 
				MetaDataDialectFactory.fromDialect(new MySQLDialect()));
		assertSameClass(
				H2MetaDataDialect.class, 
				MetaDataDialectFactory.fromDialect(new H2Dialect()));
		assertSameClass(
				HSQLMetaDataDialect.class,
				MetaDataDialectFactory.fromDialect(new HSQLDialect()));
		
	}

	@Test
	public void testFromDialectName() {
		assertSameClass(
				null, 
				MetaDataDialectFactory.fromDialectName("BlahBlah"));
		assertSameClass(
				OracleMetaDataDialect.class, 
				MetaDataDialectFactory.fromDialectName("mYorAcleDialect"));
		assertSameClass(
				OracleMetaDataDialect.class, 
				MetaDataDialectFactory.fromDialectName(Oracle8iDialect.class.getName()));
		assertSameClass(
				OracleMetaDataDialect.class, 
				MetaDataDialectFactory.fromDialectName(Oracle9iDialect.class.getName()));
		assertSameClass(
				MySQLMetaDataDialect.class, 
				MetaDataDialectFactory.fromDialectName(MySQLDialect.class.getName()));
		assertSameClass(
				H2MetaDataDialect.class, 
				MetaDataDialectFactory.fromDialectName(H2Dialect.class.getName()));
		assertSameClass(
				HSQLMetaDataDialect.class, 
				MetaDataDialectFactory.fromDialectName(HSQLDialect.class.getName()));
		
	}

	private void assertSameClass(Class<?> clazz, Object instance) {
		if(clazz==null && instance==null) {
			Assert.assertEquals((Object)null, (Object)null);
			return;
		}
		if(clazz==null) {
			Assert.assertEquals(null, instance);
			return;
		}
		if(instance==null) {
			Assert.assertEquals(clazz.getCanonicalName(), null);
			return;
		}
		Assert.assertEquals(clazz.getCanonicalName(), instance.getClass().getName());
	}
	
	private void assertSameClass(String msg, Class<?> clazz, Object instance) {
		if(clazz==null && instance==null) {
			Assert.assertEquals((Object)null, (Object)null);
			return;
		}
		if(clazz==null) {
			Assert.assertEquals(msg, null, instance);
			return;
		}
		if(instance==null) {
			Assert.assertEquals(msg, clazz.getCanonicalName(), null);
			return;
		}
		Assert.assertEquals(msg, clazz.getCanonicalName(), instance.getClass().getName());
	}
}
