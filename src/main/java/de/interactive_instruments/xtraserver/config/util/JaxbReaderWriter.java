package de.interactive_instruments.xtraserver.config.util;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import de.interactive_instruments.xtraserver.config.schema.*;
import de.interactive_instruments.xtraserver.config.util.api.*;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Helper methods for JAXB marshalling and unmarshalling
 */
public class JaxbReaderWriter {

    private final static String MAPPING_SCHEMA = "XtraServer_Mapping.xsd";

    public static XtraServerMapping readFromStream(final InputStream inputStream, final ApplicationSchema applicationSchema) throws IOException, JAXBException, SAXException {
        FeatureTypes featureTypes = unmarshal(inputStream);

        XtraServerMapping xtraServerMapping = new XtraServerMappingImpl(applicationSchema);

        for (Object a : featureTypes.getFeatureTypeOrAdditionalMappings()) {
            try {
                FeatureType ft = (FeatureType) a;
                MappingsSequenceType mappings = extractMappings(ft);

                FeatureTypeMapping ftm = FeatureTypeMapping.create(ft.getName(), applicationSchema.getType(ft.getName()), applicationSchema.getNamespaces());

                extractMappings(mappings, ftm, applicationSchema.getNamespaces());

                xtraServerMapping.addFeatureTypeMapping(ftm);

            } catch (ClassCastException e) {
                try {
                    AdditionalMappings am = (AdditionalMappings) a;

                    FeatureTypeMapping ftm = new FeatureTypeMappingImpl(am.getRootElementName(), applicationSchema.getType(am.getRootElementName()), applicationSchema.getNamespaces());

                    extractMappings(am.getMappings(), ftm, applicationSchema.getNamespaces());

                    xtraServerMapping.addFeatureTypeMapping(ftm);

                } catch (ClassCastException e2) {
                    // ignore
                }
            }
        }

        return xtraServerMapping;
    }

    private static void extractMappings(MappingsSequenceType mappings, FeatureTypeMapping featureTypeMapping, Namespaces namespaces) {

        extractTables(mappings).forEach(featureTypeMapping::addTable);
        extractValues(mappings, featureTypeMapping, namespaces).forEach(featureTypeMapping::addValue);
        extractJoins(mappings, featureTypeMapping).forEach(featureTypeMapping::addJoin);
        extractAssociationTargets(mappings).forEach(featureTypeMapping::addAssociationTarget);
    }

    private static Collection<MappingTable> extractTables(MappingsSequenceType mappings) {
        Map<String, MappingTable> mappingTables = new HashMap<>();

        if (mappings != null) {
            for (Object mapping : mappings.getTableOrJoinOrAssociationTarget()) {
                try {
                    MappingsSequenceType.Table table = (MappingsSequenceType.Table) mapping;

                    if (table.getTable_Name() != null && !table.getTable_Name().isEmpty()) {
                        if (table.getOid_Col() != null && !table.getOid_Col().isEmpty()) {
                            //if (table.getValue4() == null || table.getValue4().isEmpty()) {
                            if (!mappingTables.containsKey(table.getTable_Name())) {
                                MappingTable mappingTable = MappingTable.create();

                                mappingTable.setName(table.getTable_Name());
                                if (mappingTable.getName().contains("[")) {
                                    System.out.println("PREDICATE " + mappingTable.getName());
                                    mappingTable.setName(mappingTable.getName().substring(0, mappingTable.getName().indexOf("[")));
                                }
                                mappingTable.setOidCol(table.getOid_Col());
                                if (mappingTable.getOidCol().contains(":=SEQUENCE")) {
                                    mappingTable.setOidCol(mappingTable.getOidCol().substring(0, mappingTable.getOidCol().indexOf(":=SEQUENCE")));
                                }
                                mappingTable.setTarget(table.getTarget());

                                mappingTables.put(table.getTable_Name(), mappingTable);
                            } else {
                                MappingTable mappingTable = mappingTables.get(table.getTable_Name());
                                String newTarget = Strings.commonPrefix(table.getTarget(), mappingTable.getTarget());
                                mappingTable.setTarget(newTarget);
                            }
                        }
                    }
                } catch (ClassCastException e) {
                    //ignore
                }
            }
        }

        return mappingTables.values();
    }

    private static List<MappingValue> extractValues(MappingsSequenceType mappings, FeatureTypeMapping ftm, Namespaces namespaces) {
        List<MappingValue> mappingValues = new ArrayList<>();

        if (mappings != null) {
            for (Object mapping : mappings.getTableOrJoinOrAssociationTarget()) {
                try {
                    MappingsSequenceType.Table table = (MappingsSequenceType.Table) mapping;

                    if (table.getTable_Name() != null && !table.getTable_Name().isEmpty() && ftm.hasTable(table.getTable_Name())) {
                        if (table.getTarget() != null && !table.getTarget().isEmpty() && table.getTarget().startsWith(ftm.getTable(table.getTable_Name()).getTarget())) {
                            if ((table.getValue4() != null && !table.getValue4().isEmpty() && table.getUse_Geotypes() == null && table.isMapped_Geometry() == null) || table.getTarget().endsWith("/@xlink:href")) {
                                if (ftm.hasTable(table.getTable_Name())) {
                                    MappingValue mappingValue = MappingValue.create(namespaces);

                                    mappingValue.setTable(ftm.getTable(table.getTable_Name()));//.getValues().add(mappingValue);
                                    mappingValue.setTarget(table.getTarget());
                                    mappingValue.setValue(table.getValue4());
                                    mappingValue.setValueType(table.getValue_Type());
                                    mappingValue.setMappingMode(table.getMapping_Mode());
                                    mappingValue.setDbCodes(table.getDb_Codes());
                                    mappingValue.setDbValues(table.getSchema_Codes());

                                    if (mappingValue.getValueType() == null) {
                                        if (mappingValue.getValue() != null && (mappingValue.getValue().contains("$T$") || mappingValue.getValue().contains("||"))) {
                                            mappingValue.setValueType("expression");
                                        } else {
                                            mappingValue.setValueType("value");
                                        }
                                    }
                                    if (mappingValue.getValue() == null) {
                                        mappingValue.setValue("");
                                    }

                                    if (!mappingValues.contains(mappingValue)) {
                                        mappingValues.add(mappingValue);
                                    }
                                }
                            }
                        }
                    }
                } catch (ClassCastException e) {
                    //ignore
                }
            }
        }

        return mappingValues;
    }

    private static List<MappingJoin> extractJoins(MappingsSequenceType mappings, FeatureTypeMapping ftm) {
        List<MappingJoin> mappingJoins = new ArrayList<>();
        boolean disableMultiJoins = false;

        if (mappings != null) {
            for (Object mapping : mappings.getTableOrJoinOrAssociationTarget()) {
                try {
                    MappingsSequenceType.Join join = (MappingsSequenceType.Join) mapping;

                    if (join.getJoin_Path() != null && !join.getJoin_Path().isEmpty()) {
                        String table = join.getJoin_Path().split("/")[0];
                        if (table.contains("[")) {
                            System.out.println("JOIN PREDICATE " + join.getJoin_Path());
                            table = table.substring(0, table.indexOf("["));
                        }
                        if (ftm.hasTable(table) && (!ftm.getTable(table).hasTarget() || ftm.getTable(table).getTarget().startsWith(join.getTarget()))) {
                            MappingJoin mappingJoin = MappingJoin.create();

                            mappingJoin.setTarget(join.getTarget());
                            //mappingJoin.setAxis(join.getAxis());
                            //mappingJoin.setPath(join.getJoin_Path());
                            parseJoinPath(join.getJoin_Path(), mappingJoin, ftm);

                            if (!disableMultiJoins || mappingJoin.getJoinConditions().size() == 1) {
                                //ftm.getTable(table).addJoinPath(mappingJoin);
                                mappingJoins.add(mappingJoin);
                            }
                        }
                    }
                } catch (ClassCastException e) {
                    // ignore
                }
            }
        }

        return mappingJoins;
    }

    private static List<MappingJoin.Condition> parseJoinPath(String path, MappingJoin mappingJoin, FeatureTypeMapping ftm) {
        List<MappingJoin.Condition> pathTables = new ArrayList<>();
        Map<String, MappingTable> mappingTableMap = new HashMap<>();

        String pathElems[] = path.split("::|/");

        int i = pathElems.length - 1;
        while (i > 0) {
            String props[] = pathElems[i - 1].split(":");

            String sourceTableName = pathElems[i];
            if (sourceTableName.contains("[")) {
                if (sourceTableName.substring(sourceTableName.indexOf("[")).equals("[1=2]")) {
                    ((MappingJoinImpl) mappingJoin).suppressJoin = true;
                }
                sourceTableName = sourceTableName.substring(0, sourceTableName.indexOf("["));
            }
            String sourceField = props[1].substring(0, props[1].length() - 1);
            String targetTableName = pathElems[i - 2];
            if (targetTableName.contains("[")) {
                if (targetTableName.substring(targetTableName.indexOf("[")).equals("[1=2]")) {
                    ((MappingJoinImpl) mappingJoin).suppressJoin = true;
                }
                targetTableName = targetTableName.substring(0, targetTableName.indexOf("["));
            }
            String targetField = props[0].substring(4);
            System.out.println("JOIN PREDICATE " + sourceTableName + " " + targetTableName);

            MappingTable sourceTable = ftm.getTable(sourceTableName);
            if (sourceTable == null) {
                sourceTable = MappingTable.create();
                sourceTable.setName(sourceTableName);
                sourceTable.setOidCol("id");
                ftm.addTable(sourceTable);
            }
            MappingTable targetTable = ftm.getTable(targetTableName);
            if (targetTable == null) {
                targetTable = MappingTable.create();
                targetTable.setName(targetTableName);
                targetTable.setOidCol("id");
                ftm.addTable(targetTable);
            }

            mappingJoin.addCondition(MappingJoin.Condition.create(sourceTable, sourceField, targetTable, targetField));

            i = i - 2;
        }

        return pathTables;
    }

    private static List<AssociationTarget> extractAssociationTargets(MappingsSequenceType mappings) {
        List<AssociationTarget> associationTargets = new ArrayList<>();

        if (mappings != null) {
            for (Object mapping : mappings.getTableOrJoinOrAssociationTarget()) {
                try {
                    MappingsSequenceType.AssociationTarget associationTarget = (MappingsSequenceType.AssociationTarget) mapping;

                    if (associationTarget.getObject_Ref() != null && !associationTarget.getObject_Ref().isEmpty() && associationTarget.getTarget() != null && !associationTarget.getTarget().isEmpty()) {
                        AssociationTarget associationTarget1 = new AssociationTargetImpl();
                        associationTarget1.setObjectRef(associationTarget.getObject_Ref());
                        associationTarget1.setTarget(associationTarget.getTarget());
                        associationTargets.add(associationTarget1);
                    }
                } catch (ClassCastException e) {
                    //ignore
                }
            }
        }

        return associationTargets;
    }

    public static void writeToStream(OutputStream outputStream, XtraServerMappingImpl xtraServerMapping) throws IOException, JAXBException, SAXException {
        ObjectFactory objectFactory = new ObjectFactory();
        FeatureTypes featureTypes = objectFactory.createFeatureTypes();

        featureTypes.getFeatureTypeOrAdditionalMappings().addAll(
                xtraServerMapping.getFeatureTypeMappings().stream().map(featureTypeMapping -> {
                    SQLFeatureTypeImplType sqlFeatureTypeImplType = objectFactory.createSQLFeatureTypeImplType();

                    createMappings(sqlFeatureTypeImplType, featureTypeMapping, objectFactory);

                    FeatureType featureType = objectFactory.createFeatureType();
                    featureType.setName(featureTypeMapping.getName());
                    featureType.setPGISFeatureTypeImpl(sqlFeatureTypeImplType);

                    return featureType;
                }).collect(Collectors.toList())
        );

        featureTypes.getFeatureTypeOrAdditionalMappings().addAll(
                xtraServerMapping.getAdditionalMappings().stream().map(additionalMapping -> {
                    MappingsSequenceType mappingsSequenceType = objectFactory.createMappingsSequenceType();

                    createMappings(mappingsSequenceType, additionalMapping, objectFactory);

                    AdditionalMappings jaxbAdditionalMappings = objectFactory.createAdditionalMappings();
                    jaxbAdditionalMappings.setRootElementName(additionalMapping.getName());
                    jaxbAdditionalMappings.setMappings(mappingsSequenceType);

                    return jaxbAdditionalMappings;
                }).collect(Collectors.toList())
        );

        marshal(outputStream, featureTypes);
    }

    private static void createMappings(MappingsSequenceType mappingsSequenceType, FeatureTypeMapping featureTypeMapping, ObjectFactory objectFactory) {
        featureTypeMapping.getTables().forEach(mappingTable -> {
            // TODO
            if (!(mappingTable.getValues().isEmpty() && mappingTable.getJoinPaths().isEmpty())) {

                mappingsSequenceType.getTableOrJoinOrAssociationTarget().addAll(
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

                mappingsSequenceType.getTableOrJoinOrAssociationTarget().add(table);

                mappingsSequenceType.getTableOrJoinOrAssociationTarget().addAll(
                        mappingTable.getValues().stream().map(mappingValue -> {
                            MappingsSequenceType.Table value = objectFactory.createMappingsSequenceTypeTable();
                            value.setTable_Name(mappingValue.getTable());
                            value.setTarget(mappingValue.getTarget());
                            if (mappingValue.getValue() != null && !mappingValue.getValue().equals(""))
                                value.setValue4(mappingValue.getValue());
                            if (mappingValue.getValueType() != null && !mappingValue.getValueType().equals("value"))
                                value.setValue_Type(mappingValue.getValueType());
                            value.setMapping_Mode(mappingValue.getMappingMode());
                            value.setDb_Codes(mappingValue.getDbCodes());
                            value.setSchema_Codes(mappingValue.getDbValues());
                            return value;
                        }).collect(Collectors.toList())
                );
            }
        });

        mappingsSequenceType.getTableOrJoinOrAssociationTarget().addAll(
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
        if (url == null) {
            url = Objects.firstNonNull(
                    Thread.currentThread().getContextClassLoader(),
                    contextClass.getClassLoader()).getResource(resourceName);
        }
        checkArgument(url != null, "resource %s relative to %s not found.",
                resourceName, contextClass.getName());
        return url;
    }

}
