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


package org.hibernate.tool.hbm2x.hbm2hbmxml.Cfg2HbmToolTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.UnionSubclass;
import org.hibernate.tool.internal.export.hbm.Cfg2HbmTool;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * @author Dmitry Geraskov
 * @author koen
 */
//TODO HBX-2148: Reenable the tests
@Disabled
public class TestCase {
	
	@Test
	public void testNeedsTable(){
		Cfg2HbmTool c2h = new Cfg2HbmTool();
		PersistentClass pc = new RootClass(null);
		assertTrue(c2h.needsTable(pc));
		assertTrue(c2h.needsTable(new JoinedSubclass(pc, null)));
		assertTrue(c2h.needsTable(new UnionSubclass(pc, null)));
		assertFalse(c2h.needsTable(new SingleTableSubclass(pc, null)));
		assertFalse(c2h.needsTable(new Subclass(pc, null)));			
	}
	
}
