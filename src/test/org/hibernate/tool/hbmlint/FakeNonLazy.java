package org.hibernate.tool.hbmlint;

import net.sf.cglib.transform.impl.InterceptFieldCallback;
import net.sf.cglib.transform.impl.InterceptFieldEnabled;

import org.hibernate.bytecode.javassist.FieldHandled;
import org.hibernate.bytecode.javassist.FieldHandler;

public class FakeNonLazy implements InterceptFieldEnabled, FieldHandled {

	long id;
	
	public FakeNonLazy(long id) {
		this.id = id;
	}

	public InterceptFieldCallback getInterceptFieldCallback() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setInterceptFieldCallback(InterceptFieldCallback callback) {
		// TODO Auto-generated method stub
		
	}

	public FieldHandler getFieldHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setFieldHandler(FieldHandler handler) {
		// TODO Auto-generated method stub
		
	}
}

