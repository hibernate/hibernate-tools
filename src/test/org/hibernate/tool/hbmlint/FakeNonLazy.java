package org.hibernate.tool.hbmlint;

import org.hibernate.bytecode.internal.javassist.FieldHandled;
import org.hibernate.bytecode.internal.javassist.FieldHandler;


public class FakeNonLazy implements FieldHandled {

	long id;
	
	public FakeNonLazy(long id) {
		this.id = id;
	}

	public FieldHandler getFieldHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setFieldHandler(FieldHandler handler) {
		// TODO Auto-generated method stub
		
	}
}

