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

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    public @interface IgnoreIfDatabaseOffline {
        public boolean value();
    }
    
    boolean ignore = false;
	
	public DbSuite(Class<?> klass, RunnerBuilder builder) throws InitializationError {
		super(klass, builder);
		ignore = ignoreIfDatabaseOffline(klass);
	}
	
    @Override
    protected void runChild(Runner runner, final RunNotifier notifier) {
    	if (!ignore) {
    		runner.run(notifier);
    	} else {
    		notifier.fireTestIgnored(getDescription());
    	}
    }
    
    private boolean ignoreIfDatabaseOffline(Class<?> klass) {
    	IgnoreIfDatabaseOffline annotation = klass.getAnnotation(IgnoreIfDatabaseOffline.class);
    	if (annotation != null) {
    		return annotation.value();
    	} else {
    		return false;
    	}
    }

}
