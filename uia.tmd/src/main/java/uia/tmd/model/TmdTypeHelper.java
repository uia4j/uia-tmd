package uia.tmd.model;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

import uia.tmd.model.xml.ObjectFactory;
import uia.tmd.model.xml.TmdType;

/**
 * TMD XML file helper.
 *
 * @author Kyle K. Lin
 *
 */
public class TmdTypeHelper {

    static Unmarshaller UNMARSHALLER;

    public static void save(TmdType tmd, File file) throws Exception {
        ObjectFactory of = new ObjectFactory();

        JAXBContext jaxbContext = JAXBContext.newInstance(TmdType.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(of.createTmd(tmd), file);
    }

    /**
     * Load TMD XML and convert to model.
     * @param file TMD XML file.
     * @return Model.
     * @throws Exception Load failure.
     */
    public static TmdType load(File file) throws Exception {
        return load(new FileInputStream(file));
    }

    /**
     * Load TMD XML and convert to model.
     * @param stream TDM XML string stream.
     * @return Model.
     * @throws Exception Load failure.
     */
    public static TmdType load(InputStream stream) throws Exception {
        if (UNMARSHALLER == null) {
            initial();
        }

        // Create the XMLReader
        SAXParserFactory factory = SAXParserFactory.newInstance();
        XMLReader reader = factory.newSAXParser().getXMLReader();

        // The filter class to set the correct namespace
        XMLFilterImpl xmlFilter = new XMLNamespaceFilter(reader);
        reader.setContentHandler(UNMARSHALLER.getUnmarshallerHandler());

        SAXSource source = new SAXSource(xmlFilter, new InputSource(stream));

        @SuppressWarnings("unchecked")
        JAXBElement<TmdType> elem = (JAXBElement<TmdType>) UNMARSHALLER.unmarshal(source);
        return elem.getValue();
    }

    /**
     * Load TMD XML and convert to model.
     * @param content TDM XML string content.
     * @return Model.
     * @throws Exception Load failure.
     */
    public static TmdType load(String content) throws Exception {
        if (UNMARSHALLER == null) {
            initial();
        }

        // Create the XMLReader
        SAXParserFactory factory = SAXParserFactory.newInstance();
        XMLReader reader = factory.newSAXParser().getXMLReader();

        // The filter class to set the correct namespace
        XMLFilterImpl xmlFilter = new XMLNamespaceFilter(reader);
        reader.setContentHandler(UNMARSHALLER.getUnmarshallerHandler());

        InputStream inStream = new ByteArrayInputStream(content.getBytes("UTF-8"));
        SAXSource source = new SAXSource(xmlFilter, new InputSource(inStream));

        @SuppressWarnings("unchecked")
        JAXBElement<TmdType> elem = (JAXBElement<TmdType>) UNMARSHALLER.unmarshal(source);
        return elem.getValue();
    }

    static void initial() throws Exception {
        try {
            JAXBContext jc = JAXBContext.newInstance("uia.tmd.model.xml");
            UNMARSHALLER = jc.createUnmarshaller();
        }
        catch (JAXBException ex) {
            UNMARSHALLER = null;
            throw ex;
        }
    }

    static class XMLNamespaceFilter extends XMLFilterImpl {

        public XMLNamespaceFilter(XMLReader reader) {
            super(reader);
        }

        @Override
        public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes) throws SAXException {
            super.startElement("http://tmd.uia/model/xml", localName, qName, attributes);
        }
    }
}
