/*
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.hibernate.tool.ide.completion.CompletionHelperTest;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.tool.ide.completion.CompletionHelper;
import org.hibernate.tool.ide.completion.EntityNameReference;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author leon
 */
public class TestCase {
	
    @Test
    public void testGetCanonicalPath() {
        List<EntityNameReference> qts = new ArrayList<EntityNameReference>();
        qts.add(new EntityNameReference("Article", "art"));
        qts.add(new EntityNameReference("art.descriptions", "descr"));
        qts.add(new EntityNameReference("descr.name", "n"));
        Assert.assertEquals("Invalid path", "Article/descriptions/name/locale", CompletionHelper.getCanonicalPath(qts, "n.locale"));
        Assert.assertEquals("Invalid path", "Article/descriptions", CompletionHelper.getCanonicalPath(qts, "descr"));
        //
        qts.clear();
        qts.add(new EntityNameReference("com.company.Clazz", "clz"));
        qts.add(new EntityNameReference("clz.attr", "a"));
        Assert.assertEquals("Invalid path", "com.company.Clazz/attr", CompletionHelper.getCanonicalPath(qts, "a"));
        //
        qts.clear();
        qts.add(new EntityNameReference("Agga", "a"));
        Assert.assertEquals("Invalid path", "Agga", CompletionHelper.getCanonicalPath(qts, "a"));
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
