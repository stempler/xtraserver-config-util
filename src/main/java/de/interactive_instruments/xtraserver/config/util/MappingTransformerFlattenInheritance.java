package de.interactive_instruments.xtraserver.config.util;

import de.interactive_instruments.xtraserver.config.util.api.*;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author zahnen
 */
public class MappingTransformerFlattenInheritance implements MappingTransformer {

    private final XtraServerMapping xtraServerMapping;
    private final ApplicationSchema applicationSchema;

    public MappingTransformerFlattenInheritance(XtraServerMapping xtraServerMapping, ApplicationSchema applicationSchema) {
        this.xtraServerMapping = xtraServerMapping;
        this.applicationSchema = applicationSchema;
    }

    @Override
    public XtraServerMapping transform() {

        final List<FeatureTypeMapping> transformedFeatureTypeMappings = xtraServerMapping.getFeatureTypeMappings().stream()
                .filter(isNotAbstract())
                .map(transformFeatureType())
                .collect(Collectors.toList());

        return new XtraServerMappingBuilder()
                .featureTypeMappings(transformedFeatureTypeMappings)
                .build();
    }

    private Predicate<FeatureTypeMapping> isNotAbstract() {
        return featureTypeMapping -> !featureTypeMapping.isAbstract()
                && !featureTypeMapping.getQualifiedName().equals(new QName("http://www.opengis.net/gml/3.2", "AbstractFeature"));
    }

    @Override
    public FeatureTypeMapping transform(FeatureTypeMapping featureTypeMapping) {

        return transformFeatureType().apply(featureTypeMapping);
    }

    private Function<FeatureTypeMapping, FeatureTypeMapping> transformFeatureType() {
        return featureTypeMapping -> {


            //final List<MappingTable> parentTables = applicationSchema.getAllSuperTypeNames(featureTypeMapping.getQualifiedName()).stream()
            //        .map(xtraServerMapping::getFeatureTypeMapping)
            //        .filter(Optional::isPresent)

            // get list of all primary tables from super type mappings
            // includes mainMapping
            final List<MappingTable> parentTables = xtraServerMapping.getFeatureTypeMappingInheritanceChain(featureTypeMapping.getName()).stream()
                    .flatMap(parentMapping -> parentMapping.getPrimaryTables().stream())
                    .collect(Collectors.toList());

            return mergeMappings(featureTypeMapping, parentTables);
        };
    }

    private FeatureTypeMapping mergeMappings(FeatureTypeMapping mainMapping, List<MappingTable> mergingTables) {

        final List<MappingTable> transformedTables = mainMapping.getPrimaryTables().stream()
                .map(mergeTables(mergingTables))
                .collect(Collectors.toList());

        return new FeatureTypeMappingBuilder()
                .shallowCopyOf(mainMapping)
                .primaryTables(transformedTables)
                .build();
    }

    private Function<MappingTable, MappingTable> mergeTables(List<MappingTable> mergingTables) {
        return mainTable -> {
            // if primary do not copy values ???
            MappingTable initialTable = mainTable.isPrimary()
                    ? new MappingTableBuilder().shallowCopyOf(mainTable).joiningTables(mainTable.getJoiningTables()).build()
                    : new MappingTableBuilder().copyOf(mainTable).build();

            return mergingTables.stream()
                    .filter(mergingTable -> mergingTable.getName().equals(mainTable.getName()) && mergingTable.getTargetPath().equals(mainTable.getTargetPath()))
                    .reduce(initialTable, this::mergeTable);
        };
    }

    private MappingTable mergeTable(MappingTable mainTable, MappingTable mergingTable) {

        // TODO: for joining tables we do not want an intersection but a union
        // nontheless matching tables might have to be merged
        // do not recurse
        /*final List<MappingTable> transformedJoiningTables = mainTable.getJoiningTables().stream()
                .map(mergeTables(mergingTable.getJoiningTables().asList()))
                .collect(Collectors.toList());
*/

        return new MappingTableBuilder()
                .copyOf(mainTable)
                .values(mergingTable.getValues())
                .joinPaths(mergingTable.getJoinPaths())
                .joiningTables(mergingTable.getJoiningTables())
                .build();
    }

}
