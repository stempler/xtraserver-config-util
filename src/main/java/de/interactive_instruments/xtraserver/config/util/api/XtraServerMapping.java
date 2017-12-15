package de.interactive_instruments.xtraserver.config.util.api;

import de.interactive_instruments.xtraserver.config.util.JaxbReaderWriter;
import de.interactive_instruments.xtraserver.config.util.XtraServerMappingImpl;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

/**
 * A collection of FeatureType mappings
 *
 * @author zahnen
 */
public interface XtraServerMapping {

    /**
     * Factory method, parses mappings from InputStream
     *
     * @param inputStream
     * @return
     * @throws JAXBException
     * @throws SAXException
     * @throws IOException
     */
    static XtraServerMapping createFromStream(InputStream inputStream) throws JAXBException, SAXException, IOException {
        return JaxbReaderWriter.readFromStream(inputStream);
    }

    static XtraServerMapping create() {
        XtraServerMapping xtraServerMapping = new XtraServerMappingImpl();

        return xtraServerMapping;
    }

    /**
     * Does a mapping exist for the given non-abstract FeatureType?
     *
     * @param featureType
     * @return
     */
    boolean hasFeatureType(String featureType);

    /**
     * Does a mapping exist for the given abstract FeatureType or DataType?
     *
     * @param type
     * @return
     */
    boolean hasType(String type);

    /**
     * Returns the mappings for given FeatureType
     *
     * @param featureType
     * @param flattenInheritance If true mappings from supertypes will be merged down
     * @return
     */
    FeatureTypeMapping getFeatureTypeMapping(String featureType, boolean flattenInheritance);

    /**
     * Get the list of non-abstract FeatureTypes
     *
     * @return
     */
    Collection<String> getFeatureTypeList();

    /**
     * Get the list of FeatureTypes
     *
     * @param includeAbstract If true include abstract FeatureTypes
     * @return
     */
    Collection<String> getFeatureTypeList(boolean includeAbstract);

    /**
     * Add mappings for a specific FeatureType
     *
     * @param featureTypeMapping
     */
    void addFeatureTypeMapping(FeatureTypeMapping featureTypeMapping);

    /**
     * Write mappings to OutputStream
     *
     * @param outputStream
     */
    void writeToStream(OutputStream outputStream) throws IOException, JAXBException, SAXException;
}
