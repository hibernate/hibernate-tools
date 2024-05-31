package org.hibernate.tool.orm.jbt.internal.factory;

import java.util.Iterator;
import java.util.Properties;

import org.hibernate.FetchMode;
import org.hibernate.mapping.Any;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.Fetchable;
import org.hibernate.mapping.IdentifierBag;
import org.hibernate.mapping.IndexedCollection;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.List;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimitiveArray;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.api.ColumnWrapper;
import org.hibernate.tool.orm.jbt.api.PersistentClassWrapper;
import org.hibernate.tool.orm.jbt.api.PropertyWrapper;
import org.hibernate.tool.orm.jbt.api.TableWrapper;
import org.hibernate.tool.orm.jbt.api.TypeWrapper;
import org.hibernate.tool.orm.jbt.api.ValueWrapper;
import org.hibernate.tool.orm.jbt.internal.util.DelegatingPersistentClassWrapperImpl;

public class ValueWrapperFactory {

	public static ValueWrapper createValueWrapper(Value wrappedArrayValue) {
		return new ValueWrapperImpl(wrappedArrayValue);
	}
	
	private static class ValueWrapperImpl implements ValueWrapper {
		
		private Value value = null;
		
		private ValueWrapperImpl(Value value) {
			this.value = value;
		}
		
		@Override 
		public Value getWrappedObject() { 
			return value; 
		}
		
		@Override
		public boolean isSimpleValue() { 
			return value.isSimpleValue(); 
		}
		
		@Override
		public boolean isCollection() { 
			return Collection.class.isAssignableFrom(value.getClass()); }
		
		@Override
		public ValueWrapper getCollectionElement() {
			if (isCollection()) {
				Value v = ((Collection)value).getElement();
				if (v != null) return ValueWrapperFactory.createValueWrapper(v);
			}
			return null;
		}
		
		@Override
		public boolean isOneToMany() { 
			return OneToMany.class.isAssignableFrom(value.getClass()); 
		}
		
		@Override
		public boolean isManyToOne() { 
			return ManyToOne.class.isAssignableFrom(value.getClass()); 
		}
		
		@Override
		public boolean isOneToOne() { 
			return OneToOne.class.isAssignableFrom(value.getClass()); 
		}
		
		@Override
		public boolean isMap() { 
			return Map.class.isAssignableFrom(value.getClass()); 
		}
		
		@Override
		public boolean isComponent() { 
			return Component.class.isAssignableFrom(value.getClass()); 
		}
		
		@Override
		public boolean isEmbedded() { 
			if (isComponent()) {
				return ((Component)value).isEmbedded();
			}
			return false;
		}
		
		@Override
		public  boolean isToOne() { 
			return ToOne.class.isAssignableFrom(value.getClass()); 
		}
		
		
		@Override
		public  TableWrapper getTable() { 
			return value.getTable() == null ? null : TableWrapperFactory.createTableWrapper(value.getTable()); 
		}
		
		@Override
		public  TypeWrapper getType() { 
			return value.getType() == null ? null : TypeWrapperFactory.createTypeWrapper(value.getType()); 
		}
		
		@Override
		public  void setElement(ValueWrapper v) { 
			if (isCollection()) {
					((Collection)value).setElement((Value)v.getWrappedObject());
			}
		}
		
		@Override
		public  void setCollectionTable(TableWrapper table) {
			if (isCollection()) {
				Table t = table == null ? null : (Table)table.getWrappedObject();
				((Collection)value).setCollectionTable(t);
			}
		}
		
		@Override
		public  void setTable(TableWrapper table) {
			if (isSimpleValue()) {
				Table t = table == null ? null : (Table)table.getWrappedObject();
				((SimpleValue)value).setTable(t);
			}
		}
		
		@Override
		public  boolean isList() { 
			return List.class.isAssignableFrom(value.getClass()); 
		}
		
		@Override
		public  void setIndex(ValueWrapper v) {
			if (IndexedCollection.class.isAssignableFrom(value.getClass())) {
				Value val = v == null ? null : (Value)v.getWrappedObject();
				((IndexedCollection)value).setIndex(val);
			}
		}
		
		@Override
		public  void setTypeName(String s) { 
			if (isCollection()) {
				((Collection)value).setTypeName(s);
			} else if (isSimpleValue()) {
				((SimpleValue)value).setTypeName(s);
			}
		}
		
		@Override
		public  String getComponentClassName() { 
			if ((isComponent())) {
				return ((Component)value).getComponentClassName();
			}
			return null;
		}
		
		@Override
		public  Iterator<ColumnWrapper> getColumnIterator() {
			Iterator<Selectable> iterator = value.getSelectables().iterator();
			return new Iterator<ColumnWrapper>() {
				@Override
				public boolean hasNext() {
					return iterator.hasNext();
				}
				@Override
				public ColumnWrapper next() {
					return ColumnWrapperFactory.createColumnWrapper((Column)iterator.next());
				}
				
			};
		}
		
		@Override
		public  boolean isTypeSpecified() { 
			if (isSimpleValue()) {
				return ((SimpleValue)value).isTypeSpecified();
			} else {
				throw new UnsupportedOperationException("Class '" + value.getClass().getName() + "' does not support 'isTypeSpecified()'." );
			}
		}
		
		@Override
		public  TableWrapper getCollectionTable() {
			if (isCollection()) {
				Table t = ((Collection)value).getCollectionTable();
				return t == null ? null : TableWrapperFactory.createTableWrapper(t);
			} else {
				return null;
			}
		}
		
		@Override
		public  ValueWrapper getKey() { 
			if (isCollection()) {
				Value v = ((Collection)value).getKey();
				return v == null ? null : ValueWrapperFactory.createValueWrapper(v);
			} else {
				throw new UnsupportedOperationException("Class '" + value.getClass().getName() + "' does not support 'getKey()'." ); 
			}
		}
		
		@Override
		public  ValueWrapper getIndex() {
			if (IndexedCollection.class.isAssignableFrom(value.getClass())) {
				Value v = ((IndexedCollection)value).getIndex();
				return v == null ? null : ValueWrapperFactory.createValueWrapper(v);
			} else {
				return null;
			}
		}
		
		@Override
		public  String getElementClassName() {
			if (Array.class.isAssignableFrom(value.getClass())) {
				return ((Array)value).getElementClassName();
			} else {
				throw new UnsupportedOperationException("Class '" + value.getClass().getName() + "' does not support 'getElementClassName()'." );
			}
		}
		
		@Override
		public  String getTypeName() { 
			if (isCollection()) {
				return ((Collection)value).getTypeName();
			} else if (isSimpleValue()) {
				return ((SimpleValue)value).getTypeName();
			} else { 
				return null; 
			}
		}
		
		@Override
		public  boolean isDependantValue() { 
			return DependantValue.class.isAssignableFrom(value.getClass()); 
		}
		
		@Override
		public  boolean isAny() {
			return Any.class.isAssignableFrom(value.getClass()); 
		}
		
		@Override
		public  boolean isSet() {
			return Set.class.isAssignableFrom(value.getClass()); 
		}
		
		@Override
		public  boolean isPrimitiveArray() {
			return PrimitiveArray.class.isAssignableFrom(value.getClass()); 
		}
		
		@Override
		public  boolean isArray() {
			return Array.class.isAssignableFrom(value.getClass()); 
		}
		
		@Override
		public  boolean isIdentifierBag() {
			return IdentifierBag.class.isAssignableFrom(value.getClass()); 
		}
		
		@Override
		public  boolean isBag() {
			return Bag.class.isAssignableFrom(value.getClass()); 
		}
		
		@Override
		public  String getReferencedEntityName() { 
			if (isManyToOne() || isOneToOne()) {
				return ((ToOne)value).getReferencedEntityName();
			} else if (isOneToMany()) {
				return ((OneToMany)value).getReferencedEntityName();
			} else {
				throw new UnsupportedOperationException("Class '" + value.getClass().getName() + "' does not support 'getReferencedEntityName()'." ); 
			}
		}
		
		@Override
		public  String getEntityName() { 
			if (isOneToOne()) {
				return ((OneToOne)value).getEntityName();
			} else {
				throw new UnsupportedOperationException("Class '" + value.getClass().getName() + "' does not support 'getEntityName()'." ); }
		}
		
		@Override
		public  Iterator<PropertyWrapper> getPropertyIterator() { 
			if (isComponent()) {
				Iterator<Property> iterator = ((Component)value).getProperties().iterator();
				return new Iterator<PropertyWrapper>() {
					@Override
					public boolean hasNext() {
						return iterator.hasNext();
					}
					@Override
					public PropertyWrapper next() {
						return PropertyWrapperFactory.createPropertyWrapper(iterator.next());
					}		
				};
			} else {
				throw new UnsupportedOperationException("Class '" + value.getClass().getName() + "' does not support 'getPropertyIterator()'." ); 
			}
		}
		
		@Override
		public  void addColumn(ColumnWrapper column) { 
			if (isSimpleValue() && column != null) {
				((SimpleValue)value).addColumn((Column)column.getWrappedObject());
			} else {
				throw new UnsupportedOperationException("Class '" + value.getClass().getName() + "' does not support 'addColumn(Column)'." ); 
			}
		}
		
		@Override
		public  void setTypeParameters(Properties properties) {
			if (isCollection()) {
				((Collection)value).setTypeParameters(properties);
			} else if (isSimpleValue()) {
				((SimpleValue)value).setTypeParameters(properties);
			} else {
				throw new UnsupportedOperationException("Class '" + value.getClass().getName() + "' does not support 'setTypeParameters(Properties)'." );		
			}
		}
		
		@Override
		public  String getForeignKeyName() { 
			if (isSimpleValue()) {
				return ((SimpleValue)value).getForeignKeyName();
			} else {
				throw new UnsupportedOperationException("Class '" + value.getClass().getName() + "' does not support 'getForeignKeyName()'." ); 
			}
		}
		
		@Override
		public  PersistentClassWrapper getOwner() { 
			PersistentClass owner = null;
			if (isCollection()) {
				owner = ((Collection)value).getOwner();
				return owner == null ? null : new DelegatingPersistentClassWrapperImpl(owner);
			} else if (isComponent()) {
				owner = ((Component)value).getOwner();
				return owner == null ? null : new DelegatingPersistentClassWrapperImpl(owner);
			} else {
				throw new UnsupportedOperationException("Class '" + value.getClass().getName() + "' does not support 'getOwner()'." ); 
			}
		}
		
		@Override
		public  ValueWrapper getElement() { 
			if (isCollection()) {
				Value v = ((Collection)value).getElement();
				return v == null ? null : ValueWrapperFactory.createValueWrapper(v);
			} else {
				return null; 
			}
		}
		
		@Override
		public  String getParentProperty() { 
			if (isComponent()) {
				return ((Component)value).getParentProperty();
			} else {
				throw new UnsupportedOperationException("Class '" + value.getClass().getName() + "' does not support 'getParentProperty()'." ); 
			}
		}
		
		@Override
		public  void setElementClassName(String name) { 
			if (isArray()) {
				((Array)value).setElementClassName(name);
			} else {
				throw new UnsupportedOperationException("Class '" + value.getClass().getName() + "' does not support 'setElementClassName(String)'." ); 
			}
		}
		
		@Override
		public  void setKey(ValueWrapper v) {
			if (isCollection()) {
				KeyValue val = v == null ? null : (KeyValue)v.getWrappedObject();
				((Collection)value).setKey(val);
			} else {
				 throw new UnsupportedOperationException("Class '" + v.getClass().getName() + "' does not support 'setKey(KeyValue)'." );
			}
		}
		
		@Override
		public  void setFetchModeJoin() {
			if (Fetchable.class.isAssignableFrom(value.getClass())) {
				((Fetchable)value).setFetchMode(FetchMode.JOIN);
			} else {
				throw new UnsupportedOperationException("Class '" + value.getClass().getName() + "' does not support 'setFetchModeJoin()'." ); 
			}
		}
		
		@Override
		public  boolean isInverse() { 
			if (isCollection()) {
				return ((Collection)value).isInverse();
			} else {
				throw new UnsupportedOperationException("Class '" + value.getClass().getName() + "' does not support 'isInverse()'." ); 
			}
		}
		
		@Override
		public  PersistentClassWrapper getAssociatedClass() { 
			if (isOneToMany()) {
				PersistentClass pc = ((OneToMany)value).getAssociatedClass();
				return pc == null ? null : new DelegatingPersistentClassWrapperImpl(pc);
			} else {
			throw new UnsupportedOperationException("Class '" + value.getClass().getName() + "' does not support 'getAssociatedClass()'." ); 
			}
		}
		
		@Override
		public  void setLazy(boolean b) { 
			if (Fetchable.class.isAssignableFrom(value.getClass())) {
				((Fetchable)value).setLazy(b);
			} else if (isAny()) {
				((Any)value).setLazy(b);
				
			} else {
				throw new UnsupportedOperationException("Class '" + value.getClass().getName() + "' does not support 'setLazy(boolean)'." ); 
			}
		}
		
		@Override
		public  void setRole(String role) { 
			if (isCollection()) {
				((Collection)value).setRole(role);
			} else {
				throw new UnsupportedOperationException("Class '" + value.getClass().getName() + "' does not support 'setRole(String)'." ); 
			}
		}
		
		@Override
		public  void setReferencedEntityName(String name) { 
			if (isToOne()) {
				((ToOne)value).setReferencedEntityName(name); 
			} else if (isOneToMany()) {
				((OneToMany)value).setReferencedEntityName(name);
			} else {
				throw new UnsupportedOperationException("Class '" + value.getClass().getName() + "' does not support 'setReferencedEntityName(String)'." );
			}
		}
		@Override
		public  void setAssociatedClass(PersistentClassWrapper pcw) {
			if (isOneToMany()) {
				PersistentClass pc = pcw == null ? null : (PersistentClass)pcw.getWrappedObject();
				((OneToMany)value).setAssociatedClass(pc);
			} else {
				throw new UnsupportedOperationException("Class '" + value.getClass().getName() + "' does not support 'setAssociatedClass(PersistentClass)'." );
			}
		}
	}

}
