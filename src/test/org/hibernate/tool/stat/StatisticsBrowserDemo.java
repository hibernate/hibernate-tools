package org.hibernate.tool.stat;

import java.sql.Connection;
import java.sql.Statement;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.NonReflectiveTestCase;

public class StatisticsBrowserDemo extends NonReflectiveTestCase {

	public StatisticsBrowserDemo(String name) {
		super( name );
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		if(getSessions()==null) {
			buildSessionFactory();
		}
	}

	public void testBrowser() throws Exception {
		getSessions().getStatistics().setStatisticsEnabled( true );	
		new StatisticsBrowser().showStatistics( getSessions().getStatistics(), false );

		Session s = openSession();
		Transaction tx = s.beginTransaction();
		
		for(int i=0; i<100; i++) {
			Group group = new Group( "Hibernate" + i );
			group.addUser(new User("gavin" + i, "figo123"));
			group.addUser(new User("cbauer" + i, "figo123"));
			group.addUser(new User("steve" + i, "figo123"));
			group.addUser(new User("max" + i, "figo123"));
			group.addUser(new User("anthony" + i, "figo123"));

			s.saveOrUpdate( group );
			if(i % 20==0) s.flush();
		}
		s.flush();
		s.clear();
		s.createQuery( "from java.lang.Object" ).list();
		tx.commit();
		s.close();
	}

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
        	statement.execute("drop table Session_attributes");
        	statement.execute("drop table Users");
        	statement.execute("drop table Groups");
        	con.commit();
        } finally {
        	if (statement!=null) statement.close();
        	connectionProvider.closeConnection(con);
        }
        
		super.tearDown();
	}

	protected String getBaseForMappings() {
		return "org/hibernate/tool/stat/";
	}
	
	protected String[] getMappings() {
		return new String[] { "UserGroup.hbm.xml"};
	}

}
