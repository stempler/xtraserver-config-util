package de.interactive_instruments.xtraserver.config.util;

import de.interactive_instruments.xtraserver.config.schema.FeatureTypes;
import de.interactive_instruments.xtraserver.config.util.api.XtraServerMapping;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MappingUnmarshaller
{
   private final static String MAPPING = "/home/zahnen/development/XSProjects/AAA-Suite/config/alkis/sf/includes/XtraSrvConfig_Mapping.inc.xml";

   //@Test
   public void unmarshal() throws JAXBException, SAXException, IOException {
      XtraServerMapping xtraServerMapping = XtraServerMapping.createFromStream(new FileInputStream(MAPPING));
      ((XtraServerMappingImpl)xtraServerMapping).print();

      //FeatureTypes featureTypes = MappingParser.unmarshal( new FileInputStream(MAPPING) );
      //XtraServerMappingImpl xsm = new XtraServerMappingImpl(featureTypes, new ApplicationSchema(APPLICATION_SCHEMA, new Namespaces()));
      //xsm.print();
   }
}
