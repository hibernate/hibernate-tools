package org.hibernate.tools.test.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

public class DbSuite extends Suite {
	
	public static final String SQL_SCRIPT_ROOT = "org.hibernate.tools.test.db.sqlScriptRoot";

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    public @interface SqlScriptRoot {
        public String value();
    }
    
    boolean ignore = false;
    String sqlScriptRoot;
	
	public DbSuite(Class<?> klass, RunnerBuilder builder) throws InitializationError {
		super(klass, builder);
		ignore = !JdbcUtil.isDatabaseOnline();
		setSqlScriptRoot(klass);
	}
	
    @Override
    protected void runChild(Runner runner, final RunNotifier notifier) {
    	if (!ignore) {
    		if (sqlScriptRoot != null) {
    			System.setProperty(
    					SQL_SCRIPT_ROOT, 
    					sqlScriptRoot);
    		}
    		runner.run(notifier);
    		if (sqlScriptRoot != null) {
    			System.getProperties().remove(
    					SQL_SCRIPT_ROOT);
    		}
    	} else {
    		notifier.fireTestIgnored(getDescription());
    	}
    }
    
    private void setSqlScriptRoot(Class<?> klass) {
    	SqlScriptRoot annotation = klass.getAnnotation(SqlScriptRoot.class);
    	if (annotation != null) {
    		sqlScriptRoot = annotation.value();
    	}
    }

}
