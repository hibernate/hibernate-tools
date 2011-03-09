
package org.hibernate.tool.hbm2x.hbm2hbmxml;

import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.UnionSubclass;
import org.hibernate.tool.hbm2x.Cfg2HbmTool;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Dmitry Geraskov
 *
 */
public class Cfg2HbmToolTest extends TestCase {
	
	public void testNeedsTable(){
		Cfg2HbmTool c2h = new Cfg2HbmTool();
		PersistentClass pc = new RootClass();
		assertTrue(c2h.needsTable(pc));
		assertTrue(c2h.needsTable(new JoinedSubclass(pc)));
		assertTrue(c2h.needsTable(new UnionSubclass(pc)));
		assertFalse(c2h.needsTable(new SingleTableSubclass(pc)));
		assertFalse(c2h.needsTable(new Subclass(pc)));
				
	}
	
	public static Test suite() {
		return new TestSuite(Cfg2HbmToolTest.class);
	}
	
}
