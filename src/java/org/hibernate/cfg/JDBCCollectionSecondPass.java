package org.hibernate.cfg;

import java.util.Map;

import org.hibernate.MappingException;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Value;

@SuppressWarnings("serial")
public class JDBCCollectionSecondPass extends CollectionSecondPass {

    JDBCCollectionSecondPass(Mappings mappings, Collection coll) {
        super(mappings, coll);
    }

    @SuppressWarnings("rawtypes")
	public void secondPass(Map persistentClasses, Map inheritedMetas) throws MappingException {
        bindCollectionSecondPass(collection, persistentClasses, mappings, inheritedMetas);
    }

    @SuppressWarnings("rawtypes")
	public void doSecondPass(Map persistentClasses) throws MappingException {
    	Value element = collection.getElement();
    	DependantValue elementDependantValue = null;
    	String oldElementForeignKeyName = null;
    	if(element instanceof DependantValue) {
			elementDependantValue = (DependantValue)element;
			oldElementForeignKeyName = elementDependantValue.getForeignKeyName();
    		elementDependantValue.setForeignKeyName("none"); // Workaround to avoid DependantValue to create foreignkey just because reference columns are not the same + no need to create keys already in the db!
    	}
    	Value key = collection.getKey();
    	DependantValue keyDependantValue = null;
    	String oldKeyForeignKeyName = null;
    	if (key instanceof DependantValue) {
    		keyDependantValue = (DependantValue)key;
    		oldKeyForeignKeyName = keyDependantValue.getForeignKeyName();
    		keyDependantValue.setForeignKeyName("none");
    	}
    	super.doSecondPass(persistentClasses);
    	if(elementDependantValue!=null) {
    		elementDependantValue.setForeignKeyName(oldElementForeignKeyName);
    	}
    	if (keyDependantValue != null) {
    		keyDependantValue.setForeignKeyName(oldKeyForeignKeyName);
    	}
    }
    
    private void bindCollectionSecondPass(
            Collection collection,
            Map<?,?> persistentClasses,
            Mappings mappings,
            Map<?,?> inheritedMetas) throws MappingException {

        if(collection.isOneToMany() ) {
            OneToMany oneToMany = (OneToMany) collection.getElement();
            PersistentClass persistentClass = mappings.getClass(oneToMany.getReferencedEntityName() );

            if (persistentClass==null) throw new MappingException(
                    "Association " + collection.getRole() + " references unmapped class: " + oneToMany.getReferencedEntityName()
                );

            oneToMany.setAssociatedClass(persistentClass); // Child
        }

    }

    
}
