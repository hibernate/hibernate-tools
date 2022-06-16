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

package org.hibernate.tool.hbm2x.Hbm2JavaTest;

import java.io.Serializable;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

public class DummyDateType implements UserType<Date> {

	public int[] sqlTypes() {
		return new int[]{Types.DATE};
	}

	public Class<Date> returnedClass() {
		return java.sql.Date.class;
	}

	public boolean equals(Date x, Date y) throws HibernateException {
		return false;
	}

	public int hashCode(Date x) throws HibernateException {
		return 0;
	}

	public Date deepCopy(Date value) throws HibernateException {
		return null;
	}

	public boolean isMutable() {
		return false;
	}

	public Serializable disassemble(Date value) throws HibernateException {
		return null;
	}

	public Date assemble(Serializable cached, Object owner) 
			throws HibernateException {
		return null;
	}

	public Date replace(
			Date original, 
			Date target, 
			Object owner) throws HibernateException {
		return null;
	}
	
	//@Override
	public void nullSafeSet(
			PreparedStatement st, 
			Date value, 
			int index,
			SharedSessionContractImplementor session) throws HibernateException, SQLException {}

	@Override
	public Date nullSafeGet(
			ResultSet rs, 
			int position, 
			SharedSessionContractImplementor session, 
			Object owner)
					throws SQLException {
		return null;
	}

	@Override
	public int getSqlType() {
		return 0;
	}

}
