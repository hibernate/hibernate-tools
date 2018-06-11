
package org.hibernate.tool.hbm2x.hbm2hbmxml.Cfg2HbmToolTest;

import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.UnionSubclass;
import org.hibernate.tool.internal.export.hbm.Cfg2HbmTool;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Dmitry Geraskov
 * @author koen
 */
public class TestCase {
	
	@Test
	public void testNeedsTable(){
		Cfg2HbmTool c2h = new Cfg2HbmTool();
		PersistentClass pc = new RootClass(null);
		Assert.assertTrue(c2h.needsTable(pc));
		Assert.assertTrue(c2h.needsTable(new JoinedSubclass(pc, null)));
		Assert.assertTrue(c2h.needsTable(new UnionSubclass(pc, null)));
		Assert.assertFalse(c2h.needsTable(new SingleTableSubclass(pc, null)));
		Assert.assertFalse(c2h.needsTable(new Subclass(pc, null)));			
	}
	
}
