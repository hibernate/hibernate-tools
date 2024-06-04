package org.hibernate.tool.orm.jbt.internal.factory;

import java.util.Iterator;
import java.util.List;

import org.hibernate.mapping.Join;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.api.PersistentClassWrapper;
import org.hibernate.tool.orm.jbt.util.SpecialRootClass;

public class PersistentClassWrapperFactory {
	
	public static PersistentClassWrapper createPersistentClassWrapper(PersistentClass wrappedPersistentClass) {
		return new PersistentClassWrapperImpl(wrappedPersistentClass);
	}
	
	private static class PersistentClassWrapperImpl implements PersistentClassWrapper {
		
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
		public Property getProperty() {
			if (!isInstanceOfSpecialRootClass()) {
				throw new RuntimeException("getProperty() is only allowed on SpecialRootClass");
			}
			return ((SpecialRootClass)persistentClass).getProperty();
		}

		@Override
		public void setTable(Table table) {
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
		public void setIdentifier(Value value) {
			if (!isInstanceOfRootClass()) {
				throw new RuntimeException("Method 'setIdentifier(Value)' can only be called on RootClass instances");
			}
			((RootClass)persistentClass).setIdentifier((KeyValue)value);
		}

		@Override
		public void setKey(Value value) {
			if (!isInstanceOfJoinedSubclass()) {
				throw new RuntimeException("setKey(Value) is only allowed on JoinedSubclass");
			}
			((JoinedSubclass)persistentClass).setKey((KeyValue)value);
		}

		@Override
		public boolean isInstanceOfSpecialRootClass() {
			return SpecialRootClass.class.isAssignableFrom(persistentClass.getClass()); 
		}

		@Override
		public Property getParentProperty() {
			if (!isInstanceOfSpecialRootClass()) {
				throw new RuntimeException("getParentProperty() is only allowed on SpecialRootClass");
			}
			return ((SpecialRootClass)persistentClass).getParentProperty();
		}

		@Override
		public void setIdentifierProperty(Property property) {
			if (!isInstanceOfRootClass()) {
				throw new RuntimeException("setIdentifierProperty(Property) is only allowed on RootClass instances");
			}
			((RootClass)persistentClass).setIdentifierProperty(property);
		}

		@Override
		public void setDiscriminator(Value value) {
			if (!isInstanceOfRootClass()) {
				throw new RuntimeException("Method 'setDiscriminator(Value)' can only be called on RootClass instances"); 
			}
			((RootClass)persistentClass).setDiscriminator(value);
		}

		@Override
		public boolean isLazyPropertiesCacheable() {
			if (!isInstanceOfRootClass()) {
				throw new RuntimeException("Method 'isLazyPropertiesCacheable()' can only be called on RootClass instances");
			}
			return ((RootClass)persistentClass).isLazyPropertiesCacheable();
		}

		@Override
		public Iterator<Property> getPropertyIterator() {
			return getProperties().iterator();
		}

		@Override
		public Iterator<Join> getJoinIterator() {
			return getJoins().iterator();
		}

		@Override
		public Iterator<Subclass> getSubclassIterator() {
			return getSubclasses().iterator(); 
		}

		@Override
		public Iterator<Property> getPropertyClosureIterator() {
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
		public Property getIdentifierProperty() {
			return persistentClass.getIdentifierProperty();
		}

		@Override
		public boolean hasIdentifierProperty() {
			return persistentClass.hasIdentifierProperty();
		}

		@Override
		public PersistentClass getRootClass() {
			return persistentClass.getRootClass();
		}

		@Override
		public PersistentClass getSuperclass() {
			return persistentClass.getSuperclass();
		}

		@Override
		public Property getProperty(String name) {
			return persistentClass.getProperty(name);
		}

		@Override
		public Table getTable() {
			return persistentClass.getTable();
		}

		@Override
		public Boolean isAbstract() {
			return persistentClass.isAbstract();
		}

		@Override
		public Value getDiscriminator() {
			return persistentClass.getDiscriminator();
		}

		@Override
		public Value getIdentifier() {
			return persistentClass.getIdentifier();
		}

		@Override
		public Property getVersion() {
			return persistentClass.getVersion();
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
		public void addProperty(Property p) {
			persistentClass.addProperty(p);
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
			return persistentClass.isExplicitPolymorphism();
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
			return persistentClass.getOptimisticLockMode();
		}

		@Override
		public String getWhere() {
			return persistentClass.getWhere();
		}

		@Override
		public Table getRootTable() {
			return persistentClass.getRootTable();
		}

		@Override
		public List<Property> getProperties() {
			return persistentClass.getProperties();
		}

		@Override
		public List<Join> getJoins() {
			return persistentClass.getJoins();
		}

		@Override
		public List<Subclass> getSubclasses() {
			return persistentClass.getSubclasses();
		}

		@Override
		public List<Property> getPropertyClosure() {
			return persistentClass.getPropertyClosure();
		}
		
	}
	
}
