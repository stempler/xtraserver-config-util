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

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import de.interactive_instruments.xtraserver.config.api.*;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author zahnen
 */
class MappingTransformerSchemaInfo extends AbstractMappingTransformer implements MappingTransformer {

    private static final QName GML_ABSTRACT_FEATURE = new QName("http://www.opengis.net/gml/3.2", "AbstractFeature");

    private final ApplicationSchema applicationSchema;
    private final Namespaces namespaces;

    MappingTransformerSchemaInfo(final XtraServerMapping xtraServerMapping, final ApplicationSchema applicationSchema) {
        super(xtraServerMapping);
        this.applicationSchema = applicationSchema;
        this.namespaces = applicationSchema.getNamespaces();
    }

    @Override
    protected XtraServerMappingBuilder transformXtraServerMapping(final Context context, final List<FeatureTypeMapping> transformedFeatureTypeMappings) {
        final XtraServerMapping xtraServerMapping = context.xtraServerMapping;

        final List<FeatureTypeMapping> filteredFeatureTypeMappings = transformedFeatureTypeMappings.stream()
                .filter(existsInSchema())
                .collect(Collectors.toList());

        return new XtraServerMappingBuilder()
                //.shallowCopyOf(xtraServerMapping)
                .featureTypeMappings(filteredFeatureTypeMappings);
    }

    @Override
    protected FeatureTypeMappingBuilder transformFeatureTypeMapping(final Context context, final List<MappingTable> transformedMappingTables) {
        final FeatureTypeMapping featureTypeMapping = context.featureTypeMapping;

        // either name or qualifiedName is set, derive the other
        final String name = Strings.isNullOrEmpty(featureTypeMapping.getName()) ? namespaces.getPrefixedName(featureTypeMapping.getQualifiedName()) : featureTypeMapping.getName();
        final QName qualifiedName = Objects.isNull(featureTypeMapping.getQualifiedName()) ? namespaces.getQualifiedName(featureTypeMapping.getName()) : featureTypeMapping.getQualifiedName();

        final List<MappingTable> descriptionMappingTables = transformedMappingTables.stream()
                .map(addDescription(qualifiedName))
                .collect(Collectors.toList());

        return new FeatureTypeMappingBuilder()
                .shallowCopyOf(featureTypeMapping)
                .name(name)
                .qualifiedName(qualifiedName)
                // TODO qname
                .superTypeName(applicationSchema.getSuperTypeName(qualifiedName).orElse(null))
                .isAbstract(featureTypeMapping.isAbstract() || GML_ABSTRACT_FEATURE.equals(qualifiedName))
                .primaryTables(descriptionMappingTables);
    }

    @Override
    protected MappingTableBuilder transformMappingTable(final Context context, final List<MappingTable> transformedMappingTables, final List<MappingJoin> transformedMappingJoins, final List<MappingValue> transformedMappingValues) {
        final MappingTable mappingTable = context.mappingTable;

        // either targetPath or qualifiedTargetPath is set, derive the other
        final String targetPath = Strings.isNullOrEmpty(mappingTable.getTargetPath()) && !mappingTable.getQualifiedTargetPath().isEmpty() ? namespaces.getPrefixedPath(mappingTable.getQualifiedTargetPath()) : mappingTable.getTargetPath();
        final List<QName> targetPathElements = !Strings.isNullOrEmpty(mappingTable.getTargetPath()) ? namespaces.getQualifiedPathElements(mappingTable.getTargetPath()) : mappingTable.getQualifiedTargetPath();
        final String description = !targetPathElements.isEmpty() ? targetPathElements.get(0).getLocalPart() : "";

        return new MappingTableBuilder()
                .shallowCopyOf(mappingTable)
                .targetPath(targetPath)
                .qualifiedTargetPath(targetPathElements)
                .description(description)
                .values(transformedMappingValues)
                .joiningTables(transformedMappingTables)
                .joinPaths(transformedMappingJoins);
    }

    @Override
    protected MappingValueBuilder.ValueDefault transformMappingValue(final Context context) {
        final MappingValue mappingValue = context.mappingValue;

        // either targetPath or qualifiedTargetPath is set, derive the other
        final String targetPath = Strings.isNullOrEmpty(mappingValue.getTargetPath()) ? namespaces.getPrefixedPath(mappingValue.getQualifiedTargetPath()) : mappingValue.getTargetPath();
        final List<QName> targetPathElements = !Strings.isNullOrEmpty(mappingValue.getTargetPath()) ? namespaces.getQualifiedPathElements(mappingValue.getTargetPath()) : mappingValue.getQualifiedTargetPath();
        final String description = !targetPathElements.isEmpty() ? targetPathElements.get(0).getLocalPart() : "";

        if (isGeometry(context, targetPathElements)) {
            return new MappingValueBuilder()
                    .geometry()
                    .targetPath(targetPath)
                    .qualifiedTargetPath(targetPathElements)
                    .value(mappingValue.getValue())
                    .description(description);

        }

        return new MappingValueBuilder()
                .copyOf(mappingValue)
                .targetPath(targetPath)
                .qualifiedTargetPath(targetPathElements)
                .description(description);
    }

    private Function<MappingTable, MappingTable> addDescription(final QName qualifiedName) {
        return mappingTable -> new MappingTableBuilder()
                .copyOf(mappingTable)
                .description(qualifiedName != null ? qualifiedName.getLocalPart() : "")
                .build();
    }

    private Predicate<FeatureTypeMapping> existsInSchema() {
        return featureTypeMapping -> applicationSchema.hasElement(featureTypeMapping.getQualifiedName());
    }

    private boolean isGeometry(final Context context, final List<QName> targetPathElements) {
        final QName qualifiedFeatureTypeName = Strings.isNullOrEmpty(context.featureTypeMapping.getName())
                ? context.featureTypeMapping.getQualifiedName()
                : namespaces.getQualifiedName(context.featureTypeMapping.getName());

        return context.mappingValue.isColumn() && !targetPathElements.isEmpty()
                && applicationSchema.isGeometry(qualifiedFeatureTypeName, targetPathElements.get(0));
    }
}
