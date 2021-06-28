/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2004-2021 Red Hat, Inc.
 *
 * Licensed under the GNU Lesser General Public License (LGPL), 
 * version 2.1 or later (the "License").
 * You may not use this file except in compliance with the License.
 * You may read the licence in the 'lgpl.txt' file in the root folder of 
 * project or obtain a copy at
 *
 *     http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hibernate.tool.cfg.MetaDataDialectFactoryTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Properties;

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
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.dialect.Oracle9iDialect;
import org.junit.jupiter.api.Test;

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
			fail();
		} catch (JDBCBinderException jbe) {
			// expected
		} catch(Exception e) {
			fail();
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
			assertEquals((Object)null, (Object)null);
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
	
	private void assertSameClass(String msg, Class<?> clazz, Object instance) {
		if(clazz==null && instance==null) {
			assertEquals((Object)null, (Object)null);
			return;
		}
		if(clazz==null) {
			assertEquals(null, instance, msg);
			return;
		}
		if(instance==null) {
			assertEquals(clazz.getCanonicalName(), null, msg);
			return;
		}
		assertEquals(clazz.getCanonicalName(), instance.getClass().getName(), msg);
	}
}
