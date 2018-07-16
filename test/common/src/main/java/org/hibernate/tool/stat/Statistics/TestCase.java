package org.hibernate.tool.stat.Statistics;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.tool.api.stat.StatisticsBrowser;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class TestCase {
	
	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);
	}

	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	// HBX-1554: Ignore the test for now
	// TODO: re-enable the test
	@Ignore
	@Test
	public void testBrowser() throws Exception {
		MetadataSources mds = new MetadataSources();
		mds.addResource("/org/hibernate/tool/stat/Statistics/UserGroup.hbm.xml");
		Metadata md = mds.buildMetadata();
		SessionFactory sf = md.buildSessionFactory();
		sf.getStatistics().setStatisticsEnabled(true);

		new StatisticsBrowser().showStatistics( sf.getStatistics(), false );

		Session s = sf.openSession();
		Transaction tx = s.beginTransaction();
		
		for(int i=0; i<10; i++) {
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
		s.createQuery( "from java.lang.Object" ).getResultList();
		tx.commit();
		s.close();
		sf.close();
	}

}
