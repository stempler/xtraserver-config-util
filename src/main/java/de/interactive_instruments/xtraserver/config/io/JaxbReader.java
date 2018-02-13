/**
 * Copyright 2018 interactive instruments GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.interactive_instruments.xtraserver.config.io;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import de.interactive_instruments.xtraserver.config.schema.AdditionalMappings;
import de.interactive_instruments.xtraserver.config.schema.FeatureType;
import de.interactive_instruments.xtraserver.config.schema.FeatureTypes;
import de.interactive_instruments.xtraserver.config.schema.MappingsSequenceType;
import de.interactive_instruments.xtraserver.config.util.ApplicationSchema;
import de.interactive_instruments.xtraserver.config.util.Namespaces;
import de.interactive_instruments.xtraserver.config.util.api.*;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Helper methods for JAXB unmarshalling
 */
public class JaxbReader {

    final static String MAPPING_SCHEMA = "/XtraServer_Mapping.xsd";
    final static QName GML_ABSTRACT_FEATURE = new QName("http://www.opengis.net/gml/3.2", "AbstractFeature");

    private final ApplicationSchema applicationSchema;
    private final Namespaces namespaces;
    private XmlSchemaComplexType currentSchemaType;
    private String currentFeatureTypeDescription;

    public JaxbReader(ApplicationSchema applicationSchema) {
        this.applicationSchema = applicationSchema;
        this.namespaces = applicationSchema.getNamespaces();
    }

    public XtraServerMapping readFromStream(final InputStream inputStream) throws IOException, JAXBException, SAXException {
        final FeatureTypes featureTypes = unmarshal(inputStream);

        final Stream<Optional<FeatureTypeMapping>> featureTypeMappings = readFeatureTypeMappings(featureTypes);

        final Stream<Optional<FeatureTypeMapping>> abstractFeatureTypeMappings = readAdditionalMappings(featureTypes);

        List<FeatureTypeMapping> featureTypeMappingList = Stream
                .concat(featureTypeMappings, abstractFeatureTypeMappings)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        return new XtraServerMappingBuilder()
                .featureTypeMappings(featureTypeMappingList)
                .build();
    }

    private Stream<Optional<FeatureTypeMapping>> readFeatureTypeMappings(FeatureTypes featureTypes) {
        return featureTypes.getFeatureTypeOrAdditionalMappings().stream()
                .filter(FeatureType.class::isInstance)
                .map(FeatureType.class::cast)
                .filter(featureType -> applicationSchema.hasElement(featureType.getName()))
                .peek(featureType -> System.out.println("\nFT: " + featureType.getName()))
                .map(createFeatureTypeMapping());
    }

    private Stream<Optional<FeatureTypeMapping>> readAdditionalMappings(FeatureTypes featureTypes) {
        return featureTypes.getFeatureTypeOrAdditionalMappings().stream()
                .filter(AdditionalMappings.class::isInstance)
                .map(AdditionalMappings.class::cast)
                .filter(additionalMapping -> applicationSchema.hasElement(additionalMapping.getRootElementName()))
                .peek(additionalMapping -> System.out.println("\nAFT: " + additionalMapping.getRootElementName()))
                .map(createAbstractFeatureTypeMapping());
    }

    private Function<FeatureType, Optional<FeatureTypeMapping>> createFeatureTypeMapping() {
        return featureType -> createFeatureTypeMapping(featureType.getName(), namespaces.getQualifiedName(featureType.getName()), false, extractMappings(featureType));
    }

    private Function<AdditionalMappings, Optional<FeatureTypeMapping>> createAbstractFeatureTypeMapping() {
        return additionalMapping -> createFeatureTypeMapping(additionalMapping.getRootElementName(), namespaces.getQualifiedName(additionalMapping.getRootElementName()), true, additionalMapping.getMappings());
    }

    private Optional<FeatureTypeMapping> createFeatureTypeMapping(final String name, final QName qualifiedName, final boolean isAbstract, MappingsSequenceType mappings) {
        this.currentSchemaType = applicationSchema.getType(qualifiedName);
        this.currentFeatureTypeDescription = qualifiedName.getLocalPart();

        // TODO: is already ignored in filter above
        if (currentSchemaType == null) {
            System.out.println("FeatureType '" + name + "' not found in application schema, skipping");
            return Optional.empty();
        }

        //final List<MappingTable> mappingTables = createTables(mappings);

        List<MappingJoin> joins = createJoinPaths(mappings);

        Map<String, Collection<MappingValue>> valuePool = createValuePool(mappings);

        List<MappingTableBuilder.MappingTableDraft> tableDrafts = Stream.concat(createPrimaryTableDrafts(mappings), createJoinedTableDrafts(mappings, joins))
                .sorted(longestTargetPathFirst())
                .map(addValues(valuePool))
                //.map(addJoinPaths(joins))
                .collect(Collectors.toList());

        final List<MappingTable> mappingTables = nestAndBuildTableDrafts(tableDrafts, joins);

        return Optional.of(new FeatureTypeMappingBuilder()
                .name(name)
                .qualifiedName(qualifiedName)
                .superTypeName(applicationSchema.getSuperTypeName(qualifiedName).orElse(null))
                .isAbstract(isAbstract || qualifiedName.equals(GML_ABSTRACT_FEATURE))
                .primaryTables(mappingTables)
                .build());
    }

    private Comparator<MappingTable> longestTargetPathFirst() {
        return Comparator.<MappingTable>comparingInt(mappingTable -> mappingTable.getQualifiedTargetPath().size()).reversed();
    }

    private Stream<MappingTableBuilder.MappingTableDraft> createPrimaryTableDrafts(MappingsSequenceType mappings) {
        return mappings.getTableOrJoinOrAssociationTarget().stream()
                .filter(MappingsSequenceType.Table.class::isInstance)
                .map(MappingsSequenceType.Table.class::cast)
                .filter(isPrimaryTable())
                .peek(table -> System.out.println(MessageFormat.format("SOURCE: (name={0}, target={1})", table.getTable_Name(), table.getTarget())))
                .map(table -> createTableDraft(table.getTable_Name(), table.getTarget(), table.getOid_Col(), true, false));
    }

    private Stream<MappingTableBuilder.MappingTableDraft> createJoinedTableDrafts(MappingsSequenceType mappings, List<MappingJoin> mappingJoins) {
        return mappingJoins.stream()
                .map(createJoinedTableDraft(mappings))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .peek(table -> System.out.println("JOINED: " + table));

    }

    private Function<MappingJoin, Optional<MappingTableBuilder.MappingTableDraft>> createJoinedTableDraft(MappingsSequenceType mappings) {
        return join -> mappings.getTableOrJoinOrAssociationTarget().stream()
                .filter(MappingsSequenceType.Table.class::isInstance)
                .map(MappingsSequenceType.Table.class::cast)
                .filter(hasOidColForTable(join.getTargetTable(), join.getTargetPath()))
                .peek(table2 -> System.out.println("JOINING SOURCE: " + join.getTargetTable()))
                .map(table -> createTableDraft(join.getTargetTable(), join.getTargetPath(), table.getOid_Col(), false, table.getValue() != null && !table.getValue().isEmpty()))
                .findFirst();
    }

    private List<MappingJoin> createJoinPaths(MappingsSequenceType mappings) {
        return mappings.getTableOrJoinOrAssociationTarget().stream()
                .filter(MappingsSequenceType.Join.class::isInstance)
                .map(MappingsSequenceType.Join.class::cast)
                //TODO
                .filter(join -> !join.getJoin_Path().contains("[1=2]/ref"))
                .map(createJoin())
                .collect(Collectors.toList());
    }

    private MappingTableBuilder.MappingTableDraft createTableDraft(final String name, final String target, final String oidCol, final boolean primary, final boolean isFromValue) {

        String name1 = name;
        if (name1.contains("[")) {
            //System.out.println("PREDICATE " + mappingTable.getName());
            name1 = name1.substring(0, name1.indexOf("["));
        }

        String target1 = target == null ? "" : target;
        // table definition from value mapping, shorten target to first path element
        // why ???
        /*if (isFromValue) {
            if (target1.contains("/")) {
                target1 = target1.substring(0, target1.indexOf("/"));
            }
        }*/

        String oidCol1 = oidCol;
        if (oidCol1 != null && oidCol1.contains(":=SEQUENCE")) {
            oidCol1 = oidCol1.substring(0, oidCol1.indexOf(":=SEQUENCE"));
        }

        List<QName> targetPathElements = !target1.isEmpty() ? applicationSchema.getNamespaces().getQualifiedPathElements(target1) : ImmutableList.of();

        return new MappingTableBuilder()
                .name(name1)
                .primaryKey(oidCol1)
                .targetPath(target1)
                .qualifiedTargetPath(targetPathElements)
                .description(primary ? currentFeatureTypeDescription : target1.split(":")[1])
                .buildDraft();
    }

    private Map<String, Collection<MappingValue>> createValuePool(MappingsSequenceType mappings) {
        ImmutableListMultimap.Builder<String, MappingValue> valuePool = ImmutableListMultimap.builder();

        mappings.getTableOrJoinOrAssociationTarget().stream()
                .filter(MappingsSequenceType.Table.class::isInstance)
                .map(MappingsSequenceType.Table.class::cast)
                .filter(isValueMapping())
                .map(createValueMappingEntry(mappings))
                .forEach(valuePool::put);

        return ArrayListMultimap.create(valuePool.build()).asMap();
    }

    private Function<MappingTableBuilder.MappingTableDraft, MappingTableBuilder.MappingTableDraft> addValues(Map<String, Collection<MappingValue>> valuePool) {
        return tableDraft -> {
            List<MappingValue> mappingValues = ImmutableList.of();
            if (valuePool.get(tableDraft.getName()) != null) {
                mappingValues = valuePool.get(tableDraft.getName()).stream()
                        .filter(value -> value.getTargetPath().startsWith(tableDraft.getTargetPath()))
                        .collect(Collectors.toList());

                final List<MappingValue> mappingValues2 = mappingValues;
                final Map<MappingValue, Integer> counter = new HashMap<>();
                valuePool.get(tableDraft.getName()).removeIf(mappingValue -> {
                    if (mappingValues2.contains(mappingValue) && counter.get(mappingValue) == null) {
                        counter.putIfAbsent(mappingValue, 0);
                        return true;
                    }
                    return false;
                });
            }

            return new MappingTableBuilder()
                    .shallowCopyOf(tableDraft)
                    .values(mappingValues)
                    .buildDraft();
        };
    }

    private Function<MappingTableBuilder.MappingTableDraft, MappingTableBuilder.MappingTableDraft> addJoinPaths(List<MappingJoin> joins, String sourceTable) {
        return tableDraft -> {
            List<MappingJoin> mappingJoins = joins.stream()
                    .filter(join -> join.getTargetTable().equals(tableDraft.getName())
                            && join.getTargetPath().equals(tableDraft.getTargetPath())
                            && join.getSourceTable().equals(sourceTable))
                    .collect(Collectors.toList());

            return new MappingTableBuilder()
                    .copyOf(tableDraft)
                    .joinPaths(mappingJoins)
                    .buildDraft();
        };
    }

    private Function<MappingTableBuilder.MappingTableDraft, MappingTable> addJoiningTablesAndBuild(List<MappingTableBuilder.MappingTableDraft> tables, List<MappingJoin> joins) {
        return tableDraft -> new MappingTableBuilder()
                .copyOf(tableDraft)
                .joiningTables(tables.stream()
                        .map(addJoinPaths(joins, tableDraft.getName()))
                        .filter(isJoining(tableDraft))
                        .collect(Collectors.toList()))
                .build();
    }

    private List<MappingTable> nestAndBuildTableDrafts(List<MappingTableBuilder.MappingTableDraft> tables, List<MappingJoin> joins) {

        return tables.stream()
                .filter(MappingTable::isPrimary)
                .map(addJoiningTablesAndBuild(tables, joins))
                .collect(Collectors.toList());
    }

    private Predicate<MappingTable> isJoining(MappingTable mappingTable) {
        return joiningTable -> !joiningTable.getJoinPaths().isEmpty()
                && joiningTable.getJoinPaths().iterator().next().getSourceTable().equals(mappingTable.getName())
                && joiningTable.getTargetPath().startsWith(mappingTable.getTargetPath());
    }

    private Function<MappingsSequenceType.Join, MappingJoin> createJoin() {
        return join -> new MappingJoinBuilder()
                .targetPath(join.getTarget())
                .joinConditions(parseJoinPath(join.getJoin_Path()))
                .build();
    }

    private List<MappingJoin.Condition> parseJoinPath(String path) {
        List<MappingJoin.Condition> pathTables = new ArrayList<>();

        String pathElems[] = path.split("::|/");

        int i = pathElems.length - 1;
        while (i > 0) {
            String props[] = pathElems[i - 1].split(":");

            String sourceTableName = pathElems[i];
            if (sourceTableName.contains("[")) {
                if (sourceTableName.substring(sourceTableName.indexOf("[")).equals("[1=2]")) {
                    // TODO ((MappingJoinImpl) mappingJoin).suppressJoin = true;
                }
                sourceTableName = sourceTableName.substring(0, sourceTableName.indexOf("["));
            }
            String sourceField = props[1].substring(0, props[1].length() - 1);
            String targetTableName = pathElems[i - 2];
            if (targetTableName.contains("[")) {
                if (targetTableName.substring(targetTableName.indexOf("[")).equals("[1=2]")) {
                    // TODO ((MappingJoinImpl) mappingJoin).suppressJoin = true;
                }
                targetTableName = targetTableName.substring(0, targetTableName.indexOf("["));
            }
            String targetField = props[0].substring(4);
            //System.out.println("JOIN PREDICATE " + sourceTableName + " " + targetTableName);

            /*MappingTable sourceTable = ftm.getTable(sourceTableName)
                    .orElse(createVirtualTable(sourceTableName, ftm));

            MappingTable targetTable = ftm.getTable(targetTableName)
                    .orElse(createVirtualTable(targetTableName, ftm));*/

            //mappingJoin.addCondition(MappingJoin.Condition.create(sourceTableName, sourceField, targetTableName, targetField));

            pathTables.add(new MappingJoinBuilder.ConditionBuilder()
                    .sourceTable(sourceTableName)
                    .sourceField(sourceField)
                    .targetTable(targetTableName)
                    .targetField(targetField)
                    .build());

            i = i - 2;
        }

        return pathTables;
    }

    private Function<MappingsSequenceType.Table, Map.Entry<String, MappingValue>> createValueMappingEntry(final MappingsSequenceType mappings) {
        return value -> {

            MappingValueBuilder.ValueDefault builder = null;

            if (value.getDb_Codes() != null && !value.getDb_Codes().isEmpty()
                    && value.getSchema_Codes() != null && !value.getSchema_Codes().isEmpty()) {

                MappingValueBuilder.ValueClassification classificationBuilder;

                if (value.getMapping_Mode() != null && value.getMapping_Mode().equals("nil")) {
                    classificationBuilder = new MappingValueBuilder().nil();
                } else {
                    classificationBuilder = new MappingValueBuilder().classification();
                }

                String[] keys = value.getDb_Codes().split(" ");
                String[] values = value.getSchema_Codes().split(" ");

                for (int i = 0; i < Math.min(keys.length, values.length); i++) {
                    classificationBuilder.keyValue(keys[i], values[i]);
                }

                builder = classificationBuilder;
            } else if (value.getTarget() != null && value.getTarget().endsWith("/@xlink:href")) {
                String parentTarget = value.getTarget().substring(0, value.getTarget().lastIndexOf("/@xlink:href"));

                Optional<MappingsSequenceType.AssociationTarget> associationTarget = mappings.getTableOrJoinOrAssociationTarget().stream()
                        .filter(MappingsSequenceType.AssociationTarget.class::isInstance)
                        .map(MappingsSequenceType.AssociationTarget.class::cast)
                        .filter(associationTarget1 -> associationTarget1.getTarget() != null && associationTarget1.getTarget().equals(parentTarget))
                        .findFirst();

                if (associationTarget.isPresent()) {
                    builder = new MappingValueBuilder().reference()
                            .referencedFeatureType(associationTarget.get().getObject_Ref());
                }

            } else if ((value.getValue() != null && value.getValue().contains("$T$."))
                    || (value.getValue_Type() != null && value.getValue_Type().equals("expression"))) {
                builder = new MappingValueBuilder().expression();
            } else if (value.getValue_Type() != null && value.getValue_Type().equals("constant")) {
                builder = new MappingValueBuilder().constant();
            }

            List<QName> targetPathElements = value.getTarget() != null ? applicationSchema.getNamespaces().getQualifiedPathElements(value.getTarget()) : ImmutableList.of();

            if (builder == null) {
                if (!targetPathElements.isEmpty() && currentSchemaType != null && applicationSchema.isGeometry(currentSchemaType, targetPathElements.get(0))) {
                    builder = new MappingValueBuilder().geometry();
                } else {
                    builder = new MappingValueBuilder().column();
                }
            }

            if (!targetPathElements.isEmpty()) {
                builder = builder.description(targetPathElements.get(0).getLocalPart());
            }

            return Maps.immutableEntry(value.getTable_Name(), builder
                    .targetPath(value.getTarget())
                    .qualifiedTargetPath(targetPathElements)
                    .value(value.getValue() == null ? "" : value.getValue())
                    .build());
        };
    }









    private Predicate<MappingsSequenceType.Table> isPrimaryTable() {
        return table -> table.getTable_Name() != null && !table.getTable_Name().isEmpty()
                && table.getOid_Col() != null && !table.getOid_Col().isEmpty()
                && (table.getTarget() == null || table.getTarget().isEmpty());
    }

    private Predicate<MappingsSequenceType.Table> hasOidColForTable(final String tableName, final String tableTarget) {
        return table -> table.getTable_Name() != null && table.getTable_Name().equals(tableName)
                && table.getOid_Col() != null && !table.getOid_Col().isEmpty()
                && table.getTarget() != null && !table.getTarget().isEmpty() && table.getTarget().startsWith(tableTarget);
    }

    private Predicate<MappingsSequenceType.Table> isValueMapping() {
        return value -> value.getTable_Name() != null && !value.getTable_Name().isEmpty()
                && value.getTarget() != null && !value.getTarget().isEmpty()
                && ((value.getValue() != null && !value.getValue().isEmpty() && value.getUse_Geotypes() == null && value.isMapped_Geometry() == null
                && !value.isFilter_Mapping() && (value.getGmlVersion() == null || value.getGmlVersion().equals("3.2.1")))
                || value.getTarget().endsWith("/@xlink:href"));
    }

    private Predicate<MappingsSequenceType.Table> isValueMappingForTable(final String tableName, final String tableTarget) {
        return value -> value.getTable_Name() != null && !value.getTable_Name().isEmpty() && value.getTable_Name().equals(tableName)
                && value.getTarget() != null && !value.getTarget().isEmpty() && value.getTarget().startsWith(tableTarget)
                && ((value.getValue() != null && !value.getValue().isEmpty() && value.getUse_Geotypes() == null && value.isMapped_Geometry() == null
                && !value.isFilter_Mapping() && (value.getGmlVersion() == null || value.getGmlVersion().equals("3.2.1")))
                || value.getTarget().endsWith("/@xlink:href"));
    }

    private Predicate<MappingsSequenceType.Join> isJoinForSourceTable(final String tableName, final String tableTarget) {
        return join -> {

            if (join.getJoin_Path() != null && !join.getJoin_Path().isEmpty()
                    && join.getTarget() != null && !join.getTarget().isEmpty()
                    && join.getTarget().startsWith(tableTarget) && !join.getTarget().equals(tableTarget)) {
                String table = join.getJoin_Path().contains("::") ? join.getJoin_Path().substring(join.getJoin_Path().lastIndexOf("::") + 2) : join.getJoin_Path();
                if (table.contains("[")) {
                    //System.out.println("JOIN PREDICATE " + join.getJoin_Path());
                    table = table.substring(0, table.indexOf("["));
                }
                return table.equals(tableName);
            }
            return false;
        };
    }

    private Predicate<MappingsSequenceType.Join> isJoinForSourceAndTargetTable(final String sourceTableName, final String targetTableName, final String tableTarget) {
        return join -> {

            if (join.getJoin_Path() != null && !join.getJoin_Path().isEmpty()
                    && join.getTarget() != null && !join.getTarget().isEmpty()
                    && join.getTarget().startsWith(tableTarget)) {
                String sourceTable = join.getJoin_Path().contains("::") ? join.getJoin_Path().substring(join.getJoin_Path().lastIndexOf("::") + 2) : join.getJoin_Path();
                if (sourceTable.contains("[")) {
                    //System.out.println("JOIN PREDICATE " + join.getJoin_Path());
                    sourceTable = sourceTable.substring(0, sourceTable.indexOf("["));
                }
                String targetTable = join.getJoin_Path().split("/")[0];
                if (targetTable.contains("[")) {
                    //System.out.println("JOIN PREDICATE " + join.getJoin_Path());
                    targetTable = targetTable.substring(0, targetTable.indexOf("["));
                }
                return sourceTable.equals(sourceTableName) && targetTable.equals(targetTableName);
            }
            return false;
        };
    }

    private MappingsSequenceType extractMappings(FeatureType featureType) {
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

    private FeatureTypes unmarshal(InputStream inputStream) throws JAXBException, IOException, SAXException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(Resources.getResource(JaxbReader.class, MAPPING_SCHEMA));
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
}