package org.hibernate.tool.hbmlint.detector;

import org.hibernate.MappingException;
import org.hibernate.bytecode.cglib.BytecodeProviderImpl;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.Settings;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.hbmlint.Issue;
import org.hibernate.tool.hbmlint.IssueCollector;

public class InstrumentationDetector extends EntityModelDetector {
	
	public String getName() {
		return "instrument";
	}
	private boolean cglibEnabled;
	private boolean javassistEnabled;
	
	public void initialize(Configuration cfg, Settings settings) {
		super.initialize( cfg, settings );
		
		cglibEnabled = false;
		javassistEnabled = false;
		
		if(Environment.getBytecodeProvider() instanceof BytecodeProviderImpl) {
			cglibEnabled = true;
		} else if(Environment.getBytecodeProvider() instanceof org.hibernate.bytecode.javassist.BytecodeProviderImpl) {
			javassistEnabled = true;
		}		
	}
	
	public void visit(Configuration cfg, PersistentClass clazz, IssueCollector collector) {
		Class mappedClass;

		
		try {
			mappedClass = clazz.getMappedClass();
		} catch(MappingException me) {
			// ignore
			return;
		}

		if(clazz.isLazy()) {
			try {
				mappedClass.getConstructor( new Class[0] );
			}
			catch (SecurityException e) {
				// ignore
			}
			catch (NoSuchMethodException e) {
				collector.reportIssue(new Issue("LAZY_NO_DEFAULT_CONSTRUCTOR",Issue.NORMAL_PRIORITY, "lazy='true' set for '" + clazz.getEntityName() +"', but class has no default constructor." ));
				return;
			}

		} else if(cglibEnabled || javassistEnabled){
			Class[] interfaces = mappedClass.getInterfaces();
			boolean cglib = false;
			boolean javaassist = false;
			for (int i = 0; i < interfaces.length; i++) {
				Class intface = interfaces[i];				
				if(intface.getName().equals( "net.sf.cglib.transform.impl.InterceptFieldEnabled" )) {
					cglib = true;
				} else if(intface.getName().equals( "org.hibernate.bytecode.javassist.FieldHandled" )) {
					javaassist = true;
				} 							
			}
			
			if(cglibEnabled && !cglib) {
				collector.reportIssue( new Issue("LAZY_NOT_INSTRUMENTED", Issue.HIGH_PRIORITY, "'" + clazz.getEntityName() + "' has lazy='false', but its class '" + mappedClass.getName() + "' has not been instrumented with cglib") );
				return;
			} else if (javassistEnabled && !javaassist) {
				collector.reportIssue( new Issue("LAZY_NOT_INSTRUMENTED", Issue.HIGH_PRIORITY, "'" + clazz.getEntityName() + "' has lazy='false', but its class '" + mappedClass.getName() + "' has not been instrumented with javaassist") );
				return;
			} else {
				// unknown bytecodeprovider...can't really check for that.
			}
			
		}
	}
}
