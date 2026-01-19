package org.hibernate.tool.ant.JDBCConfigWithRevEngXml;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SomeUserType implements UserType<String> {

    @Override
    public int getSqlType() {
        return 0;
    }

    @Override
    public Class<String> returnedClass() {
        return null;
    }

    @Override
    public boolean equals(String s, String j1) {
        return false;
    }

    @Override
    public int hashCode(String s) {
        return 0;
    }

    @Override
    public String nullSafeGet(ResultSet resultSet, int i, SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws SQLException {
        return "";
    }

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, String s, int i, SharedSessionContractImplementor sharedSessionContractImplementor) throws SQLException {

    }

    @Override
    public String deepCopy(String value) {
        return null;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(String s) {
        return null;
    }

    @Override
    public String assemble(Serializable serializable, Object o) {
        return "";
    }

}
