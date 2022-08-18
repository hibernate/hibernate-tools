package org.hibernate.tool.orm.jbt.type;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class CharacterTypeTest {
	
	@Test
	public void testInstance() {
		assertNotNull(CharacterType.INSTANCE);
	}
	
	@Test
	public void testGetName() {
		assertEquals("character", CharacterType.INSTANCE.getName());
	}
	
	@Test
	public void testGetRegistrationKeys() {
		assertArrayEquals(
				new String[] {
					"character",
					"char",
					"java.lang.Character"
				},
				CharacterType.INSTANCE.getRegistrationKeys());
	}

}
