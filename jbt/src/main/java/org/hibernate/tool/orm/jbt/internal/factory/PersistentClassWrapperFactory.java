package org.hibernate.tool.orm.jbt.internal.factory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.AssertionFailure;
import org.hibernate.engine.OptimisticLockStyle;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.api.wrp.JoinWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.PersistentClassWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.PropertyWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.TableWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ValueWrapper;
import org.hibernate.tool.orm.jbt.internal.util.DummyMetadataBuildingContext;
import org.hibernate.tool.orm.jbt.internal.util.SpecialRootClass;
import org.hibernate.tool.orm.jbt.internal.wrp.AbstractWrapper;

public class PersistentClassWrapperFactory {
	
	public static PersistentClassWrapper createRootClassWrapper() {
		return createPersistentClassWrapper(new RootClass(DummyMetadataBuildingContext.INSTANCE));
	}
	
	public static Object createSingleTableSubClassWrapper(PersistentClassWrapper persistentClassWrapper) {
		PersistentClass pc = (PersistentClass)persistentClassWrapper.getWrappedObject();
		SingleTableSubclass sts = new SingleTableSubclass(pc, DummyMetadataBuildingContext.INSTANCE);
		return createPersistentClassWrapper(sts);
	}

	public static Object createJoinedTableSubClassWrapper(PersistentClassWrapper persistentClassWrapper) {
		PersistentClass pc = (PersistentClass)persistentClassWrapper.getWrappedObject();
		JoinedSubclass js = new JoinedSubclass(pc, DummyMetadataBuildingContext.INSTANCE);
		return createPersistentClassWrapper(js);
	}

	public static Object createSpecialRootClassWrapper(PropertyWrapper propertyWrapper) {
		Property p = (Property)propertyWrapper.getWrappedObject();
		SpecialRootClass src = new SpecialRootClass(p);
		return createPersistentClassWrapper(src);
	}

	public static PersistentClassWrapper createPersistentClassWrapper(PersistentClass wrappedPersistentClass) {
		return new PersistentClassWrapperImpl(wrappedPersistentClass);
	}
	
	private static class PersistentClassWrapperImpl 
			extends AbstractWrapper
			implements PersistentClassWrapper {
		
		private PersistentClass persistentClass = null;
		
		private PersistentClassWrapperImpl(PersistentClass persistentClass) {
			this.persistentClass = persistentClass;
		}
		
		@Override
		public PersistentClass getWrappedObject() {
			return persistentClass;
		}

		@Override
		public boolean isAssignableToRootClass() {
			return isInstanceOfRootClass();
		}

		@Override
		public boolean isRootClass() {
			return persistentClass.getClass() == RootClass.class;
		}

		@Override
		public boolean isInstanceOfRootClass() {
			return RootClass.class.isAssignableFrom(persistentClass.getClass());
		}

		@Override
		public boolean isInstanceOfSubclass() {
			return Subclass.class.isAssignableFrom(persistentClass.getClass());
		}

		@Override
		public boolean isInstanceOfJoinedSubclass() {
			return JoinedSubclass.class.isAssignableFrom(persistentClass.getClass());
		}

		@Override
		public PropertyWrapper getProperty() {
			if (!isInstanceOfSpecialRootClass()) {
				throw new RuntimeException("getProperty() is only allowed on SpecialRootClass");
			} else {
				Property p = ((SpecialRootClass)persistentClass).getProperty();
				return p == null ? null : PropertyWrapperFactory.createPropertyWrapper(p);
			}
		}

		@Override
		public void setTable(TableWrapper tableWrapper) {
			Table table = tableWrapper == null ? null : (Table)tableWrapper.getWrappedObject();
			if (isInstanceOfRootClass()) {
				((RootClass)persistentClass).setTable(table);
			} else if (isInstanceOfJoinedSubclass()) {
				((JoinedSubclass)persistentClass).setTable(table);
			} else if (isInstanceOfSpecialRootClass()) {
				((SpecialRootClass)persistentClass).setTable(table);
			} else {
				throw new RuntimeException("Method 'setTable(Table)' is not supported.");
			}
		}

		@Override
		public void setIdentifier(ValueWrapper value) {
			if (!isInstanceOfRootClass()) {
				throw new RuntimeException("Method 'setIdentifier(Value)' can only be called on RootClass instances");
			} else {
			}
			((RootClass)persistentClass).setIdentifier(value == null ? null : (KeyValue)value.getWrappedObject());
		}

		@Override
		public void setKey(ValueWrapper value) {
			if (!isInstanceOfJoinedSubclass()) {
				throw new RuntimeException("setKey(Value) is only allowed on JoinedSubclass");
			}
			((JoinedSubclass)persistentClass).setKey(value == null ? null : (KeyValue)value.getWrappedObject());
		}

		@Override
		public boolean isInstanceOfSpecialRootClass() {
			return SpecialRootClass.class.isAssignableFrom(persistentClass.getClass()); 
		}

		@Override
		public PropertyWrapper getParentProperty() {
			if (!isInstanceOfSpecialRootClass()) {
				throw new RuntimeException("getParentProperty() is only allowed on SpecialRootClass");
			} else {
				Property p = ((SpecialRootClass)persistentClass).getParentProperty();
				return p == null ? null : PropertyWrapperFactory.createPropertyWrapper(p);
			}
		}

		@Override
		public void setIdentifierProperty(PropertyWrapper p) {
			if (!isInstanceOfRootClass()) {
				throw new RuntimeException("setIdentifierProperty(Property) is only allowed on RootClass instances");
			}
			((RootClass)persistentClass).setIdentifierProperty(p == null ? null : (Property)p.getWrappedObject());
		}

		@Override
		public void setDiscriminator(ValueWrapper value) {
			if (!isInstanceOfRootClass()) {
				throw new RuntimeException("Method 'setDiscriminator(Value)' can only be called on RootClass instances"); 
			}
			((RootClass)persistentClass).setDiscriminator(value == null ? null : (Value)value.getWrappedObject());
		}

		@Override
		public boolean isLazyPropertiesCacheable() {
			if (!isInstanceOfRootClass()) {
				throw new RuntimeException("Method 'isLazyPropertiesCacheable()' can only be called on RootClass instances");
			}
			return ((RootClass)persistentClass).isLazyPropertiesCacheable();
		}

		@Override
		public Iterator<PropertyWrapper> getPropertyIterator() {
			return getProperties().iterator();
		}

		@Override
		public Iterator<JoinWrapper> getJoinIterator() {
			return getJoins().iterator();
		}

		@Override
		public Iterator<PersistentClassWrapper> getSubclassIterator() {
			return getSubclasses().iterator(); 
		}

		@Override
		public Iterator<PropertyWrapper> getPropertyClosureIterator() {
			return getPropertyClosure().iterator();
		}

		@Override
		public String getEntityName() {
			return persistentClass.getEntityName();
		}

		@Override
		public String getClassName() {
			return persistentClass.getClassName();
		}

		@Override
		public PropertyWrapper getIdentifierProperty() {
			Property p = persistentClass.getIdentifierProperty();
			return p == null ? null : PropertyWrapperFactory.createPropertyWrapper(p);
		}

		@Override
		public boolean hasIdentifierProperty() {
			return persistentClass.hasIdentifierProperty();
		}

		@Override
		public PersistentClassWrapper getRootClass() {
			PersistentClass pc = persistentClass.getRootClass();
			return pc == null ? null : PersistentClassWrapperFactory.createPersistentClassWrapper(pc);
		}

		@Override
		public PersistentClassWrapper getSuperclass() {
			PersistentClass pc = persistentClass.getSuperclass();
			return pc == null ? null : PersistentClassWrapperFactory.createPersistentClassWrapper(pc);
		}

		@Override
		public PropertyWrapper getProperty(String name) {
			Property p = persistentClass.getProperty(name);
			return p == null ? null : PropertyWrapperFactory.createPropertyWrapper(p);
		}

		@Override
		public TableWrapper getTable() {
			Table t = persistentClass.getTable();
			return t == null ? null : TableWrapperFactory.createTableWrapper(t);
		}

		@Override
		public Boolean isAbstract() {
			return persistentClass.isAbstract();
		}

		@Override
		public ValueWrapper getDiscriminator() {
			Value v = persistentClass.getDiscriminator();
			return v == null ? null : ValueWrapperFactory.createValueWrapper(v);
		}

		@Override
		public ValueWrapper getIdentifier() {
			Value v = persistentClass.getIdentifier();
			return v == null ? null : ValueWrapperFactory.createValueWrapper(v);
		}

		@Override
		public PropertyWrapper getVersion() {
			Property p = persistentClass.getVersion();
			return p == null ? null : PropertyWrapperFactory.createPropertyWrapper(p);
		}

		@Override
		public void setClassName(String name) {
			persistentClass.setClassName(name);
		}

		@Override
		public void setEntityName(String name) {
			persistentClass.setEntityName(name);
		}

		@Override
		public void setDiscriminatorValue(String str) {
			persistentClass.setDiscriminatorValue(str);
		}

		@Override
		public void setAbstract(Boolean b) {
			persistentClass.setAbstract(b);
		}

		@Override
		public void addProperty(PropertyWrapper p) {
			persistentClass.addProperty((Property)p.getWrappedObject());
		}

		@Override
		public void setProxyInterfaceName(String name) {
			persistentClass.setProxyInterfaceName(name);
		}

		@Override
		public void setLazy(boolean b) {
			persistentClass.setLazy(b);
		}

		@Override
		public boolean isCustomDeleteCallable() {
			return persistentClass.isCustomDeleteCallable();
		}

		@Override
		public boolean isCustomInsertCallable() {
			return persistentClass.isCustomInsertCallable();
		}

		@Override
		public boolean isCustomUpdateCallable() {
			return persistentClass.isCustomUpdateCallable();
		}

		@Override
		public boolean isDiscriminatorInsertable() {
			return persistentClass.isDiscriminatorInsertable();
		}

		@Override
		public boolean isDiscriminatorValueNotNull() {
			return persistentClass.isDiscriminatorValueNotNull();
		}

		@Override
		public boolean isDiscriminatorValueNull() {
			return persistentClass.isDiscriminatorValueNull();
		}

		@Override
		public boolean isExplicitPolymorphism() {
			return false;
		}

		@Override
		public boolean isForceDiscriminator() {
			return persistentClass.isForceDiscriminator();
		}

		@Override
		public boolean isInherited() {
			return persistentClass.isInherited();
		}

		@Override
		public boolean isJoinedSubclass() {
			return persistentClass.isJoinedSubclass();
		}

		@Override
		public boolean isLazy() {
			return persistentClass.isLazy();
		}

		@Override
		public boolean isMutable() {
			return persistentClass.isMutable();
		}

		@Override
		public boolean isPolymorphic() {
			return persistentClass.isPolymorphic();
		}

		@Override
		public boolean isVersioned() {
			return persistentClass.isVersioned();
		}

		@Override
		public int getBatchSize() {
			return persistentClass.getBatchSize();
		}

		@Override
		public String getCacheConcurrencyStrategy() {
			return persistentClass.getCacheConcurrencyStrategy();
		}

		@Override
		public String getCustomSQLDelete() {
			return persistentClass.getCustomSQLDelete();
		}

		@Override
		public String getCustomSQLInsert() {
			return persistentClass.getCustomSQLInsert();
		}

		@Override
		public String getCustomSQLUpdate() {
			return persistentClass.getCustomSQLUpdate();
		}

		@Override
		public String getDiscriminatorValue() {
			return persistentClass.getDiscriminatorValue();
		}

		@Override
		public String getLoaderName() {
			return persistentClass.getLoaderName();
		}

		@Override
		public int getOptimisticLockMode() {
			return getOldCode(persistentClass.getOptimisticLockStyle());
		}

		@Override
		public String getWhere() {
			return persistentClass.getWhere();
		}

		@Override
		public TableWrapper getRootTable() {
			Table t = persistentClass.getRootTable();
			return t == null ? null : TableWrapperFactory.createTableWrapper(t);
		}

		@Override
		public List<PropertyWrapper> getProperties() {
			List<PropertyWrapper> result = new ArrayList<PropertyWrapper>();
			for (Property p : persistentClass.getProperties()) {
				result.add(PropertyWrapperFactory.createPropertyWrapper(p));
			}
			return result;
		}

		@Override
		public List<JoinWrapper> getJoins() {
			List<JoinWrapper> result = new ArrayList<JoinWrapper>();
			for (Join j : persistentClass.getJoins()) {
				result.add(JoinWrapperFactory.createJoinWrapper(j));
			}
			return result;
		}

		@Override
		public List<PersistentClassWrapper> getSubclasses() {
			List<PersistentClassWrapper> result = new ArrayList<PersistentClassWrapper>();
			for (Subclass s : persistentClass.getSubclasses()) {
				result.add(PersistentClassWrapperFactory.createPersistentClassWrapper(s));
			}
			return result;
		}

		@Override
		public List<PropertyWrapper> getPropertyClosure() {
			List<PropertyWrapper> result = new ArrayList<PropertyWrapper>();
			for (Property p : persistentClass.getPropertyClosure()) {
				result.add(PropertyWrapperFactory.createPropertyWrapper(p));
			}
			return result;
		}
		
	}
	
	private static int getOldCode(OptimisticLockStyle ols) {
		switch (ols) {
			case NONE:
				return -1;
			case VERSION:
				return 0;
			case DIRTY:
				return 1;
			case ALL:
				return 2;
			default:
				throw new AssertionFailure("Unknown OptimisticLockStyle");
		}
	}
}
