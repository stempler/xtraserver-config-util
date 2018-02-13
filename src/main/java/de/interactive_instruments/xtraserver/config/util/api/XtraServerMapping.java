package de.interactive_instruments.xtraserver.config.util.api;

import com.google.common.collect.ImmutableList;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Collection of mappings for feature types from a common GML application schema
 *
 * @author zahnen
 */
public class XtraServerMapping {
    private final Map<String, FeatureTypeMapping> featureTypeMappings;
    // TODO: add description with targetNamespace and version from applicationSchema

    XtraServerMapping(final Map<String, FeatureTypeMapping> featureTypeMappings) {
        this.featureTypeMappings = featureTypeMappings;
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
    public ImmutableList<FeatureTypeMapping> getFeatureTypeMappings(boolean excludeAbstract) {
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
    public ImmutableList<String> getFeatureTypeNames(boolean excludeAbstract) {
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
    public boolean hasFeatureType(String featureTypeName) {
        return featureTypeMappings.containsKey(featureTypeName);
    }

    /**
     * Returns the mapping for the given feature type if found
     *
     * @param featureTypeName the prefixed feature type name
     * @return the mapping
     */
    public Optional<FeatureTypeMapping> getFeatureTypeMapping(String featureTypeName) {
        return Optional.ofNullable(featureTypeMappings.get(featureTypeName));
    }

    /**
     * Returns the mapping for the given feature type if found as well as the mappings for all recursively found supertypes.
     * Sorting order is from top to bottom, so normally starting with gml:AbstractFeature if available.
     *
     * @param featureTypeName the prefixed feature type name
     * @return the list of mappings
     */
    public ImmutableList<FeatureTypeMapping> getFeatureTypeMappingInheritanceChain(String featureTypeName) {
        ImmutableList.Builder<FeatureTypeMapping> inheritanceChain = new ImmutableList.Builder<>();

        FeatureTypeMapping featureTypeMapping = featureTypeMappings.get(featureTypeName);
        while (featureTypeMapping != null) {
            inheritanceChain.add(featureTypeMapping);
            featureTypeMapping = featureTypeMapping.getSuperTypeName()
                    .map(featureTypeMappings::get)
                    .orElse(null);
        }

        return inheritanceChain.build().reverse();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XtraServerMapping that = (XtraServerMapping) o;
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
