import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.EntityMode;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;
import org.hibernate.metadata.ClassMetadata;


/*
 * Created on 07-Dec-2004
 *
 */

/**
 * @author max
 * TODO: auto generate this stuff
 */
public class HibernateTestAuto {
	
	public static void main(String[] args) {
		Configuration cfg = new Configuration();
		
		cfg.configure("/hibernate.cfg.xml");
		
		SessionFactory factory = cfg.buildSessionFactory();
		
		Session session = factory.openSession();
		
		Map allClassMetadata = factory.getAllClassMetadata();
		
		Iterator iterator = allClassMetadata.values().iterator();
		
		while (iterator.hasNext() ) {
			ClassMetadata element =  (ClassMetadata) iterator.next();
			
			List list = session.find("from " + element.getMappedClass(EntityMode.POJO).getName() );
			System.out.println(list);
		}
	}

}
