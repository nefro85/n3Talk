package pl.n3fr0.n3talk.mirror;

import org.w3c.dom.Element;
import pl.n3fr0.n3talk.message.XML;
import pl.n3fr0.n3talk.mirror.entity.MirrorBase;
import pl.n3fr0.n3talk.mirror.entity.MirrorMapModificator;
import pl.n3fr0.n3talk.mirror.entity.PlainJObject;

public class MirrorMapRebuilder implements MirrorMapModificator, XML {

    public MirrorMapRebuilder(Element element) {
        this.element = element;
    }

    public void setContainer(MirrorContainer container) {
        this.container = container;
    }

    public Object getKey() {
        Object o = null;
        final Element key = (Element) element.getElementsByTagName(TAG_KEY).item(0);
        if (key.hasAttribute(ATT_OBJECT_UID)) {
            final MirrorBase mb = container.objects.get(key.getAttribute(ATT_OBJECT_UID));
            if (mb instanceof PlainJObject) {
                final PlainJObject pjo = (PlainJObject) mb;
                o = pjo.getValue();
                pjo.quietDetach();
            }
        }
        return o;
    }

    public Kind getKind() {
        for (Kind a : Kind.values()) {
            if (a.toString().equals(element.getAttribute(ATT_TYPE))) {
                return a;
            }
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getName() {
        return element.getAttribute(ATT_NAME);
    }

    public String getObjectUID() {
        return element.getAttribute(ATT_OBJECT_UID);
    }

    public Object getValue() {
        Object o = null;
        final Element value = (Element) element.getElementsByTagName(TAG_VALUE).item(0);
        if (value.hasAttribute(ATT_OBJECT_UID)) {
            o = container.objects.get(value.getAttribute(ATT_OBJECT_UID));
        }
        return o;
    }

    private final Element element;
    private MirrorContainer container;
}
