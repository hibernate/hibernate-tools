package org.hibernate.tool.stat;

import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.hibernate.tool.NonReflectiveTestCase;

public class StatisticsBrowserTest extends NonReflectiveTestCase {

	public StatisticsBrowserTest(String name) {
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
		
		Thread.sleep( 100000 );
		
	}

	protected String getBaseForMappings() {
		return "org/hibernate/tool/stat/";
	}
	protected String[] getMappings() {
		return new String[] { "UserGroup.hbm.xml"};
	}

}
