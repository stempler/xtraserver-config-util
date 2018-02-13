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

import com.google.common.collect.ImmutableList;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder for {@link FeatureTypeMapping}
 *
 * @author zahnen
 */
public class FeatureTypeMappingBuilder {
    private String name;
    private QName qualifiedName;
    private String superTypeName;
    private boolean isAbstract;
    private final List<MappingTable> primaryTables;

    /**
     * Create new builder
     */
    public FeatureTypeMappingBuilder() {
        this.primaryTables = new ArrayList<>();
    }

    /**
     * Set the prefixed feature type name (required)
     *
     * @param name prefixed name
     * @return the builder
     */
    public FeatureTypeMappingBuilder name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Set the qualified feature type name (required)
     *
     * @param qualifiedName qualified name
     * @return the builder
     */
    public FeatureTypeMappingBuilder qualifiedName(QName qualifiedName) {
        this.qualifiedName = qualifiedName;
        return this;
    }

    /**
     * Set the prefixed feature type name of this types supertype
     *
     * @param superTypeName prefixed name
     * @return the builder
     */
    public FeatureTypeMappingBuilder superTypeName(String superTypeName) {
        this.superTypeName = superTypeName;
        return this;
    }

    /**
     * Is the feature type abstract?
     *
     * @param isAbstract is it?
     * @return the builder
     */
    public FeatureTypeMappingBuilder isAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
        return this;
    }

    /**
     * Add a primary table for the feature type (at least one primary table is required).
     * Primary tables can be created with {@link MappingTableBuilder}
     *
     * @param primaryTable the primary table
     * @return the builder
     */
    public FeatureTypeMappingBuilder primaryTable(MappingTable primaryTable) {
        this.primaryTables.add(primaryTable);
        return this;
    }

    /**
     * Add a list of primary tables for the feature type (at least one primary table is required).
     * Primary tables can be created with {@link MappingTableBuilder}
     *
     * @param primaryTables the primary tables
     * @return the builder
     */
    public FeatureTypeMappingBuilder primaryTables(List<MappingTable> primaryTables) {
        this.primaryTables.addAll(primaryTables);
        return this;
    }

    /**
     * Copy name, qualifiedName, superTypeName and isAbstract from given {@link FeatureTypeMapping}
     *
     * @param featureTypeMapping the copy source
     * @return the builder
     */
    public FeatureTypeMappingBuilder shallowCopyOf(FeatureTypeMapping featureTypeMapping) {
        this.name = featureTypeMapping.getName();
        this.qualifiedName = featureTypeMapping.getQualifiedName();
        this.superTypeName = featureTypeMapping.getSuperTypeName().orElse(null);
        this.isAbstract = featureTypeMapping.isAbstract();

        return this;
    }

    /**
     * Same as {@link FeatureTypeMappingBuilder#shallowCopyOf(FeatureTypeMapping)}, additionally copies primary tables
     *
     * @param featureTypeMapping the copy source
     * @return the builder
     */
    public FeatureTypeMappingBuilder copyOf(FeatureTypeMapping featureTypeMapping) {
        shallowCopyOf(featureTypeMapping);

        this.primaryTables.addAll(featureTypeMapping.getPrimaryTables());

        return this;
    }

    /**
     * Builds the {@link FeatureTypeMapping}, validates required fields
     *
     * @return a new immutable {@link FeatureTypeMapping}
     */
    public FeatureTypeMapping build() {
        final FeatureTypeMapping featureTypeMapping = new FeatureTypeMapping(name, qualifiedName, superTypeName, isAbstract, ImmutableList.copyOf(primaryTables));

        validate(featureTypeMapping);

        return featureTypeMapping;
    }

    private void validate(final FeatureTypeMapping featureTypeMapping) {
        if (featureTypeMapping.getName() == null || featureTypeMapping.getName().isEmpty()) {
            throw new IllegalStateException("FeatureTypeMapping has no name");
        }
        if (featureTypeMapping.getQualifiedName() == null) {
            throw new IllegalStateException("FeatureTypeMapping has no qualified name");
        }

        if (featureTypeMapping.getPrimaryTables().isEmpty()) {
            throw new IllegalStateException("FeatureTypeMapping has no primary tables");
        }

        // TODO: no drafts in primary tables
    }
}