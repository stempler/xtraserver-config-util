package de.interactive_instruments.xtraserver.config.util;

import com.google.common.io.Resources;
import de.interactive_instruments.xtraserver.config.schema.FeatureTypes;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;

/**
 * Helper methods for JAXB marshalling and unmarshalling
 */
public class MappingParser {

    private final static String MAPPING_SCHEMA = "XtraServer_Mapping.xsd";

    public static FeatureTypes unmarshal(InputStream inputStream) throws JAXBException, SAXException, IOException {
        return MappingParser.unmarshal(MAPPING_SCHEMA, inputStream, FeatureTypes.class);
    }

    private static <T> T unmarshal(String xsdSchema, InputStream inputStream, Class<T> clss) throws JAXBException, SAXException, IOException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = (xsdSchema == null || xsdSchema.trim().length() == 0) ? null : schemaFactory.newSchema(Resources.getResource(xsdSchema));
        JAXBContext jaxbContext = JAXBContext.newInstance(clss.getPackage().getName());

        return unmarshal(jaxbContext, schema, inputStream, clss);
    }

    private static <T> T unmarshal(JAXBContext jaxbContext, Schema schema, InputStream inputStream, Class<T> clss) throws JAXBException, IOException {

        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream(in);

        SubstitutionProcessor substitutionProcessor = new SubstitutionProcessor();
        //substitutionProcessor.addParameter("xpathAliasPattern.AX_Flurstueck.15", "foo");
        //substitutionProcessor.addParameter("xpathAliasReplacement.AX_Flurstueck.15", "bar");
        new Thread(
                new Runnable() {
                    public void run() {
                        try {
                            substitutionProcessor.process(new InputStreamReader(inputStream), new OutputStreamWriter(out));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).start();

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setSchema(schema);
        return clss.cast(unmarshaller.unmarshal(in));
    }

    public static void marshal(OutputStream outputStream, FeatureTypes featureTypes) throws JAXBException, SAXException, IOException {
        MappingParser.marshal(MAPPING_SCHEMA, outputStream, featureTypes);
    }

    private static void marshal(String xsdSchema, OutputStream outputStream, Object jaxbElement) throws JAXBException, SAXException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = (xsdSchema == null || xsdSchema.trim().length() == 0) ? null : schemaFactory.newSchema(Resources.getResource(xsdSchema));
        JAXBContext jaxbContext = JAXBContext.newInstance(jaxbElement.getClass().getPackage().getName());
        marshal(jaxbContext, schema, outputStream, jaxbElement);
    }

    private static void marshal(JAXBContext jaxbContext, Schema schema, OutputStream outputStream, Object jaxbElement) throws JAXBException {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setSchema(schema);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(jaxbElement, outputStream);
    }

}
