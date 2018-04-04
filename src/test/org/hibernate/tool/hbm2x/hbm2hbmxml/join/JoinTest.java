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
package org.hibernate.tool.hbm2x.hbm2hbmxml.join;

import java.io.File;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.NonReflectiveTestCase;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.util.MetadataHelper;

/**
 * @author koen
 */
public class JoinTest extends NonReflectiveTestCase {

	public JoinTest(String name) {
		super(name, "cfg2hbmoutput");
	}

	private Exporter hbmexporter = null;

	protected void setUp() throws Exception {
		super.setUp();
		hbmexporter = new HibernateMappingExporter(getCfg(), getOutputDir() );
		hbmexporter.start();
	}

	public void testReadable() {
        Configuration cfg = new Configuration();
        cfg.addFile(new File(getOutputDir(), getBaseForMappings() + "Parent.hbm.xml"));
        cfg.addFile(new File(getOutputDir(), getBaseForMappings() + "Child.hbm.xml"));
        MetadataHelper.getMetadata(cfg);
    }

	protected String getBaseForMappings() {
		return "org/hibernate/tool/hbm2x/hbm2hbmxml/join/";
	}

	@Override
	protected String[] getMappings() {
		return new String[] {
			"Parent.hbm.xml"
		};
	}
	
}
