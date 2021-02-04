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
package org.hibernate.tool.test.db;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	org.hibernate.tool.jdbc2cfg.ForeignKeys.TestCase.class,
	org.hibernate.tool.jdbc2cfg.Identity.TestCase.class,
	org.hibernate.tool.jdbc2cfg.Index.TestCase.class,
	org.hibernate.tool.jdbc2cfg.KeyPropertyCompositeId.TestCase.class,
	org.hibernate.tool.jdbc2cfg.ManyToMany.TestCase.class,
	org.hibernate.tool.jdbc2cfg.MetaData.TestCase.class,
	org.hibernate.tool.jdbc2cfg.NoPrimaryKey.TestCase.class,
	org.hibernate.tool.jdbc2cfg.OneToOne.TestCase.class,
	org.hibernate.tool.jdbc2cfg.OverrideBinder.TestCase.class,
	org.hibernate.tool.jdbc2cfg.Performance.TestCase.class,
	org.hibernate.tool.jdbc2cfg.PersistentClasses.TestCase.class,
	org.hibernate.tool.jdbc2cfg.RevEngForeignKey.TestCase.class,
	org.hibernate.tool.jdbc2cfg.SearchEscapeString.TestCase.class,
	org.hibernate.tool.jdbc2cfg.TernarySchema.TestCase.class,
	org.hibernate.tool.jdbc2cfg.Versioning.TestCase.class,
	org.hibernate.tool.stat.Statistics.TestCase.class
})
public class CommonTestSuite {}
