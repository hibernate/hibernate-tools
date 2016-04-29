package org.hibernate.tool.proxies;

import java.sql.Connection;
import java.sql.Statement;

import org.hibernate.Session;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.NonReflectiveTestCase;

public class EqualProxyTest extends NonReflectiveTestCase {

	public EqualProxyTest(String name) {
		super(name, "equalproxy");
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		if (getSessions() == null) {
			buildSessionFactory();
		}
	}

	@Override
	protected void tearDown() throws Exception {
		Statement statement = null;
		Connection con = null;
		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
			.applySettings( getConfiguration().getProperties() )
			.build();
		ConnectionProvider connectionProvider = 
				serviceRegistry.getService(ConnectionProvider.class);
		try {
			con = connectionProvider.getConnection();
			statement = con.createStatement();
			statement.execute("drop table EqualBean");
			statement.execute("drop table EqualBean2");
			con.commit();
		} finally {
			if (statement!=null) statement.close();
			connectionProvider.closeConnection(con);
		}
		
		super.tearDown();
	}
	
	@Override
	protected void customiseConfiguration(Configuration cfg) {
		cfg.setProperty(Environment.HBM2DDL_AUTO, "update");
		
		super.customiseConfiguration(cfg);
	}

	@Override
	protected String[] getMappings() {
		return new String[] { "EqualProxy.hbm.xml" };
	}

	public void testTwoIdenticalProxiesAreNotEqual() {
		Session session = openSession();
		try {
			EqualBean a = new EqualBean();
			a.setId(1);
			session.save(a);
			
			a = new EqualBean();
			a.setId(2);
			session.save(a);
			
			session.flush();
			session.clear();
			
			boolean classCastException = false;
			try {
				/* A ClassCastException will be thrown as we are lazy loading and the EqualBean has a proxy */
				a = (EqualBean) session.load(EqualBean.class, 1);
			} catch (ClassCastException e) {
				classCastException = true;
			}
			assertTrue("Expected a ClassCastException to be thrown", classCastException);
			
			EqualBeanProxy p1 = (EqualBeanProxy) session.load(EqualBean.class, 1);
			EqualBeanProxy p2 = (EqualBeanProxy) session.load(EqualBean.class, 1);
			
			/* These two proxies are the exact same object */
			assertSame("Expected the two proxies to be ==", p1, p2);
			
			/* However because of the way equals() is implemented, they are not equals() */
			assertFalse("Expected the two proxies not to be .equals()", p1.equals(p2));
		} finally {
			session.close();
		}
	}

	public void testTwoIdenticalProxiesAreNowEqual() {
		Session session = openSession();
		try {
			EqualBean2 a = new EqualBean2();
			a.setId(1);
			session.save(a);
			
			a = new EqualBean2();
			a.setId(2);
			session.save(a);
			
			session.flush();
			session.clear();
			
			boolean classCastException = false;
			try {
				/* A ClassCastException will be thrown as we are lazy loading and the EqualBean2 has a proxy */
				a = (EqualBean2) session.load(EqualBean2.class, 1);
			} catch (ClassCastException e) {
				classCastException = true;
			}
			assertTrue("Expected a ClassCastException to be thrown", classCastException);
			
			EqualBean2Proxy p1 = (EqualBean2Proxy) session.load(EqualBean2.class, 1);
			EqualBean2Proxy p2 = (EqualBean2Proxy) session.load(EqualBean2.class, 1);
			
			/* These two proxies are the exact same object */
			assertSame("Expected the two proxies to be ==", p1, p2);
			
			/* And their .equals() works */
			assertTrue("Expected the two proxies to be .equals()", p1.equals(p2));
		} finally {
			session.close();
		}
	}

	protected String getBaseForMappings() {
		return "org/hibernate/tool/proxies/";
	}

}
