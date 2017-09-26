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
 * @author zahnen
 */
public interface XtraServerMapping {
    final static String MAPPING_SCHEMA = "/home/zahnen/development/xtraserver-config-util/src/main/resources/XtraServer_Mapping.xsd";
    final static String APPLICATION_SCHEMA = "/home/zahnen/development/XSProjects/AAA-Suite/www/schema/NAS/6.0/schema/AAA-Fachschema.xsd";

    static XtraServerMapping fromStream(InputStream inputStream) throws JAXBException, SAXException, IOException {
        FeatureTypes featureTypes = MappingParser.unmarshal( MAPPING_SCHEMA, inputStream, FeatureTypes.class );
        XtraServerMapping xsm = new XtraServerMappingImpl(featureTypes, new ApplicationSchema(APPLICATION_SCHEMA, new Namespaces()));

        return xsm;
    }

    boolean hasFeatureType(String featureType);

    boolean hasType(String type);

    FeatureTypeMapping getFeatureTypeMapping(String featureType, boolean flattenInheritance);

    Collection<String> getFeatureTypeList();

    Collection<String> getFeatureTypeList(boolean includeAbstract);
}
