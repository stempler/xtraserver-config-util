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