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
package de.interactive_instruments.xtraserver.config.transformer;

import de.interactive_instruments.xtraserver.config.api.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zahnen
 */
class AbstractMappingTransformer implements MappingTransformer {

    private final XtraServerMapping xtraServerMapping;

    AbstractMappingTransformer(final XtraServerMapping xtraServerMapping) {
        this.xtraServerMapping = xtraServerMapping;
    }

    @Override
    public XtraServerMapping transform() {
        final Context context = new Context();
        context.xtraServerMapping = xtraServerMapping;

        final List<FeatureTypeMapping> transformedFeatureTypeMappings = xtraServerMapping.getFeatureTypeMappings().stream()
                .map(traverseFeatureTypeMapping(context))
                .collect(Collectors.toList());

        return transformXtraServerMapping(context, transformedFeatureTypeMappings).build();
    }

    @Override
    public FeatureTypeMapping transform(final FeatureTypeMapping featureTypeMapping) {
        final Context context = new Context();
        context.xtraServerMapping = xtraServerMapping;
        context.featureTypeMapping = featureTypeMapping;

        return traverseFeatureTypeMapping(context).apply(featureTypeMapping);
    }

    protected XtraServerMappingBuilder transformXtraServerMapping(final Context context, final List<FeatureTypeMapping> transformedFeatureTypeMappings) {
        return new XtraServerMappingBuilder()
                //.shallowCopyOf(xtraServerMapping)
                .featureTypeMappings(transformedFeatureTypeMappings);
    }

    protected FeatureTypeMappingBuilder transformFeatureTypeMapping(final Context context, final List<MappingTable> transformedMappingTables) {
        return new FeatureTypeMappingBuilder()
                .shallowCopyOf(context.featureTypeMapping)
                .primaryTables(transformedMappingTables);
    }

    protected MappingTableBuilder transformMappingTable(final Context context, final List<MappingTable> transformedMappingTables, final List<MappingJoin> transformedMappingJoins, final List<MappingValue> transformedMappingValues) {
        return new MappingTableBuilder()
                .shallowCopyOf(context.mappingTable)
                .values(transformedMappingValues)
                .joiningTables(transformedMappingTables)
                .joinPaths(transformedMappingJoins);
    }

    protected MappingValueBuilder.ValueDefault transformMappingValue(final Context context) {
        return new MappingValueBuilder()
                .copyOf(context.mappingValue);
    }

    private Function<FeatureTypeMapping, FeatureTypeMapping> traverseFeatureTypeMapping(final Context context) {
        return featureTypeMapping -> {
            context.featureTypeMapping = featureTypeMapping;

            final List<MappingTable> transformedTables = featureTypeMapping.getPrimaryTables().stream()
                    .map(traverseMappingTable(context))
                    .collect(Collectors.toList());

            return transformFeatureTypeMapping(context, transformedTables).build();
        };
    }

    private Function<MappingTable, MappingTable> traverseMappingTable(final Context context) {
        return mappingTable -> {
            // recurse
            final List<MappingTable> transformedJoiningTables = mappingTable.getJoiningTables().stream()
                    .map(traverseMappingTable(context))
                    .collect(Collectors.toList());

            context.mappingTable = mappingTable;

            final List<MappingValue> transformedValues = mappingTable.getValues().stream()
                    .map(traverseValue(context))
                    .collect(Collectors.toList());

            return transformMappingTable(context, transformedJoiningTables, mappingTable.getJoinPaths().asList(), transformedValues).build();

        };
    }

    private Function<MappingValue, MappingValue> traverseValue(final Context context) {
        return mappingValue -> {
            context.mappingValue = mappingValue;

            return transformMappingValue(context).build();
        };
    }

    class Context {
        XtraServerMapping xtraServerMapping;
        FeatureTypeMapping featureTypeMapping;
        MappingTable mappingTable;
        MappingValue mappingValue;
    }
}
