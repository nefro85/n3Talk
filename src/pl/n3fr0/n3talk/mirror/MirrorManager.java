package pl.n3fr0.n3talk.mirror;

import java.rmi.dgc.VMID;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.n3fr0.n3talk.Facility;
import pl.n3fr0.n3talk.core.data.Header;
import pl.n3fr0.n3talk.core.data.RawEntity.Orgin;
import pl.n3fr0.n3talk.message.Message;
import pl.n3fr0.n3talk.message.MirrorMessage;
import pl.n3fr0.n3talk.message.MirrorMessage.Command;
import pl.n3fr0.n3talk.mirror.entity.MirrorBase;
import pl.n3fr0.n3talk.mirror.entity.MirrorListModificator;
import pl.n3fr0.n3talk.mirror.entity.MirrorMapModificator;
import pl.n3fr0.n3talk.mirror.entity.MirrorProperty;

public class MirrorManager extends Facility {

    public void onMessageInbound(Message message) {
        if (message instanceof MirrorMessage) {
            onMessage((MirrorMessage) message);

            if (message.getOrgin() == Orgin.SLAVE) {
                message.setFeedback(true);
                outboundMessage(message);
            }
            message.dispose();
        } else {
            throw new UnsupportedOperationException("UNSUPP MESSAGE");
        }
    }

    public void onMessage(MirrorMessage message) {
        for (Command cmd : message.getCommands()) {
            switch (cmd.getType()) {
                case CREATE:
                    create(cmd);
                    break;
                case UPDATE:
                    update(cmd);
                    break;
                case DELETE:
                    delete(cmd);
                    break;
                case INIT:
                    doInitialization(message);
                    break;
            }
        }

    }
    
    private void outboundMessage(Message message, ContainerStub container) {
        if (container == null || (container != null && !container.hasQueue())) {
            outboundMessage(message);
        } else {
            container.getQueue().add((MirrorMessage) message);
        }
    }

    public String genObjectUID() {
        return new VMID().toString();
    }

    public void doCreate(MirrorBase base, ContainerStub container) {
        doCreate(new MirrorBase[]{base}, container);
    }

    private void doCreate(MirrorBase[] bases, ContainerStub container) {
        doCreate(bases, null, container);
    }

    private void doCreate(MirrorBase[] bases, MirrorMessage msg, ContainerStub container) {
        if (bases.length > 0) {
            if (msg == null) {
                msg = new MirrorMessage();
            }

            for (MirrorBase base : bases) {
                msg.create(base);
                MirrorProperty[] properties = base.getMirrorProperties();
                if (properties.length > 0) {
                    msg.update(base, properties);
                }
            }

            outboundMessage(msg, container);
        }
    }

    public void doUpdate(final MirrorBase base, final String prop, ContainerStub container) {
        final MirrorMessage msg = new MirrorMessage();
        msg.update(base, new MirrorProperty[]{new MirrorProperty() {

                public String getName() {
                    return prop;
                }

                public Object getValue() {
                    return base.getPropertyValue(prop);
                }

                public boolean isList() {
                    return Tool.isList(base.getPropertyValue(prop).getClass());
                }

                public boolean isMap() {
                    return Tool.isMap(base.getPropertyValue(prop));
                }
            }});
        outboundMessage(msg, container);
    }

    public void doUpdate(MirrorBase base, MirrorMapModificator mmm, ContainerStub container){
        final MirrorMessage msg = new MirrorMessage();
        msg.update(base, mmm);
        outboundMessage(msg, container);
    }

    public void doUpdate(MirrorBase base, MirrorListModificator mlm, ContainerStub container) {
        final MirrorMessage msg = new MirrorMessage();
        msg.update(base, mlm);
        outboundMessage(msg, container);
    }

    public void doDelete(MirrorBase base, ContainerStub container) {
        final MirrorMessage msg = new MirrorMessage();
        msg.delete(base);
        outboundMessage(msg, container);
    }

    private synchronized void create(Command command){
        final MirrorBase mb = creator.create(command);
        if (mb != null) {
            getContainer().add(mb);            
        }
    }

    private void update(Command command) {
        for (UpdateMirrorObject u : command.getUpdateMirrorObjects()) {
            switch (u.getTask()) {
                case UPDATE_PROPERTY:
                    final MirrorBase updobj = getContainer().objects.get(u.getObject().getObjectUID());
                    for (MirrorProperty p : u.getProperties()) {
                        if (p instanceof MirrorPropertyRebuilder) {
                            final MirrorPropertyRebuilder mpr = (MirrorPropertyRebuilder) p;
                            mpr.setContainer(updobj.getContainer());
                        }
                        updobj.updateProperty(p);
                    }
                    break;
                case MODIF_LIST:
                    final MirrorListModificator mlm = u.getMirrorListModificator();
                    if (mlm instanceof MirrorListRebuilder) {
                        final MirrorListRebuilder mlr = (MirrorListRebuilder) mlm;
                        mlr.setContainer(getContainer());
                        final MirrorBase mb = getContainer().objects.get(mlr.getObjectUID());
                        mb.updateProperty(mlr);                        
                    }
                    break;
                case MODIF_MAP:
                    final MirrorMapModificator mmm = u.getMirrorMapModificator();
                    if (mmm instanceof MirrorMapRebuilder) {
                        final MirrorMapRebuilder mmr = (MirrorMapRebuilder) mmm;
                        mmr.setContainer(getContainer());
                        final MirrorBase mb = getContainer().objects.get(mmr.getObjectUID());
                        mb.updataProperty(mmm);
                    }
                    break;
            }
        }
    }

    private void delete(Command command) {
        getContainer().remove(command.getObjectUID());
    }

    public MirrorContainer getContainer() {
        return containers.get(DEFAULT_CONTAINER_NAME);
    }

    public MirrorContainer getContainer(String name) {
        return containers.get(name);
    }

    public void setContainer(MirrorContainer container) {
        container.setManager(this);
        this.containers.put(DEFAULT_CONTAINER_NAME, container);
    }

    private void doInitialization(MirrorMessage message) {
        message.clearCommands();
        final Collection<MirrorBase> bases = getContainer().objects.values();
        doCreate(bases.toArray(new MirrorBase[bases.size()]), message, null);
    }

    private class Creator {

        MirrorBase create(Command command) {
            MirrorBase m = null;
            try {
                m = (MirrorBase) Class.forName(command.getClassName()).newInstance();
                m.setObjectUID(command.getObjectUID());
            } catch (InstantiationException ex) {
                Logger.getLogger(MirrorManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(MirrorManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(MirrorManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            return m;
        }
    }

    final private Map<String, MirrorContainer> containers = new Hashtable<String, MirrorContainer>();
    final private Creator creator = new Creator();
    private static final String DEFAULT_CONTAINER_NAME = "default-container";

    public interface Headers {
        Header FACILITY = new Header(Facility.FACILITY, "Mirror");
    }
}
