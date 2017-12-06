package de.interactive_instruments.xtraserver.config.util.api;

import de.interactive_instruments.xtraserver.config.schema.FeatureTypes;
import de.interactive_instruments.xtraserver.config.util.ApplicationSchema;
import de.interactive_instruments.xtraserver.config.util.MappingParser;
import de.interactive_instruments.xtraserver.config.util.Namespaces;
import de.interactive_instruments.xtraserver.config.util.XtraServerMappingImpl;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * A collection of FeatureType mappings
 *
 * @author zahnen
 */
public interface XtraServerMapping {

    /**
     * Parse mappings from InputStream
     * @param inputStream
     * @return
     * @throws JAXBException
     * @throws SAXException
     * @throws IOException
     */
    static XtraServerMapping fromStream(InputStream inputStream) throws JAXBException, SAXException, IOException {
        FeatureTypes featureTypes = MappingParser.unmarshal( inputStream );
        XtraServerMapping xsm = new XtraServerMappingImpl(featureTypes, new ApplicationSchema());

        return xsm;
    }

    /**
     * Does a mapping exist for the given non-abstract FeatureType?
     * @param featureType
     * @return
     */
    boolean hasFeatureType(String featureType);

    /**
     * Does a mapping exist for the given abstract FeatureType or DataType?
     * @param type
     * @return
     */
    boolean hasType(String type);

    /**
     * Returns the mappings for given FeatureType
     * @param featureType
     * @param flattenInheritance
     * If true mappings from supertypes will be merged down
     * @return
     */
    FeatureTypeMapping getFeatureTypeMapping(String featureType, boolean flattenInheritance);

    /**
     * Get the list of non-abstract FeatureTypes
     * @return
     */
    Collection<String> getFeatureTypeList();

    /**
     * Get the list of FeatureTypes
     * @param includeAbstract
     * If true include abstract FeatureTypes
     * @return
     */
    Collection<String> getFeatureTypeList(boolean includeAbstract);
}
