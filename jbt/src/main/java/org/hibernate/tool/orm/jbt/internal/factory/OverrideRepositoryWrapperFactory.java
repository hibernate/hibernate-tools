package org.hibernate.tool.orm.jbt.internal.factory;

import java.io.File;

import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.reveng.strategy.OverrideRepository;
import org.hibernate.tool.internal.reveng.strategy.TableFilter;
import org.hibernate.tool.orm.jbt.api.OverrideRepositoryWrapper;

public class OverrideRepositoryWrapperFactory {

	public static OverrideRepositoryWrapper createOverrideRepositoryWrapper(OverrideRepository wrappedOverrideRepository) {
		return new OverrideRepositoryWrapperImpl(wrappedOverrideRepository);
	}
	
	public static class OverrideRepositoryWrapperImpl implements OverrideRepositoryWrapper {
		
		private OverrideRepository overrideRepository = null;
		
		private OverrideRepositoryWrapperImpl(OverrideRepository overrideRepository) {
			this.overrideRepository = overrideRepository;
		}
		
		@Override 
		public OverrideRepository getWrappedObject() { 
			return overrideRepository; 
		}
		
		@Override
		public void addFile(File file) {
			overrideRepository.addFile(file);
		}
		
		@Override
		public RevengStrategy getReverseEngineeringStrategy(RevengStrategy res) {
			return overrideRepository.getReverseEngineeringStrategy(res);
		}
		
		@Override
		public void addTableFilter(TableFilter tf) {
			overrideRepository.addTableFilter(tf);
		}

	}
	
}
