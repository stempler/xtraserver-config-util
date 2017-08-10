package de.interactive_instruments.xtraserver.util;

import javax.xml.bind.JAXBException;

import de.interactive_instruments.xtraserver.config.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import org.xml.sax.SAXException;
import de.interactive_instruments.xtraserver.schema.*;

import java.io.FileNotFoundException;
import java.util.Collection;

public class MappingUnmarshaller
{
   public static void main( String[] args ) throws JAXBException, SAXException, FileNotFoundException {
      if( args.length < 2 ) {
         System.out.println( "\nBitte XSD-Schema und XML-Dokument angeben" );
         return;
      }
      FeatureTypes featureTypes = MappingParser.unmarshal( args[0], args[1], FeatureTypes.class );
      XtraServerMapping xsm = new XtraServerMapping(featureTypes, new ApplicationSchema(args[2], new Namespaces()));
      xsm.print();
   }
}
