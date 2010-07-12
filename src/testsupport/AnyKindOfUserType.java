import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
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

	public Class returnedClass() {
		return Integer.class;
	}

	public boolean equals(Object x, Object y) throws HibernateException {
		return x.equals(y);
	}

	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
			throws HibernateException, SQLException {
		return new Integer(rs.getInt(names[0]) );
	}

	public void nullSafeSet(PreparedStatement st, Object value, int index)
			throws HibernateException, SQLException {
		st.setInt(index, ( (Integer)value).intValue() );

	}

	public Object deepCopy(Object value) throws HibernateException {
		return new Integer( ( (Integer)value).intValue() );
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
