/**
 * Copyright 2018 interactive instruments GmbH
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.interactive_instruments.xtraserver.config.transformer;

import de.interactive_instruments.xtraserver.config.api.*;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author zahnen
 */
class MappingTransformerFlattenInheritance implements MappingTransformer {

    private final XtraServerMapping xtraServerMapping;

    MappingTransformerFlattenInheritance(final XtraServerMapping xtraServerMapping) {
        this.xtraServerMapping = xtraServerMapping;
    }

    @Override
    public XtraServerMapping transform() {

        final List<FeatureTypeMapping> transformedFeatureTypeMappings = xtraServerMapping.getFeatureTypeMappings().stream()
                .filter(hasNoChildren())
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

    private Predicate<FeatureTypeMapping> hasNoChildren() {
        return featureTypeMapping -> xtraServerMapping.getFeatureTypeMappings().stream()
                .noneMatch(featureTypeMapping1 -> featureTypeMapping1.getSuperTypeName().isPresent() && featureTypeMapping.getName().equals(featureTypeMapping1.getSuperTypeName().get()));
    }

    @Override
    public FeatureTypeMapping transform(final FeatureTypeMapping featureTypeMapping) {

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

    private FeatureTypeMapping mergeMappings(final FeatureTypeMapping mainMapping, final List<MappingTable> mergingTables) {

        final List<MappingTable> transformedTables = mainMapping.getPrimaryTables().stream()
                .map(mergeTables(mergingTables))
                .collect(Collectors.toList());

        return new FeatureTypeMappingBuilder()
                .shallowCopyOf(mainMapping)
                .primaryTables(transformedTables)
                .build();
    }

    private Function<MappingTable, MappingTable> mergeTables(final List<MappingTable> mergingTables) {
        return mainTable -> {
            // if primary do not copy values ???
            final MappingTable initialTable = mainTable.isPrimary()
                    ? new MappingTableBuilder().shallowCopyOf(mainTable).joiningTables(mainTable.getJoiningTables()).build()
                    : new MappingTableBuilder().copyOf(mainTable).build();

            return mergingTables.stream()
                    .filter(mergingTable -> mergingTable.getName().equals(mainTable.getName()) && mergingTable.getTargetPath().equals(mainTable.getTargetPath()))
                    .reduce(initialTable, this::mergeTable);
        };
    }

    private MappingTable mergeTable(final MappingTable mainTable, final MappingTable mergingTable) {

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
