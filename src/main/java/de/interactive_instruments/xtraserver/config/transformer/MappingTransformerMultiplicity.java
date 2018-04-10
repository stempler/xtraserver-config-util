package de.interactive_instruments.xtraserver.config.transformer;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import de.interactive_instruments.xtraserver.config.api.*;

import javax.xml.namespace.QName;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * @author zahnen
 */
public class MappingTransformerMultiplicity extends AbstractMappingTransformer {

    final ApplicationSchema applicationSchema;

    MappingTransformerMultiplicity(XtraServerMapping xtraServerMapping, final ApplicationSchema applicationSchema) {
        super(xtraServerMapping);
        this.applicationSchema = applicationSchema;
    }

    @Override
    protected MappingTableBuilder transformMappingTable(Context context, List<MappingTable> transformedMappingTables, List<MappingJoin> transformedMappingJoins, List<MappingValue> transformedMappingValues) {
        final FeatureTypeMapping featureTypeMapping = context.featureTypeMapping;
        final MappingTable mappingTable = context.mappingTable;

        List<MappingValue> transformedMappingValues2 = transformedMappingValues.stream()
                                                                               .sorted(Comparator.comparing(MappingValue::getTargetPath))
                                                                               .collect(new ForEachSelectIdCollector(featureTypeMapping.getQualifiedName(), mappingTable, transformedMappingTables));

        return new MappingTableBuilder()
                .shallowCopyOf(mappingTable)
                .values(transformedMappingValues2)
                .joiningTables(transformedMappingTables)
                .joinPaths(transformedMappingJoins);
    }

    private class ForEachSelectIdCollector implements Collector<MappingValue, List<MappingValue>, List<MappingValue>> {

        private final QName currentFeatureType;
        private final MappingTable currentTable;
        private final List<MappingTable> transformedMappingTables;
        private final Map<List<QName>, List<Integer>> selectIds;

        public ForEachSelectIdCollector(final QName currentFeatureType, final MappingTable currentTable, final List<MappingTable> transformedMappingTables) {
            this.currentFeatureType = currentFeatureType;
            this.currentTable = currentTable;
            this.transformedMappingTables = transformedMappingTables;
            this.selectIds = new LinkedHashMap<>();
        }

        @Override
        public Supplier<List<MappingValue>> supplier() {
            return ArrayList::new;
        }

        @Override
        public BiConsumer<List<MappingValue>, MappingValue> accumulator() {
            return (values, mappingValue) -> {
                final Optional<MappingValue> first = values.stream()
                                                           .filter(value -> value.getTargetPath()
                                                                                 .equals(mappingValue.getTargetPath())
                                                                   && !value.getValue()
                                                                            .equals(mappingValue.getValue()))
                                                           .sorted((mappingValue1, mappingValue2) -> mappingValue2.getSelectId() - mappingValue1.getSelectId())
                                                           .findFirst();

                if (first.isPresent()) {
                    MappingValue firstValue = first.get();
                    if (Objects.isNull(firstValue.getSelectId())) { // no select id
                        int i = values.indexOf(firstValue);
                        firstValue = new MappingValueBuilder()
                                .copyOf(firstValue)
                                .selectId(1) // set select id
                                .build();
                        values.set(i, firstValue); // set select id

                        selectIds.put(firstValue.getQualifiedTargetPath(), Lists.newArrayList(1));
                    }

                    int newId = firstValue.getSelectId()+1;

                    final MappingValue mappingValue2 = new MappingValueBuilder()
                            .copyOf(mappingValue)
                            .selectId(newId) // set select id
                            .build();
                    values.add(mappingValue2);

                    selectIds.get(mappingValue.getQualifiedTargetPath()).add(newId);
                } else {
                    values.add(mappingValue);
                }
            };
        }

        @Override
        public BinaryOperator<List<MappingValue>> combiner() {
            return (left, right) -> {
                left.addAll(right);
                return left;
            };
        }

        @Override
        public Function<List<MappingValue>, List<MappingValue>> finisher() {
            return mappingValues -> {

                selectIds.forEach((key, value) -> {
                    final List<QName> lastMultiplePropertyPath = applicationSchema.getLastMultiplePropertyPath(currentFeatureType, key);

                    transformedMappingTables.add(new MappingTableBuilder()
                            .name(currentTable.getName())
                            //TODO
                            .primaryKey(currentTable.getPrimaryKey())
                            .qualifiedTargetPath(lastMultiplePropertyPath)
                            .selectIds(Joiner.on(",")
                                             .join(value))
                            .description("multiplicity - for_each_select_id for " + applicationSchema.getNamespaces().getPrefixedPath(lastMultiplePropertyPath))
                            .build()
                    ); // add for_each_select_id, find multiple prop first
                });


                return mappingValues;
            };
        }

        @Override
        public Set<Characteristics> characteristics() {
            return EnumSet.of(Characteristics.UNORDERED);
        }
    }
}
