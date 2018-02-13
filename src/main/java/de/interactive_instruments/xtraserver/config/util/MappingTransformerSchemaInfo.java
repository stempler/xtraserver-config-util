package de.interactive_instruments.xtraserver.config.util;

import de.interactive_instruments.xtraserver.config.util.api.*;

import java.util.List;

/**
 * @author zahnen
 */
public class MappingTransformerSchemaInfo extends AbstractMappingTransformer implements MappingTransformer {

    private final ApplicationSchema applicationSchema;

    public MappingTransformerSchemaInfo(final XtraServerMapping xtraServerMapping, final ApplicationSchema applicationSchema) {
        super(xtraServerMapping);
        this.applicationSchema = applicationSchema;
    }

    @Override
    protected XtraServerMappingBuilder transformXtraServerMapping(XtraServerMapping xtraServerMapping, List<FeatureTypeMapping> transformedFeatureTypeMappings) {
        return super.transformXtraServerMapping(xtraServerMapping, transformedFeatureTypeMappings);
    }

    @Override
    protected FeatureTypeMappingBuilder transformFeatureTypeMapping(FeatureTypeMapping featureTypeMapping, List<MappingTable> transformedMappingTables) {
        return super.transformFeatureTypeMapping(featureTypeMapping, transformedMappingTables);
    }

    @Override
    protected MappingTableBuilder transformMappingTable(MappingTable mappingTable, List<MappingTable> transformedMappingTables, List<MappingJoin> transformedMappingJoins, List<MappingValue> transformedMappingValues) {
        return super.transformMappingTable(mappingTable, transformedMappingTables, transformedMappingJoins, transformedMappingValues);
    }

    @Override
    protected MappingValueBuilder.ValueDefault transformMappingValue(MappingValue mappingValue) {
        /* ((MappingValueImpl)mappingValue).setRootProperty(pathElements.get(0).getLocalPart());
            if (applicationSchema.isGeometry(type, pathElements.get(0))) {
                ((MappingValueImpl)mappingValue).setGeometry(true);
            }*/

        return super.transformMappingValue(mappingValue);
    }
}
