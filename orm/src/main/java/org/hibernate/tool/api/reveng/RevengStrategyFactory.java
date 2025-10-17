package org.hibernate.tool.api.reveng;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.hibernate.tool.internal.reveng.strategy.DelegatingStrategy;
import org.hibernate.tool.internal.reveng.strategy.OverrideRepository;
import org.hibernate.tool.util.ReflectionUtil;

public class RevengStrategyFactory {
	
	private static final String DEFAULT_REVERSE_ENGINEERING_STRATEGY_CLASS_NAME = 
			DefaultStrategy.class.getName();
	
	public static RevengStrategy createReverseEngineeringStrategy(
			String reverseEngineeringClassName) {
		return createReverseEngineeringStrategy(reverseEngineeringClassName, (OverrideRepository) null);
	}

	public static RevengStrategy createReverseEngineeringStrategy(
			String reverseEngineeringClassName,
			File[] revengFiles) {
		RevengStrategy result = null;
		Class<?> revengClass = getRevengClass(reverseEngineeringClassName);
		if (!DelegatingStrategy.class.isAssignableFrom(revengClass)) {
			result = createReverseEngineeringStrategy(reverseEngineeringClassName);
		}
		if (revengFiles != null && revengFiles.length > 0) {
			OverrideRepository overrideRepository = new OverrideRepository();
			for (File file : revengFiles) {
				overrideRepository.addFile(file);
			}
			if (DelegatingStrategy.class.isAssignableFrom(revengClass)) {
				return createReverseEngineeringStrategy(reverseEngineeringClassName, overrideRepository);
			}
			result = overrideRepository.getReverseEngineeringStrategy(result);
		}
		return result;
	}

	private static Class<?> getRevengClass(String reverseEngineeringClassName) {
		try {
			return ReflectionUtil.classForName(
							reverseEngineeringClassName == null ? 
									DEFAULT_REVERSE_ENGINEERING_STRATEGY_CLASS_NAME : 
									reverseEngineeringClassName);
		} catch (ClassNotFoundException exception) {
			throw new RuntimeException("An exporter of class '" + reverseEngineeringClassName + "' could not be created", exception);
		}
	}
	
	public static RevengStrategy createReverseEngineeringStrategy(
			String reverseEngineeringClassName,
			OverrideRepository overrideRepository) {
		try {
			Class<?> reverseEngineeringClass = getRevengClass(reverseEngineeringClassName);
			if (DelegatingStrategy.class.isAssignableFrom(reverseEngineeringClass)) {
				Constructor<?>[] constructors = reverseEngineeringClass.getConstructors();
				Optional<Constructor<?>> doesHasDelegateParameter = Arrays.stream(constructors).filter(item -> item.getParameterCount() == 1 &&
						RevengStrategy.class.isAssignableFrom(item.getParameterTypes()[0]))
				.findFirst();
				if (doesHasDelegateParameter.isPresent()) {
					Constructor<?> reverseEngineeringConstructor = reverseEngineeringClass.getConstructor(new Class[] {RevengStrategy.class});
					if (Objects.nonNull(overrideRepository))
						return (RevengStrategy) reverseEngineeringConstructor.newInstance(overrideRepository.getReverseEngineeringStrategy(new DefaultStrategy()));
					else 
						return (RevengStrategy) reverseEngineeringConstructor.newInstance(new DefaultStrategy());
				}
			}

			Constructor<?> reverseEngineeringConstructor = reverseEngineeringClass.getConstructor(new Class[] {});
			return (RevengStrategy)reverseEngineeringConstructor.newInstance();
		} catch (IllegalAccessException | InstantiationException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException exception) {
			throw new RuntimeException("An exporter of class '" + reverseEngineeringClassName + "' could not be created", exception);
		}
	}
	
	public static RevengStrategy createReverseEngineeringStrategy() {
		return createReverseEngineeringStrategy(null);
	}
	
}

