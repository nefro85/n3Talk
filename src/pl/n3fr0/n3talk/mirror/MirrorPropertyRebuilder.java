package pl.n3fr0.n3talk.mirror;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import pl.n3fr0.n3talk.message.XML;
import pl.n3fr0.n3talk.mirror.entity.MirrorBase;
import pl.n3fr0.n3talk.mirror.entity.MirrorProperty;

public class MirrorPropertyRebuilder implements MirrorProperty, XML {

    public MirrorPropertyRebuilder(Element property, MirrorMarshaller marshaller) {
        this.property = property;
        this.marshaller = marshaller;
    }

    public String getName() {
        return property.getAttribute(ATT_NAME);
    }

    public Object getValue() {
        Object o = null;
        //XXX
        if (!property.hasChildNodes()) {
            o = marshaller.toObject(property);
        } else {
            if (container != null) {
                if (isList()) {
                    final List<Object> list = new LinkedList<Object>();
                    final NodeList nl = ((Element) property.getFirstChild()).getChildNodes();
                    for (int i = 0; i < nl.getLength(); i++) {
                        final Element e = (Element) nl.item(i);
                        if (e.getTagName().equals(TAG_OBJECT)) {
                            list.add(container.objects.get(e.getAttribute(ATT_OBJECT_UID)));
                        }
                    }
                    o = list;
                } else if (isMap()) {
                    try {
                        final Map map = (Map) Class.forName(property.getAttribute(ATT_CLASS_NAME)).newInstance();
                        final NodeList keys = ((Element)property.getElementsByTagName(TAG_KEY).item(0)).getChildNodes();
                        final NodeList values = ((Element)property.getElementsByTagName(TAG_VALUE).item(0)).getChildNodes();
                        int im = 0;

                        while (im < keys.getLength()) {
                            final Element ek = (Element) keys.item(im);
                            final Element ev = (Element) values.item(im);
                            
                            final MirrorBase k = container.objects.get(ek.getAttribute(ATT_OBJECT_UID));
                            final MirrorBase v = container.objects.get(ev.getAttribute(ATT_OBJECT_UID));

                            map.put(k, v);
                            im++;
                        }
                        o = map;
                        
                    } catch (InstantiationException ex) {
                        Logger.getLogger(MirrorPropertyRebuilder.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(MirrorPropertyRebuilder.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(MirrorPropertyRebuilder.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return o;
    }

    public boolean isList() {
        final Element e = (Element) property.getFirstChild();
        if (e != null) {
            return e.getTagName().equals(TAG_LIST);
        }
        return false;
    }

    public boolean isMap() {
        final Element e = (Element) property.getFirstChild();
        if (e != null) {
            return e.getTagName().equals(TAG_MAP);
        }
        return false;
    }

    public MirrorContainer getContainer() {
        return container;
    }

    public void setContainer(MirrorContainer container) {
        this.container = container;
    }

    Element property;
    MirrorMarshaller marshaller;
    MirrorContainer container;

}
