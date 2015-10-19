/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.tool.xml;

import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;

public final class XMLHelper {

	public static final EntityResolver DEFAULT_DTD_RESOLVER = new DTDEntityResolver();

	public static SAXReader createSAXReader(ErrorHandler errorHandler) {
		SAXReader saxReader = resolveSAXReader();
		saxReader.setEntityResolver(DEFAULT_DTD_RESOLVER);
		saxReader.setErrorHandler( errorHandler );
		return saxReader;
	}

	private static SAXReader resolveSAXReader() {
		SAXReader saxReader = new SAXReader();
		saxReader.setMergeAdjacentText( true );
		saxReader.setValidation( true );
		return saxReader;
	}

}
