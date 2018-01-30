package de.interactive_instruments.xtraserver.config.util;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
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
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

                try {
                    xtraServerMapping.addFeatureTypeMapping(ftm);
                } catch (IllegalArgumentException e) {
                    //ignore
                }
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
        extractAssociationTargets(mappings).forEach(associationTarget -> {
            // ignore AssociationTargets that do not match a href mapping
            try {
                featureTypeMapping.addAssociationTarget(associationTarget);
            } catch (IllegalArgumentException e) {
                // ignore
            }
        });
    }

    private static Collection<MappingTable> extractTables(MappingsSequenceType mappings) {
        Map<String, MappingTable> mappingTables = new HashMap<>();

        if (mappings != null) {
            for (Object mapping : mappings.getTableOrJoinOrAssociationTarget()) {
                try {
                    MappingsSequenceType.Table table = (MappingsSequenceType.Table) mapping;

                    if (table.getTable_Name() != null && !table.getTable_Name().isEmpty()) {
                        if ((table.getOid_Col() != null && !table.getOid_Col().isEmpty())
                                || (table.getTarget() != null && !table.getTarget().isEmpty() && table.getValue4() == null)) {
                            //if (table.getValue4() == null || table.getValue4().isEmpty()) {
                            //if (!mappingTables.containsKey(table.getTable_Name())
                            //        || (mappingTables.get(table.getTable_Name()).hasTarget() && (table.getTarget() == null || table.getTarget().isEmpty()))) {
                            MappingTable mappingTable = MappingTable.create();
                            //System.out.println(table.getDerivation_Pattern() + table.getTarget() + table.getOid_Col() + table.getTable_Name());

                            mappingTable.setName(table.getTable_Name());
                            if (mappingTable.getName().contains("[")) {
                                //System.out.println("PREDICATE " + mappingTable.getName());
                                mappingTable.setName(mappingTable.getName().substring(0, mappingTable.getName().indexOf("[")));
                            }
                            mappingTable.setOidCol(table.getOid_Col());
                            if (mappingTable.getOidCol() != null && mappingTable.getOidCol().contains(":=SEQUENCE")) {
                                mappingTable.setOidCol(mappingTable.getOidCol().substring(0, mappingTable.getOidCol().indexOf(":=SEQUENCE")));
                            }

                            mappingTable.setTarget(table.getTarget());
                            // table definition from value mapping, shorten target to first path element
                            if (table.getValue4() != null && !table.getValue4().isEmpty()) {
                                if (mappingTable.getTarget() != null && mappingTable.getTarget().contains("/")) {
                                    mappingTable.setTarget(mappingTable.getTarget().substring(0, mappingTable.getTarget().indexOf("/")));
                                }
                            }

                            // TODO: add all tables, filter in hale import plugin
                            if (!mappingTables.containsKey(mappingTable.getName())
                                    || (mappingTables.get(mappingTable.getName()).hasTarget() && !mappingTable.hasTarget())) {
                                mappingTables.put(mappingTable.getName(), mappingTable);
                            }
                            //}
                            /*else {
                                MappingTable mappingTable = mappingTables.get(table.getTable_Name());
                                String newTarget = Strings.commonPrefix(table.getTarget(), mappingTable.getTarget());
                                mappingTable.setTarget(newTarget);
                            }*/
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
                        if (table.getTarget() != null && !table.getTarget().isEmpty() && table.getTarget().startsWith(ftm.getTable(table.getTable_Name()).get().getTarget())) {
                            if ((table.getValue4() != null && !table.getValue4().isEmpty() && table.getUse_Geotypes() == null && table.isMapped_Geometry() == null) || table.getTarget().endsWith("/@xlink:href")) {
                                if (ftm.hasTable(table.getTable_Name())) {
                                    MappingValue mappingValue = MappingValue.create(namespaces);

                                    mappingValue.setTable(ftm.getTable(table.getTable_Name()).get());//.getValues().add(mappingValue);
                                    mappingValue.setTarget(table.getTarget());
                                    mappingValue.setValue(table.getValue4());
                                    mappingValue.setValueType(table.getValue_Type());
                                    if (!table.getMapping_Mode().equals("value")) {
                                        mappingValue.setMappingMode(table.getMapping_Mode());
                                    }
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
                            //System.out.println("JOIN PREDICATE " + join.getJoin_Path());
                            table = table.substring(0, table.indexOf("["));
                        }
                        if (ftm.hasTable(table) && (ftm.getTable(table).get().hasTarget() && join.getTarget().startsWith(ftm.getTable(table).get().getTarget()))) {
                            MappingJoin mappingJoin = MappingJoin.create();

                            mappingJoin.setTarget(join.getTarget());
                            //mappingJoin.setAxis(join.getAxis());
                            //mappingJoin.setPath(join.getJoin_Path());
                            parseJoinPath(join.getJoin_Path(), mappingJoin, ftm);

                            if (!disableMultiJoins || mappingJoin.getJoinConditions().size() == 1) {
                                //ftm.getTable(table).addJoinPath(mappingJoin);
                                mappingJoins.add(mappingJoin);
                            }
                            // self joins have to be added for value filtering in import
                        } else if (ftm.hasTable(table) && !ftm.getTable(table).get().hasTarget()) {
                            List<String> t = Splitter.on("::").splitToList(join.getJoin_Path());
                            String source = t.get(t.size() - 1);
                            if (t.size() > 2 && source.equals(table)) {
                                MappingJoin mappingJoin = MappingJoin.create();
                                mappingJoin.setTarget(join.getTarget());
                                parseJoinPath(join.getJoin_Path(), mappingJoin, ftm);
                                ((MappingJoinImpl) mappingJoin).suppressJoin = true;
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
            //System.out.println("JOIN PREDICATE " + sourceTableName + " " + targetTableName);

            MappingTable sourceTable = ftm.getTable(sourceTableName)
                    .orElse(createVirtualTable(sourceTableName, ftm));

            MappingTable targetTable = ftm.getTable(targetTableName)
                    .orElse(createVirtualTable(targetTableName, ftm));

            mappingJoin.addCondition(MappingJoin.Condition.create(sourceTable, sourceField, targetTable, targetField));

            i = i - 2;
        }

        return pathTables;
    }

    private static MappingTable createVirtualTable(String name, FeatureTypeMapping featureTypeMapping) {
        MappingTable mappingTable = MappingTable.create();
        mappingTable.setName(name);
        mappingTable.setOidCol("id");
        // TODO
        ((MappingTableImpl) mappingTable).isJoined = true;

        featureTypeMapping.addTable(mappingTable);

        return mappingTable;
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

    public static void writeToStream(OutputStream outputStream, XtraServerMappingImpl xtraServerMapping, boolean createArchiveWithAdditionalFiles) throws IOException, JAXBException, SAXException {
        ObjectFactory objectFactory = new ObjectFactory();
        FeatureTypes featureTypes = objectFactory.createFeatureTypes();

        featureTypes.getFeatureTypeOrAdditionalMappings().addAll(
                xtraServerMapping.getFeatureTypeMappings().stream().map(featureTypeMapping -> {
                    SQLFeatureTypeImplType sqlFeatureTypeImplType = objectFactory.createSQLFeatureTypeImplType();

                    createMappings(sqlFeatureTypeImplType, featureTypeMapping, objectFactory, xtraServerMapping);
                    createXtraServerParameters(sqlFeatureTypeImplType, featureTypeMapping);

                    FeatureType featureType = objectFactory.createFeatureType();
                    featureType.setName(featureTypeMapping.getName());
                    featureType.setPGISFeatureTypeImpl(sqlFeatureTypeImplType);

                    return featureType;
                }).collect(Collectors.toList())
        );

        featureTypes.getFeatureTypeOrAdditionalMappings().addAll(
                xtraServerMapping.getAdditionalMappings().stream().map(additionalMapping -> {
                    MappingsSequenceType mappingsSequenceType = objectFactory.createMappingsSequenceType();

                    createMappings(mappingsSequenceType, additionalMapping, objectFactory, xtraServerMapping);

                    AdditionalMappings jaxbAdditionalMappings = objectFactory.createAdditionalMappings();
                    jaxbAdditionalMappings.setRootElementName(additionalMapping.getName());
                    jaxbAdditionalMappings.setMappings(mappingsSequenceType);

                    return jaxbAdditionalMappings;
                }).collect(Collectors.toList())
        );

        if (createArchiveWithAdditionalFiles) {

            ZipOutputStream zipStream = new ZipOutputStream(outputStream);

            zipStream.putNextEntry(new ZipEntry("XtraSrvConfig_Mapping.inc.xml"));
            marshal(zipStream, featureTypes);

            createAdditionalFiles(zipStream, xtraServerMapping);

            zipStream.close();

        } else {
            marshal(outputStream, featureTypes);
        }
    }

    private static void createAdditionalFiles(ZipOutputStream zipStream, XtraServerMapping xtraServerMapping) throws IOException {

        zipStream.putNextEntry(new ZipEntry("XtraSrvConfig_FeatureTypes.inc.xml"));
        createFeatureTypesFile(zipStream, xtraServerMapping);

        zipStream.putNextEntry(new ZipEntry("XtraSrvConfig_Geoindexes.inc.xml"));
        createGeoIndexesFile(zipStream, xtraServerMapping);

        zipStream.putNextEntry(new ZipEntry("XtraSrvConfig_GetSpatialDataSetSQ.inc.xml"));
        Resources.asByteSource(Resources.getResource(JaxbReaderWriter.class, "/XtraSrvConfig_GetSpatialDataSetSQ.inc.xml.start")).copyTo(zipStream);
        createGetSpatialDataSetSQFileEnd(zipStream, xtraServerMapping);

        zipStream.putNextEntry(new ZipEntry("XtraSrvConfig_StoredQueriesToCache.inc.xml"));
        Resources.asByteSource(Resources.getResource(JaxbReaderWriter.class, "/XtraSrvConfig_StoredQueriesToCache.inc.xml")).copyTo(zipStream);

    }

    private static void createFeatureTypesFile(OutputStream outputStream, XtraServerMapping xtraServerMapping) throws IOException {
        final OutputStreamWriter writer = new OutputStreamWriter(outputStream);

        writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<FeatureTypes xmlns=\"http://www.interactive-instruments.de/namespaces/XtraServer\">\n");

        xtraServerMapping.getFeatureTypeList(false).forEach(featureTypeName -> {
            String featureTypeNameWithoutPrefix = Splitter.on(':').splitToList(featureTypeName).get(1);

            try {
                writer.append("\t<FeatureType defaultSRS=\"{if {$defaultSRS}}{$defaultSRS}{else}EPSG:{$nativeEpsgCode}{fi}\" supportedSRSs=\"srslistWFS\">\n\t\t");
                writer.append("<Name>");
                writer.append(featureTypeName);
                writer.append("</Name>\n\t\t");
                writer.append("{if {$featureTypes.metadataUrl.enabled} == true}{if {$featureTypes.");
                writer.append(featureTypeNameWithoutPrefix);
                writer.append(".metadataUrl}}{if {$featureTypes.");
                writer.append(featureTypeNameWithoutPrefix);
                writer.append(".WFS11.metadataFormat}}{if {$featureTypes.");
                writer.append(featureTypeNameWithoutPrefix);
                writer.append(".WFS11.metadataType}}<MetadataURL format=\"{$featureTypes.");
                writer.append(featureTypeNameWithoutPrefix);
                writer.append(".WFS11.metadataFormat}\" type=\"{$featureTypes.");
                writer.append(featureTypeNameWithoutPrefix);
                writer.append(".WFS11.metadataType}\">{$featureTypes.");
                writer.append(featureTypeNameWithoutPrefix);
                writer.append(".metadataUrl}</MetadataURL>{fi}{else}<MetadataURL>{$featureTypes.");
                writer.append(featureTypeNameWithoutPrefix);
                writer.append(".metadataUrl}</MetadataURL>{fi}{fi}{fi}\n\t");
                writer.append("</FeatureType>\n");
            } catch (IOException e) {

            }
        });

        writer.append("</FeatureTypes>\n");
        writer.flush();
    }

    private static void createGeoIndexesFile(OutputStream outputStream, XtraServerMapping xtraServerMapping) throws IOException {
        final OutputStreamWriter writer = new OutputStreamWriter(outputStream);

        writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<GeoIndexes xmlns=\"http://www.interactive-instruments.de/namespaces/XtraServer\">\n");

        xtraServerMapping.getFeatureTypeList(false).forEach(featureTypeName -> {
            String featureTypeNameWithoutPrefix = Splitter.on(':').splitToList(featureTypeName).get(1);

            xtraServerMapping.getFeatureTypeMapping(featureTypeName, true)
                    .ifPresent(featureTypeMapping -> {
                        // find the first geometric property descending from top to bottom of the inheritance tree
                        Optional<MappingValue> geometricProperty = Lists.reverse(featureTypeMapping.getValues()).stream()
                                .filter(mappingValue -> ((MappingValueImpl) mappingValue).isGeometry())
                                .findFirst();

                        if (geometricProperty.isPresent()) {
                            String propertyName = geometricProperty.get().getTarget();
                            String propertyNameWithoutPrefix = Splitter.on(':').splitToList(propertyName).get(1);

                            try {
                                writer.append("\t<GeoIndex id=\"gidx_");
                                writer.append(featureTypeNameWithoutPrefix);
                                writer.append("_");
                                writer.append(propertyNameWithoutPrefix);
                                writer.append("\">\n\t\t");
                                writer.append("<PGISGeoIndexImpl>\n\t\t\t");
                                writer.append("<PGISGeoIndexFeatures>\n\t\t\t\t");
                                writer.append("<PGISFeatureType>");
                                writer.append(featureTypeName);
                                writer.append("</PGISFeatureType>\n\t\t\t\t");
                                writer.append("<PGISGeoPropertyName>");
                                writer.append(propertyName);
                                writer.append("</PGISGeoPropertyName>\n\t\t\t");
                                writer.append("</PGISGeoIndexFeatures>\n\t\t");
                                writer.append("</PGISGeoIndexImpl>\n\t");
                                writer.append("</GeoIndex>\n");
                            } catch (IOException e) {
                                // ignore
                            }
                        }
                    });
        });

        writer.append("</GeoIndexes>\n");
        writer.flush();
    }

    private static void createGetSpatialDataSetSQFileEnd(OutputStream outputStream, XtraServerMapping xtraServerMapping) throws IOException {
        final OutputStreamWriter writer = new OutputStreamWriter(outputStream);

        xtraServerMapping.getFeatureTypeList(false).forEach(featureTypeName -> {
            try {
                writer.append("\t\t\t<wfs:Query srsName=\"${CRS}\" typeNames=\"");
                writer.append(featureTypeName);
                writer.append("\"/>\n");
            } catch (IOException e) {

            }
        });

        writer.append("\t\t</wfs:QueryExpressionText>\n");
        writer.append("\t</StoredQueryDefinition>\n");
        writer.append("</InitialStoredQueries>\n");
        writer.flush();
    }

    private static void createMappings(MappingsSequenceType mappingsSequenceType, FeatureTypeMapping featureTypeMapping, ObjectFactory objectFactory, XtraServerMapping xtraServerMapping) {
        featureTypeMapping.getTables().forEach(mappingTable -> {
            // TODO
            //if (!(mappingTable.getValues().isEmpty() && mappingTable.getJoinPaths().isEmpty())) {
            if (!(((MappingTableImpl) mappingTable).isJoined && mappingTable.getValues().isEmpty() && mappingTable.getJoinPaths().isEmpty())) {

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

                mappingsSequenceType.getTableOrJoinOrAssociationTarget().addAll(
                        ((MappingTableImpl) mappingTable).getAssociationTargets().stream().map(associationTarget -> {
                            MappingsSequenceType.AssociationTarget associationTarget1 = objectFactory.createMappingsSequenceTypeAssociationTarget();
                            associationTarget1.setObject_Ref(associationTarget.getObjectRef());
                            associationTarget1.setTarget(associationTarget.getTarget());
                            return associationTarget1;
                        }).collect(Collectors.toList())
                );

                ((MappingTableImpl) mappingTable).getAssociationTargets()
                        .forEach(associationTarget -> {

                            Optional<MappingJoin> refJoin = mappingTable.getJoinPaths().stream()
                                    .filter(mappingJoin -> mappingJoin.getTarget().equals(associationTarget.getTarget()))
                                    .findFirst();

                            Optional<MappingValue> refValue = mappingTable.getValues().stream()
                                    .filter(value -> value.getTarget().equals(associationTarget.getTarget() + "/@xlink:href"))
                                    .findFirst();

                            Optional<FeatureTypeMapping> refMapping = ((XtraServerMappingImpl) xtraServerMapping).getTypeMapping(associationTarget.getObjectRef(), true);

                            if (refValue.isPresent() && refMapping.isPresent()) {
                                // join is not connected to FeatureType
                                if ((refJoin.isPresent() && !refMapping.get().getPrimaryTableNames().contains(refJoin.get().getTargetTable()))
                                        || !refMapping.get().getPrimaryTableNames().contains(refValue.get().getTable())) {
                                    //System.out.println("Adding navigation join for AssociationTarget " + associationTarget.toString());
                                    String sourceTable = refValue.get().getTable();
                                    String sourceField = refValue.get().getValue().replaceAll(".*\\$T\\$\\.", "");
                                    //System.out.println(sourceTable);
                                    //System.out.println(sourceField);

                                    // special case reference without join, add not-null predicate for optimization
                                    if (!refJoin.isPresent()) {
                                        MappingsSequenceType.Table predicate = objectFactory.createMappingsSequenceTypeTable();
                                        predicate.setTable_Name(refValue.get().getTable() + "[" + refValue.get().getValue().substring(refValue.get().getValue().indexOf("$T$.")) + " IS NOT NULL]");
                                        predicate.setTarget(associationTarget.getTarget());
                                        mappingsSequenceType.getTableOrJoinOrAssociationTarget().add(predicate);
                                    }

                                    List<MappingValue> refMappingIds = refMapping.get().getValues().stream()
                                            .filter(value -> value.getTarget().equals("@gml:id"))
                                            .collect(Collectors.toList());

                                    refMappingIds.forEach(refMappingId -> {
                                        String targetTable = refMappingId.getTable();
                                        String targetField = refMappingId.getValue().replaceAll(".*\\$T\\$\\.", "");
                                        //System.out.println(targetTable);
                                        //System.out.println(targetField);

                                        MappingJoin mappingJoin = MappingJoin.create();
                                        mappingJoin.setTarget(associationTarget.getTarget() + "/" + associationTarget.getObjectRef());
                                        mappingJoin.addCondition(MappingJoin.Condition.create(mappingTable, sourceField, refMapping.get().getTable(refMappingId.getTable()).get(), targetField));

                                        MappingsSequenceType.Join join = objectFactory.createMappingsSequenceTypeJoin();
                                        join.setAxis(mappingJoin.getAxis());
                                        join.setTarget(mappingJoin.getTarget());
                                        join.setJoin_Path(mappingJoin.getPath());
                                        mappingsSequenceType.getTableOrJoinOrAssociationTarget().add(join);
                                    });


                                } else {
                                    // warn
                                    //System.out.println("AssociationTarget is navigable " + associationTarget.toString());
                                }
                            } else {
                                // warn
                                //System.out.println("Mapping for AssociationTarget not found " + associationTarget.toString());
                            }
                        });
            }
        });

        /*mappingsSequenceType.getTableOrJoinOrAssociationTarget().addAll(
                featureTypeMapping.getAssociationTargets().stream().map(associationTarget -> {
                    MappingsSequenceType.AssociationTarget associationTarget1 = objectFactory.createMappingsSequenceTypeAssociationTarget();
                    associationTarget1.setObject_Ref(associationTarget.getObjectRef());
                    associationTarget1.setTarget(associationTarget.getTarget());
                    return associationTarget1;
                }).collect(Collectors.toList())
        );*/

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

    private static void createXtraServerParameters(SQLFeatureTypeImplType sqlFeatureTypeImplType, FeatureTypeMapping featureTypeMapping) {
        sqlFeatureTypeImplType.setLogging("false");
        sqlFeatureTypeImplType.setUseTempTable(false);
        if (!featureTypeMapping.getName().endsWith(":AbstractFeature")) {
            sqlFeatureTypeImplType.setTempTableName("_xsv_tmp_" + Joiner.on('_').join(featureTypeMapping.getPrimaryTableNames()));
        }
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
