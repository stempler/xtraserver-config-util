package de.interactive_instruments.xtraserver.config.util;

import de.interactive_instruments.xtraserver.config.schema.FeatureTypes;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MappingUnmarshaller
{
   private final static String MAPPING_SCHEMA = "src/main/resources/XtraServer_Mapping.xsd";
   //private final static String MAPPING = "src/main/resources/XtraSrvConfig_Mapping.inc.xml";
   //private final static String APPLICATION_SCHEMA = "src/main/resources/Cities.xsd";
   //private final static String MAPPING = "/home/zahnen/development/XSProjects/AAA-Suite/config/atkis-bdlm/sf/includes/XtraSrvConfig_Mapping.inc.xml";
   private final static String MAPPING = "/home/zahnen/development/XSProjects/AAA-Suite/config/alkis/sf/includes/XtraSrvConfig_Mapping.inc.xml";
   private final static String APPLICATION_SCHEMA = "/home/zahnen/development/XSProjects/AAA-Suite/www/schema/NAS/6.0/schema/AAA-Fachschema.xsd";

   @Test
   public void unmarshal() throws JAXBException, SAXException, IOException {

      FeatureTypes featureTypes = MappingParser.unmarshal( MAPPING_SCHEMA, new FileInputStream(MAPPING), FeatureTypes.class );
      XtraServerMappingImpl xsm = new XtraServerMappingImpl(featureTypes, new ApplicationSchema(APPLICATION_SCHEMA, new Namespaces()));
      xsm.print();
   }
}
