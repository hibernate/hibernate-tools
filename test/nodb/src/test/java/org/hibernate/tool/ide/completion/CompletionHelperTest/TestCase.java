/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2017-2021 Red Hat, Inc.
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

package org.hibernate.tool.ide.completion.CompletionHelperTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.tool.ide.completion.CompletionHelper;
import org.hibernate.tool.ide.completion.EntityNameReference;
import org.junit.jupiter.api.Test;

/**
 * @author leon
 * @author koen
 */
public class TestCase {
	
    @Test
    public void testGetCanonicalPath() {
        List<EntityNameReference> qts = new ArrayList<EntityNameReference>();
        qts.add(new EntityNameReference("Article", "art"));
        qts.add(new EntityNameReference("art.descriptions", "descr"));
        qts.add(new EntityNameReference("descr.name", "n"));
        assertEquals("Article/descriptions/name/locale", CompletionHelper.getCanonicalPath(qts, "n.locale"), "Invalid path");
        assertEquals("Article/descriptions", CompletionHelper.getCanonicalPath(qts, "descr"), "Invalid path");
        //
        qts.clear();
        qts.add(new EntityNameReference("com.company.Clazz", "clz"));
        qts.add(new EntityNameReference("clz.attr", "a"));
        assertEquals("com.company.Clazz/attr", CompletionHelper.getCanonicalPath(qts, "a"), "Invalid path");
        //
        qts.clear();
        qts.add(new EntityNameReference("Agga", "a"));
        assertEquals("Agga", CompletionHelper.getCanonicalPath(qts, "a"), "Invalid path");
    }

    @Test
    public void testStackOverflowInGetCanonicalPath() {
        List<EntityNameReference> qts = new ArrayList<EntityNameReference>();
        qts.add(new EntityNameReference("Article", "art"));
        qts.add(new EntityNameReference("art.stores", "store"));
        qts.add(new EntityNameReference("store.articles", "art"));
        // This should not result in a stack overflow
        CompletionHelper.getCanonicalPath(qts, "art");
    }
        
    
}
