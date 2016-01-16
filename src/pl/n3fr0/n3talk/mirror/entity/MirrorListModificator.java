package pl.n3fr0.n3talk.mirror.entity;

public interface MirrorListModificator {
    String getObjectUID();
    String getName();
    Kind getKind();
    Object getObject();

    enum Kind {
        ADD, REMOVE;
    }
}