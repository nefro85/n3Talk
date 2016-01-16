package pl.n3fr0.n3talk.mirror.entity;

@Mirror
public class Item extends MirrorBase implements IItem {

    public Item() {
        super();
    }

    public Item(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Property
    String name;
}
