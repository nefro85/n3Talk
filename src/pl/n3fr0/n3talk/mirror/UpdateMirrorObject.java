package pl.n3fr0.n3talk.mirror;

import pl.n3fr0.n3talk.mirror.entity.MirrorListModificator;
import pl.n3fr0.n3talk.mirror.entity.MirrorMapModificator;
import pl.n3fr0.n3talk.mirror.entity.MirrorObject;
import pl.n3fr0.n3talk.mirror.entity.MirrorProperty;

public class UpdateMirrorObject {

    public UpdateMirrorObject(MirrorObject object, MirrorProperty[] properties) {
        this.object = object;
        this.properties = properties;
        task = Task.UPDATE_PROPERTY;
    }

    public UpdateMirrorObject(MirrorObject object, MirrorListModificator mlm) {
        this.object = object;
        this.mlm = mlm;
        task = Task.MODIF_LIST;
    }

    public UpdateMirrorObject(MirrorObject object, MirrorMapModificator mmm){
        this.object = object;
        this.mmm = mmm;
        task = Task.MODIF_MAP;
    }

    public UpdateMirrorObject() {
        
    }

    public MirrorObject getObject() {
        return object;
    }

    public void setObject(MirrorObject object) {
        this.object = object;
    }

    public MirrorProperty[] getProperties() {
        return properties;
    }

    public void setProperties(MirrorProperty[] properties) {
        this.properties = properties;
    }

    public MirrorListModificator getMirrorListModificator() {
        return mlm;
    }

    public void setMirrorListModificator(MirrorListModificator mlm) {
        this.mlm = mlm;
    }

    public MirrorMapModificator getMirrorMapModificator() {
        return mmm;
    }

    public void setMirrorMapModificator(MirrorMapModificator mmm) {
        this.mmm = mmm;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

    private MirrorObject object;
    private MirrorProperty[] properties;
    private MirrorListModificator mlm;
    private MirrorMapModificator mmm;
    private Task task;

    public enum Task {
        UPDATE_PROPERTY, MODIF_LIST, MODIF_MAP;
    }
}
