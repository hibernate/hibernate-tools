package org.hibernate.tool.orm.jbt.type;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.AdjustableBasicType;
import org.hibernate.type.descriptor.java.CharacterJavaType;
import org.hibernate.type.descriptor.jdbc.CharJdbcType;

public class CharacterType extends AbstractSingleColumnStandardBasicType<Character>
		implements AdjustableBasicType<Character> {

	public static final CharacterType INSTANCE = new CharacterType();

	public CharacterType() {
		super(CharJdbcType.INSTANCE, CharacterJavaType.INSTANCE);
	}

	public String getName() {
		return "character";
	}

	@Override
	public String[] getRegistrationKeys() {
		return new String[] { getName(), char.class.getName(), Character.class.getName() };
	}

}
