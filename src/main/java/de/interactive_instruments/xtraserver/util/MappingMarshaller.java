package de.interactive_instruments.xtraserver.util;

/**
 * @author zahnen
 */

import org.xml.sax.SAXException;
import javax.xml.bind.JAXBException;
import de.interactive_instruments.xtraserver.schema.*;

public class MappingMarshaller
{
    public static void main( String[] args ) throws JAXBException, SAXException
    {
        if( args.length != 3 ) {
            System.out.println( "\nBitte XSD-Schema und XML-Zieldateiname angeben." );
            return;
        }
        MappingParser.marshal( args[0], args[2], erzeugeBuecherObjekt(args[0], args[1]) );
        System.out.println( "\n" + args[2] + " erzeugt." );
    }

    static FeatureTypes erzeugeBuecherObjekt(String schema, String source) throws JAXBException, SAXException {
        return MappingParser.unmarshal( schema, source, FeatureTypes.class );
    }
}