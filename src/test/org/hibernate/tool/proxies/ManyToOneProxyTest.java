package org.hibernate.tool.proxies;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.Settings;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.NonReflectiveTestCase;

public class ManyToOneProxyTest extends NonReflectiveTestCase {

	public ManyToOneProxyTest(String name) {
		super(name, "manytooneproxy");
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		buildSessionFactory();
	}
	
	@Override
	protected void tearDown() throws Exception {
		Statement statement = null;
		Connection con = null;
		Settings settings = null;
		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
			.applySettings( getConfiguration().getProperties() )
			.build();
    	
        try {
        	settings = getConfiguration().buildSettings(serviceRegistry);
        	con = serviceRegistry.getService(JdbcServices.class).getConnectionProvider().getConnection();
        	statement = con.createStatement();
        	statement.execute("drop table ClassA");
        	statement.execute("drop table ClassC");
        	statement.execute("drop table ClassB");
        	con.commit();
        } catch (SQLException e) {
        	System.err.println(e);
        } finally {
        	if (statement!=null) statement.close();
        	serviceRegistry.getService(JdbcServices.class).getConnectionProvider().closeConnection(con);
        }
        
		super.tearDown();
	}

	public void testFailWhenManyToOnePropertyTypeIsntProxy() {
		Session session = openSession();
		try {
			/* Create some objects in the database to later retrieve */
			ClassA a = new ClassA();
			a.setId(1);
			
			ClassB b = new ClassBSubA();
			b.setId(1);
			a.setMyClassB(b);
			
			session.save(b);
			session.save(a);
			
			/* Clear the session so we can retrieve */
			session.flush();
			session.clear();
	
			/* Retrieve the object. Throws an IllegalArgumentException as it tries to set the lazy proxy to the
			 * property with the concrete basetype.
			 */
			try {
				a = (ClassA) session.get(ClassA.class, 1);
			} catch (HibernateException e) {
				assertTrue("Unexpected exception type thrown: " + e.getCause(), e.getCause() instanceof IllegalArgumentException);
				return;
			}
			
			assertTrue("An exception was expected to be thrown.", false);
		} finally {
			session.close();
		}
	}

	public void testSuccessWhenManyToOnePropertyTypeIsProxy() {
		Session session = openSession();
		try {
			/* Create some objects in the database to later retrieve */
			ClassC c = new ClassC();
			c.setId(2);
			
			ClassB b = new ClassBSubA();
			b.setId(2);
			c.setMyClassB(b);
			
			session.save(b);
			session.save(c);
			
			/* Clear the session so we can retrieve */
			session.flush();
			session.clear();
	
			/* Retrieve the object. Throws an IllegalArgumentException as it tries to set the lazy proxy to the
			 * property with the concrete basetype.
			 */
			try {
				c = (ClassC) session.get(ClassC.class, 2);
			} catch (HibernateException e) {
				assertTrue("Didn't expect an exception to be thrown: " + e.getCause(), false);
				return;
			}
		} finally {
			session.close();
		}
	}
	
	protected String getBaseForMappings() {
		return "org/hibernate/tool/proxies/";
	}

	@Override
	protected String[] getMappings() {
		return new String[] { "ManyToOneProxies.hbm.xml" };
	}

}
