package org.hibernate.tool.orm.jbt.wrp;

import java.util.Iterator;

import org.hibernate.mapping.Join;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;

public class DelegatingPersistentClassWrapperImpl extends RootClass implements PersistentClassWrapper {
	
	private PersistentClass delegate = null;

	public DelegatingPersistentClassWrapperImpl(PersistentClass pc) {
		super(DummyMetadataBuildingContext.INSTANCE);
		delegate = pc;
	}
	
	@Override
	public PersistentClass getWrappedObject() { 
		return delegate; 
	}

	@Override 
	public String getEntityName() {
		return delegate.getEntityName();
	}
	
	@Override 
	public String getClassName() {
		return delegate.getClassName();
	}

	@Override 
	public 	Property getIdentifierProperty() {
		return wrapPropertyIfNeeded(delegate.getIdentifierProperty());
	}

	@Override 
	public boolean hasIdentifierProperty() {
		return delegate.hasIdentifierProperty();
	}

	@Override 
	public RootClass getRootClass() {
		RootClass result = delegate.getRootClass();
		if (result == delegate) {
			return this;
		} else {
			return result == null ? null : new DelegatingPersistentClassWrapperImpl(result);
		}
	}

	@Override 
	public Iterator<Property> getPropertyClosureIterator() {
		return getPropertyIterator();
	}

	@Override 
	public PersistentClass getSuperclass() {
		return delegate.getSuperclass();
	}
	
	@Override 
	public Iterator<Property> getPropertyIterator() {
		final Iterator<Property> iterator = delegate.getProperties().iterator();
		return new Iterator<Property>() {
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}
			@Override
			public Property next() {
				return PropertyWrapperFactory.createPropertyWrapper(iterator.next());
			}
			
		};
	}

	@Override 
	public Property getProperty(String name) {
		return wrapPropertyIfNeeded(delegate.getProperty(name));
	}

	@Override 
	public Table getTable() {
		Table result = delegate.getTable();
		return result == null ? null : new DelegatingTableWrapperImpl(result);
	}

	@Override 
	public Boolean isAbstract() {
		return delegate.isAbstract();
	}

	@Override 
	public Value getDiscriminator() {
		return wrapValueIfNeeded(delegate.getDiscriminator());
	}

	@Override 
	public KeyValue getIdentifier() {
		return (KeyValue)wrapValueIfNeeded(delegate.getIdentifier());
	}

	@Override 
	public Iterator<Join> getJoinIterator() {
		return delegate.getJoins().iterator();
	}

	@Override 
	public Property getVersion() {
		return wrapPropertyIfNeeded(delegate.getVersion());
	}

	@Override 
	public void setClassName(String name) {
		delegate.setClassName(name);
	}

	@Override 
	public void setEntityName(String name) {
		delegate.setEntityName(name);
	}

	@Override 
	public void setDiscriminatorValue(String str) {
		delegate.setDiscriminatorValue(str);
	}

	@Override 
	public void setAbstract(Boolean b) {
		delegate.setAbstract(b);
	}

	@Override 
	public void addProperty(Property p) {
		delegate.addProperty(p);
	}

	@Override 
	public void setProxyInterfaceName(String name) {
		delegate.setProxyInterfaceName(name);
	}

	@Override 
	public void setLazy(boolean b) {
		delegate.setLazy(b);
	}

	@Override 
	public Iterator<Subclass> getSubclassIterator() {
		return delegate.getSubclasses().iterator();
	}

	@Override 
	public boolean isCustomDeleteCallable() {
		return delegate.isCustomDeleteCallable();
	}

	@Override 
	public boolean isCustomInsertCallable() {
		return delegate.isCustomInsertCallable();
	}

	@Override 
	public boolean isCustomUpdateCallable() {
		return delegate.isCustomUpdateCallable();
	}

	@Override 
	public boolean isDiscriminatorInsertable() {
		return delegate.isDiscriminatorInsertable();
	}

	@Override 
	public boolean isDiscriminatorValueNotNull() {
		return delegate.isDiscriminatorValueNotNull();
	}

	@Override 
	public boolean isDiscriminatorValueNull() {
		return delegate.isDiscriminatorValueNull();
	}

	@Override 
	public boolean isExplicitPolymorphism() {
		return delegate.isExplicitPolymorphism();
	}

	@Override 
	public boolean isForceDiscriminator() {
		return delegate.isForceDiscriminator();
	}

	@Override 
	public boolean isInherited() {
		return delegate.isInherited();
	}

	@Override 
	public boolean isJoinedSubclass() {
		return delegate.isJoinedSubclass();
	}

	@Override 
	public boolean isLazy() {
		return delegate.isLazy();
	}

	@Override 
	public boolean isMutable() {
		return delegate.isMutable();
	}

	@Override 
	public boolean isPolymorphic() {
		return delegate.isPolymorphic();
	}

	@Override 
	public boolean isVersioned() {
		return delegate.isVersioned();
	}

	@Override 
	public int getBatchSize() {
		return delegate.getBatchSize();
	}

	@Override 
	public String getCacheConcurrencyStrategy() {
		return delegate.getCacheConcurrencyStrategy();
	}

	@Override 
	public String getCustomSQLDelete() {
		return delegate.getCustomSQLDelete();
	}

	@Override 
	public String getCustomSQLInsert() {
		return delegate.getCustomSQLInsert();
	}

	@Override 
	public String getCustomSQLUpdate() {
		return delegate.getCustomSQLUpdate();
	}

	@Override 
	public String getDiscriminatorValue() {
		return delegate.getDiscriminatorValue();
	}

	@Override 
	public String getLoaderName() {
		return delegate.getLoaderName();
	}

	@Override 
	public int getOptimisticLockMode() {
		return delegate.getOptimisticLockMode();
	}

	@Override 
	public String getWhere() {
		return delegate.getWhere();
	}

	@Override 
	public Table getRootTable() {
		return delegate.getRootTable();
	}
	
	
	private static Value wrapValueIfNeeded(Value v) {
		return (v != null) && !(v instanceof Wrapper) ? ValueWrapperFactory.createValueWrapper(v) : v;
	}

	private static Property wrapPropertyIfNeeded(Property p) {
		return (p != null) && !(p instanceof Wrapper) ? PropertyWrapperFactory.createPropertyWrapper(p) : p;
	}
}
