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
package de.interactive_instruments.xtraserver.config.util;

import de.interactive_instruments.xtraserver.config.util.api.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zahnen
 */
public class AbstractMappingTransformer implements MappingTransformer {

    protected final XtraServerMapping xtraServerMapping;

    public AbstractMappingTransformer(XtraServerMapping xtraServerMapping) {
        this.xtraServerMapping = xtraServerMapping;
    }

    @Override
    public XtraServerMapping transform() {
        final List<FeatureTypeMapping> transformedFeatureTypeMappings = xtraServerMapping.getFeatureTypeMappings().stream()
                .map(traverseFeatureTypeMapping())
                .collect(Collectors.toList());

        return transformXtraServerMapping(xtraServerMapping, transformedFeatureTypeMappings).build();
    }

    @Override
    public FeatureTypeMapping transform(FeatureTypeMapping featureTypeMapping) {
        return traverseFeatureTypeMapping().apply(featureTypeMapping);
    }

    protected XtraServerMappingBuilder transformXtraServerMapping(final XtraServerMapping xtraServerMapping, final List<FeatureTypeMapping> transformedFeatureTypeMappings) {
        return new XtraServerMappingBuilder()
                //.shallowCopyOf(xtraServerMapping)
                .featureTypeMappings(transformedFeatureTypeMappings);
    }

    protected FeatureTypeMappingBuilder transformFeatureTypeMapping(final FeatureTypeMapping featureTypeMapping, final List<MappingTable> transformedMappingTables) {
        return new FeatureTypeMappingBuilder()
                .shallowCopyOf(featureTypeMapping)
                .primaryTables(transformedMappingTables);
    }

    protected MappingTableBuilder transformMappingTable(final MappingTable mappingTable, final List<MappingTable> transformedMappingTables, final List<MappingJoin> transformedMappingJoins, final List<MappingValue> transformedMappingValues) {
        return new MappingTableBuilder()
                .shallowCopyOf(mappingTable)
                .values(transformedMappingValues)
                .joiningTables(transformedMappingTables)
                .joinPaths(transformedMappingJoins);
    }

    protected MappingValueBuilder.ValueDefault transformMappingValue(final MappingValue mappingValue) {
        return new MappingValueBuilder()
                .copyOf(mappingValue);
    }

    private Function<FeatureTypeMapping, FeatureTypeMapping> traverseFeatureTypeMapping() {
        return featureTypeMapping -> {

            final List<MappingTable> transformedTables = featureTypeMapping.getPrimaryTables().stream()
                    .map(traverseMappingTable())
                    .collect(Collectors.toList());

            return transformFeatureTypeMapping(featureTypeMapping, transformedTables).build();
        };
    }

    private Function<MappingTable, MappingTable> traverseMappingTable() {
        return mappingTable -> {

            // recurse
            final List<MappingTable> transformedJoiningTables = mappingTable.getJoiningTables().stream()
                    .map(traverseMappingTable())
                    .collect(Collectors.toList());

            final List<MappingValue> transformedValues = mappingTable.getValues().stream()
                    .map(traverseValue())
                    .collect(Collectors.toList());

            return transformMappingTable(mappingTable, transformedJoiningTables, mappingTable.getJoinPaths().asList(), transformedValues).build();

        };
    }

    private Function<MappingValue, MappingValue> traverseValue() {
        return mappingValue -> {

            return transformMappingValue(mappingValue).build();
        };
    }
}
