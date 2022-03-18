/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2017-2020 Red Hat, Inc.
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
package org.hibernate.tools.test.util;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Properties;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.dialect.DatabaseVersion;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;

public class HibernateUtil {
	
	public static class Dialect extends org.hibernate.dialect.Dialect {
		public Dialect() {
			super((DatabaseVersion)null);
		}
	}
	
	public static class ConnectionProvider 
			extends org.hibernate.tools.test.util.internal.ConnectionProvider {
		private static final long serialVersionUID = 1L;
	}
	
	public static ForeignKey getForeignKey(Table table, String fkName) {
		ForeignKey result = null;
		for (ForeignKey fk : table.getForeignKeys().values()) {
			if (fk.getName().equals(fkName)) {
				result = fk;
				break;
			}
		}
		return result;
	}
	
	public static Table getTable(Metadata metadata, String tabName) {
		if (metadata != null) {
			Iterator<Table> iter = metadata.collectTableMappings().iterator();
			while (iter.hasNext()) {
				Table table = (Table) iter.next();
				if (table.getName().equals(tabName)) {
					return table;
				}
			}
		}
		return null;
	}
	
	public static MetadataDescriptor initializeMetadataDescriptor(
			Object test, 
			String[] hbmResourceNames, 
			File hbmFileDir) {
		ResourceUtil.createResources(test, hbmResourceNames, hbmFileDir);
		File[] hbmFiles = new File[hbmResourceNames.length];
		for (int i = 0; i < hbmResourceNames.length; i++) {
			hbmFiles[i] = new File(hbmFileDir, hbmResourceNames[i]);
		}
		Properties properties = new Properties();
		properties.put(AvailableSettings.DIALECT, HibernateUtil.Dialect.class.getName());
		properties.setProperty(AvailableSettings.CONNECTION_PROVIDER, ConnectionProvider.class.getName());
		return MetadataDescriptorFactory.createNativeDescriptor(null, hbmFiles, properties);
	}
	
	public static void addAnnotatedClass(
			MetadataDescriptor metadataDescriptor, 
			Class<?> annotatedClass) {
		try {
			Field metadataSourcesField = metadataDescriptor
					.getClass()
					.getDeclaredField("metadataSources");
			metadataSourcesField.setAccessible(true);
			MetadataSources metadataSources = 
					(MetadataSources)metadataSourcesField.get(metadataDescriptor);
			metadataSources.addAnnotatedClass(annotatedClass);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
}
