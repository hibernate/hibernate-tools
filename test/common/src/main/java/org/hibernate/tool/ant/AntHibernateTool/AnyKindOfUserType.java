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
package org.hibernate.tool.ant.AntHibernateTool;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
/*
 * Just used for testing classpath loading for ant tasks!
 * Do not use as basis for your own usertypes!
 * 
 * Created on 25-Feb-2005
 *
 */
public class AnyKindOfUserType implements UserType {

	public int[] sqlTypes() {
		return new int[] { Types.INTEGER };
	}

	public Class<?> returnedClass() {
		return Integer.class;
	}

	public boolean equals(Object x, Object y) throws HibernateException {
		return x.equals(y);
	}

	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
			throws HibernateException, SQLException {
		return Integer.valueOf(rs.getInt(names[0]) );
	}

	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
			throws HibernateException, SQLException {
		st.setInt(index, ( (Integer)value).intValue() );

	}

	public Object deepCopy(Object value) throws HibernateException {
		return Integer.valueOf( ( (Integer)value).intValue() );
	}
	public Object assemble(Serializable cached, Object owner)
	throws HibernateException {
		return cached;
	}

	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	public Object replace(Object original, Object target, Object owner)
	throws HibernateException {
		return original;
	}

	public boolean isMutable() {
		return false;
	}

}
