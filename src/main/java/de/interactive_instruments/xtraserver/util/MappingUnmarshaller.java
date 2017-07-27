package de.interactive_instruments.xtraserver.util;

import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;
import de.interactive_instruments.xtraserver.schema.*;

public class MappingUnmarshaller
{
   public static void main( String[] args ) throws JAXBException, SAXException
   {
      if( args.length < 2 ) {
         System.out.println( "\nBitte XSD-Schema und XML-Dokument angeben" );
         return;
      }
      FeatureTypes featureTypes = MappingParser.unmarshal( args[0], args[1], FeatureTypes.class );
      zeigeBuecher( featureTypes );
   }

   static void zeigeBuecher( FeatureTypes featureTypes )
   {
      System.out.println( "Autoren:" + featureTypes.getFeatureTypeOrAdditionalMappings().size() );
      for( Object a : featureTypes.getFeatureTypeOrAdditionalMappings() ) {
         try {
            FeatureType ft = (FeatureType)a;
            System.out.println( "Name: " + ft.getName() + "\n" );

            for( Object b : ft.getPGISFeatureTypeImpl().getTableOrJoinOrAssociationTarget() ) {
               try {
                  MappingsSequenceType.Table table = (MappingsSequenceType.Table)b;
                  System.out.println( "  Table: " + table.getTable_Name() );
                  System.out.println( "  Value: " + table.getValue4() );
                  System.out.println( "  Target: " + table.getTarget() + "\n" );
               } catch (ClassCastException e) {

               }
            }
         } catch (ClassCastException e) {

         }
      }
   }
}
