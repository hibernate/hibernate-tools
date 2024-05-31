package org.hibernate.tool.orm.jbt.internal.factory;

import java.io.File;

import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.reveng.strategy.OverrideRepository;
import org.hibernate.tool.internal.reveng.strategy.TableFilter;
import org.hibernate.tool.orm.jbt.api.OverrideRepositoryWrapper;
import org.hibernate.tool.orm.jbt.api.RevengStrategyWrapper;
import org.hibernate.tool.orm.jbt.api.TableFilterWrapper;

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
		public RevengStrategyWrapper getReverseEngineeringStrategy(RevengStrategyWrapper res) {
			return RevengStrategyWrapperFactory.createRevengStrategyWrapper(
					overrideRepository.getReverseEngineeringStrategy((RevengStrategy)res.getWrappedObject()));
		}
		
		@Override
		public void addTableFilter(TableFilterWrapper tf) {
			overrideRepository.addTableFilter((TableFilter)tf.getWrappedObject());
		}

	}
	
}
