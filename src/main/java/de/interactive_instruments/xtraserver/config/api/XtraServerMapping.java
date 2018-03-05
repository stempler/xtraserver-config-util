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
package de.interactive_instruments.xtraserver.config.api;

import com.google.common.collect.ImmutableList;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
// TODO: description, set from hale and from transformer
/**
 * Collection of mappings for feature types from a common GML application schema
 *
 * @author zahnen
 */
public class XtraServerMapping {
    private final Map<String, FeatureTypeMapping> featureTypeMappings;
    // TODO: add description with targetNamespace and version from applicationSchema
    private final String description;

    XtraServerMapping(final Map<String, FeatureTypeMapping> featureTypeMappings, String description) {
        this.featureTypeMappings = featureTypeMappings;
        this.description = description;
    }

    /**
     * Get the list of mappings for all feature types
     *
     * @return the list of mappings
     */
    public ImmutableList<FeatureTypeMapping> getFeatureTypeMappings() {
        return getFeatureTypeMappings(false);
    }

    /**
     * Get the list of mappings for all or all non-abstract feature types
     *
     * @param excludeAbstract if true exclude abstract FeatureTypes
     * @return the list of mappings
     */
    public ImmutableList<FeatureTypeMapping> getFeatureTypeMappings(final boolean excludeAbstract) {
        return featureTypeMappings.values().stream()
                .filter(featureTypeMapping -> !excludeAbstract || !featureTypeMapping.isAbstract())
                // TODO: can be replaced with ImmutableList.toImmutableList() starting with guava 21
                .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
    }

    /**
     * Get the names of all feature types
     *
     * @return the list of prefixed feature type names
     */
    public ImmutableList<String> getFeatureTypeNames() {
        return getFeatureTypeNames(false);
    }

    /**
     * Get the names of all or all non-abstract feature types
     *
     * @param excludeAbstract if true exclude abstract FeatureTypes
     * @return the list of prefixed feature type names
     */
    public ImmutableList<String> getFeatureTypeNames(final boolean excludeAbstract) {
        return featureTypeMappings.values().stream()
                .filter(featureTypeMapping -> !excludeAbstract || !featureTypeMapping.isAbstract())
                .map(FeatureTypeMapping::getName)
                // TODO: can be replaced with ImmutableList.toImmutableList() starting with guava 21
                .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
    }

    /**
     * Does a mapping exist for the given feature type?
     *
     * @param featureTypeName the prefixed feature type name
     * @return true if exists
     */
    public boolean hasFeatureType(final String featureTypeName) {
        return featureTypeMappings.containsKey(featureTypeName);
    }

    /**
     * Returns the mapping for the given feature type if found
     *
     * @param featureTypeName the prefixed feature type name
     * @return the mapping
     */
    public Optional<FeatureTypeMapping> getFeatureTypeMapping(final String featureTypeName) {
        return Optional.ofNullable(featureTypeMappings.get(featureTypeName));
    }

    /**
     * Returns the mapping for the given feature type if found as well as the mappings for all recursively found supertypes.
     * Sorting order is from top to bottom, so normally starting with gml:AbstractFeature if available.
     *
     * @param featureTypeName the prefixed feature type name
     * @return the list of mappings
     */
    public ImmutableList<FeatureTypeMapping> getFeatureTypeMappingInheritanceChain(final String featureTypeName) {
        final ImmutableList.Builder<FeatureTypeMapping> inheritanceChain = new ImmutableList.Builder<>();

        FeatureTypeMapping featureTypeMapping = featureTypeMappings.get(featureTypeName);
        while (featureTypeMapping != null) {
            inheritanceChain.add(featureTypeMapping);
            featureTypeMapping = featureTypeMapping.getSuperTypeName()
                    .map(featureTypeMappings::get)
                    .orElse(null);
        }

        return inheritanceChain.build().reverse();
    }

    /**
     * Returns the description
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final XtraServerMapping that = (XtraServerMapping) o;
        return Objects.equals(featureTypeMappings, that.featureTypeMappings);
    }

    @Override
    public int hashCode() {

        return Objects.hash(featureTypeMappings);
    }

    @Override
    public String toString() {
        return "\nXtraServerMappingImpl{" +
                "\nfeatureTypeMappings=" + featureTypeMappings +
                "\n}";
    }
}
