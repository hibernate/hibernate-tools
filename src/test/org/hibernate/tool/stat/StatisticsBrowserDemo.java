package org.hibernate.tool.stat;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Properties;

import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.Settings;
import org.hibernate.classic.Session;
import org.hibernate.tool.NonReflectiveTestCase;

public class StatisticsBrowserDemo extends NonReflectiveTestCase {

	public StatisticsBrowserDemo(String name) {
		super( name );
	}
	/*
	protected void configure(Configuration cfg) {
		super.configure( cfg );
		cfg.setProperty( Environment.USE_STRUCTURED_CACHE, "true" );
	}*/
	
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
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
		
		
		
		//Uncomment if you want to look on StatisticsBrowser
        //Thread.sleep( 100000 ); 
		
	}

	protected void tearDown() throws Exception {
		Statement statement = null;
		Connection con = null;
		Settings settings = null;
        try {
        	settings = getConfiguration().buildSettings();
        	con = settings.getConnectionProvider().getConnection();
        	statement = con.createStatement();
        	statement.execute("drop table Session_attributes");
        	statement.execute("drop table Users");
        	statement.execute("drop table Groups");
        	con.commit();
        } finally {
        	if (statement!=null) statement.close();
        	settings.getConnectionProvider().closeConnection(con);
        }
        
		super.tearDown();
	}

	protected String getBaseForMappings() {
		return "org/hibernate/tool/stat/";
	}
	
	protected void addMappings(String[] files, Configuration cfg) {
		Properties prop = new Properties();
		prop.put(Environment.CACHE_PROVIDER,
				"org.hibernate.cache.EhCacheProvider");
		cfg.addProperties(prop);
		super.addMappings(files, cfg);
	}
	
	protected String[] getMappings() {
		return new String[] { "UserGroup.hbm.xml"};
	}

}
