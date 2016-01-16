package pl.n3fr0.n3talk.message;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pl.n3fr0.n3talk.core.data.Header;
import pl.n3fr0.n3talk.core.data.RawData;

public abstract class Message extends RawData implements XML {

    static {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            documentBuilder = factory.newDocumentBuilder();

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static Document newDocument() {
        return documentBuilder.newDocument();
    }

    public Message(String tagName) {
        document = newDocument();
        final Element e = document.createElement(tagName);
        e.setAttribute(ATT_CLASS_NAME, this.getClass().getName());
        root = (Element) document.appendChild(e);

        setHeaders(new LinkedList<Header>());
    }

    public void setDocument(Document document) {
        this.document = document;
        root = document.getDocumentElement();
    }

    public Document getDocument() {
        return document;
    }

    @Override
    public void dispose() {
        super.dispose();
        disposeXML();
    }

    public void disposeXML() {
        if (document != null && root != null) {
            document.removeChild(root);
        }
        root = null;
        document = null;
    }

    protected Element root;
    protected Document document;
    // STATIC
    private static DocumentBuilder documentBuilder;
}
