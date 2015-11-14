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

    /**
     * @param mappings
     * @param coll
     */
    JDBCCollectionSecondPass(Mappings mappings, Collection coll) {
        super(mappings, coll);
    }

    /* (non-Javadoc)
     * @see org.hibernate.cfg.HbmBinder.SecondPass#secondPass(java.util.Map, java.util.Map)
     */
    public void secondPass(Map persistentClasses, Map inheritedMetas) throws MappingException {
        bindCollectionSecondPass(collection, persistentClasses, mappings, inheritedMetas);
    }

    public void doSecondPass(Map persistentClasses) throws MappingException {
    	Value element = collection.getElement();
    	DependantValue dep = null;
    	String oldFkName = null;
    	if(element instanceof DependantValue) {
			dep = (DependantValue)element;
    		oldFkName = dep.getForeignKeyName();
    		dep.setForeignKeyName("none"); // Workaround to avoid DependantValue to create foreignkey just because reference columns are not the same + no need to create keys already in the db!
    	}
    	super.doSecondPass(persistentClasses);
    	if(dep!=null) {
    		dep.setForeignKeyName(oldFkName);
    	}

    }
    
    private void bindCollectionSecondPass(
            Collection collection,
            java.util.Map persistentClasses,
            Mappings mappings,
            java.util.Map inheritedMetas) throws MappingException {

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
