package pl.n3fr0.n3talk.mirror.entity;

import java.util.List;

@Mirror
public class Box extends MirrorBase {

    public Box() {
        super();
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        setProperty(items);
    }

    @Adder(property = "items")
    public void addItem(Item item){
        addToList(items, item);
    }

    @Remover(property = "items")
    public void removeItem(Item item) {
        removeFromList(items, item);
    }

    @Property
    List<Item> items;
}
