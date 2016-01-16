package pl.n3fr0.n3talk.mirror;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import pl.n3fr0.n3talk.mirror.entity.MirrorBase;
import pl.n3fr0.n3talk.mirror.entity.MirrorProperty;
import pl.n3fr0.n3talk.mirror.entity.PlainJObject;

public class MirrorContainer implements ContainerStub {

    public void add(Object object) {
        if (Proxy.isProxyClass(object.getClass())) {
            final InvocationHandler ih = Proxy.getInvocationHandler(object);
            if (ih instanceof MirrorBase) {
                add((MirrorBase)ih);
            }
        } else if (object instanceof MirrorBase) {
            add((MirrorBase)object);
        }
    }

    @SuppressWarnings("unchecked")
    public void add(MirrorBase object) {
        if (!object.isMirrored()) {
            // checks for lists and maps...
            for (MirrorProperty mp : object.getMirrorProperties()) {
                final Object value = mp.getValue();
                if (value != null) {
                    if (MirrorProperty.Tool.isList(value.getClass())) {
                        for (Object o : (List<Object>) value) {
                            if (o instanceof MirrorBase) {
                                this.add((MirrorBase) o);
                            }
                        }
                    } else if (MirrorProperty.Tool.isMap(value)) {
                        final Map map = (Map) value;
                        // keys
                        for (Object k : map.keySet()) {
                            if (!(k instanceof MirrorBase)) {
                                k = new PlainJObject(k);
                                add(k);
                            }
                        }
                        // values
                        for (Object v : map.values()) {
                            if (v instanceof MirrorBase) {
                                add((MirrorBase)v);
                            }
                        }
                    }
                }
            }
            object.setManager(manager);
            object.setContainer(this);
            object.attach();
            objects.put(object.getObjectUID(), object);
        }
        if (listener != null) {
            listener.onChange();
        }
    }

    public void remove(String objectUID) {
        final MirrorBase base = objects.remove(objectUID);
        if (listener != null && base != null) {
            listener.objectDeleted(base);
            listener.onChange();
        }
    }

    public void remove(Object object) {
        if (Proxy.isProxyClass(object.getClass())) {
            final InvocationHandler ih = Proxy.getInvocationHandler(object);
            if (ih instanceof MirrorBase) {
                remove((MirrorBase)ih);
            }
        } else if (object instanceof MirrorBase) {
            remove((MirrorBase)object);
        }
    }

    public void remove(MirrorBase base) {
        base.detach();
    }

    public MessageQueue startQueueing() {
        final MessageQueue queue = new MessageQueue();
        setQueue(queue);
        return queue;
    }

    public MessageQueue getQueue() {
        return messageQueue;
    }

    public boolean hasQueue() {
        return messageQueue != null;
    }

    public void setQueue(MessageQueue queue) {
        messageQueue = queue;
        queue.container = this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MirrorManager getManager() {
        return manager;
    }

    void setManager(MirrorManager manager) {
        this.manager = manager;
    }

    public Collection<MirrorBase> getObjects() {
        return objects.values();
    }

    public void doChange() {
         if (listener != null) {
            listener.onChange();
        }
    }

    public MirrorContainerListener getListener() {
        return listener;
    }

    public void setListener(MirrorContainerListener listener) {
        this.listener = listener;
    }

    MessageQueue messageQueue;
    MirrorManager manager;
    final protected Map<String, MirrorBase> objects = new Hashtable<String, MirrorBase>();
    private String name;
    private MirrorContainerListener listener;
}
