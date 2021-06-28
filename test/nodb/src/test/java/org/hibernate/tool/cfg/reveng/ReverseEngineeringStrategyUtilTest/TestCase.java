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

package org.hibernate.tool.cfg.reveng.ReverseEngineeringStrategyUtilTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.hibernate.cfg.reveng.ReverseEngineeringStrategyUtil;
import org.junit.jupiter.api.Test;

public class TestCase {

	@Test
    public void testSimplePluralizeWithSingleH() throws Exception {
        String plural = ReverseEngineeringStrategyUtil.simplePluralize("h");
        assertEquals("hs", plural);
    }
	
	@Test
	public void testPluralize(){
		assertEquals("boxes", ReverseEngineeringStrategyUtil.simplePluralize("box"));
		assertEquals("buses", ReverseEngineeringStrategyUtil.simplePluralize("bus"));
		assertEquals("keys", ReverseEngineeringStrategyUtil.simplePluralize("key"));
		assertEquals("countries", ReverseEngineeringStrategyUtil.simplePluralize("country"));
		assertEquals("churches", ReverseEngineeringStrategyUtil.simplePluralize("church"));
		assertEquals("bushes", ReverseEngineeringStrategyUtil.simplePluralize("bush"));
		assertEquals("roofs", ReverseEngineeringStrategyUtil.simplePluralize("roof"));
	}

}