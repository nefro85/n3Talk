package pl.n3fr0.n3talk.message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import pl.n3fr0.n3talk.core.data.RawData;

public class Assembler {

    public Message asm(RawData data) {        
        return makeMessage(data);
    }

    public void disasm(Message message) {
        try {
            final DOMSource source = new DOMSource(message.getDocument());
            final StringWriter stringWriter = new StringWriter();
            final StreamResult streamResult = new StreamResult(stringWriter);
            getTransformer().transform(source, streamResult);
            message.setContent(stringWriter.toString());
            message.disposeXML();

        } catch (TransformerException ex) {
            Logger.getLogger(Assembler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private DocumentBuilder getBuilder() {
        if (documentBuilder == null) {
            try {
                final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                documentBuilder = builderFactory.newDocumentBuilder();

            } catch (ParserConfigurationException ex) {
                Logger.getLogger(Assembler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return documentBuilder;
    }

    Message makeMessage(RawData data) {
        Message message = null;
        try {

            final Document d = getBuilder().parse(new ByteArrayInputStream(data.getContent().getBytes()));
            final String className = d.getDocumentElement().getAttribute(Message.ATT_CLASS_NAME);
            message = (Message) Class.forName(className).newInstance();
            message.setHeaders(data.getHeaders());
            message.setOrgin(data.getOrgin());
            message.setSource(data.getSource());
            message.setDocument(d);

            data.dispose();           

        } catch (InstantiationException ex) {
            Logger.getLogger(Assembler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Assembler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Assembler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(Assembler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Assembler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return message;
    }

    private Transformer getTransformer() {
        if (transformer == null) {
            TransformerFactory tf = TransformerFactory.newInstance();
            try {
                transformer = tf.newTransformer();
            } catch (TransformerConfigurationException ex) {
                Logger.getLogger(Assembler.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return transformer;
    }

    private DocumentBuilder documentBuilder;
    private Transformer transformer;
}
