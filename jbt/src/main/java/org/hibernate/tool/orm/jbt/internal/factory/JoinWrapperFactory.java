package org.hibernate.tool.orm.jbt.internal.factory;

import java.util.Iterator;

import org.hibernate.mapping.Join;
import org.hibernate.mapping.Property;
import org.hibernate.tool.orm.jbt.api.wrp.JoinWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.PropertyWrapper;
import org.hibernate.tool.orm.jbt.internal.wrp.AbstractWrapper;

public class JoinWrapperFactory {

	public static JoinWrapper createJoinWrapper(Join wrappedJoin) {
		return new JoinWrapperImpl(wrappedJoin);
	}
	
	private static class JoinWrapperImpl 
			extends AbstractWrapper
			implements JoinWrapper {
		
		private Join join = null;
		
		private JoinWrapperImpl(Join join) {
			this.join = join;
		}
		
		@Override 
		public Join getWrappedObject() { 
			return join; 
		}
		
		@Override
		public Iterator<PropertyWrapper> getPropertyIterator() {
			Iterator<Property> propertyIterator = join.getProperties().iterator();
			return new Iterator<PropertyWrapper>() {
				@Override
				public boolean hasNext() {
					return propertyIterator.hasNext();
				}

				@Override
				public PropertyWrapper next() {
					return PropertyWrapperFactory.createPropertyWrapper(propertyIterator.next());
				}
				
			};
		}
		
	}
	
}
