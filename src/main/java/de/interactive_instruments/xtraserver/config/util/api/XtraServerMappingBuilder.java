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
package de.interactive_instruments.xtraserver.config.util.api;

import com.google.common.collect.ImmutableMap;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Builder for {@link XtraServerMapping}
 *
 * @author zahnen
 */
public class XtraServerMappingBuilder {
    private final Map<String, FeatureTypeMapping> featureTypeMappings;

    /**
     * Create new builder
     */
    public XtraServerMappingBuilder() {
        this.featureTypeMappings = new LinkedHashMap<>();
    }

    /**
     * Add a feature type mapping (at least one is required).
     * Feature type mappings can be created with {@link FeatureTypeMappingBuilder}
     *
     * @param featureTypeMapping the feature type mapping
     * @return the builder
     */
    public XtraServerMappingBuilder featureTypeMapping(FeatureTypeMapping featureTypeMapping) {
        this.featureTypeMappings.put(featureTypeMapping.getName(), featureTypeMapping);
        return this;
    }

    /**
     * Add a list of feature type mappings (at least one is required).
     * Feature type mappings can be created with {@link FeatureTypeMappingBuilder}
     *
     * @param featureTypeMappings the feature type mappings
     * @return the builder
     */
    public XtraServerMappingBuilder featureTypeMappings(List<FeatureTypeMapping> featureTypeMappings) {
        featureTypeMappings.forEach(this::featureTypeMapping);
        return this;
    }

    /**
     * Copy the feature type mappings from given {@link XtraServerMapping}
     *
     * @param xtraServerMapping the copy source
     * @return the builder
     */
    public XtraServerMappingBuilder copyOf(XtraServerMapping xtraServerMapping) {
        xtraServerMapping.getFeatureTypeMappings().forEach(this::featureTypeMapping);
        return this;
    }

    /**
     * Builds the {@link XtraServerMapping}, validates required fields
     *
     * @return a new immutable {@link XtraServerMapping}
     */
    public XtraServerMapping build() {
        final XtraServerMapping xtraServerMapping = new XtraServerMapping(ImmutableMap.copyOf(featureTypeMappings));

        validate(xtraServerMapping);

        return xtraServerMapping;
    }

    private void validate(final XtraServerMapping xtraServerMapping) {
        if (xtraServerMapping.getFeatureTypeMappings().isEmpty()) {
            throw new IllegalStateException("XtraServerMapping has no feature type mappings");
        }
    }
}