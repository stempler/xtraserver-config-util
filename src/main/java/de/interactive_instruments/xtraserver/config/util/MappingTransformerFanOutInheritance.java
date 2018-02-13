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

import com.google.common.collect.ImmutableList;
import de.interactive_instruments.xtraserver.config.util.api.*;
import org.apache.ws.commons.schema.XmlSchemaComplexType;

import javax.xml.namespace.QName;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author zahnen
 */
public class MappingTransformerFanOutInheritance implements MappingTransformer {

    private final XtraServerMapping xtraServerMapping;
    private final ApplicationSchema applicationSchema;
    private final Map<QName, FeatureTypeMapping> transformedMappings;

    public MappingTransformerFanOutInheritance(XtraServerMapping xtraServerMapping, ApplicationSchema applicationSchema) {
        this.xtraServerMapping = xtraServerMapping;
        this.applicationSchema = applicationSchema;
        this.transformedMappings = new LinkedHashMap<>();
    }

    @Override
    public XtraServerMapping transform() {
        xtraServerMapping.getFeatureTypeMappings()
                .forEach(this::transform);

        mergeAbstractGMLintoAbstractFeature();

        return new XtraServerMappingBuilder()
                .featureTypeMappings(ImmutableList.copyOf(transformedMappings.values()))
                .build();
    }

    @Override
    public FeatureTypeMapping transform(final FeatureTypeMapping featureTypeMapping) {
List<QName> bla = applicationSchema.getAllSuperTypeQualifiedNames(featureTypeMapping.getQualifiedName());
bla.add(featureTypeMapping.getQualifiedName());
        //applicationSchema.getAllSuperTypeQualifiedNames(featureTypeMapping.getQualifiedName()).stream()
        bla.stream()
                .filter(applicationSchema::hasElement)
                .map(createSuperType(featureTypeMapping))
                .forEach(transformedMapping -> {
                    //if (!transformedMappings.containsKey(transformedMapping.getQualifiedName())) {
                    transformedMappings.put(transformedMapping.getQualifiedName(), transformedMapping);
                    //}
                });

        return transformedMappings.get(featureTypeMapping.getQualifiedName());
    }

    private Function<QName, FeatureTypeMapping> createSuperType(final FeatureTypeMapping featureTypeMapping) {
        return superTypeName -> {

            final XmlSchemaComplexType superType = applicationSchema.getType(superTypeName);

            if (superType == null) {
                boolean b = true;
            }

            final FeatureTypeMappingBuilder parentMappingBuilder = Optional.ofNullable(transformedMappings.get(superTypeName))
                    .map(new FeatureTypeMappingBuilder()::copyOf)
                    .orElse(createFeatureTypeMappingBuilder(superTypeName, featureTypeMapping.getPrimaryTables()));

            final List<MappingTable> transformedOldTables = parentMappingBuilder.build().getPrimaryTables().stream()
                    //.filter(parentTable -> featureTypeMapping.hasTable(parentTable.getName()))
                    .map(createTable(parentMappingBuilder.build(), superType, featureTypeMapping))
                    .collect(Collectors.toList());

            final List<MappingTable> transformedNewTables = featureTypeMapping.getPrimaryTables().stream()
                    .filter(newTable -> !parentMappingBuilder.build().hasTable(newTable.getName()))
                    .map(createNewTable(parentMappingBuilder.build(), superType))
                    .collect(Collectors.toList());

            /*final List<MappingTable> unrelatedTables = parentMappingBuilder.build().getTables().stream()
                    .filter(parentTable -> !featureTypeMapping.hasTable(parentTable.getName()))
                    .collect(Collectors.toList());
*/
            return new FeatureTypeMappingBuilder().shallowCopyOf(parentMappingBuilder.build())
                    //return parentMappingBuilder
                    .primaryTables(transformedOldTables)
                    .primaryTables(transformedNewTables)
                    .build();
        };
    }

    /*
     * copy primary table as well as all values and joins whose targetPath belongs to the superType
     */
    private Function<MappingTable, MappingTable> createNewTable(final FeatureTypeMapping parentFeatureTypeMapping, final XmlSchemaComplexType superType) {
        return mappingTable -> {

            // do not recurse, if joiningTable belongs to superType, all child joins, tables and values will as well
            final List<MappingTable> transformedJoiningTables = mappingTable.getJoiningTables().stream()
                    .filter(joinBelongsToSuperType(superType))
                    //.map(createTable(parentFeatureTypeMapping, superType))
                    .collect(Collectors.toList());


            final List<MappingValue> transformedValues = mappingTable.getValues().stream()
                    .filter(valueBelongsToSuperType(superType))
                    //.map(createValue())
                    .collect(Collectors.toList());


            final MappingTableBuilder parentTableBuilder = parentFeatureTypeMapping.getTable(mappingTable.getName())
                    .map(new MappingTableBuilder()::copyOf)
                    .orElse(new MappingTableBuilder().shallowCopyOf(mappingTable));

            //return parentTableBuilder
            return new MappingTableBuilder().shallowCopyOf(mappingTable)
                    .values(transformedValues)
                    .joiningTables(transformedJoiningTables)
                    .joinPaths(mappingTable.getJoinPaths())
                    .build();
        };
    }

    private Function<MappingTable, MappingTable> createTable(final FeatureTypeMapping parentFeatureTypeMapping, final XmlSchemaComplexType superType, FeatureTypeMapping featureTypeMapping) {
        return mappingTable1 -> {
            if (featureTypeMapping.hasTable(mappingTable1.getName())) {
                MappingTable mappingTable = featureTypeMapping.getTable(mappingTable1.getName()).get();

                // do not recurse, if joiningTable belongs to superType, all child joins, tables and values will as well
                final List<MappingTable> transformedJoiningTables = mappingTable.getJoiningTables().stream()
                        .filter(joinBelongsToSuperType(superType))
                        //.map(createTable(parentFeatureTypeMapping, superType))
                        .collect(Collectors.toList());


                final List<MappingValue> transformedValues = mappingTable.getValues().stream()
                        .filter(valueBelongsToSuperType(superType))
                        //.map(createValue())
                        .collect(Collectors.toList());


                final MappingTableBuilder parentTableBuilder = parentFeatureTypeMapping.getTable(mappingTable.getName())
                        .map(new MappingTableBuilder()::copyOf)
                        .orElse(new MappingTableBuilder().shallowCopyOf(mappingTable));

                //return parentTableBuilder
                return new MappingTableBuilder().shallowCopyOf(mappingTable1)
                        .values(transformedValues)
                        .joiningTables(transformedJoiningTables)
                        .joinPaths(mappingTable.getJoinPaths())
                        .build();
            }
            return mappingTable1;
        };
    }

    private FeatureTypeMappingBuilder createFeatureTypeMappingBuilder(QName qualifiedName, ImmutableList<MappingTable> primaryTables) {
        final List<MappingTable> shallowCopyOfPrimaryTables = primaryTables.stream()
                .map(mappingTable -> new MappingTableBuilder().copyOf(mappingTable).build())
                .collect(Collectors.toList());

        return new FeatureTypeMappingBuilder()
                .name(applicationSchema.getNamespaces().getPrefixedName(qualifiedName))
                .qualifiedName(qualifiedName)
                .isAbstract(applicationSchema.isAbstract(qualifiedName))
                .superTypeName(applicationSchema.getSuperTypeName(qualifiedName).orElse(null))
                .primaryTables(shallowCopyOfPrimaryTables);
    }

    private void mergeAbstractGMLintoAbstractFeature() {
        final QName abstractGml = new QName("http://www.opengis.net/gml/3.2", "AbstractGML");
        final QName abstractFeature = new QName("http://www.opengis.net/gml/3.2", "AbstractFeature");

        if (transformedMappings.containsKey(abstractGml) && transformedMappings.containsKey(abstractFeature)) {
            final FeatureTypeMapping mergedMapping = new FeatureTypeMappingBuilder()
                    .copyOf(transformedMappings.get(abstractGml))
                    .shallowCopyOf(transformedMappings.get(abstractFeature))
                    .build();

            transformedMappings.remove(abstractGml);
            transformedMappings.replace(abstractFeature, mergedMapping);
        }
    }

    private Predicate<MappingTable> joinBelongsToSuperType(final XmlSchemaComplexType superType) {
        return mappingTable -> !mappingTable.getQualifiedTargetPath().isEmpty() && applicationSchema.hasProperty(superType, mappingTable.getQualifiedTargetPath().get(0));
    }

    private Predicate<MappingValue> valueBelongsToSuperType(final XmlSchemaComplexType superType) {
        return mappingValue -> !mappingValue.getQualifiedTargetPath().isEmpty() && applicationSchema.hasProperty(superType, mappingValue.getQualifiedTargetPath().get(0));
    }

}
