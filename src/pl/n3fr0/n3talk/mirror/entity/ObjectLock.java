package pl.n3fr0.n3talk.mirror.entity;

public class ObjectLock {

    public ObjectLock(String ownerUID) {
        this.ownerUID = ownerUID;
    }

    public String getOwnerUID() {
        return ownerUID;
    }

    public void setOwnerUID(String ownerUID) {
        this.ownerUID = ownerUID;
    }

    private String ownerUID;
}
