
package persistentclasses;
import java.util.HashSet;
import java.util.Set;

/**
 * @author max
 * 
 */
public class Orders {

    Long id;

    String name;

    Set items = new HashSet();
    Set items_1 = new HashSet();
    

    /**
     * @return Returns the id.
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     *            The id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the setOfItem.
     */
    public Set getItemsForOrderId() {
        return items;
    }

    /**
     * @param items
     *            The setOfItem to set.
     */
    public void setItemsForOrderId(Set items) {
        this.items = items;
    }
    /**
     * @return Returns the setOfItem_1.
     */
    public Set getItemsForRelatedOrderId() {
        return items_1;
    }
    /**
     * @param items_1 The setOfItem_1 to set.
     */
    public void setItemsForRelatedOrderId(Set items_1) {
        this.items_1 = items_1;
    }
}
