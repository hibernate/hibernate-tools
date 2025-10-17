/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2018-2020 Red Hat, Inc.
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
package org.hibernate.tool.api.reveng;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.net.URISyntaxException;

import javax.management.RuntimeErrorException;

import org.hibernate.id.insert.GetGeneratedKeysDelegate;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.hibernate.tool.internal.reveng.strategy.DelegatingStrategy;
import org.junit.jupiter.api.Test;


public class ReverseEngineeringStrategyFactoryTest {
	
	@Test
	public void testCreateReverseEngineeringStrategy() {
		RevengStrategy reverseEngineeringStrategy = 
				RevengStrategyFactory.createReverseEngineeringStrategy();
		assertNotNull(reverseEngineeringStrategy);
		assertEquals(
				DefaultStrategy.class.getName(), 
				reverseEngineeringStrategy.getClass().getName());
		reverseEngineeringStrategy = 
				RevengStrategyFactory.createReverseEngineeringStrategy(
						TestReverseEngineeringStrategyFactory.class.getName());
		assertNotNull(reverseEngineeringStrategy);
		assertEquals(
				TestReverseEngineeringStrategyFactory.class.getName(), 
				reverseEngineeringStrategy.getClass().getName());
		reverseEngineeringStrategy = 
				RevengStrategyFactory.createReverseEngineeringStrategy(null);
		assertEquals(
				DefaultStrategy.class.getName(), 
				reverseEngineeringStrategy.getClass().getName());		
		
		reverseEngineeringStrategy = 
				RevengStrategyFactory.createReverseEngineeringStrategy(TestDelegatingReverseEngineeringStrategyFactory.class.getName());
		assertEquals(
				TestDelegatingReverseEngineeringStrategyFactory.class.getName(), 
				reverseEngineeringStrategy.getClass().getName());
		assertEquals(DefaultStrategy.class.getName(), ((TestDelegatingReverseEngineeringStrategyFactory) reverseEngineeringStrategy).getDelegateTest().getClass().getName());

		
		try {
			File file = new File(this.getClass().getResource("/test.reveng.xml").toURI());
			reverseEngineeringStrategy = 
					RevengStrategyFactory.createReverseEngineeringStrategy(TestDelegatingReverseEngineeringStrategyFactory.class.getName(), new File[] {file});
			assertEquals(
					TestDelegatingReverseEngineeringStrategyFactory.class.getName(), 
					reverseEngineeringStrategy.getClass().getName());
			assertTrue(DelegatingStrategy.class.isAssignableFrom(((TestDelegatingReverseEngineeringStrategyFactory) reverseEngineeringStrategy).getDelegateTest().getClass()));
			// TODO this does not keep track of the eventually DefaultStrategy.
		} catch (URISyntaxException exception) {
			throw new RuntimeException("Unable to load /test.reveng.xml from test resources", exception);
		}

	}
	
	public static class TestReverseEngineeringStrategyFactory extends DefaultStrategy {}

	public static class TestDelegatingReverseEngineeringStrategyFactory extends DelegatingStrategy {

		private RevengStrategy delegateTest;

		public TestDelegatingReverseEngineeringStrategyFactory(RevengStrategy delegate) {
			super(delegate);
			this.delegateTest = delegate;
		}

		public RevengStrategy getDelegateTest() {
			return delegateTest;
		}

		public void setDelegateTest(RevengStrategy delegateTest) {
			this.delegateTest = delegateTest;
		}

	}
}
