package de.interactive_instruments.xtraserver.config.util;

import com.google.common.base.Objects;
import de.interactive_instruments.xtraserver.config.schema.*;
import de.interactive_instruments.xtraserver.config.util.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.util.api.XtraServerMapping;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.net.URL;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Helper methods for JAXB marshalling and unmarshalling
 */
public class JaxbReaderWriter {

    private final static String MAPPING_SCHEMA = "XtraServer_Mapping.xsd";

    public static XtraServerMapping readFromStream(InputStream inputStream) throws IOException, JAXBException, SAXException {
        FeatureTypes featureTypes = unmarshal( inputStream );

        ApplicationSchema applicationSchema = new ApplicationSchema();
        XtraServerMapping xtraServerMapping = new XtraServerMappingImpl(applicationSchema);

        for (Object a : featureTypes.getFeatureTypeOrAdditionalMappings()) {
            try {
                FeatureType ft = (FeatureType) a;

                FeatureTypeMapping ftm = new FeatureTypeMappingImpl(extractMappings(ft), ft.getName(), applicationSchema.getType(ft.getName()), applicationSchema.getNamespaces());

                xtraServerMapping.addFeatureTypeMapping(ftm);

            } catch (ClassCastException e) {
                try {
                    AdditionalMappings am = (AdditionalMappings) a;

                    FeatureTypeMapping ftm = new FeatureTypeMappingImpl(am.getMappings(), am.getRootElementName(), applicationSchema.getType(am.getRootElementName()), applicationSchema.getNamespaces());

                    xtraServerMapping.addFeatureTypeMapping(ftm);

                } catch (ClassCastException e2) {
                    // ignore
                }
            }
        }

        return xtraServerMapping;
    }

    public static void writeToStream(OutputStream outputStream, XtraServerMappingImpl xtraServerMapping) throws IOException, JAXBException, SAXException {
        ObjectFactory objectFactory = new ObjectFactory();
        FeatureTypes featureTypes = objectFactory.createFeatureTypes();

        featureTypes.getFeatureTypeOrAdditionalMappings().addAll(
                xtraServerMapping.getAdditionalMappings().stream().map(additionalMapping -> {
                    MappingsSequenceType mappingsSequenceType = objectFactory.createMappingsSequenceType();

                    mappingsSequenceType.getTableOrJoinOrAssociationTarget().addAll(
                            additionalMapping.getTables().stream().map(mappingTable -> {
                                MappingsSequenceType.Table table = objectFactory.createMappingsSequenceTypeTable();
                                table.setTable_Name(mappingTable.getName());
                                table.setOid_Col(mappingTable.getOidCol());
                                return table;
                            }).collect(Collectors.toList())
                    );

                    AdditionalMappings jaxbAdditionalMappings = objectFactory.createAdditionalMappings();
                    jaxbAdditionalMappings.setRootElementName(additionalMapping.getName());
                    jaxbAdditionalMappings.setMappings(mappingsSequenceType);

                    return jaxbAdditionalMappings;
                }).collect(Collectors.toList())
        );

        featureTypes.getFeatureTypeOrAdditionalMappings().addAll(
                xtraServerMapping.getFeatureTypeMappings().stream().map(featureTypeMapping -> {
                    SQLFeatureTypeImplType sqlFeatureTypeImplType = objectFactory.createSQLFeatureTypeImplType();

                    featureTypeMapping.getTables().forEach(mappingTable -> {
                        sqlFeatureTypeImplType.getTableOrJoinOrAssociationTarget().addAll(
                                mappingTable.getJoinPaths().stream().map(mappingJoin -> {
                                    MappingsSequenceType.Join join = objectFactory.createMappingsSequenceTypeJoin();
                                    join.setAxis(mappingJoin.getAxis());
                                    join.setTarget(mappingJoin.getTarget());
                                    join.setJoin_Path(mappingJoin.getPath());
                                    return join;
                                }).collect(Collectors.toList())
                        );

                        MappingsSequenceType.Table table = objectFactory.createMappingsSequenceTypeTable();
                        table.setTable_Name(mappingTable.getName());
                        table.setOid_Col(mappingTable.getOidCol());
                        table.setTarget(mappingTable.getTarget());

                        sqlFeatureTypeImplType.getTableOrJoinOrAssociationTarget().add(table);

                        sqlFeatureTypeImplType.getTableOrJoinOrAssociationTarget().addAll(
                                mappingTable.getValues().stream().map(mappingValue -> {
                                    MappingsSequenceType.Table value = objectFactory.createMappingsSequenceTypeTable();
                                    value.setTable_Name(mappingValue.getTable());
                                    value.setTarget(mappingValue.getTarget());
                                    value.setValue4(mappingValue.getValue());
                                    value.setValue_Type(mappingValue.getValueType());
                                    value.setMapping_Mode(mappingValue.getMappingMode());
                                    value.setDb_Codes(mappingValue.getDbCodes());
                                    value.setSchema_Codes(mappingValue.getDbValues());
                                    return value;
                                }).collect(Collectors.toList())
                        );
                    });

                    sqlFeatureTypeImplType.getTableOrJoinOrAssociationTarget().addAll(
                            featureTypeMapping.getAssociationTargets().stream().map(associationTarget -> {
                                MappingsSequenceType.AssociationTarget associationTarget1 = objectFactory.createMappingsSequenceTypeAssociationTarget();
                                associationTarget1.setObject_Ref(associationTarget.getObjectRef());
                                associationTarget1.setTarget(associationTarget.getTarget());
                                return associationTarget1;
                            }).collect(Collectors.toList())
                    );

                    /*sqlFeatureTypeImplType.getTableOrJoinOrAssociationTarget().addAll(
                            featureTypeMapping.getTables().stream().map(mappingTable -> {
                                MappingsSequenceType.Table table = objectFactory.createMappingsSequenceTypeTable();
                                table.setTable_Name(mappingTable.getName());
                                table.setOid_Col(mappingTable.getOidCol());
                                table.setTarget(mappingTable.getTarget());
                                return table;
                            }).collect(Collectors.toList())
                    );*/


                    FeatureType featureType = objectFactory.createFeatureType();
                    featureType.setName(featureTypeMapping.getName());
                    featureType.setPGISFeatureTypeImpl(sqlFeatureTypeImplType);

                    return featureType;
                }).collect(Collectors.toList())
        );

        marshal(outputStream, featureTypes);
    }

    private static FeatureTypes unmarshal(InputStream inputStream) throws JAXBException, IOException, SAXException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(getResource(JaxbReaderWriter.class, MAPPING_SCHEMA));
        JAXBContext jaxbContext = JAXBContext.newInstance(FeatureTypes.class.getPackage().getName());

        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream(in);

        SubstitutionProcessor substitutionProcessor = new SubstitutionProcessor();
        //substitutionProcessor.addParameter("xpathAliasPattern.AX_Flurstueck.15", "foo");
        //substitutionProcessor.addParameter("xpathAliasReplacement.AX_Flurstueck.15", "bar");
        new Thread(
                () -> {
                    try {
                        substitutionProcessor.process(new InputStreamReader(inputStream), new OutputStreamWriter(out));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        ).start();

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setSchema(schema);

        return FeatureTypes.class.cast(unmarshaller.unmarshal(in));
    }

    private static void marshal(OutputStream outputStream, FeatureTypes featureTypes) throws JAXBException, SAXException, IOException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(getResource(JaxbReaderWriter.class, MAPPING_SCHEMA));
        JAXBContext jaxbContext = JAXBContext.newInstance(FeatureTypes.class.getPackage().getName());

        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setSchema(schema);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(featureTypes, outputStream);
    }

    private static MappingsSequenceType extractMappings(FeatureType featureType) {
        MappingsSequenceType mappings = null;

        if (featureType.getPGISFeatureTypeImpl() != null) {
            mappings = featureType.getPGISFeatureTypeImpl();
        } else if (featureType.getOraSFeatureTypeImpl() != null) {
            mappings = featureType.getOraSFeatureTypeImpl();
        } else if (featureType.getGDBSQLFeatureTypeImpl() != null) {
            mappings = featureType.getGDBSQLFeatureTypeImpl();
        }

        return mappings;
    }

    static URL getResource(final Class<?> contextClass, String resourceName) {
        // googles Resource api does not take all classloaders into account
        URL url = contextClass.getResource(resourceName);
        if(url==null) {
            url = Objects.firstNonNull(
                    Thread.currentThread().getContextClassLoader(),
                    contextClass.getClassLoader()).getResource(resourceName);
        }
        checkArgument(url != null, "resource %s relative to %s not found.",
                resourceName, contextClass.getName());
        return url;
    }

}
