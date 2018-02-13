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
