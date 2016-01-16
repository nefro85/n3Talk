package pl.n3fr0.n3talk.mirror;

public interface ContainerStub {
    String getName();
    boolean hasQueue();
    MessageQueue getQueue();
    void setQueue(MessageQueue queue);
}
