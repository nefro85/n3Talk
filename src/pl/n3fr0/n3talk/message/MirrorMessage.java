package pl.n3fr0.n3talk.message;

import java.util.LinkedList;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import pl.n3fr0.n3talk.mirror.MirrorCommands;
import pl.n3fr0.n3talk.mirror.MirrorListRebuilder;
import pl.n3fr0.n3talk.mirror.MirrorManager;
import pl.n3fr0.n3talk.mirror.MirrorMapRebuilder;
import pl.n3fr0.n3talk.mirror.MirrorMarshaller;
import pl.n3fr0.n3talk.mirror.MirrorPropertyRebuilder;
import pl.n3fr0.n3talk.mirror.UpdateMirrorObject;
import pl.n3fr0.n3talk.mirror.entity.MirrorListModificator;
import pl.n3fr0.n3talk.mirror.entity.MirrorMapModificator;
import pl.n3fr0.n3talk.mirror.entity.MirrorObject;
import pl.n3fr0.n3talk.mirror.entity.MirrorProperty;

public class MirrorMessage extends Message {

    public MirrorMessage() {        
        super(TAG_NAME);
        marshaller.setDOMDocument(document);
        addHeader(MirrorManager.Headers.FACILITY);
    }

    public void create(MirrorObject object) {
        final Element e = document.createElement(MirrorCommands.CREATE.command());
        e.setAttribute(ATT_CLASS_NAME, object.getObjectClass());
        e.setAttribute(ATT_OBJECT_UID, object.getObjectUID());
        addCommand(e);
    }

    public void update(UpdateMirrorObject[] umo) {
        final Element e = document.createElement(MirrorCommands.UPDATE.command());
        for (UpdateMirrorObject u : umo) {
            switch (u.getTask()) {
                case UPDATE_PROPERTY:
                    final Element o = document.createElement(TAG_OBJECT);
                    o.setAttribute(ATT_OBJECT_UID, u.getObject().getObjectUID());
                    for (MirrorProperty p : u.getProperties()) {
                        if (p.getValue() != null) {
                            final Element updProp = marshaller.toDOM(p);
                            if (updProp == null) {
                                return;
                            }
                            o.appendChild(updProp);
                        }
                    }
                    e.appendChild(o);
                    break;
                case MODIF_LIST:
                    if (u.getMirrorListModificator().getObject() instanceof MirrorObject) {
                        e.appendChild(marshaller.toDOM(u.getMirrorListModificator()));
                    }
                    break;
                case MODIF_MAP:
                    e.appendChild(marshaller.toDOM(u.getMirrorMapModificator()));
                    break;
                default:
                    break;
            }

        }
        addCommand(e);
    }

    public void update(MirrorObject object, MirrorProperty[] properties) {       
        final UpdateMirrorObject[] o = new UpdateMirrorObject[1];
        o[0] = new UpdateMirrorObject(object, properties);
        update(o);
    }

    public void update(MirrorObject object, MirrorListModificator mlm) {
        final UpdateMirrorObject[] o = new UpdateMirrorObject[1];
        o[0] = new UpdateMirrorObject(object, mlm);
        update(o);
    }

    public void update(MirrorObject object, MirrorMapModificator mmm) {
        final UpdateMirrorObject[] o = new UpdateMirrorObject[1];
        o[0] = new UpdateMirrorObject(object, mmm);
        update(o);
    }

    public void delete(MirrorObject object) {
        final Element e = document.createElement(MirrorCommands.DELETE.command());
        e.setAttribute(ATT_OBJECT_UID, object.getObjectUID());
        addCommand(e);
    }

    private void addCommand(Element element){
        if (command == null) {
            command = document.createElement(TAG_COMMAND);
            root.appendChild(command);
        }
        command.appendChild(element);        
    }

    public void clearCommands() {
        if (cmd != null) {
            cmd.clear();
            cmd = null;
        }
        if (command != null) {
            root.removeChild(command);
            command = null;
        }
    }
    
    public Command[] getCommands(){
        if (cmd == null) {
            if (command == null) {
                command = (Element) root.getElementsByTagName(TAG_COMMAND).item(0);
            }
            final NodeList cmds = command.getChildNodes();
            cmd = new LinkedList<Command>();
            for (int i = 0; i < cmds.getLength(); i++) {
                Command c = null;
                final Element e = (Element) cmds.item(i);
                final MirrorCommands type = MirrorCommands.valueOf(e.getTagName().toUpperCase());

                switch (type) {
                    case CREATE:
                        c = commandCreate(e, type);
                        break;
                    case UPDATE:
                        c = commandUpdate(e, type);
                        break;
                    case DELETE:
                        c = commandDelete(e, type);
                        break;
                    default:
                        continue;
                }
                cmd.add(c);
            }
        }
        return  cmd.toArray(new Command[cmd.size()]);
    }

    private Command commandCreate(Element e, MirrorCommands mirrorCommands) {
        return new Command(mirrorCommands, e.getAttribute(ATT_CLASS_NAME),
                e.getAttribute(ATT_OBJECT_UID).isEmpty() ? null : e.getAttribute(ATT_OBJECT_UID));
    }

    private Command commandUpdate(final Element e, MirrorCommands mirrorCommands) {
        final NodeList nl = e.getChildNodes();
        final UpdateMirrorObject[] updobjs = new UpdateMirrorObject[nl.getLength()];

        for (int i = 0; i < nl.getLength(); i++) {
            final Element o = (Element) nl.item(i);
            final UpdateMirrorObject updo = new UpdateMirrorObject();
            // update property
            if (o.getTagName().equals(TAG_OBJECT)) {
                updo.setTask(UpdateMirrorObject.Task.UPDATE_PROPERTY);

                updo.setObject(new MirrorObject() {

                    public String getObjectUID() {
                        return o.getAttribute(ATT_OBJECT_UID);
                    }

                    public String getObjectClass() {
                        throw new UnsupportedOperationException("Shoud be don't used.");
                    }
                });

                final NodeList nlprops = o.getChildNodes();
                final MirrorProperty[] mp = new MirrorProperty[nlprops.getLength()];
                for (int j = 0; j < nlprops.getLength(); j++) {
                    final Element p = (Element) nlprops.item(j);
                    mp[j] = new MirrorPropertyRebuilder(p, marshaller);
                }
                updo.setProperties(mp);

            } else if (o.getTagName().equals(TAG_LIST)) {
                updo.setTask(UpdateMirrorObject.Task.MODIF_LIST);
                updo.setMirrorListModificator(new MirrorListRebuilder(o));                

            } else if (o.getTagName().equals(TAG_MAP)) {
                updo.setTask(UpdateMirrorObject.Task.MODIF_MAP);
                updo.setMirrorMapModificator(new MirrorMapRebuilder(o));
            }
            updobjs[i] = updo;
        }
        return new Command(mirrorCommands, null, null, updobjs);
    }

    public void doClientInitialization() {
        cmd = new LinkedList<Command>();
        cmd.add(new Command(MirrorCommands.INIT, null, null));
    }

    private Command commandDelete(Element e, MirrorCommands mirrorCommands) {
        return new Command(mirrorCommands, null, e.getAttribute(ATT_OBJECT_UID));
    }

    public void joinCommands(MirrorMessage msg2Join) {
        final NodeList cmds = msg2Join.command.getChildNodes();
        for (int i = 0; i < cmds.getLength(); i++) {
            final Element alienCmd = (Element) document.importNode(cmds.item(i), true);
            command.appendChild(alienCmd);
        }
    }

    public class Command {

        public Command(MirrorCommands type, String clazz, String uid){
            this(type, clazz, uid, null);
        }

        public Command(MirrorCommands type, String clazz, String uid, UpdateMirrorObject[] umo) {
            this.type = type;
            this.clazz = clazz;
            this.uid = uid;
            this.updateMirrorObject = umo;
        }

        public MirrorCommands getType(){
            return type;
        }
        public String getClassName(){
            return clazz;
        }
        public String getObjectUID(){
            return uid;
        }        

        public UpdateMirrorObject[] getUpdateMirrorObjects() {
            return updateMirrorObject;
        }

        MirrorCommands type;
        String clazz;
        String uid;
        String containerName;
        UpdateMirrorObject[] updateMirrorObject;
    }

    Element command;
    private List<Command> cmd;
    final private MirrorMarshaller marshaller = new MirrorMarshaller();
}
