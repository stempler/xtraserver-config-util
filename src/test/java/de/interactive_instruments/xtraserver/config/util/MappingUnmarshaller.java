import javax.xml.bind.JAXBException;

import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import de.interactive_instruments.xtraserver.config.schema.FeatureTypes;
import de.interactive_instruments.xtraserver.config.util.ApplicationSchema;
import de.interactive_instruments.xtraserver.config.util.MappingParser;
import de.interactive_instruments.xtraserver.config.util.Namespaces;
import org.junit.Test;
import org.xml.sax.SAXException;
import de.interactive_instruments.xtraserver.schema.*;

import java.io.FileNotFoundException;

public class MappingUnmarshaller
{
   private final static String MAPPING_SCHEMA = "src/main/resources/XtraServer_Mapping.xsd";
   private final static String MAPPING = "src/main/resources/XtraSrvConfig_Mapping.inc.xml";
   private final static String APPLICATION_SCHEMA = "src/main/resources/Cities.xsd";

   @Test
   public void unmarshal() throws JAXBException, SAXException, FileNotFoundException {

      FeatureTypes featureTypes = MappingParser.unmarshal( MAPPING_SCHEMA, MAPPING, FeatureTypes.class );
      XtraServerMapping xsm = new XtraServerMapping(featureTypes, new ApplicationSchema(APPLICATION_SCHEMA, new Namespaces()));
      xsm.print();
   }
}
