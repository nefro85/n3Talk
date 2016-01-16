package pl.n3fr0.n3talk.mirror;

import pl.n3fr0.n3talk.mirror.entity.MirrorBase;

public interface MirrorContainerListener {
    void onChange();
    void objectCreated(MirrorBase base);
    void objectUpdated(MirrorBase base);
    void objectDeleted(MirrorBase base);
}
